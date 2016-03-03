//package servermessage;
package com.example.android.cardemulation;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sevilljm
 * Modified by: Erwin Alejo
 */
public class ServerMessage extends Thread {

    //Declaration and Initialization of variables
    Thread serverMsg;
    com.example.android.cardemulation.AndroidGUI myUI;
    static String updatedBal;
    static String updatedTime;
    static String updatedDate;
    static float floatBal;
    static boolean isReady = true;
    volatile static boolean doNotCopyTime = true;
    volatile static boolean doNotCopyBal = true;
    volatile static boolean readyToGo = true;
    String message = "";
    String tempBal = "";
    String min = "", hour = "", seconds = "", expiryHour = "", expiryMins = "";
    int mins = 0, hours = 0;


    //Constructor
    public ServerMessage(com.example.android.cardemulation.AndroidGUI myUI){
        this.myUI = myUI;
    }

    /*
        Purpose: Processes the messages from the server
    */
    public  void process(com.example.android.cardemulation.Client myClient, byte msg){
        if (msg == -2){
            myClient.closeConnection();
            myUI.display("Disconnected from Server");
        }       //if
        else if (msg == -127){
            myUI.display("Failed to connect to the server. Check login info");
            myClient.disconnectFromServer();
        }//else if
        else if (msg == -128){
            if(message.equals(null))
            {
                //do nothing
            }//if

            //Check balance and check time stamp
            else {
                //Process the current balance sent by the database
                if (message.length() > 0 && message.length() <= 10) {
                    floatBal = Float.parseFloat(message);
                    tempBal = Integer.toHexString(Float.floatToIntBits(floatBal));
                    updatedBal = tempBal.toLowerCase();

                    if (message.contains(".5")) {
                        myUI.display("Balance: $" + message + "0");
                    }//if
                    else {
                        myUI.display("Balance: $" + message + ".00");
                    }//else

                    message = "";
                    doNotCopyBal = false;
                    isReady = false; // a lock to allow the client to get the server message before it disconnects from the server
                }//if

                //Process the timestamp from sent by the database
                else if (message.length() > 6) {

                    //This processes a date format that uses yyyy-mm-dd
                    if (message.contains("-")) {
                        System.out.println("Last time updated: " + message);

                        mins = (Integer.parseInt(message.substring(14, 16)) + 75) % 60;
                        if ((Integer.parseInt(message.substring(14, 16)) + 75) / 60 + Integer.parseInt(message.substring(11, 13)) == 24) {
                            hours = 0;
                            hour = "0" + Integer.toString(hours);
                        }//if
                        else if ((Integer.parseInt(message.substring(14, 16)) + 75) / 60 + Integer.parseInt(message.substring(11, 13)) == 25) {
                            hours = 1;
                            hour = "0" + Integer.toString(hours);
                        }//elseif
                        else if ((Integer.parseInt(message.substring(14, 16)) + 75) / 60 + Integer.parseInt(message.substring(11, 13)) == 26) {
                            hours = 2;
                            hour = "0" + Integer.toString(hours);
                        }//elseif
                        else {
                            hours = (Integer.parseInt(message.substring(14, 16)) + 75) / 60 + Integer.parseInt(message.substring(11, 13));
                            hour = Integer.toString(hours);
                        }//else
                        min = Integer.toString(mins);
                        seconds = message.substring(17, 19);

                        updatedDate = message.substring(0, 4) + message.substring(5, 7) + message.substring(8, 10);
                        updatedTime = message.substring(11, 13) + message.substring(14, 16) + message.substring(17, 19);
                    }//if

                    //This processes a date format that uses month (3 letters) dd yyyy
                    else {
                            System.out.println("Current date and time is: " + message);

                            //Convert the 24-hour format to 12-hour format
                            switch (Integer.parseInt(message.substring(12, 14))) {
                                case 13:
                                    expiryHour = "1";
                                    break;
                                case 14:
                                    expiryHour = "2";
                                    break;
                                case 15:
                                    expiryHour = "3";
                                    break;
                                case 16:
                                    expiryHour = "4";
                                    break;
                                case 17:
                                    expiryHour = "5";
                                    break;
                                case 18:
                                    expiryHour = "6";
                                    break;
                                case 19:
                                    expiryHour = "7";
                                    break;
                                case 20:
                                    expiryHour = "8";
                                    break;
                                case 21:
                                    expiryHour = "9";
                                    break;
                                case 22:
                                    expiryHour = "10";
                                    break;
                                case 23:
                                    expiryHour = "11";
                                    break;
                                default:
                                    break;
                            }//switch

                            if (Integer.parseInt(message.substring(12, 14)) < hours || (Integer.parseInt(message.substring(12, 14)) == hours && Integer.parseInt(message.substring(15, 17)) < mins)) {
                                expiryHour = Integer.toString(hours - Integer.parseInt(message.substring(12, 14)));
                                expiryMins = Integer.toString(mins - Integer.parseInt(message.substring(15, 17)));

                                if (Integer.parseInt(expiryMins) < 0) {
                                    expiryMins = Integer.toString(Integer.parseInt(expiryMins) + 60);
                                    myUI.display("Transfer valid until: " + hour + ":" + min + ":" + seconds + "\n" + "Expires in: " + expiryHour + " hours and " + expiryMins + " minutes");
                                }//if
                                else if(Integer.parseInt(expiryMins) > 0){
                                myUI.display("Transfer valid until: " + hour + ":" + min + ":" + seconds + "\n" + "Expires in: " + expiryHour + " hours and " + expiryMins + " minutes");
                                }
                            }//if
                            else {
                                expiryHour = "0";
                                expiryMins = "0";
                                myUI.display("Transfer is already expired.");
                            }//else
                        }//else
                        message = "";
                        doNotCopyTime = false;
                        isReady = false;
                    //else
                }//elseif
                else{
                    myUI.display(message);
                    message = "";
                    doNotCopyBal = false;
                    doNotCopyTime = false;
                    isReady = false;
                }//else
            }//else
        }//elseif
        else if(msg == 0x7E){
            message = "";
        }
        else {
            message += byteToString(msg);
        }
    }

    private String byteToString(byte theByte){
        byte[] theByteArray = new byte[1];
        String theString = null;
        theByteArray[0] = theByte;
        try {
            theString = new String(theByteArray, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            System.err.println("Cannot convert from UTF-8 to String; exiting program.");
            System.exit(1);
        }
        finally{
            return theString;
        }
    }

}
