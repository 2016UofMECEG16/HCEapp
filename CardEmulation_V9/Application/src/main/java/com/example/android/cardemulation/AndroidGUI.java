package com.example.android.cardemulation;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.*;
import java.*;

/**
 * Author: Miguel Seviila
 * Modified by: Erwin Alejo
 */
public class AndroidGUI implements Runnable {

    private Thread myGUIThread;
    com.example.android.cardemulation.UserCommand userCmd;
     com.example.android.cardemulation.MainActivity t;


    //com.example.erwin.androidclient_server.CmdToServer commandToServer;
    static com.example.android.cardemulation.MainActivity activity;

    public void setUserCmd(com.example.android.cardemulation.UserCommand userCmd, com.example.android.cardemulation.MainActivity activity){
        this.userCmd = userCmd;
        this.activity = activity;
    }


    public void run()
    {
        //new AndroidGUI().setVisible(true);
        while(true)
        {
            if (!t.usercmd.equals(""))
            {
                userCmd.process(t.usercmd);
                System.out.println(t.usercmd);
                t.usercmd = "";
            }
        }
    }

    public void start()
    {
        myGUIThread = new Thread(this, "myGUI");
        myGUIThread.start();
    }

    public static void display(String theResult) {
        activity.update(theResult);

    }

}
