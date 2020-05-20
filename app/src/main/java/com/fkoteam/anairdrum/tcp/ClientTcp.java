package com.fkoteam.anairdrum.tcp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import android.util.Log;

public class ClientTcp extends Thread{

    DataOutputStream out;


    String mensaje=null;
    Socket socket = null;
    String ip;
    int puerto;

    public synchronized void enviar(String packet)
    {
        this.mensaje=packet;
        notify();


    }
    public ClientTcp(String ip, int puerto)
    {
        this.ip=ip;
        this.puerto=puerto;
    }
    public void run(){


        try
        {

            socket = new Socket(InetAddress.getByName(ip), puerto);
            out = new DataOutputStream( socket.getOutputStream() );
          /*  solo para log de la respuesta
          BufferedReader in =
                    new BufferedReader( new InputStreamReader( socket.getInputStream() ) );*/
            socket.setTcpNoDelay(true);

            try {
                while (true) {
                    synchronized (this)
                    {

                        while (mensaje == null || socket==null) {
                            wait();
                        }

                        try {

                            out.writeBytes( mensaje + "\n" );
                            //System.out.println( "response from server : "+in.readLine() +System.currentTimeMillis());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mensaje=null;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();

            }




        }
        catch(SocketException e)
        {
            e.printStackTrace();
            /*String error = e.toString();
            Log.e("Error by Sender", error);*/
        }
        catch(UnknownHostException e)
        {
            e.printStackTrace();
            /*String error = e.toString();
            Log.e("Error by Sender", error);*/
        }
        catch(IOException e)
        {
            e.printStackTrace();
            /*String error = e.toString();
            Log.e("Error by Sender", error);*/
        }
        catch(Exception e)
        {
            e.printStackTrace();
            /*String error = e.toString();
            Log.e("Error by Sender", error);*/
        }
        finally{
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}