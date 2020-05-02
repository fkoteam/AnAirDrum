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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        radioGroupOffline = findViewById(R.id.offline_online);
        radioGroupCliente = findViewById(R.id.cliente_servidor);


        String ip = getLocalIpAddress();

        ((TextView) findViewById(R.id.txt_ip)).setText("Ip de este dispositivo: " + ip);

        puertoEdit = (EditText) findViewById(R.id.puerto);

        puertoEdit.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                if (s == null || s.toString().length() == 0) {
                    puertoEdit.setText("7001");
                    puerto = 7001;
                } else
                    puerto = Integer.parseInt(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        ipServidorEdit = (EditText) findViewById(R.id.ip_servidor);

        ipServidorEdit.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                if (s == null || s.toString().length() == 0) {
                    ipServidorEdit.setText("192.168.1.");
                    ipServidor = "192.168.1.1";
                } else
                    ipServidor = s.toString();
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
            Toast.makeText(getApplicationContext(), "No wifi", Toast.LENGTH_SHORT).show();
        }
        return null;
    }


    public void checkButton(View view) {

        if (radioGroupOffline.getCheckedRadioButtonId() == findViewById(R.id.offline).getId()) {
            offline = true;
        } else offline = false;
        if (radioGroupCliente.getCheckedRadioButtonId() == findViewById(R.id.cliente).getId()) {
            cliente = true;
        } else cliente = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ipServidorEdit.setText(ipServidor);
        puertoEdit.setText(""+puerto);
        if(!cliente)
            radioGroupCliente.check(R.id.servidor);
        if(!offline)
            radioGroupOffline.check(R.id.online);
    }
}
