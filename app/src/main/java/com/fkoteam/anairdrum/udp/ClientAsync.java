package com.fkoteam.anairdrum.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

public class ClientAsync extends Thread{

    private final DatagramPacket packet;

    private AsyncTask<Void, Void, Void> async_cient;

    public ClientAsync(DatagramPacket newPacket){
        this.packet = newPacket;
    }



    @SuppressLint("NewApi")
    public void enviar()
    {
        async_cient = new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                DatagramSocket socket = null;

                try
                {
                    socket = new DatagramSocket();

                    //send socket
                    socket.send(packet);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if(socket != null){
                        socket.close();
                    }
                }
                return null;
            }

            /*protected void onPostExecute(Void result)
            {
                super.onPostExecute(result);
            }*/
        };

        if (Build.VERSION.SDK_INT >= 11) async_cient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else async_cient.execute();
    }


/*
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

        }
        catch(UnknownHostException e)
        {

        }
        catch(IOException e)
        {

        }
        catch(Exception e)
        {

        }
        finally{
            if(socket != null){
                socket.close();
            }
        }
    }*/


}