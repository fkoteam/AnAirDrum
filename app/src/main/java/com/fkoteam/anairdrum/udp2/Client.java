package com.fkoteam.anairdrum.udp2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import android.util.Log;

public class Client extends Thread{

    private final DatagramPacket packet;

    public Client(DatagramPacket packet)
    {

        this.packet=packet;
    }
    public void run(){
        DatagramSocket socket = null;
        try
        {
            socket = new DatagramSocket();

            //send socket
            socket.send(packet);

        }
        catch(SocketException e)
        {
            e.printStackTrace();
            String error = e.toString();
            Log.e("Error by Sender", error);
        }
        catch(UnknownHostException e)
        {
            e.printStackTrace();
            String error = e.toString();
            Log.e("Error by Sender", error);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            String error = e.toString();
            Log.e("Error by Sender", error);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            String error = e.toString();
            Log.e("Error by Sender", error);
        }
        finally{
            if(socket != null){
                socket.close();
            }
        }
    }


}