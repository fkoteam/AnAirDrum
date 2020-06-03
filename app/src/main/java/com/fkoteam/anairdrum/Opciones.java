package com.fkoteam.anairdrum;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
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
import ru.maklas.mnet2.BroadcastServlet;
import ru.maklas.mnet2.BroadcastSocket;

public class Opciones extends AppCompatActivity {
    public static boolean offline = true;
    public static boolean cliente = true;
    public static boolean no_gravity = false;
    public static boolean no_vibracion = false;
    public static int num_clientes=0;


    public static AtomicBoolean receivedResponse = new AtomicBoolean();


    RadioGroup radioGroupOffline, radioGroupCliente;
    CheckBox check_no_gravity,check_no_vibracion,check_modo_thread,check_modo_tcp;
    public static int puerto = 7001;
    public static String ipServidor = "192.168.1.1";
    public static String ip;

    private EditText puertoEdit, ipServidorEdit;
    private int mInterval = 2000; // 5 seconds by default, can be changed later
    private Handler mHandler;
    public static GestionarConexiones gestionarConexiones;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opciones);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        radioGroupOffline = findViewById(R.id.offline_online);
        radioGroupCliente = findViewById(R.id.cliente_servidor);
        check_no_gravity = findViewById(R.id.checkBoxGravity);
        check_no_vibracion=findViewById(R.id.checkBoxVibracion);

        if (gestionarConexiones == null)
            gestionarConexiones = GestionarConexiones.getSingletonInstance();


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


    public void checkButton(View view) throws Exception {
        String haLlamado = "";
        if(view.getTag()!=null)
            haLlamado=view.getTag().toString();

        offline = radioGroupOffline.getCheckedRadioButtonId() == findViewById(R.id.offline).getId();
        cliente = radioGroupCliente.getCheckedRadioButtonId() == findViewById(R.id.cliente).getId();
        no_gravity=check_no_gravity.isChecked();
        no_vibracion=check_no_vibracion.isChecked();

        Preferencias.write(Preferencias.OFFLINE, offline);//save string in shared preference.
        Preferencias.write(Preferencias.CLIENTE, cliente);//save int in shared preference.
        Preferencias.write(Preferencias.NO_GRAVITY, no_gravity);//save int in shared preference.
        Preferencias.write(Preferencias.NO_VIBRACION, no_vibracion);//save int in shared preference.

    if(!offline && ("cliente".equals(haLlamado) || "servidor".equals(haLlamado)))
    {
        if(cliente)
        {
            num_clientes=0;
            stopRepeatingTask();
            gestionarConexiones.stop();
            TextView txt_servidor = findViewById(R.id.servidor);
            txt_servidor.setText(getString(R.string.servidor));
            gestionarConexiones.creaBroadCastCliente();
            Toast.makeText(getApplicationContext(), "Buscando...", Toast.LENGTH_SHORT).show();
            receivedResponse.set(false);





            Handler handler = new Handler();
            handler.postDelayed(new Runnable(){
                @Override
                public void run(){
                    // do something
                    if(receivedResponse.get()) {
                        Toast.makeText(getApplicationContext(), "Servidor encontrado.", Toast.LENGTH_SHORT).show();


                        ipServidorEdit.setText(ipServidor);
                        Preferencias.write(Preferencias.IP_SERVIDOR, ipServidor);//save string in shared preference.
                        TextView txt_cliente = findViewById(R.id.cliente);
                        txt_cliente.setText(getString(R.string.cliente_asociado));

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Servidor no encontrado. Vuelve a intentar o configua manualmente.", Toast.LENGTH_SHORT).show();
                        TextView txt_cliente = findViewById(R.id.cliente);
                        txt_cliente.setText(getString(R.string.cliente_no_asociado));
                    }
                }
            }, 2000);



            }
        else
        {
            gestionarConexiones.stop();
            gestionarConexiones.inicializaServidor();
            gestionarConexiones.creaBroadCastServidor();


            Toast.makeText(getApplicationContext(), "Escuchando...", Toast.LENGTH_SHORT).show();

            mHandler = new Handler();
            startRepeatingTask();
            TextView txt_cliente = findViewById(R.id.cliente);
            txt_cliente.setText(getString(R.string.cliente));





        }











    }

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
        CheckBox opciones_avanzadas=findViewById(R.id.opciones_avanzadas);

        if(offline)
        {
            findViewById(R.id.cliente).setVisibility(View.GONE);
            findViewById(R.id.servidor).setVisibility(View.GONE);
            findViewById(R.id.puerto).setVisibility(View.GONE);
            findViewById(R.id.txt_puerto).setVisibility(View.GONE);
            findViewById(R.id.ip_servidor).setVisibility(View.GONE);
            findViewById(R.id.txt_ip_servidor).setVisibility(View.GONE);
            findViewById(R.id.opciones_avanzadas).setVisibility(View.GONE);

        }
        else
        {
            findViewById(R.id.cliente).setVisibility(View.VISIBLE);
            findViewById(R.id.servidor).setVisibility(View.VISIBLE);
            if(opciones_avanzadas.isChecked()) {
                findViewById(R.id.puerto).setVisibility(View.VISIBLE);
                findViewById(R.id.txt_puerto).setVisibility(View.VISIBLE);
                if (cliente) {
                    findViewById(R.id.ip_servidor).setVisibility(View.VISIBLE);
                    findViewById(R.id.txt_ip_servidor).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.ip_servidor).setVisibility(View.GONE);
                    findViewById(R.id.txt_ip_servidor).setVisibility(View.GONE);
                }
            }
            else
            {
                findViewById(R.id.ip_servidor).setVisibility(View.GONE);
                findViewById(R.id.txt_ip_servidor).setVisibility(View.GONE);
                findViewById(R.id.txt_ip).setVisibility(View.GONE);
                findViewById(R.id.puerto).setVisibility(View.GONE);
                findViewById(R.id.txt_puerto).setVisibility(View.GONE);
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        stopRepeatingTask();
        gestionarConexiones.stopMenosServidor();



    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gestionarConexiones.stopMenosServidor();

        this.finish();
    }


    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                TextView txt_servidor = findViewById(R.id.servidor);
                txt_servidor.setText(getString(R.string.clientes_asociados, num_clientes));
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        if(mHandler!=null)
        mHandler.removeCallbacks(mStatusChecker);
    }

}
