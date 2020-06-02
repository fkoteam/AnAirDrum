package com.fkoteam.anairdrum;

import ru.maklas.mnet2.Socket;

class Player {
    String name;
    Socket socket;
    public Player(String name, Socket socket) {
        this.name=name;
        this.socket=socket;
    }

    public String getName() {
        return name;
    }
}
