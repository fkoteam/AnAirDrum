package com.fkoteam.anairdrum.udp;

import com.fkoteam.anairdrum.MainActivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ServerUdp extends Thread {

    private DatagramSocket server;
    /*private  MediaPlayers mediaPlayers;

public void setMediaPlayer(MediaPlayers mp)
{
    mediaPlayers=mp;

}*/
    public void changePort(int puerto) throws IOException
    {
        server = new DatagramSocket(puerto);


    }
    public ServerUdp(int puerto/*,MediaPlayers mp*/) throws IOException {
       // mediaPlayers=mp;

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
            DatagramPacket sendPacket = new DatagramPacket("99".getBytes(),("99").length(), InetAddress.getByName("192.168.1.1"), 7001);

            //Log.d("User","runing run()");
            while (true) {
                server.receive(dPacket);
                while (true) {
                    recibido = new String(byte1024, 0, dPacket.getLength());

/*if(!"99".equals(recibido) && !"192.168.1.1".equals(getLocalIpAddress()))
{
    server.send(sendPacket);
}
else
{
    System.out.println("recibidoooo99 "+System.currentTimeMillis());
}*/
                    if ("6".equals(recibido)) {
MainActivity.mediaplayers.pie_der();
                      //  mediaPlayers.pie_der();

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

                    }

                    else if ("9".equals(recibido)) {
                        MainActivity.mediaplayers.der3();
                        //pie_izq_no_pulsado

                    }
                    else if ("0".equals(recibido)) {
                        MainActivity.mediaplayers.izq3();
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

    public void CloseSocket( )  {
        server.close();
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