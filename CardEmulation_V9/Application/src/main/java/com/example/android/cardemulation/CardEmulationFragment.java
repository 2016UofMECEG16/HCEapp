/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.cardemulation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



/**
 * Generic UI for sample discovery.
 * API code
 * Modified by: Erwin Alejo
 */
public class CardEmulationFragment extends Fragment {

    //Declaration and Initialization of variables
    com.example.android.cardemulation.MainActivity activity;
    public static final String TAG = "CardEmulationFr";
    volatile static String phonePayload="";
    static String finalAmt = "";
    static boolean lockRead;
    static boolean lockWrite;
    static String updatedEncryptedPhonePayload = "";
    static Button phoneButtonEnabler;
    Button checkBal;
    Button timeStamp;
    Button updateFive;
    Button updateTen;
    Button updateTwentyFive;
    Button updateFifty;
    Button add;
    Button phone;
    Button card;
    int prevAdded = 0;
    int currAdded = 0;

    /*
        Author: Erwin Alejo
        Purpose: Create an instance of lockRead, lockWrite, and activity
     */
    public void setLock(boolean lockRead, boolean lockWrite, com.example.android.cardemulation.MainActivity activity)
    {
        this.lockRead = lockRead;
        this.lockWrite = lockWrite;
        this.activity = activity;
    }


    /** Called when sample is created. Displays generic UI with welcome text. */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }//onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.main_fragment, container, false);
        phoneButtonEnabler = (Button) v.findViewById(R.id.emulate_button);
        phone = (Button) v.findViewById(R.id.emulate_button);
        card = (Button) v.findViewById(R.id.write_back);
        checkBal = (Button) v.findViewById(R.id.check_bal);
        timeStamp = (Button) v.findViewById(R.id.time_stamp);
        updateFive = (Button) v.findViewById(R.id.add_balance);
        updateTen = (Button) v.findViewById(R.id.add_10);
        updateTwentyFive = (Button) v.findViewById(R.id.add_25);
        updateFifty = (Button) v.findViewById(R.id.add_50);
        add = (Button) v.findViewById(R.id.add_bal);

        //Checks if the account is not empty
        if(!AccountStorage.GetAccount(activity).equals("") && AccountStorage.GetAccount(activity).length() == 32) {
            if (activity.saveAccount.substring(8, 9).contains("1") && activity.saveAccount.substring(9, 10).contains("1")) {
                v.findViewById(R.id.emulate_button).setEnabled(false);
            }

            else if((activity.saveAccount.substring(8, 9).contains("0") && activity.saveAccount.substring(9, 10).contains("0"))) {
                v.findViewById(R.id.emulate_button).setEnabled(true);
            }
        }
        //Case when the card is active
        else{
            //The account is currently set to ""
            if(AccountStorage.GetAccount(activity).equals("")) {
                v.findViewById(R.id.emulate_button).setEnabled(false);
                v.findViewById(R.id.write_back).setEnabled(false);
                v.findViewById(R.id.check_bal).setEnabled(false);
                v.findViewById(R.id.time_stamp).setEnabled(false);
                v.findViewById(R.id.add_bal).setEnabled(false);
                v.findViewById(R.id.add_balance).setEnabled(false);
                v.findViewById(R.id.add_10).setEnabled(false);
                v.findViewById(R.id.add_25).setEnabled(false);
                v.findViewById(R.id.add_50).setEnabled(false);
            }
        }


        /*Button setip = (Button) v.findViewById(R.id.ip_button);
        setip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View t) {
                TextView iptxt = (TextView) activity.findViewById(R.id.IP_field);
                String ip = iptxt.getText().toString();
                //activity.addBalCommands(amountAdded);
                activity.usercmd = "sethost" + ip;
                activity.clientUI.display("IP set to: " + ip);
            }
        });*/

        phone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View t) {
                System.out.println("looking at the card payload read before init: " + activity.cardPayload);

                //Case when there is no payload read but the phone has an existing payload
                if (activity.cardPayload.equals("") && !activity.saveAccount.equals("")) {

                    //Checks if the CID has < 10 characters
                    if(activity.saveAccount.substring(0,8).length() < 10) {

                        String paddedCid;
                        paddedCid = String.format("%010d",Integer.parseInt(activity.hexToString(activity.saveAccount.substring(0,8))));
                        phonePayload = paddedCid + "1" + "1" + activity.saveAccount.substring(10, 18).toLowerCase() + activity.saveAccount.substring(18, activity.saveAccount.length());
                    }//if

                    else{
                        phonePayload = activity.saveAccount.substring(0,8) + "1" +  "1" + activity.saveAccount.substring(10,18).toLowerCase() + activity.saveAccount.substring(18,activity.saveAccount.length());
                    }//else
                }//if

                //Case when there is a payload read
                else if (!activity.cardPayload.equals("")) {
                    phonePayload = activity.cardPayload.substring(0, 8) + "1" + "1" + activity.cardPayload.substring(10, 18).toLowerCase() + activity.cardPayload.substring(18, activity.cardPayload.length());
                }//elseif

                System.out.println("looking at the phone payload after init: " + phonePayload);
                //Encrypt the payload before setting the account
                updatedEncryptedPhonePayload = activity.encrypt(phonePayload, activity.key, phonePayload.length());
                AccountStorage.SetAccount(getActivity(), updatedEncryptedPhonePayload);
                //Enable write intent
                lockWrite = true;
                lockRead = false;
                activity.clientUI.display(phonePayload);
                Toast.makeText(activity, "Tap E-Pass card to activate phone.", Toast.LENGTH_SHORT).show();

                //Enable buttons
                activity.findViewById(R.id.write_back).setEnabled(true);
                activity.findViewById(R.id.check_bal).setEnabled(true);
                activity.findViewById(R.id.time_stamp).setEnabled(true);
                activity.findViewById(R.id.add_bal).setEnabled(true);
                activity.findViewById(R.id.add_balance).setEnabled(true);
                activity.findViewById(R.id.add_10).setEnabled(true);
                activity.findViewById(R.id.add_25).setEnabled(true);
                activity.findViewById(R.id.add_50).setEnabled(true);
                activity.findViewById(R.id.emulate_button).setEnabled(false);

                //Execute the command to activate phone as E-Pass
                if (lockWrite && !lockRead) {
                    System.out.println("Updating DB");
                    activity.emulateCommands();
                }//if
            }

        });

        card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View t) {
                System.out.println("payload of the phone prior write intent: " + phonePayload);
                //Execute commands to activate card as E-Pass
                activity.writebackCommands();

                //Case when there is no payload read but the phone has an existing payload
                if(activity.cardPayload.equals("") && !activity.saveAccount.equals("")){

                    if(activity.saveAccount.substring(0,8).length() < 10) {
                        String paddedCid = String.format("%010d",Integer.parseInt(activity.hexToString(activity.saveAccount.substring(0,8))));
                        phonePayload = paddedCid + "0" + "0" + activity.saveAccount.substring(10, 18).toLowerCase() + activity.saveAccount.substring(18, activity.saveAccount.length());
                    }//if

                    else{
                    phonePayload = activity.saveAccount.substring(0,8) + "0" +  "0" + activity.saveAccount.substring(10,18).toLowerCase() + activity.saveAccount.substring(18,activity.saveAccount.length());
                    }//else
                }//if

                else if(!activity.cardPayload.equals("")){
                    phonePayload = activity.cardPayload.substring(0, 8) + "0" + "0" + activity.cardPayload.substring(10, 18).toLowerCase() + activity.cardPayload.substring(18, activity.cardPayload.length());
                }//else if

                updatedEncryptedPhonePayload = activity.encrypt(phonePayload, activity.key,phonePayload.length());
                AccountStorage.SetAccount(getActivity(), updatedEncryptedPhonePayload);

                lockWrite = true;
                lockRead = false;

                System.out.println("payload of the phone after enabling write intent: " + phonePayload);
                Toast.makeText(activity, "Tap E-Pass card to activate card.", Toast.LENGTH_SHORT).show();

                activity.findViewById(R.id.write_back).setEnabled(false);
                activity.findViewById(R.id.add_bal).setEnabled(false);
                activity.findViewById(R.id.add_balance).setEnabled(false);
                activity.findViewById(R.id.add_10).setEnabled(false);
                activity.findViewById(R.id.add_25).setEnabled(false);
                activity.findViewById(R.id.add_50).setEnabled(false);
            }

        });

        //Execute command for balance checking once "Check" button is pressed
        checkBal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View t) {
                activity.checkBalCommands();
            }
        });

        //Execute command for transfer time information checking once "Time"
        // button is pressed
        timeStamp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View t) {
                activity.timeStampCommands();
            }
        });

        //Decrements the user-specified amount by 5 on the GUI textbox
        updateFive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View t) {
                prevAdded = -5;
                currAdded  += prevAdded;

                //Checks if the added value is negative
                if(currAdded <= 0)
                {
                    currAdded = 0;
                    activity.clientUI.display("Amount to be added: $0.00");
                }//if
                else
                {
                    activity.clientUI.display("Amount to be added: $" + currAdded + ".00\n" + "Press 'ADD' to add the desired amount." );
                }//else
            }
        });

        //Decrements the user-specified amount by 20 on the GUI textbox
        updateTen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View t) {
                prevAdded = -20;
                currAdded += prevAdded;
                if(currAdded <= 0)
                {
                    currAdded = 0;
                    activity.clientUI.display("Amount to be added: $0.00");
                }//if
                else
                {
                    activity.clientUI.display("Amount to be added: $" + currAdded + ".00\n" + "Press 'ADD' to add the desired amount." );
                }//else
            }
        });

        //Increments the user-specified amount by 5 on the GUI textbox
        updateTwentyFive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View t) {
                prevAdded = 5;
                currAdded += prevAdded;
                if(currAdded <= 0)
                {
                    currAdded = 0;
                    activity.clientUI.display("Amount to be added: $0.00");
                }//if
                else
                {
                    activity.clientUI.display("Amount to be added: $" + currAdded + ".00\n" + "Press 'ADD' to add the desired amount." );
                }//else
            }
        });

        //Increments the user-specified amount by 20 on the GUI textbox
        updateFifty.setOnClickListener(new View.OnClickListener() {
            public void onClick(View t) {
                prevAdded = 20;
                currAdded += prevAdded;
                if(currAdded <= 0)
                {
                    currAdded = 0;
                    activity.clientUI.display("Amount to be added: $0.00");
                }//if
                else
                {
                    activity.clientUI.display("Amount to be added: $" + currAdded + ".00\n" + "Press 'ADD' to add the desired amount." );
                }//else
            }
        });

        //Executes the command for adding balance to the database once "Add"
        //button is pressed
        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View t) {
                finalAmt = Integer.toString(currAdded);
                activity.addBalCommands(finalAmt);
            }
        });
        return v;
    }//onCreateView
}//CardEmulation Fragment
