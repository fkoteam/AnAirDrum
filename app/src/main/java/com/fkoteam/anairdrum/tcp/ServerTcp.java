package com.fkoteam.anairdrum.tcp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.fkoteam.anairdrum.MainActivity;
import com.fkoteam.anairdrum.MediaPlayers;

public class ServerTcp extends Thread {
    ServerSocket serverSocket;
    private int port;
    ObjectInputStream fromClient = null;
    private OutputStream outputStream;
    private OutputStreamWriter pp;
    private DataInputStream dataInputStream;
    private String messageFromClient;

    public ServerTcp(int port) {
        this.port=port;


    }


    public void run() {
        String recibido;

        try {
            serverSocket = new ServerSocket(port);


            DataOutputStream out;

            Socket socket = serverSocket.accept();

            BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
             out = new DataOutputStream( socket.getOutputStream() );
            while (true) {
                recibido=in.readLine();
                /*System.out.println( "esto es recibido: "+  recibido );
                if(!"99".equals(recibido) && !"192.168.1.1".equals(getLocalIpAddress())) {
                    out.writeBytes("99\n");
                    out.flush();
                }*/


                    if ("6".equals(recibido)) {
                        MainActivity.mediaplayers.pie_der();

                    } else if ("4".equals(recibido)) {

                        MainActivity.mediaplayers.izq1();

                    } else if ("5".equals(recibido)) {

                        MainActivity.mediaplayers.izq2();

                    } else if ("1".equals(recibido)) {

                        MainActivity.mediaplayers.der1_cl();

                    } else if ("2".equals(recibido)) {

                        MainActivity.mediaplayers.der1_op();


                    } else if ("3".equals(recibido)) {

                        MainActivity.mediaplayers.der2();

                    } else if ("7".equals(recibido)) {
                        MainActivity.mediaplayers.setPie_izq_pulsado(true);
                        //pie_izq_pulsado


                    } else if ("8".equals(recibido)) {
                        MainActivity.mediaplayers.setPie_izq_pulsado(false);
                        MainActivity.mediaplayers.pie_izq();
                        //pie_izq_no_pulsado

                    } else if ("9".equals(recibido)) {
                        MainActivity.mediaplayers.der3();
                        //pie_izq_no_pulsado

                    } else if ("0".equals(recibido)) {
                        MainActivity.mediaplayers.izq3();
                        //pie_izq_no_pulsado

                    }



            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

    }


    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return null;
    }
}