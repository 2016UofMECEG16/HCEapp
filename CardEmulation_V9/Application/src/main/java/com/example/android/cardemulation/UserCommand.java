 //package usercommand;
package com.example.android.cardemulation;

import java.util.concurrent.TimeUnit;

/**
 *
 * Author: Miguel Sevilla
 * Modified by: Erwin Alejo
 */
public class UserCommand {
    
    com.example.android.cardemulation.Client myClient;
    com.example.android.cardemulation.AndroidGUI myUI;
    com.example.android.cardemulation.ServerMessage serverMsg;
    com.example.android.cardemulation.MainActivity activity;
    int i = 0;
    volatile static boolean isOkToDisc = true;
    volatile static boolean doNotCopyTime = true;
    volatile static boolean doNotCopyBal = true;

    //Constructor
    public UserCommand(com.example.android.cardemulation.Client myClient, com.example.android.cardemulation.AndroidGUI myUI, com.example.android.cardemulation.ServerMessage serverMsg,com.example.android.cardemulation.MainActivity activity){
        this.myClient = myClient;
        this.myUI = myUI;
        this.serverMsg = serverMsg;
        this.activity = activity;
    }//UserCommand

    /*
        Purpose: Processes the commands sent by the client
    */
    public void process(String line){
        //String[] command = line.split("\t\n ");
        //String cmd = command[0];
        if(line.length() != 0){
            switch (line.charAt(0)){
                case '1':
                    myClient.quit();
                    System.exit(-1);
                    break;
                case '2': //For emulate and writeback commands


                    //Connect to server
                    if (!myClient.isConnected()) {
                        myClient.connectToServer();
                        myClient.start();
                    }
                    else{
                        myUI.display("Already connected to server.");
                    }
                    for(int i = 2; i < line.length() - (5 + activity.cid.length()); i++){
                        myClient.sendMessageToServer((byte) line.charAt(i));
                    }
                    myClient.sendMessageToServer((byte) -128);



                    //Update dev bit command
                    if (myClient.isConnected()) {
                        for (int i = 26; i < line.length(); i++) {
                            myClient.sendMessageToServer((byte) line.charAt(i));
                        }
                        myClient.sendMessageToServer((byte) -128);
                    }
                    else{
                        myUI.display("Not connected to a server.");
                    }
                    //Disconnect from server
                    if (myClient.isConnected()){
                        myClient.disconnectFromServer();
                    }
                    else{
                        myUI.display("Not connected to a server.");
                    }
                    break;
                case '3': //For check balance command

                    //Connect to server
                    if (!myClient.isConnected()) {
                        myClient.connectToServer();
                        myClient.start();
                    }
                    else{
                        myUI.display("Already connected to server.");
                    }
                    for(int i = 2; i < line.length() - (3 + activity.cid.length()); i++){
                        myClient.sendMessageToServer((byte) line.charAt(i));
                    }
                    myClient.sendMessageToServer((byte) -128);

                    //Check bal
                    if (myClient.isConnected()) {
                        for (int i = 26; i < line.length(); i++) {
                            myClient.sendMessageToServer((byte) line.charAt(i));
                        }
                        myClient.sendMessageToServer((byte) -128);
                    }
                    else{
                        myUI.display("Not connected to a server.");
                    }

                    doNotCopyBal = false;
                    while(activity.serverMsg.isReady);
                    activity.serverMsg.isReady = true;
                    if (myClient.isConnected()){
                        myClient.disconnectFromServer();
                    } else {
                        myUI.display("Not connected to a server.");
                    }
                    break;

                case '4':
                    //Connect to server
                    if (!myClient.isConnected()) {
                        myClient.connectToServer();
                        myClient.start();
                    }
                    else{
                        myUI.display("Already connected to server.");
                    }
                    for(int i = 2; i < line.length() - (4 + activity.cid.length() + activity.frag.finalAmt.length())  ; i++){
                        myClient.sendMessageToServer((byte) line.charAt(i));
                    }
                    myClient.sendMessageToServer((byte) -128);

                    //Add bal
                    if (myClient.isConnected()) {
                        for (int i = 26; i < line.length(); i++) {
                            myClient.sendMessageToServer((byte) line.charAt(i));
                        }
                        myClient.sendMessageToServer((byte) -128);
                    }
                    else{
                        myUI.display("Not connected to a server.");
                    }
                    //Disconnect from server
                    if (myClient.isConnected()){
                        myClient.disconnectFromServer();
                    } else {
                        myUI.display("Not connected to a server.");
                    }
                    break;

                case '5':
                    //Connect to server
                    if (!myClient.isConnected()) {
                        myClient.connectToServer();
                        myClient.start();
                    }
                    else{
                        myUI.display("Already connected to server.");
                    }
                    for(int i = 2; i < line.length() - (6 + activity.cid.length()); i++){
                        myClient.sendMessageToServer((byte) line.charAt(i));
                    }
                    myClient.sendMessageToServer((byte) -128);

                    //Time Stamp
                    if (myClient.isConnected()) {
                        for (int i = 26; i < line.length() - 2; i++) {
                            myClient.sendMessageToServer((byte) line.charAt(i));
                        }
                        myClient.sendMessageToServer((byte) -128);
                    }
                    else{
                        myUI.display("Not connected to a server.");
                    }

                    while(activity.serverMsg.isReady);
                    activity.serverMsg.isReady = true;

                    if (myClient.isConnected()) {
                        for (int i = 40; i < line.length(); i++) {
                            myClient.sendMessageToServer((byte) line.charAt(i));
                        }
                        myClient.sendMessageToServer((byte) -128);
                    }
                    else{
                        myUI.display("Not connected to a server.");
                    }


                    while(activity.serverMsg.isReady);
                    activity.serverMsg.isReady = true;
                    if (myClient.isConnected()){
                        myClient.disconnectFromServer();
                    } else {
                        myUI.display("Not connected to a server.");
                    }
                    serverMsg.readyToGo = false;

                break;
//
                case '6':
                    //Connect to server
                    if (!myClient.isConnected()) {
                        myClient.connectToServer();
                        myClient.start();
                    }
                    else{
                        myUI.display("Already connected to server.");
                    }
                    for(int i = 2; i < line.length() - (14 + (activity.cid.length()) * 3); i++){
                        myClient.sendMessageToServer((byte) line.charAt(i));
                    }
                    myClient.sendMessageToServer((byte) -128);


                    //Update dev bit command
                    if (myClient.isConnected()) {
                        for (int i = 26; i < line.length() - (33 + (activity.cid.length() * 2)); i++) {
                            myClient.sendMessageToServer((byte) line.charAt(i));
                        }
                        myClient.sendMessageToServer((byte) -128);
                    }
                    else{
                        myUI.display("Not connected to a server.");
                    }

                    //Check bal
                    if (myClient.isConnected()) {
                        for (int i = 40; i < line.length() - (30 + activity.cid.length()); i++) {
                            myClient.sendMessageToServer((byte) line.charAt(i));
                        }
                        myClient.sendMessageToServer((byte) -128);
                    }
                    else{
                        myUI.display("Not connected to a server.");
                    }

                    while(activity.serverMsg.isReady);
                    activity.serverMsg.isReady = true;
                    if (myClient.isConnected()){
                        myClient.disconnectFromServer();
                    } else {
                        myUI.display("Not connected to a server.");
                    }

                    if (!myClient.isConnected()) {
                        myClient.connectToServer();
                        myClient.start();
                    }
                    else{
                        myUI.display("Already connected to server.");
                    }
                    for(int i = 54; i < line.length() - (4 + activity.cid.length()); i++){
                        myClient.sendMessageToServer((byte) line.charAt(i));
                    }
                    myClient.sendMessageToServer((byte) -128);


                    //Time Stamp
                    if (myClient.isConnected()) {
                        for (int i = 78; i < line.length(); i++) {
                            myClient.sendMessageToServer((byte) line.charAt(i));
                        }
                        myClient.sendMessageToServer((byte) -128);

                    }
                    else{
                        myUI.display("Not connected to a server.");
                    }


                    while(isOkToDisc);
                    isOkToDisc = true;
                    if (myClient.isConnected()){
                        myClient.disconnectFromServer();
                    } else {
                        myUI.display("Not connected to a server.");
                    }
                    break;

                case '7':

                    //Connect to server
                    if (!myClient.isConnected()) {
                        myClient.connectToServer();
                        myClient.start();
                    }
                    else{
                        myUI.display("Already connected to server.");
                    }
                    for(int i = 2; i < line.length() - (9 + (activity.cid.length()) * 3); i++){
                        myClient.sendMessageToServer((byte) line.charAt(i));
                    }
                    myClient.sendMessageToServer((byte) -128);


                    //Update dev bit command
                    if (myClient.isConnected()) {
                        for (int i = 25; i < line.length() - (4 + (activity.cid.length() * 2)); i++) {
                            myClient.sendMessageToServer((byte) line.charAt(i));
                        }
                        myClient.sendMessageToServer((byte) -128);
                    }
                    else{
                        myUI.display("Not connected to a server.");
                    }

                    //Time Stamp
                    if (myClient.isConnected()) {
                        for (int i = 39; i < line.length() - (2 + activity.cid.length()); i++) {
                            myClient.sendMessageToServer((byte) line.charAt(i));
                        }
                        myClient.sendMessageToServer((byte) -128);
                    }
                    else{
                        myUI.display("Not connected to a server.");
                    }

                    while(activity.serverMsg.isReady);
                    activity.serverMsg.isReady = true;



                    //Check bal
                    if (myClient.isConnected()) {
                        for (int i = 52; i < line.length() ; i++) {
                            myClient.sendMessageToServer((byte) line.charAt(i));
                        }
                        myClient.sendMessageToServer((byte) -128);

                    }
                    else{
                        myUI.display("Not connected to a server.");
                    }

                    isOkToDisc = false;
                    while(activity.serverMsg.isReady);
                    activity.serverMsg.isReady = true;

                    if (myClient.isConnected()){
                        myClient.disconnectFromServer();
                    } else {
                        myUI.display("Not connected to a server.");
                    }
                    break;

                case '8':

                    //Connect to server
                    if (!myClient.isConnected()) {
                        myClient.connectToServer();
                        myClient.start();
                    }
                    else{
                        myUI.display("Already connected to server.");
                    }
                    for(int i = 2; i < line.length() - (7 + activity.cid.length() * 2); i++){
                        myClient.sendMessageToServer((byte) line.charAt(i));
                    }
                    myClient.sendMessageToServer((byte) -128);

                    //Time Stamp
                    if (myClient.isConnected()) {
                        for (int i = 26; i < line.length() - (3 + activity.cid.length()); i++) {
                            myClient.sendMessageToServer((byte) line.charAt(i));
                        }
                        myClient.sendMessageToServer((byte) -128);
                    }
                    else{
                        myUI.display("Not connected to a server.");
                    }

                    while(activity.serverMsg.isReady);
                    activity.serverMsg.isReady = true;

                    //Check bal
                    if (myClient.isConnected()) {
                        for (int i = 39; i < line.length(); i++) {
                            myClient.sendMessageToServer((byte) line.charAt(i));
                        }
                        myClient.sendMessageToServer((byte) -128);

                    }
                    else{
                        myUI.display("Not connected to a server.");
                    }

                    while(activity.serverMsg.isReady);
                    activity.serverMsg.isReady = true;

                    if (myClient.isConnected()){
                        myClient.disconnectFromServer();
                    } else {
                        myUI.display("Not connected to a server.");
                    }
                    break;

                case '9':
                    //Connect to server
                    if (!myClient.isConnected()) {
                        myClient.connectToServer();
                        myClient.start();
                    }
                    else{
                        myUI.display("Already connected to server.");
                    }
                    for(int i = 2; i < line.length() - (12 + (activity.cid.length()) * 3); i++){
                        myClient.sendMessageToServer((byte) line.charAt(i));
                    }
                    myClient.sendMessageToServer((byte) -128);




                    //Time Stamp
                    if (myClient.isConnected()) {
                        for (int i = 26; i < line.length() - (9 + activity.cid.length() * 2); i++) {
                            myClient.sendMessageToServer((byte) line.charAt(i));
                        }
                        myClient.sendMessageToServer((byte) -128);
                    }
                    else{
                        myUI.display("Not connected to a server.");
                    }


                    //Check bal
                    if (myClient.isConnected()) {
                        for (int i = 38; i < line.length() - (5 + activity.cid.length()); i++) {
                            myClient.sendMessageToServer((byte) line.charAt(i));
                        }
                        myClient.sendMessageToServer((byte) -128);

                    }
                    else{
                        myUI.display("Not connected to a server.");
                    }

                    //Update dev bit command
                    if (myClient.isConnected()) {
                        for (int i = 52; i < line.length(); i++) {
                            myClient.sendMessageToServer((byte) line.charAt(i));
                        }
                        myClient.sendMessageToServer((byte) -128);
                    }
                    else{
                        myUI.display("Not connected to a server.");
                    }

                    if (myClient.isConnected()){
                        myClient.disconnectFromServer();
                    }
                    else{
                        myUI.display("Not connected to a server.");
                    }

                    break;


                default:
                    break;
            }
            if(line.contains("sethost"))
            {
                System.out.println("It is setting ip address here");
                String newHost = line.substring(7, line.length());
                myClient.setHost(newHost);
            }
            if(line.contains("setport"))
            {
                System.out.println("It is setting port here");
                //int newPort = Integer.parseInt(line.substring(7, line.length()));
            }
        }
    }
    
}
