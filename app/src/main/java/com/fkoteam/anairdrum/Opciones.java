package com.fkoteam.anairdrum;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.maklas.mnet2.BroadcastProcessor;
import ru.maklas.mnet2.BroadcastReceiver;
import ru.maklas.mnet2.BroadcastResponse;
import ru.maklas.mnet2.BroadcastSocket;

public class Opciones extends AppCompatActivity  implements BroadcastProcessor {
    public static boolean offline = true;
    public static boolean cliente = true;
    public static boolean no_gravity = false;
    public static boolean no_vibracion = false;

    public static AtomicBoolean receivedResponse = new AtomicBoolean();


    RadioGroup radioGroupOffline, radioGroupCliente;
    CheckBox check_no_gravity,check_no_vibracion,check_modo_thread,check_modo_tcp;
    public static int puerto = 7001;
    public static String ipServidor = "192.168.1.1";
    String ip;

    private EditText puertoEdit, ipServidorEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opciones);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        radioGroupOffline = findViewById(R.id.offline_online);
        radioGroupCliente = findViewById(R.id.cliente_servidor);
        check_no_gravity = findViewById(R.id.checkBoxGravity);
        check_no_vibracion=findViewById(R.id.checkBoxVibracion);



        ip = getLocalIpAddress();
        visibilidad();

        ((TextView) findViewById(R.id.txt_ip)).setText(getString(R.string.ip_de_dispositivo,ip) );

        puertoEdit =  findViewById(R.id.puerto);

        puertoEdit.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                if (s == null || s.toString().length() == 0) {
                    puertoEdit.setText("7001");
                    puerto = 7001;
                } else
                    puerto = Integer.parseInt(s.toString());
                Preferencias.write(Preferencias.PUERTO, puerto);//save string in shared preference.
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        ipServidorEdit =  findViewById(R.id.ip_servidor);
        ipServidorEdit.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        ipServidorEdit.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);

        ipServidorEdit.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                if (s == null || s.toString().length() == 0) {
                    ipServidorEdit.setText("192.168.1.");
                    ipServidor = "192.168.1.1";
                } else
                    ipServidor = s.toString();
                Preferencias.write(Preferencias.IP_SERVIDOR, ipServidor);//save string in shared preference.

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

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
            Toast.makeText(getApplicationContext(), R.string.no_wifi, Toast.LENGTH_SHORT).show();
        }
        return null;
    }


    public void checkButton(View view) throws SocketException, UnknownHostException {

        offline = radioGroupOffline.getCheckedRadioButtonId() == findViewById(R.id.offline).getId();
        cliente = radioGroupCliente.getCheckedRadioButtonId() == findViewById(R.id.cliente).getId();
        no_gravity=check_no_gravity.isChecked();
        no_vibracion=check_no_vibracion.isChecked();

        Preferencias.write(Preferencias.OFFLINE, offline);//save string in shared preference.
        Preferencias.write(Preferencias.CLIENTE, cliente);//save int in shared preference.
        Preferencias.write(Preferencias.NO_GRAVITY, no_gravity);//save int in shared preference.
        Preferencias.write(Preferencias.NO_VIBRACION, no_vibracion);//save int in shared preference.

    /*if(!offline)
    {
        final BroadcastSocket socket;

            socket = new BroadcastSocket(TestUtils.udp(0, 0, 50), "255.255.255.255", 7368, 512, "uuid".getBytes(), TestUtils.serializerSupplier.get());

        ConnectionRequest request = new ConnectionRequest("maklas");
        Toast.makeText(getApplicationContext(), "Buscando...", Toast.LENGTH_SHORT).show();
        receivedResponse.set(false);


        socket.search(request, 1000, 5, new BroadcastReceiver() {
            @Override
            public void receive(BroadcastResponse response) {
                receivedResponse.set(true);
ipServidor=response.getAddress().toString();




            }

            @Override
            public void finished(boolean interrupted) {
                System.out.println("Finsihed: " + interrupted);
            }
        });

        if(receivedResponse.get())
        {
            //cliente
            ipServidorEdit.setText(ipServidor);
            Preferencias.write(Preferencias.IP_SERVIDOR, ipServidor);//save string in shared preference.
            cliente=true;
            Preferencias.write(Preferencias.CLIENTE, cliente);//save int in shared preference.
            Toast.makeText(getApplicationContext(), "Cliente!...", Toast.LENGTH_SHORT).show();

        }
        else
        {
            //servidor
            cliente=false;
            Preferencias.write(Preferencias.CLIENTE, cliente);//save int in shared preference.
            Toast.makeText(getApplicationContext(), "Servidor!...", Toast.LENGTH_SHORT).show();

        }






        socket.close();
    }*/

        visibilidad();
    }

    @Override
    protected void onResume() {
        super.onResume();
        visibilidad();
        ipServidorEdit.setText(ipServidor);
        puertoEdit.setText(""+puerto);
        if(!cliente)
            radioGroupCliente.check(R.id.servidor);
        if(!offline)
            radioGroupOffline.check(R.id.online);
        if(no_gravity)
            check_no_gravity.setChecked(true);
        if(no_vibracion)
            check_no_vibracion.setChecked(true);

    }

    private void visibilidad()
    {
        if(offline)
        {
            findViewById(R.id.cliente).setVisibility(View.GONE);
            findViewById(R.id.servidor).setVisibility(View.GONE);
            findViewById(R.id.puerto).setVisibility(View.GONE);
            findViewById(R.id.txt_puerto).setVisibility(View.GONE);
            findViewById(R.id.ip_servidor).setVisibility(View.GONE);
            findViewById(R.id.txt_ip_servidor).setVisibility(View.GONE);

        }
        else
        {
            findViewById(R.id.cliente).setVisibility(View.VISIBLE);
            findViewById(R.id.servidor).setVisibility(View.VISIBLE);
            findViewById(R.id.puerto).setVisibility(View.VISIBLE);
            findViewById(R.id.txt_puerto).setVisibility(View.VISIBLE);
            if(cliente)
            {
                findViewById(R.id.ip_servidor).setVisibility(View.VISIBLE);
                findViewById(R.id.txt_ip_servidor).setVisibility(View.VISIBLE);
            }
            else
            {
                findViewById(R.id.ip_servidor).setVisibility(View.GONE);
                findViewById(R.id.txt_ip_servidor).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public Object process(InetAddress address, int port, Object request) {
       System.out.println("!!!Server received request: " + request);
        ConnectionResponse welcome = new ConnectionResponse(ip);
        System.out.println("Responding with: " + welcome);

        return welcome;
    }
}
