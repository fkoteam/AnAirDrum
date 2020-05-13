package com.fkoteam.anairdrum;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.fkoteam.anairdrum.udp2.Client;
import com.fkoteam.anairdrum.udp2.Client2;
import com.fkoteam.anairdrum.udp2.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    float[] rMat = new float[9];



    SensorManager sensorManager;
    private Sensor mAccelerometer;
    TextView txt_mAzimuth;
    RadioGroup radioGroup;
    DatagramPacket packet_izq1, packet_izq2, packet_izq3, packet_der1cl, packet_der1op, packet_der2, packet_der3, packet_pie_der, packet_pie_izq_pulsado, packet_pie_izq_no_pulsado;
    com.fkoteam.anairdrum.udp.Client cl_izq1, cl_izq2, cl_izq3, cl_der1cl, cl_der1op, cl_der2, cl_der3, cl_pie_der, cl_pie_izq_pulsado, cl_pie_izq_no_pulsado;
    Client2 clienteUDP;


     public static MediaPlayers mediaplayers;


            Server chatserver;



    Vibrator v;
    boolean arriba=true;


    int compararDer = -1;
    int compararIzq = -1;
    boolean sonandoA = false, sonandoB = false, sonandoC = false;


    double fuerza;
    boolean haveSensor = false,haveGravity = false;
    DatagramSocket socket = null;


    double min_z = 99;
    double min_x = 99;
    // - 1  mano izq, 1 mano derecha
    double izq = 1;

    int xx, yy, zz;


    TextView txt_progresoA, txt_progresoB, txt_progresoC;
    //sensibilidadA lado izquierdo, B centro, C lado derecho
    int min_value = 1, sensibilidadA = 1, sensibilidadB = 1, sensibilidadC = 1;
    double primeroX, segundoX, terceroX, primeroZ, segundoZ, terceroZ;
    boolean started = false;
    LayoutInflater layoutInflaterAndroid;
    SeekBar seekBarA, seekBarB, seekBarC;
    private float[] mRotationVector = new float[5];

    float[] gData = new float[3]; // accelerometer
    float[] mData = new float[3]; // magnetometer
    float[] iMat = new float[9];
    float[] mGeomagnetic;

    SensorEventListener sensorEventListener;
    int whipX = 0;
    int whipY = 0;
    int whipZ = 0;
    private Sensor mRotationVectorSensor;
    private Sensor mGravity;
    private boolean mUseRotationVectorSensor = false;
    private double mAzimuth;
    AlertDialog alertDialogAndroid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Preferencias.init(getApplicationContext());
        if(mediaplayers==null)
            mediaplayers = new MediaPlayers(getApplicationContext());

        construirDatagram();
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        radioGroup = findViewById(R.id.radioGroup);

        final Button button = findViewById(R.id.pedal_der);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!Opciones.cliente || Opciones.offline)
                    mediaplayers.pie_der();
                else
                    if(Opciones.modo_thread)
                        new Client(packet_pie_der).start();
                    else
                        cl_pie_der.enviar();


            }
        });


        final Button button_izq = findViewById(R.id.pedal_izq);
        button_izq.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (Opciones.offline) {
                    Toast.makeText(getApplicationContext(), R.string.pedal_offline, Toast.LENGTH_SHORT).show();
                    mediaplayers.setPie_izq_pulsado(false);
                } else {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mediaplayers.setPie_izq_pulsado(true);

                        if (Opciones.cliente)
                            if(Opciones.modo_thread)
                                new Client(packet_pie_izq_pulsado).start();
                            else
                                cl_pie_izq_pulsado.enviar();

                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        mediaplayers.setPie_izq_pulsado(false);
                        if (!Opciones.cliente) {
                            mediaplayers.pie_izq();
                        } else
                        if(Opciones.modo_thread)
                            new Client(packet_pie_izq_no_pulsado).start();
                        else
                            cl_pie_izq_no_pulsado.enviar();
                    }

                }
                return true;
            }


        });
        final Button volver = findViewById(R.id.volver);
        volver.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                izq = 1;
                //calculaNumeros();
                findViewById(R.id.volver).setVisibility(View.GONE);
                findViewById(R.id.pedal_der).setVisibility(View.GONE);
                findViewById(R.id.pedal_izq).setVisibility(View.GONE);
                radioGroup.check(R.id.mano_der);
                Preferencias.write(Preferencias.MODO, R.id.mano_der);//save string in shared preference.

                if (!started)
                    start();
            }
        });
        seekBarA = findViewById(R.id.seekBarA);
        txt_progresoA = findViewById(R.id.txt_progresoA);
        seekBarA.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sensibilidadA = progress + min_value;
                txt_progresoA.setText(getString(R.string.titulo_sensibilidad, getString(R.string.izquierda), sensibilidadA));

                //calculaNumeros();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Preferencias.write(Preferencias.SENSIBILIDADA, sensibilidadA);//save string in shared preference.


            }
        });

        seekBarB = findViewById(R.id.seekBarB);
        txt_progresoB = findViewById(R.id.txt_progresoB);
        seekBarB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sensibilidadB = progress + min_value;
                txt_progresoB.setText(getString(R.string.titulo_sensibilidad, getString(R.string.centro), sensibilidadB));

                //calculaNumeros();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Preferencias.write(Preferencias.SENSIBILIDADB, sensibilidadB);//save string in shared preference.


            }
        });
        seekBarC = findViewById(R.id.seekBarC);
        txt_progresoC = findViewById(R.id.txt_progresoC);
        seekBarC.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sensibilidadC = progress + min_value;
                txt_progresoC.setText(getString(R.string.titulo_sensibilidad, getString(R.string.derecha), sensibilidadC));

                //calculaNumeros();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Preferencias.write(Preferencias.SENSIBILIDADC, sensibilidadC);//save string in shared preference.


            }
        });
        //calculaNumeros();
        txt_mAzimuth = findViewById(R.id.txt_mAzimuth);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);


       try {
            if (!Opciones.cliente && chatserver == null && !Opciones.offline) {

                    chatserver = new Server(Opciones.puerto);
                    chatserver.start();


            }
           if (Opciones.cliente && clienteUDP == null && !Opciones.offline) {

                   clienteUDP = new Client2();
               clienteUDP.start();


           }

        } catch (IOException e) {
            //e.printStackTrace();
        }

    }


    private void izq1() {
        if (!Opciones.cliente || Opciones.offline) {
            mediaplayers.izq1();
        } else
        if(Opciones.modo_thread)
            new Client(packet_izq1).start();
        else
            cl_izq1.enviar();


    }

    private void izq2() {
        if (!Opciones.cliente || Opciones.offline) {
            mediaplayers.izq2();
        } else
        if(Opciones.modo_thread)
            new Client(packet_izq2).start();
        else
            cl_izq2.enviar();

    }


    private void der1() {
        if (!Opciones.cliente || Opciones.offline) {
            if (mediaplayers.isPie_izq_pulsado())
                mediaplayers.der1_op();
            else
                mediaplayers.der1_cl();

        } else {
            if(Opciones.modo_thread)
                clienteUDP.enviar(packet_der1op);
           /* {
                synchronized(clienteUDP){
                    try{
                        System.out.println("Waiting for b to complete...");
                        clienteUDP.wait();
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }

                    clienteUDP.enviar(packet_der1op);
                }
              //  synchronized(clienteUDP){
            //        clienteUDP.notifyAll();

            //    }

                //new Client(packet_der1op).start();
            }*/
            else
                cl_der1op.enviar();
        }


    }

    private void der2() {
        if (!Opciones.cliente || Opciones.offline) {
            mediaplayers.der2();

        } else
        if(Opciones.modo_thread)
            clienteUDP.enviar(packet_der1op);
/*{
           // synchronized(clienteUDP){
         //       clienteUDP.notifyAll();

            synchronized(clienteUDP){
                try{
                    System.out.println("Waiting for b to complete...");
                    clienteUDP.wait();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }

                clienteUDP.enviar(packet_der1op);
            }
         //   }
            //clienteUDP.notify();
            //new Client(packet_der2).start();
          //  clienteUDP.enviar(packet_der2);
        }*/

        else
            cl_der2.enviar();


    }

    private void der3() {
        if (!Opciones.cliente || Opciones.offline) {
            mediaplayers.der3();

        } else
        if(Opciones.modo_thread)
            clienteUDP.enviar(packet_der1op);

        //new Client(packet_der3).start();
           // synchronized(clienteUDP){
                //clienteUDP.notifyAll();
            /*synchronized(clienteUDP){
                try{
                    System.out.println("Waiting for b to complete...");
                    clienteUDP.wait();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }

                clienteUDP.enviar(packet_der1op);
            }

          //  }
        }*/

        else
            cl_der3.enviar();


    }

    private void izq3() {
        if (!Opciones.cliente || Opciones.offline) {
            mediaplayers.izq3();

        } else
        if(Opciones.modo_thread)
            new Client(packet_izq3).start();
        else
            cl_izq3.enviar();


    }


    private void start() {
        if(!started) {
            mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            haveSensor = sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);

            mGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

            if (!Opciones.no_gravity) {
                haveGravity = sensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_FASTEST);

            }

            started = true;
        }




    }

    private void stop() {

        sensorManager.unregisterListener(sensorEventListener);
        sensorManager.unregisterListener(this, mAccelerometer);

        if(haveGravity)
            sensorManager.unregisterListener(this, mGravity);



        started = false;

    }

    @Override
    protected void onPause() {
        stop();
        super.onPause();
        if (chatserver != null) {
            if (!chatserver.isInterrupted()) {
                chatserver.interrupt();
            }
        }
        if (clienteUDP != null) {
            if (!clienteUDP.isInterrupted()) {
                clienteUDP.interrupt();
            }
        }

    }

    @Override
    protected void onResume() {
        Opciones.no_gravity = Preferencias.read(Preferencias.NO_GRAVITY, Opciones.no_gravity);//read string in shared preference.

        start();
        Opciones.no_vibracion = Preferencias.read(Preferencias.NO_VIBRACION, Opciones.no_vibracion);//read string in shared preference.



        Opciones.cliente = Preferencias.read(Preferencias.CLIENTE, Opciones.cliente);//read string in shared preference.
        Opciones.offline = Preferencias.read(Preferencias.OFFLINE, Opciones.offline);//read string in shared preference.
        Opciones.puerto = Preferencias.read(Preferencias.PUERTO, Opciones.puerto);//read string in shared preference.
        Opciones.ipServidor = Preferencias.read(Preferencias.IP_SERVIDOR, Opciones.ipServidor);//read string in shared preference.
        int check = Preferencias.read(Preferencias.MODO, R.id.mano_der);
        if(check!=R.id.mano_der && check !=R.id.mano_izq && check !=R.id.pie_der && check !=R.id.pie_izq) {
            check = R.id.mano_der;
            Preferencias.write(Preferencias.MODO, check);//save string in shared preference.

        }

        radioGroup.check(check);
        gestionaCheck(check);//read string in shared preference);
        construirDatagram();

        int sensibilidad_priv_a = Preferencias.read(Preferencias.SENSIBILIDADA, sensibilidadA);
        sensibilidadA = sensibilidad_priv_a;
        txt_progresoA.setText(getString(R.string.titulo_sensibilidad, getString(R.string.izquierda), sensibilidad_priv_a));

        seekBarA.setProgress(sensibilidadA - min_value);

        int sensibilidad_priv_b = Preferencias.read(Preferencias.SENSIBILIDADB, sensibilidadB);
        sensibilidadB = sensibilidad_priv_b;
        txt_progresoB.setText(getString(R.string.titulo_sensibilidad, getString(R.string.centro), sensibilidad_priv_b));

        seekBarB.setProgress(sensibilidadB - min_value);

        int sensibilidad_priv_c= Preferencias.read(Preferencias.SENSIBILIDADC, sensibilidadC);
        sensibilidadC = sensibilidad_priv_c;
        txt_progresoC.setText(getString(R.string.titulo_sensibilidad, getString(R.string.derecha), sensibilidad_priv_c));

        seekBarC.setProgress(sensibilidadC - min_value);

        //calculaNumeros();


        try {
            if (!Opciones.cliente && !Opciones.offline) {

                    chatserver = new Server(Opciones.puerto);
                    chatserver.start();

            }
            if (Opciones.cliente && !Opciones.offline) {

                clienteUDP = new Client2();
                clienteUDP.start();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        String log=(haveGravity?getString(R.string.usando_gravedad):getString(R.string.no_usando_gravedad));

        if(!Opciones.offline && Opciones.cliente)
            log=log+" "+getString(R.string.aviso_cliente);
        txt_mAzimuth.setText(log);


        super.onResume();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public void checkButton(View v) {
        if (radioGroup.getCheckedRadioButtonId() == findViewById(R.id.pie_izq).getId() && Opciones.offline) {
            Toast.makeText(getApplicationContext(), R.string.pedal_offline, Toast.LENGTH_SHORT).show();

            radioGroup.check(Preferencias.read(Preferencias.MODO, R.id.mano_der));
        } else {

            Preferencias.write(Preferencias.MODO, radioGroup.getCheckedRadioButtonId());//save string in shared preference.
            gestionaCheck(radioGroup.getCheckedRadioButtonId());
        }


    }


    private void gestionaCheck(int checked) {
        if (checked == findViewById(R.id.mano_der).getId()) {

            izq = 1;
            //calculaNumeros();
            findViewById(R.id.volver).setVisibility(View.GONE);
            findViewById(R.id.pedal_der).setVisibility(View.GONE);
            findViewById(R.id.pedal_izq).setVisibility(View.GONE);

            if (!started)
                start();
        }
        if (checked == findViewById(R.id.mano_izq).getId()) {

            izq = -1;
            //calculaNumeros();

            findViewById(R.id.volver).setVisibility(View.GONE);
            findViewById(R.id.pedal_der).setVisibility(View.GONE);
            findViewById(R.id.pedal_izq).setVisibility(View.GONE);

            if (!started)
                start();


        }
        if (checked == findViewById(R.id.pie_der).getId()) {
            findViewById(R.id.volver).setVisibility(View.VISIBLE);
            findViewById(R.id.pedal_der).setVisibility(View.VISIBLE);
            findViewById(R.id.pedal_izq).setVisibility(View.GONE);

            if (started)
                stop();

        }
        if (checked == findViewById(R.id.pie_izq).getId()) {
            findViewById(R.id.volver).setVisibility(View.VISIBLE);
            findViewById(R.id.pedal_der).setVisibility(View.GONE);
            findViewById(R.id.pedal_izq).setVisibility(View.VISIBLE);

            if (started)
                stop();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent1 = new Intent(this, Opciones.class);
        this.startActivity(intent1);
        return true;

    }

    private void construirDatagram() {
        if (!Opciones.offline && Opciones.cliente) {
            try {
                packet_der1cl = new DatagramPacket("1".getBytes(), "1".length(), InetAddress.getByName(Opciones.ipServidor), Opciones.puerto);
                packet_der1op = new DatagramPacket("2".getBytes(), "2".length(), InetAddress.getByName(Opciones.ipServidor), Opciones.puerto);

                packet_der2 = new DatagramPacket("3".getBytes(), "3".length(), InetAddress.getByName(Opciones.ipServidor), Opciones.puerto);
                packet_izq1 = new DatagramPacket("4".getBytes(), "4".length(), InetAddress.getByName(Opciones.ipServidor), Opciones.puerto);
                packet_izq2 = new DatagramPacket("5".getBytes(), "5".length(), InetAddress.getByName(Opciones.ipServidor), Opciones.puerto);
                packet_pie_der = new DatagramPacket("6".getBytes(), "6".length(), InetAddress.getByName(Opciones.ipServidor), Opciones.puerto);
                packet_pie_izq_pulsado = new DatagramPacket("7".getBytes(), "7".length(), InetAddress.getByName(Opciones.ipServidor), Opciones.puerto);
                packet_pie_izq_no_pulsado = new DatagramPacket("8".getBytes(), "8".length(), InetAddress.getByName(Opciones.ipServidor), Opciones.puerto);
                packet_der3 = new DatagramPacket("9".getBytes(), "9".length(), InetAddress.getByName(Opciones.ipServidor), Opciones.puerto);
                packet_izq3 = new DatagramPacket("0".getBytes(), "0".length(), InetAddress.getByName(Opciones.ipServidor), Opciones.puerto);

                cl_der1cl=new com.fkoteam.anairdrum.udp.Client(packet_der1cl);
                cl_der1op=new com.fkoteam.anairdrum.udp.Client(packet_der1op);
                cl_der2=new com.fkoteam.anairdrum.udp.Client(packet_der2);
                cl_izq1=new com.fkoteam.anairdrum.udp.Client(packet_izq1);
                cl_izq2=new com.fkoteam.anairdrum.udp.Client(packet_izq2);
                cl_pie_der=new com.fkoteam.anairdrum.udp.Client(packet_pie_der);
                cl_pie_izq_pulsado=new com.fkoteam.anairdrum.udp.Client(packet_pie_izq_pulsado);
                cl_pie_izq_no_pulsado=new com.fkoteam.anairdrum.udp.Client(packet_pie_izq_no_pulsado);
                cl_der3=new com.fkoteam.anairdrum.udp.Client(packet_der3);
                cl_izq3=new com.fkoteam.anairdrum.udp.Client(packet_izq3);











            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            float x = event.values[0];
            float z = event.values[2];


                if (z < -20 - sensibilidadA && !sonandoA && !sonandoB && !sonandoC && (!arriba || !haveGravity)) {

                    sonandoA = true;
                    if (izq == 1)
                        der1();
                    else
                        izq1();

                    if(!Opciones.no_vibracion)
                        v.vibrate(50);

                }



                if (x > 20 + sensibilidadB && !sonandoA && !sonandoB && !sonandoC) {
                    sonandoB = true;
                    if (izq == 1)
                        der3();
                    else
                        izq3();

                    if(!Opciones.no_vibracion)
                        v.vibrate(50);
                }
                if (z > 20 + sensibilidadC && !sonandoA && !sonandoB && !sonandoC && (arriba || !haveGravity)) {

                    sonandoC = true;
                    if (izq == 1)
                        der2();
                    else
                        izq2();

                    if(!Opciones.no_vibracion)
                        v.vibrate(50);

                }

                if (sonandoA && z > 0)
                    sonandoA = false;
                if (sonandoB && x < 0)
                    sonandoB = false;
                if (sonandoC && z < 0)
                    sonandoC = false;


        }
        else if (event.sensor.getType() == Sensor.TYPE_GRAVITY)
        {
            if( event.values[2]>0)arriba=true; else arriba=false;


        }





    }





}