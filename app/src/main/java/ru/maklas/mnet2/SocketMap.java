package ru.maklas.mnet2;

import com.badlogic.gdx.utils.Array;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Iterator;

class SocketMap {

    Array<SocketWrap> sockets = new Array<SocketWrap>();

    public void put(SocketImpl socket){
        put(socket.address, socket.port, socket);
    }

    public synchronized void put(InetAddress address, int port, SocketImpl socket){
        sockets.add(new SocketWrap(address, port, socket));
    }

    public SocketImpl get(DatagramPacket packet){
        return get(packet.getAddress(), packet.getPort());
    }

    public synchronized SocketImpl get(InetAddress address, int port){
        for (SocketWrap socket : sockets) {
            if (socket.address.equals(address) && socket.port == port){
                return socket.socket;
            }
        }
        return null;
    }

    public synchronized void remove(SocketImpl socket){
        for (Iterator<SocketWrap> iter = sockets.iterator(); iter.hasNext();) {
            SocketWrap wrap = iter.next();
            if (wrap.socket == socket){
                iter.remove();
                return;
            }
        }
    }

    public synchronized void clear() {
        sockets.clear();
    }

    public synchronized int size() {
        return sockets.size;
    }

    static class SocketWrap {
        InetAddress address;
        int port;
        SocketImpl socket;

        public SocketWrap(InetAddress address, int port, SocketImpl socket) {
            this.address = address;
            this.port = port;
            this.socket = socket;
        }
    }

}
