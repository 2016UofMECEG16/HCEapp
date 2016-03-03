//package abstractclient;
package com.example.android.cardemulation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Author: Erwin Alejo
 * Modified by: Erwin Alejo
 */
public abstract class AbstractClient implements Runnable {
    
    Thread client;
    InputStream input;
    OutputStream output;
    Socket clientSocket;
    protected com.example.android.cardemulation.ServerMessage serverMsg;
    byte msg;
    String host = "192.168.43.20";
    int port = 5555;
    volatile boolean isConnected = false;
   
    public AbstractClient(com.example.android.cardemulation.ServerMessage serverMsg){
        this.serverMsg = serverMsg;
    }
    
    public void run(){
        while(isConnected()){
            msg = getMessageFromServer();
            handleMessageFromServer(msg);
        }
    }

    public void start(){
        client = new Thread(this, "ServerMessage");
        client.start();
    }
    
    public abstract void handleMessageFromServer(byte msg);
      
    public void connectToServer(){
        try{
            isConnected = true;
            clientSocket = new Socket(host, port);
            input = clientSocket.getInputStream();
            output = clientSocket.getOutputStream();
        }
        catch(IOException e){
            System.err.println("Cannot create Socket, exiting program.");
            System.exit(1);
        }
    }
    
    public void disconnectFromServer() {
        try {
            if (isConnected) {
                sendMessageToServer((byte) -1);
                isConnected = false;
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Cannot close Socket, exiting program.");
            System.exit(1);
        }
    }
    
    public void quit(){
        try{
            if (isConnected){
                sendMessageToServer((byte) -3);
                clientSocket.close();
            }
            System.exit(1);
        }
        catch(IOException e){
            System.err.println("Cannot close Socket, exiting program.");
            System.exit(1);
        }
    }
    
    public void sendMessageToServer(byte msg){
        try {
            if (isConnected){
                output.write(msg);
                output.flush();
            }
        } catch (IOException e) {
        }
    }
    
    public byte getMessageFromServer() {
        try {
            msg = (byte) input.read();
            return msg;
        } catch (IOException e) {
            return -1;           
        }
    }
    
    public boolean isConnected(){
        return isConnected;
    }    
    
    public void closeConnection(){
        if(clientSocket != null){
            try {
                isConnected = false;
                clientSocket.close();
            } catch (IOException ex) {
                System.err.println("Could not close socket.");
            }
        }
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public void setHost(String host)
    {
        this.host = host;
    }
    
    public void connectionClosed(){

    } 
    
    public void connectionEstablished(){
        
    }
    
    public void connectionException(){
        
    }
}
