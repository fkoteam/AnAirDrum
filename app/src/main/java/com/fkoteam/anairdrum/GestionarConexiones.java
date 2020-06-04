package com.fkoteam.anairdrum;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import ru.maklas.mnet2.BroadcastProcessor;
import ru.maklas.mnet2.BroadcastReceiver;
import ru.maklas.mnet2.BroadcastResponse;
import ru.maklas.mnet2.BroadcastServlet;
import ru.maklas.mnet2.BroadcastSocket;
import ru.maklas.mnet2.Connection;
import ru.maklas.mnet2.ServerAuthenticator;
import ru.maklas.mnet2.ServerResponse;
import ru.maklas.mnet2.ServerSocket;
import ru.maklas.mnet2.Socket;
import ru.maklas.mnet2.SocketImpl;

public class GestionarConexiones implements ServerAuthenticator , BroadcastProcessor {
    static ServerSocket serverSocket;
    static ru.maklas.mnet2.Socket client;
    static boolean servidorIniciado=false;
    static boolean clienteIniciado=false;
    static boolean servidorBroadIniciado=false;
    static boolean clienteBroadIniciado=false;
    private static GestionarConexiones gestionarConexiones;
    private static BroadcastSocket socket;
    private BroadcastServlet servlet;

    public static GestionarConexiones getSingletonInstance() {
        if (gestionarConexiones == null){
            gestionarConexiones = new GestionarConexiones();
        }


        return gestionarConexiones;
    }

    public void sendUnreliable(EntityUpdate entityUpdate)  {
        try {
            inicializaCliente();
        } catch (IOException e) {
            e.printStackTrace();
        }
        client.sendUnreliable(entityUpdate);
    }

    public void stop()
    {
        if(serverSocket!=null)
            serverSocket.close();
        if(client!=null)
            client.close();
        servidorIniciado=false;
        clienteIniciado=false;
        servidorBroadIniciado=false;
         clienteBroadIniciado=false;
        if(socket!=null)
            socket.close();
        if(servlet!=null)
            servlet.close();
    }

    public void paraCliente()
    {
        if(client!=null)
            client.close();
        clienteIniciado=false;

    }
    public void paraServidor()
    {
        if(serverSocket!=null)
            serverSocket.close();
        servidorIniciado=false;

    }

    public void inicializaCliente() throws IOException {
        {
            if(!clienteIniciado) {
                client = new SocketImpl(InetAddress.getByName(Opciones.ipServidor), Opciones.puerto, TestUtils.serializerSupplier.get());


                ServerResponse response = client.connect(new ConnectionRequest("maklas"), 5_000);

                //Here is our response object that Server replied with. Check it for being NULL just in case.
                ConnectionResponse connResp = (ConnectionResponse) response.getResponse();

//There is 4 types of possible outcomes during connection.
//The only time we can be sure to be connected is when ResponseType == ACCEPTED.
//In any other case, socket is not connected.
                switch (response.getType()) {
                    case ACCEPTED:
                        System.out.println("Successfully connected with message " + connResp.getMessage());
                        clienteIniciado = true;
                        break;
                    case REJECTED:
                        System.out.println("Server rejected our request with message " + connResp.getMessage());
                        clienteIniciado = false;
                        break;
                    case NO_RESPONSE:
                        System.out.println("Server doesn't respond");
                        clienteIniciado = false;
                        break;
                    case WRONG_STATE:
                        System.out.println("Socket was closed or was already connected");
                        clienteIniciado = false;
                        break;
                }
            }
        }
    }

    public void inicializaServidor() throws SocketException {
        if(!servidorIniciado) {
            ServerSocket serverSocket = TestUtils.newServerSocket(TestUtils.udp(Opciones.puerto, 0, 0), this);
            TestUtils.startUpdating(serverSocket, 1); //TODO probar diferentes valores. por defecto, 16
            servidorIniciado = true;
        }
    }

    @Override
    public void acceptConnection(Connection conn) {
        System.out.println("Received connection request: " + conn);

        if (conn.getRequest() instanceof ConnectionRequest){
            ConnectionResponse response = new ConnectionResponse("Welcome, " + ((ConnectionRequest) conn.getRequest()).getName() + "!");
            System.out.println("Responding with " + response);
            Socket socket = conn.accept(response);


        }
    }

    void creaBroadCastServidor() throws Exception {
        if(!servidorBroadIniciado) {
            servlet = new BroadcastServlet(7368, 512, "uuid", TestUtils.serializerSupplier.get(), this);
            TestUtils.startUpdating(servlet, 16);
        }
        servidorBroadIniciado=true;
    }

    void paraBroadCastCliente()
    {
        clienteBroadIniciado=false;
        if(socket!=null)
            socket.close();

    }
    void paraBroadCastServidor()
    {
        servidorBroadIniciado=false;
        if(servlet!=null)
            servlet.close();
    }
    void creaBroadCastCliente() throws SocketException, UnknownHostException {
        if(!clienteBroadIniciado) {
            socket = new BroadcastSocket(TestUtils.udp(0, 0, 50), "255.255.255.255", 7368, 512, "uuid".getBytes(), TestUtils.serializerSupplier.get());

            ConnectionRequest request = new ConnectionRequest("maklas");

            socket.search(request, 1000, 5, new BroadcastReceiver() {
                @Override
                public void receive(BroadcastResponse response) {
                    Opciones.receivedResponse.set(true);
                    Opciones.ipServidor = response.getAddress().toString();
                    Opciones.ipServidor = Opciones.ipServidor.replaceAll("[^\\d.]", "");


                }

                @Override
                public void finished(boolean interrupted) {
                    System.out.println("Finsihed: " + interrupted);

                }
            });
        }
        clienteBroadIniciado=true;

    }

    @Override
    public Object process(InetAddress address, int port, Object request) {
        System.out.println("!!!Server received request: " + request);
        ConnectionResponse welcome = new ConnectionResponse(Opciones.ip);
        System.out.println("Responding with: " + welcome);
        Opciones.num_clientes++;
        return welcome;
    }

    public void stopMenosServidor() {

        if(client!=null)
            client.close();
        clienteIniciado=false;
        servidorBroadIniciado=false;
        clienteBroadIniciado=false;
        if(socket!=null)
            socket.close();
        if(servlet!=null)
            servlet.close();
    }
}
