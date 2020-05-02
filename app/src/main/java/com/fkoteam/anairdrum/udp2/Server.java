package com.fkoteam.anairdrum.udp2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.fkoteam.anairdrum.MainActivity;

public class Server extends Thread{

    private DatagramSocket server = null;
    private Context mContext;
    MediaPlayer izq1_1,izq1_2,izq1_3,izq2_1,izq2_2,izq2_3,der1_1,der1_2,der1_3,der2_1,der2_2,der2_3,pie_der1,pie_der2,pie_der3;


    public Server(Context applicationContext, int puerto) throws IOException {
        mContext=applicationContext;

        izq1_1 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("izq1", "raw", mContext.getPackageName()));
        izq1_2 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("izq1", "raw", mContext.getPackageName()));
        izq1_3 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("izq1", "raw", mContext.getPackageName()));
        der1_1 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("der1", "raw", mContext.getPackageName()));
        der1_2 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("der1", "raw", mContext.getPackageName()));
        der1_3 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("der1", "raw", mContext.getPackageName()));
        izq2_1 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("izq2", "raw", mContext.getPackageName()));
        izq2_2 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("izq2", "raw", mContext.getPackageName()));
        izq2_3 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("izq2", "raw", mContext.getPackageName()));
        der2_1 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("der2", "raw", mContext.getPackageName()));
        der2_2 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("der2", "raw", mContext.getPackageName()));
        der2_3 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("der2", "raw", mContext.getPackageName()));
        pie_der1 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("pie_der", "raw", mContext.getPackageName()));
        pie_der2 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("pie_der", "raw", mContext.getPackageName()));
        pie_der3 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("pie_der", "raw", mContext.getPackageName()));

        server = new DatagramSocket(puerto);
    }
    public void run(){

        //byte[] byte1024 = new byte[1024];
        byte[] byte1024 = "pie_der".getBytes();
        //Message msg = new Message();
        //Bundle data = new Bundle();
        DatagramPacket dPacket = new DatagramPacket(byte1024, byte1024.length);
        String recibido;
        try{
            Log.d("User","runing run()");
            while(true){
                server.receive(dPacket);
                while(true)
                {
                    recibido = new String(byte1024, 0, dPacket.getLength());
                    System.out.println("recibidooo:"+recibido);


                    if("pie_der".equals(recibido)) {

                        pie_der();

                    }
                    else if("izq1".equals(recibido)) {

                        izq1();

                    } else
                    if("izq2".equals(recibido)) {

                        izq2();

                    }
                    else
                    if("der1".equals(recibido)) {

                        der1();

                    }
                    else
                    if("der2".equals(recibido)) {

                        der2();

                    }

                    if(true) break;
                }
                //CloseSocket(client);
            }
        }
        catch(IOException e)
        {} finally{
            server.close();

        }
    }

    private void CloseSocket(DatagramSocket socket) throws IOException{
        socket.close();
    }




    private void pie_der(){

        if(pie_der1.isPlaying())
        {
            if(pie_der2.isPlaying())
                pie_der3.start();
            else
                pie_der2.start();
        }
        else
            pie_der1.start();


    }
    private void izq1(){

        if(izq1_1.isPlaying())
        {
            if(izq1_2.isPlaying())
                izq1_3.start();
            else
                izq1_2.start();
        }
        else
            izq1_1.start();


    }

    private void izq2(){

        if(izq2_1.isPlaying())
        {
            if(izq2_2.isPlaying())
                izq2_3.start();
            else
                izq2_2.start();
        }
        else
            izq2_1.start();


    }

    private void der1(){

        if(der1_1.isPlaying())
        {
            if(der1_2.isPlaying())
                der1_3.start();
            else
                der1_2.start();
        }
        else
            der1_1.start();


    }

    private void der2(){

        if(der2_1.isPlaying())
        {
            if(der2_2.isPlaying())
                der2_3.start();
            else
                der2_2.start();
        }
        else
            der2_1.start();


    }

}