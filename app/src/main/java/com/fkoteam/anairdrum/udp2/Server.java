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
    private final MediaPlayers mediaPlayers;


    public Server(int puerto,MediaPlayers mp) throws IOException {
        mediaPlayers=mp;

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

                        mediaPlayers.der1_cl();

                    } else if ("2".equals(recibido)) {

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

                    else if ("9".equals(recibido)) {
                        mediaPlayers.der3();
                        //pie_izq_no_pulsado

                    }
                    else if ("0".equals(recibido)) {
                        mediaPlayers.izq3();
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


}