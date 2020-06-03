package com.fkoteam.anairdrum;


import com.badlogic.gdx.utils.Array;

import ru.maklas.mnet2.BroadcastServlet;
import ru.maklas.mnet2.HighPingUDPSocket;
import ru.maklas.mnet2.JavaUDPSocket;
import ru.maklas.mnet2.PacketLossUDPSocket;
import ru.maklas.mnet2.ServerAuthenticator;
import ru.maklas.mnet2.ServerSocket;
import ru.maklas.mnet2.Socket;
import ru.maklas.mnet2.SocketProcessor;
import ru.maklas.mnet2.Supplier;
import ru.maklas.mnet2.UDPSocket;
import ru.maklas.mnet2.serialization.Serializer;
import com.fkoteam.anairdrum.objects.*;
import java.net.SocketException;
import java.util.Random;

public class TestUtils {

    public static Supplier<Serializer> serializerSupplier = new MySerializer(128);

    public static void startUpdating(BroadcastServlet broadServerSocket, int freq){
        startUpdating(broadServerSocket, freq, new SocketProcessor() {
            @Override
            public void process(Socket s, Object o) {
            }
        });
    }

                public static void startUpdating(ServerSocket serverSocket, int freq){
        startUpdating(serverSocket, freq, new SocketProcessor() {
            @Override
            public void process(Socket s, Object o) {
                if (((EntityUpdate)o).getId()==6) {
                    MainActivity.mediaplayers.pie_der();
                    //  mediaPlayers.pie_der();

                } else if (((EntityUpdate)o).getId()==4) {

                    MainActivity.mediaplayers.izq1();

                } else if (((EntityUpdate)o).getId()==5) {

                    MainActivity.mediaplayers.izq2();

                } else if (((EntityUpdate)o).getId()==1) {

                    MainActivity.mediaplayers.der1_cl();

                } else if (((EntityUpdate)o).getId()==2) {

                    MainActivity.mediaplayers.der1_op();


                } else if (((EntityUpdate)o).getId()==3) {

                    MainActivity.mediaplayers.der2();

                } else if (((EntityUpdate)o).getId()==7) {
                    MainActivity.mediaplayers.setPie_izq_pulsado(true);
                    //pie_izq_pulsado


                } else if (((EntityUpdate)o).getId()==8) {
                    MainActivity.mediaplayers.setPie_izq_pulsado(false);
                    MainActivity.mediaplayers.pie_izq();
                    //pie_izq_no_pulsado

                }

                else if (((EntityUpdate)o).getId()==9) {
                    MainActivity.mediaplayers.der3();
                    //pie_izq_no_pulsado

                }
                else if (((EntityUpdate)o).getId()==0) {
                    MainActivity.mediaplayers.izq3();
                    //pie_izq_no_pulsado

                }
            }
        });
    }

    public static void startUpdating(final ServerSocket serverSocket, final int freq, final SocketProcessor processor){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Array<Socket> sockets = new Array<Socket>();
                while (!serverSocket.isClosed()) {
                    serverSocket.update();
                    serverSocket.getSockets(sockets);
                    for (Socket socket : sockets) {
                        socket.update(processor);
                    }
                    try {
                        Thread.sleep(freq);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static void startUpdating(final BroadcastServlet broadServerSocket, final int freq, final SocketProcessor processor){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!broadServerSocket.isClosed()) {

                    broadServerSocket.update();
                    try {
                        Thread.sleep(freq);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }


    public static ServerSocket newServerSocket(UDPSocket udp, ServerAuthenticator auth){
        return new ServerSocket(udp, 128, 15000, 1500, 125, auth, serializerSupplier);
    }

    public static UDPSocket udp(int port, int additionalPing, double packetLoss) throws SocketException {
        UDPSocket udp = port < 1024 ? new JavaUDPSocket() : new JavaUDPSocket(port);
        if (additionalPing > 0){
            udp = new HighPingUDPSocket(udp, additionalPing);
        }
        if (packetLoss > 0){
            udp = new PacketLossUDPSocket(udp, packetLoss, packetLoss);
        }
        return udp;
    }
/*
    public static void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignore) {}
    }


    public static byte[] randBytes(int len){
        if (len <= 0) return new byte[0];
        byte[] bytes = new byte[len];
        new Random().nextBytes(bytes);
        return bytes;
    }*/
}
