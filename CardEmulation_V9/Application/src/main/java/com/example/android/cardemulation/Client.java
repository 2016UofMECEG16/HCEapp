//package client;
package com.example.android.cardemulation;

import java.io.*;
import java.net.*;


/**
 *
 * Author: Miguel Sevilla
 */
public class Client extends com.example.android.cardemulation.AbstractClient{
    
    public Client(com.example.android.cardemulation.ServerMessage serverMsg){
        super(serverMsg);
    }

    @Override
    public void handleMessageFromServer(byte msg) {
        serverMsg.process((Client) this,msg);
    }
        
}
