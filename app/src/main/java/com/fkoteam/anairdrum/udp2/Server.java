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
import com.fkoteam.anairdrum.MediaPlayers;

public class Server extends Thread {

    private DatagramSocket server;
    private Context mContext;
    //public boolean  pie_izq_pulsado = false;
    private final MediaPlayers mediaPlayers;
   // MediaPlayer izq1_1, izq1_2, izq1_3, izq2_1, izq2_2, izq2_3, der1op_1, der1op_2, der1op_3, der1cl_1, der1cl_2, der1cl_3, der2_1, der2_2, der2_3, pie_der1, pie_der2, pie_der3, pie_izq_cerr1, pie_izq_cerr2, pie_izq_cerr3;


    public Server(Context applicationContext, int puerto,MediaPlayers mp) throws IOException {
        mediaPlayers=mp;
        /*mContext = applicationContext;

        izq1_1 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("izq1", "raw", mContext.getPackageName()));
        izq1_2 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("izq1", "raw", mContext.getPackageName()));
        izq1_3 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("izq1", "raw", mContext.getPackageName()));
        der1op_1 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("der1op", "raw", mContext.getPackageName()));
        der1op_2 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("der1op", "raw", mContext.getPackageName()));
        der1op_3 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("der1op", "raw", mContext.getPackageName()));
        der1cl_1 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("der1cl", "raw", mContext.getPackageName()));
        der1cl_2 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("der1cl", "raw", mContext.getPackageName()));
        der1cl_3 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("der1cl", "raw", mContext.getPackageName()));
        izq2_1 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("izq2", "raw", mContext.getPackageName()));
        izq2_2 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("izq2", "raw", mContext.getPackageName()));
        izq2_3 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("izq2", "raw", mContext.getPackageName()));
        der2_1 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("der2", "raw", mContext.getPackageName()));
        der2_2 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("der2", "raw", mContext.getPackageName()));
        der2_3 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("der2", "raw", mContext.getPackageName()));
        pie_der1 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("pie_der", "raw", mContext.getPackageName()));
        pie_der2 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("pie_der", "raw", mContext.getPackageName()));
        pie_der3 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("pie_der", "raw", mContext.getPackageName()));
        pie_izq_cerr1 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("pie_izq_cer", "raw", mContext.getPackageName()));
        pie_izq_cerr2 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("pie_izq_cer", "raw", mContext.getPackageName()));
        pie_izq_cerr3 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("pie_izq_cer", "raw", mContext.getPackageName()));
*/
        server = new DatagramSocket(puerto);
    }

    public void run() {

        //byte[] byte1024 = new byte[1024];
        byte[] byte1024 = "1".getBytes();
        //Message msg = new Message();
        //Bundle data = new Bundle();
        DatagramPacket dPacket = new DatagramPacket(byte1024, byte1024.length);
        String recibido;
        try {
            //Log.d("User","runing run()");
            while (true) {
                server.receive(dPacket);
                while (true) {
                    recibido = new String(byte1024, 0, dPacket.getLength());
                    // System.out.println("recibidooo:"+recibido);


                    if ("6".equals(recibido)) {

                        mediaPlayers.pie_der();

                    } else if ("4".equals(recibido)) {

                        mediaPlayers.izq1();

                    } else if ("5".equals(recibido)) {

                        mediaPlayers.izq2();

                    } else if ("1".equals(recibido)) {
//TODO close
                        mediaPlayers.der1_cl();

                    } else if ("2".equals(recibido)) {
//TODO open
                        mediaPlayers.der1_op();


                    } else if ("3".equals(recibido)) {

                        mediaPlayers.der2();

                    } else if ("7".equals(recibido)) {
                        mediaPlayers.setPie_izq_pulsado(true);
                        //pie_izq_pulsado


                    } else if ("8".equals(recibido)) {
                        mediaPlayers.setPie_izq_pulsado(false);
                        mediaPlayers.pie_izq();
                        //pie_izq_no_pulsado

                    }

                    if (true) break;
                }
                //CloseSocket(client);
            }
        } catch (IOException e) {
        } finally {
            server.close();

        }
    }

    private void CloseSocket(DatagramSocket socket) throws IOException {
        socket.close();
    }

/*
    private void pie_der() {

        if (pie_der1.isPlaying()) {
            if (pie_der2.isPlaying())
                pie_der3.start();
            else
                pie_der2.start();
        } else
            pie_der1.start();


    }

    private void izq1() {

        if (izq1_1.isPlaying()) {
            if (izq1_2.isPlaying())
                izq1_3.start();
            else
                izq1_2.start();
        } else
            izq1_1.start();


    }

    private void izq2() {

        if (izq2_1.isPlaying()) {
            if (izq2_2.isPlaying())
                izq2_3.start();
            else
                izq2_2.start();
        } else
            izq2_1.start();


    }

    private void pie_izq() {
        if(der1op_1.isPlaying())
            der1op_1.stop();
        if(der1op_2.isPlaying())
            der1op_2.stop();
        if(der1op_2.isPlaying())
            der1op_2.stop();
        if (pie_izq_cerr1.isPlaying()) {
            if (pie_izq_cerr2.isPlaying())
                pie_izq_cerr3.start();
            else
                pie_izq_cerr2.start();
        } else
            pie_izq_cerr1.start();

    }


    private void der1_op() {
        if (pie_izq_pulsado) {
            if (der1op_1.isPlaying()) {
                if (der1op_2.isPlaying())
                    der1op_3.start();
                else
                    der1op_2.start();
            } else
                der1op_1.start();
        }
        else
        {
            if (der1cl_1.isPlaying()) {
                if (der1cl_2.isPlaying())
                    der1cl_3.start();
                else
                    der1cl_2.start();
            } else
                der1cl_1.start();
        }

    }

    private void der1_cl() {
        if (!pie_izq_pulsado){

            if (der1cl_1.isPlaying()) {
                if (der1cl_2.isPlaying())
                    der1cl_3.start();
                else
                    der1cl_2.start();
            } else
                der1cl_1.start();
    }
        else
        {
            if (der1op_1.isPlaying()) {
                if (der1op_2.isPlaying())
                    der1op_3.start();
                else
                    der1op_2.start();
            } else
                der1op_1.start();
        }

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


    }*/

}