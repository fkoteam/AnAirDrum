package com.fkoteam.anairdrum;

import ru.maklas.mnet2.Socket;
import ru.maklas.mnet2.SocketProcessor;


public class SocketProcessorImpl implements SocketProcessor {
    @Override
    public void process(Socket socket, Object o) {
        System.out.println("Event received by " + ((Player) socket.getUserData()).getName() + ":" + o);
        if (o instanceof EntityUpdate) {
            System.out.println("Client received :  " + o);
        }
    }
}
