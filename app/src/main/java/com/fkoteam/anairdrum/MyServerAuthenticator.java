package com.fkoteam.anairdrum;

import com.badlogic.gdx.utils.Array;

import ru.maklas.mnet2.Connection;
import ru.maklas.mnet2.ServerAuthenticator;
import ru.maklas.mnet2.Socket;

public class MyServerAuthenticator implements ServerAuthenticator {
    Socket socket=null;

    @Override
    public void acceptConnection(Connection conn) {
        Array<Player> players = new Array<Player>();

         if (!(conn.getRequest() instanceof ConnectionRequest)) { //request was wrong
            conn.reject(new ConnectionResponse("Wrong type of request"));
        } else {

            ConnectionRequest req = (ConnectionRequest) conn.getRequest();
                socket = conn.accept(new ConnectionResponse("Welcome, " + req.getName() + "!")); //obtain Socket
                final Player player = new Player(req.getName(), socket);
                socket.setUserData(player); //Save Player in socket, so that we can know who send us data
                players.add(player);
                socket.addDcListener((sock, msg) -> { //Add dc listener. We need to remove Player from Array after he disconnects
                    players.removeValue(player, true);
                });
            }
        }



}
