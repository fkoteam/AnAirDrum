package ru.maklas.mnet2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * Created by amaklakov on 02.11.2017.
 * Java DatagramSocket implementation
 */
public class JavaUDPSocket implements UDPSocket {

    private final DatagramSocket socket;

    public JavaUDPSocket() throws SocketException {
        this(new DatagramSocket(null));
    }

    public JavaUDPSocket(int port) throws SocketException {
        this(new DatagramSocket(null),port);
    }

    public JavaUDPSocket(DatagramSocket dSocket) throws SocketException {
        dSocket.setReuseAddress(true);
        this.socket = dSocket;

    }

    public JavaUDPSocket(DatagramSocket dSocket,int port) throws SocketException {
        dSocket.setReuseAddress(true);
        dSocket.bind(new InetSocketAddress(port));
        this.socket = dSocket;

    }


    @Override
    public int getLocalPort() {
        return socket.getLocalPort();
    }

    @Override
    public void send(DatagramPacket packet) throws IOException {
        socket.send(packet);
    }

    @Override
    public void receive(DatagramPacket packet) throws IOException {
        socket.receive(packet);
    }

    @Override
    public void setReceiveTimeout(int millis) throws SocketException {
        socket.setSoTimeout(millis);
    }

    @Override
    public void close() {
        socket.close();
    }

    @Override
    public boolean isClosed() {
        return socket.isClosed();
    }

    @Override
    public void connect(InetAddress address, int port) {
        if (!socket.isConnected())
            socket.connect(address, port);
    }

    @Override
    public void setBroadcast(boolean enabled) throws SocketException {
        socket.setBroadcast(enabled);
    }
}
