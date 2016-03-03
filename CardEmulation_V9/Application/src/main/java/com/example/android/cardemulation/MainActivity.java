/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


package com.example.android.cardemulation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;
import com.example.android.common.activities.SampleActivityBase;
import com.example.android.common.logger.Log;
import com.example.android.common.logger.LogFragment;
import com.example.android.common.logger.LogWrapper;
import com.example.android.common.logger.MessageOnlyLogFilter;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Locale;


/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
public class MainActivity extends SampleActivityBase {

    //Declaration and Initialization of variables
    public static final String TAG = "MainActivity";
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static Context c;
    final MainActivity s = this;
    static String cardPayload = "";
    static String updatedCardPayload = "";
    static String key = "GHIJKLMNOPQRSTUVWXYZYXWVUTSRQPON";
    static String updatedEncrypedCardPayload = "";
    static String saveAccount ="";
    boolean lockRead = true;
    boolean lockWrite = false;
    private TextView myTextView;
    private NfcAdapter mNfcadapter;
    private AlertDialog mDialog;
    NfcAdapter nfcAdapter;
    CardEmulationFragment frag = new CardEmulationFragment();

    static volatile String usercmd = "";
    static String serverMessage;
    volatile static String cid = "";

    com.example.android.cardemulation.AndroidGUI clientUI = new com.example.android.cardemulation.AndroidGUI();
    com.example.android.cardemulation.ServerMessage serverMsg = new com.example.android.cardemulation.ServerMessage(clientUI);
    com.example.android.cardemulation.Client myClient = new com.example.android.cardemulation.Client(serverMsg);
    com.example.android.cardemulation.UserCommand myCmd = new com.example.android.cardemulation.UserCommand(myClient, clientUI, serverMsg,this);
    com.example.android.cardemulation.CardService card = new com.example.android.cardemulation.CardService();


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            TextView myTextBox = (TextView) findViewById(R.id.serverMsg_Box);
            myTextBox.setText(serverMessage);
        }
    };

   /*
  * Author: Erwin Alejo
  * Purpose: Display messages on
  * the GUI textbox.
  * */
    public void update(String theString) {
        this.serverMessage = theString;
        handler.sendMessage(handler.obtainMessage());
    }

    // Whether the Log Fragment is currently shown
    private boolean mLogShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myClient.setPort(5555);
        myClient.setHost("140.193.239.197");
        clientUI.setUserCmd(myCmd, this);
        clientUI.start();

        myTextView = (TextView) findViewById(R.id.textView_explanation);
        mDialog = new AlertDialog.Builder(this).setNeutralButton("OK", null).create();
        mNfcadapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);


        if(mNfcadapter == null)
        {

            showMessage(R.string.error, R.string.no_nfc);
            finish();
            return;
        }//if
        else
        {
            //Checks if there is an existing payload stored in the account
            if(!AccountStorage.GetAccount(this).equals("") && AccountStorage.GetAccount(this).length() == 32){
                saveAccount = encrypt(AccountStorage.GetAccount(this), key, AccountStorage.GetAccount(this).length());
                //Handles the case when the CID is not 10 characters long
                if(hexToString(saveAccount.substring(0,8)).length() < 10) {
                    cid = String.format("%010d",Integer.parseInt(hexToString(saveAccount.substring(0, 8))));
                }//if
                else
                {
                    cid = hexToString(saveAccount.substring(0, 8));
                }//else
                System.out.println("This is saveAccount: "+saveAccount);

                //Note the user which device is currently active
                if (saveAccount.substring(8,9).contains("1") && saveAccount.substring(9,10).contains("1")) {
                    showMessage(R.string.welcome, R.string.phone_active);
                    clientUI.display("Phone is currently active E-Pass.");

                }//if
            }//if
            else {
                showMessage(R.string.welcome, R.string.intro);
                clientUI.display("Card is currently active E-Pass." + "\n" + "Scan card to enable 'Phone' Button");
            }//else
        }//else

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            CardEmulationFragment fragment = new CardEmulationFragment();
            fragment.setLock(lockRead, lockWrite, this);
            processReadIntent(getIntent());
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }//if
    }//onCreate

    /*
    * Author: Erwin Alejo
    * Purpose: To execute the user command for updating
    * device bit to 1.*/
    public void emulateCommands()
    {
        usercmd = "2 phone Capstone2015Phone 8 " + cid + " 1";
    }//emulateCommands

    /*
   * Author: Erwin Alejo
   * Purpose: To execute the user command for updating
   * device bit to 1, extracting the current time stamp
   * and updated balance.*/
    public void writebackCommands() {
      usercmd = String.format("7 phone Capstone2015Phone8 %010d 010 %010d9 %010d", Integer.parseInt(cid), Integer.parseInt(cid), Integer.parseInt(cid));

    }//writebackCommands

    /*
  * Author: Erwin Alejo
  * Purpose: Requests the database
  * for the updated balance.
  * */
    public static void checkBalCommands() {
        usercmd = "3 phone Capstone2015Phone 9 " + cid;
    }//checkBalCommands

    /*
  * Author: Erwin Alejo
  * Purpose: Requests the database
  * for the updated time stamp.
  * */
    public static void timeStampCommands() {
        usercmd = "5 phone Capstone2015Phone 10 " + cid +
                  " 1";
    }//timeStampCommands

    /*
  * Author: Erwin Alejo
  * Purpose: Adds user-specified
  * amount to the associated account
  * on the database
  * */
    public void addBalCommands(String amount)
    {
            usercmd = "4 phone Capstone2015Phone 5 " + cid + " " + amount;
            clientUI.display("Successfully added $" + amount + ".00");
    }//addBalCommands

    //API Code
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //API Code
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);
        logToggle.setVisible(findViewById(R.id.sample_output) instanceof ViewAnimator);
        logToggle.setTitle(mLogShown ? R.string.sample_hide_log : R.string.sample_show_log);

        return super.onPrepareOptionsMenu(menu);
    }

    //API Code
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_toggle_log:
                mLogShown = !mLogShown;
                ViewAnimator output = (ViewAnimator) findViewById(R.id.sample_output);
                if (mLogShown) {
                    output.setDisplayedChild(1);
                } else {
                    output.setDisplayedChild(0);
                }
                supportInvalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //API Code
    /** Create a chain of targets that will receive log data */
    @Override
    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());

        Log.i(TAG, "Ready");
    }


    //Author: Ralf Wondratschek
    //Modified by: Erwin Alejo
    protected void onResume()
    {
        super.onResume();
        //Enable read intents only
        if(frag.lockRead) {
            if (mNfcadapter != null) {
                if (!mNfcadapter.isEnabled()) {
                    showWirelessSettingsDialog();
                }//if
            }//if
            setupForegroundDispatch(this, mNfcadapter);
        }//if

        //Enable write intents only
        else if(frag.lockWrite) {
            enableforegroundDispatchSystem();
        }//elseif
    }//onResume

    //Author: Ralf Wondratschek
    //Modified by: Erwin Alejo
    /**
     * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        //Enable read intents only
        if(frag.lockRead) {
            stopForegroundDispatch(this, mNfcadapter);
        }//if
        //Enable write intents only
        else if(frag.lockWrite) {
            disableforegroundDispatchSystem();
        }//elseif
    }//onPause

    //Author: Ralf Wondratschek
    //Modified by: Erwin Alejo
    /**
     * This method gets called, when a new Intent gets associated with the current activity instance.
     * Instead of creating a new activity, onNewIntent will be called. For more information have a look
     * at the documentation.
     *
     * In our case this method gets called, when the user attaches a Tag to the device.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        //Enable read intents only
        if (frag.lockRead) {
            processReadIntent(intent);
        }//if
        //Enable read intents only
        else if (frag.lockWrite) {
            super.onNewIntent(intent);
            if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
                Toast.makeText(this, "Writing card...", Toast.LENGTH_SHORT).show();

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                //Handles the activation of the card from the phone
                    if (frag.phonePayload.substring(9, 10).equals("0")) {
                        //Checks if the current payload stored on the phone is the same as the payload read from the card
                        if (frag.phonePayload.substring(10, 18).equals(serverMsg.updatedBal) && frag.phonePayload.substring(18, 26).equals(serverMsg.updatedDate) && frag.phonePayload.substring(26, 32).equals(serverMsg.updatedTime)) {
                            while (myCmd.isOkToDisc) ;
                            updatedCardPayload = frag.phonePayload.substring(0, 8) + "0" + "1" + frag.phonePayload.substring(10, frag.phonePayload.length());
                            updatedEncrypedCardPayload = encrypt(updatedCardPayload, key, updatedCardPayload.length());

                        }//if
                        else{
                            while (myCmd.isOkToDisc) ;
                            updatedCardPayload = frag.phonePayload.substring(0, 8) + "0" + "1" + serverMsg.updatedBal + serverMsg.updatedDate + serverMsg.updatedTime;
                            updatedEncrypedCardPayload = encrypt(updatedCardPayload, key, updatedCardPayload.length());

                        }//else
                        NdefMessage ndefMessageAct = createNdefMessage(updatedEncrypedCardPayload);
                        writeNdefMessageToAct(tag, ndefMessageAct);

                    }//if

                    //Handles the deactivation of the card
                    else if (frag.phonePayload.substring(9, 10).equals("1")) {
                        updatedCardPayload = frag.phonePayload.substring(0, 8) + "1" + "0" + frag.phonePayload.substring(10, 18) + frag.phonePayload.substring(18, 26) + frag.phonePayload.substring(26, 32);
                        updatedEncrypedCardPayload = encrypt(updatedCardPayload, key, updatedCardPayload.length());
                        NdefMessage ndefMessage = createNdefMessage(updatedEncrypedCardPayload);
                        writeNdefMessageToDeact(tag, ndefMessage);
                    }//elseif

                //Disables write intent and enables read intent
                frag.lockWrite = false;
                frag.lockRead = true;

            }//if
        }//elseif
    }//onNewIntent

    //Author: Ralf Wondratschek
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter)
    {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try
        {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        }//try

        catch (IntentFilter.MalformedMimeTypeException e)
        {
            throw new RuntimeException("Check your mime type.");
        }//catch

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }//setupForegroundDispatch

    //Author: Ralf Wondratschek
    //Modified by: Erwin Alejo
    /**
     * @param activity The corresponding {@link //BaseActivity} requesting to stop the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter)
    {
        adapter.disableForegroundDispatch(activity);
    }//stopForegroundDispatch


    //This method handles the intent wanted by the tag
    private void processReadIntent(Intent intent)
    {
        String action = intent.getAction();

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
        {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type))
            {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);
                //Enables the "Phone" button when the card is currently
                //active and when the phone account is currently set to ""
                //after receiving a read intent
                /*if(AccountStorage.GetAccount(this).equals("")) {
                    frag.phoneButtonEnabler.setEnabled(true);
                }//if
                else {
                    //The phone has a payload with a device and valid byte of 0
                    if(AccountStorage.GetAccount(this).substring(8,9).equals("0") && AccountStorage.GetAccount(this).substring(9,10).equals("0")) {
                        frag.phoneButtonEnabler.setEnabled(true);
                    }//if
                }//else*/
                frag.phoneButtonEnabler.setEnabled(true);
            }//if
            else
            {
                showMessage(R.string.error, R.string.not_supported);
            }//else
        }//if
    }//processIntent


    /*
        Purpose: Shows a pop-up that tells the user
        to turn on NFC connectivity
     */
    private void showWirelessSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.nfc_disabled);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }//onClick
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }//onClick
        });
        builder.create().show();
        return;
    }//showWirelessSettingsDialog

    /*
        Purpose: Show a small pop-up message on the app
     */
    private void showMessage(int title, int message)
    {
        mDialog.setTitle(title);
        mDialog.setMessage(getText(message));
        mDialog.show();
    }//showMessage




    /**************************************NFC WRITING*******************************************************/


    //Author: NFC Tutorials on Youtube
    private void enableforegroundDispatchSystem()
    {
        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[] {};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }//enableforegroundDispatchSystem

    //Author: NFC Tutorials on Youtube
    private void disableforegroundDispatchSystem()
    {
        nfcAdapter.disableForegroundDispatch(this);
    }//disableforegroundDispatchSystem

    //Author: NFC Tutorials on Youtube
    private void formatTag(Tag tag, NdefMessage ndefMessage)
    {
        try
        {
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);
            if(ndefFormatable == null)
            {
                Toast.makeText(this, "Tag is not ndef formatable", Toast.LENGTH_SHORT).show();
                return;
            }//if

            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

           Toast.makeText(this, "Tag formatted!", Toast.LENGTH_SHORT).show();
        }//try
        catch(Exception e)
        {
            android.util.Log.e("formatTag", e.getMessage());
        }//catch
    }//foramtTag

    //Author: NFC Tutorials on Youtube
    //Modified by: Erwin Alejo
    private void writeNdefMessageToDeact(Tag tag, NdefMessage ndefMessage)
    {
        try
        {
            if(tag == null)
            {
                Toast.makeText(this,"Tag object cannot be null", Toast.LENGTH_SHORT).show();
                return;
            }//if

            Ndef ndef = Ndef.get(tag);

            if(ndef == null)
            {
                //format tag wth ndef format and writes the message
                formatTag(tag, ndefMessage);
            }//if
            else {
                ndef.connect();
                if(!ndef.isWritable())
                {
                    Toast.makeText(this, "Tag is not writable!", Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return;
                }//if
                ndef.writeNdefMessage(ndefMessage);
                ndef.close();
                Toast.makeText(this, "Activating phone...", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Phone Activated as E-Pass successfully!", Toast.LENGTH_SHORT).show();
            }//else
        }//try
        catch(Exception e)
        {
            android.util.Log.e("writeNdefMessageDeact", e.getMessage());
        }//catch
        clientUI.display("E-Pass successfully transferred to the phone.");
    }//writeNdefMessageToDeact

    //Author: NFC Tutorials on Youtube
    //Modified by: Erwin Alejo
    private void writeNdefMessageToAct(Tag tag, NdefMessage ndefMessage)
    {
        try
        {
            if(tag == null)
            {
                Toast.makeText(this,"Tag object cannot be null", Toast.LENGTH_SHORT).show();
                return;
            }//if

            Ndef ndef = Ndef.get(tag);

            if(ndef == null)
            {
                //format tag wth ndef format and writes the message
                formatTag(tag, ndefMessage);
            }//if
            else {
                ndef.connect();
                if(!ndef.isWritable())
                {
                    Toast.makeText(this, "Tag is not writable!", Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return;
                }//if
                ndef.writeNdefMessage(ndefMessage);
                ndef.close();
                Toast.makeText(this, "Activating card...", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Card activated as E-Pass successfully!", Toast.LENGTH_SHORT).show();
            }//else
        }//try
        catch(Exception e)
        {
            android.util.Log.e("writeNdefMessageAct", e.getMessage());
        }//catch
        clientUI.display("E-Pass successfully transferred to the card.");
    }//writeNdefMessageToAct

    //Author: NFC Tutorials on Youtube
    private NdefRecord createTextRecord(String content)
    {
        try
        {
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = content.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);

            payload.write((byte) (languageSize & 0x1F));
            payload.write(language, 0 , languageSize);
            payload.write(text,0,textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0],payload.toByteArray());
        }//try
        catch(Exception e)
        {
            android.util.Log.e("createTextRecord", e.getMessage());
        }//catch
        return null;
    }//NdefRecord

    //Author: NFC Tutorials on Youtube
    private NdefMessage createNdefMessage(String content)
    {
        NdefRecord ndefRecord = createTextRecord(content);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[] {ndefRecord});

        return ndefMessage;
    }//NdefMessage

    /*
        Author: Erwin Alejo
        Purpose: To convert hexadecimals
        to String
        Acknowledgement: Stack Overflow
     */
    public static  String hexToString(String hex)
    {

        int val = Integer.valueOf(hex, 16);
        String s = Integer.toString(val).toLowerCase();
        return s;
    }//hexToString

    /*
        Author: Michael Apuya
        Modified: Erwin Alejo
        Purpose: Encrypts and decrypts the payload
        with the key
        Acknowledgement Stack Overflow
     */
    public static String encrypt(String input, String key, int inputLen)
    {
        int keyLen = key.length();
        char[] a = new char[inputLen];
        StringBuilder strBuild = new StringBuilder();
        String string = "";

        for(int i = 0; i <inputLen; i++)
        {
            a[i] = (char)(input.charAt(i)^key.charAt(i%keyLen));
            strBuild.append(Character.toString(a[i]));
        }//for
        string = strBuild.toString();
        return string;
    }//encrypt




    /**
     * Background task for reading the data. Do not block the UI thread while reading.
     *
     * Author Ralf Wondratschek
     * Modified by: Erwin Alejo
     *
     */
    private class NdefReaderTask extends AsyncTask<Tag, Void, String>
    {
        @Override
        protected String doInBackground(Tag... params)
        {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null)
            {
                // NDEF is not supported by this Tag.
                return null;
            }//if

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();
            NdefRecord[] records = ndefMessage.getRecords();

            for (NdefRecord ndefRecord : records)
            {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT))
                {
                    try
                    {
                        return readText(ndefRecord);
                    }//try
                    catch (UnsupportedEncodingException e)
                    {
                        android.util.Log.e(TAG, "Unsupported Encoding", e);

                    }//catch
                }//if
            }//for

            return null;
        }//doInBackground

        /*
        * See NFC forum specification for "Text Record Type Definition" at 3.2.1
        *
        * http://www.nfc-forum.org/specs/
        *
        * bit_7 defines encoding
        * bit_6 reserved for future use, must be 0
        * bit_5..0 length of IANA language code
        */
        private String readText(NdefRecord record) throws UnsupportedEncodingException
        {

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }//readText

        @Override
        protected void onPostExecute(String result)
        {
            if (result != null)
            {
                myTextView.setText("Tag Contents: " + result);
                cardPayload = encrypt(result, key, result.length());
                cid = hexToString(cardPayload.substring(0,8));
                System.out.println("cid before padding: " + cid);
                System.out.println("cid before padding length: " + cid.length());
                //Pads cid with zeros if the length is < 10
                if(cid.length() < 10){
                    cid = (String.format("%010d",Integer.parseInt(cid)));
                    System.out.println("cid after padding: " +cid);
                }//if
                System.out.println(cardPayload);
            }//if
        }//onPostExecute


    }//NdefReaderTask
 }//MainActivity
