package com.fkoteam.anairdrum;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Objects;

public class Opciones extends AppCompatActivity {
    public static boolean offline = true;
    public static boolean cliente = true;
    RadioGroup radioGroupOffline, radioGroupCliente;
    public static int puerto = 7001;
    public static String ipServidor = "192.168.1.1";

    private EditText puertoEdit, ipServidorEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opciones);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        radioGroupOffline = findViewById(R.id.offline_online);
        radioGroupCliente = findViewById(R.id.cliente_servidor);


        String ip = getLocalIpAddress();
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


    public void checkButton(View view) {

        offline = radioGroupOffline.getCheckedRadioButtonId() == findViewById(R.id.offline).getId();
        cliente = radioGroupCliente.getCheckedRadioButtonId() == findViewById(R.id.cliente).getId();
        Preferencias.write(Preferencias.OFFLINE, offline);//save string in shared preference.
        Preferencias.write(Preferencias.CLIENTE, cliente);//save int in shared preference.
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
}
