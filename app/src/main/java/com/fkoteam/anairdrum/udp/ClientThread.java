package com.fkoteam.anairdrum.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ClientThread extends Thread{

    private DatagramPacket packet;
    private DatagramSocket socket;

    public synchronized void enviar(DatagramPacket packet)
    {
        this.packet=packet;
        notify();


    }
    public void run(){
        try {
            socket=  new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
                while (true) {
                    //putMessage();
                    synchronized (this)
                    {
                        while (packet == null || socket==null) {
                            wait();
                        }

                        try {
                            socket.send(packet);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        packet=null;
                    }
                }
            } catch (InterruptedException e) {
            }
        }
/*
        private synchronized void putMessage() throws InterruptedException {
            while (packet == null || socket==null) {
                System.out.println("antes packet null");

                wait();
                System.out.println("despues packet null");
            }
            System.out.println("salgo");

            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("enviado");

            packet=null;
            //Later, when the necessary event happens, the thread that is running it calls notify() from a block synchronized on the same object.
        }
*/
        // Called by Consumer

/*
        try
        {
            socket = new DatagramSocket();

            while(true)
            {
                if(enviar)
                {
                    socket.send(packet);
                    enviar=false;
                }
                if (Thread.interrupted())
                    return;
            }
            //send socket

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
        }*/



}