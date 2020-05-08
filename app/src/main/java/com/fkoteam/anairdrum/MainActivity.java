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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements SensorEventListener  {
    float[] orientation = new float[3];
    float[] rMat = new float[9];

    SensorManager sensorManager;
    private Sensor mAccelerometer;
    TextView txt_mAzimuth;
    RadioGroup radioGroup;
    com.fkoteam.anairdrum.udp2.Server chatserver;
    DatagramPacket packet_izq1,packet_izq2,packet_izq3,packet_der1cl,packet_der1op,packet_der2,packet_der3,packet_pie_der,packet_pie_izq_pulsado,packet_pie_izq_no_pulsado;

    MediaPlayers mediaplayers;



    Vibrator v ;



    int compararDer=-1;
    int compararIzq=-1;
    boolean sonando1=false,sonando2=false,sonando3=false;


    double fuerza;
    boolean haveSensor = false;



    double min_z=99;
    double min_x=99;
    // - 1  mano izq, 1 mano derecha
    double izq=1;

    int xx, yy,zz;


    TextView txt_progresoX,txt_progresoY,txt_progresoZ;
    //sensibilidadX lado izquierdo, Y centro, Z lado derecho
    int min_value=1,sensibilidadX=1,sensibilidadY=1,sensibilidadZ=1;
    double primeroX,segundoX,terceroX,primeroZ,segundoZ,terceroZ;
    boolean started=false;
    LayoutInflater layoutInflaterAndroid;
    SeekBar seekBarX,seekBarY,seekBarZ;
    private float[] mRotationVector = new float[5];

    float[] gData = new float[3]; // accelerometer
    float[] mData = new float[3]; // magnetometer
    float[] iMat = new float[9];



    SensorEventListener sensorEventListener;
    int whip = 0;
    int whip2 = 0;
    private Sensor mRotationVectorSensor;
    private Sensor mMagnetometerSensor;
    private boolean mUseRotationVectorSensor=false;
    private double mAzimuth;
    AlertDialog alertDialogAndroid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Preferencias.init(getApplicationContext());
        mediaplayers=new MediaPlayers(getApplicationContext());

        construirDatagram();
v= (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        radioGroup = findViewById(R.id.radioGroup);

        final Button button = findViewById(R.id.pedal_der);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(!Opciones.cliente || Opciones.offline)
                    mediaplayers.pie_der();
                else

                    new Client (packet_pie_der).start();


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
                            new Client(packet_pie_izq_pulsado).start();

                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        mediaplayers.setPie_izq_pulsado(false);
                        if (!Opciones.cliente) {
                            mediaplayers.pie_izq();
                        }
                        else
                            new Client(packet_pie_izq_no_pulsado).start();
                    }

                }
                return true;
            }


        });
        final Button volver = findViewById(R.id.volver);
        volver.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                izq=1;
                //calculaNumeros();
                findViewById(R.id.volver).setVisibility(View.GONE);
                findViewById(R.id.pedal_der).setVisibility(View.GONE);
                findViewById(R.id.pedal_izq).setVisibility(View.GONE);
                radioGroup.check(R.id.mano_der);
                Preferencias.write(Preferencias.MODO, R.id.mano_der);//save string in shared preference.

                if(!started)
                    start();
            }
        });
        seekBarX = findViewById(R.id.seekBarX);
        txt_progresoX = findViewById(R.id.txt_progresoX);
        seekBarX.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sensibilidadX=progress+min_value;
                txt_progresoX.setText(getString(R.string.titulo_sensibilidad, getString(R.string.izquierda),sensibilidadX)  );

                //calculaNumeros();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Preferencias.write(Preferencias.SENSIBILIDADX, sensibilidadX);//save string in shared preference.


            }
        });

        seekBarY = findViewById(R.id.seekBarY);
        txt_progresoY = findViewById(R.id.txt_progresoY);
        seekBarY.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sensibilidadY=progress+min_value;
                txt_progresoY.setText(getString(R.string.titulo_sensibilidad, getString(R.string.centro),sensibilidadY)  );

                //calculaNumeros();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Preferencias.write(Preferencias.SENSIBILIDADY, sensibilidadY);//save string in shared preference.


            }
        });
        seekBarZ = findViewById(R.id.seekBarZ);
        txt_progresoZ = findViewById(R.id.txt_progresoZ);
        seekBarZ.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sensibilidadZ=progress+min_value;
                txt_progresoZ.setText(getString(R.string.titulo_sensibilidad, getString(R.string.derecha),sensibilidadZ)  );

                //calculaNumeros();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Preferencias.write(Preferencias.SENSIBILIDADZ, sensibilidadZ);//save string in shared preference.


            }
        });
        //calculaNumeros();
        txt_mAzimuth = findViewById(R.id.txt_mAzimuth);
        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);


        start();
        try {
            if(!Opciones.cliente && chatserver==null &&  !Opciones.offline)

             {
                chatserver = new com.fkoteam.anairdrum.udp2.Server(Opciones.puerto,mediaplayers);
                chatserver.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void izq1(){
        if(!Opciones.cliente || Opciones.offline) {
            mediaplayers.izq1();
        }
        else
            new Client (packet_izq1).start();


    }

    private void izq2() {
        if (!Opciones.cliente || Opciones.offline) {
            mediaplayers.izq2();
        }
        else

            new Client (packet_izq2).start();

    }



    private void der1() {
        if(!Opciones.cliente || Opciones.offline) {
            if(mediaplayers.isPie_izq_pulsado())
                mediaplayers.der1_op();
            else
                mediaplayers.der1_cl();

        }
        else {
            new Client(packet_der1op).start();
        }




    }

    private void der2(){
        if (!Opciones.cliente || Opciones.offline) {
            mediaplayers.der2();

        }  else

            new Client (packet_der2).start();


    }

    private void der3(){
        if (!Opciones.cliente || Opciones.offline) {
            mediaplayers.der3();

        }  else

            new Client (packet_der3).start();


    }

    private void izq3(){
        if (!Opciones.cliente || Opciones.offline) {
            mediaplayers.izq3();

        }  else

            new Client (packet_izq3).start();


    }


    private void start(){
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        haveSensor = sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        started=true;


    }

    private void stop(){

        sensorManager.unregisterListener(sensorEventListener);
        sensorManager.unregisterListener(this,mAccelerometer);


        started=false;

    }

    @Override
    protected void onPause() {
        stop();
        super.onPause();
        if(chatserver != null)
        {
            if(!chatserver.isInterrupted())
            {
                chatserver.interrupt();
            }
        }

    }

    @Override
    protected void onResume() {
        start();


        Opciones.cliente=Preferencias.read(Preferencias.CLIENTE, Opciones.cliente);//read string in shared preference.
        Opciones.offline=Preferencias.read(Preferencias.OFFLINE, Opciones.offline);//read string in shared preference.
        Opciones.puerto=Preferencias.read(Preferencias.PUERTO, Opciones.puerto);//read string in shared preference.
        Opciones.ipServidor=Preferencias.read(Preferencias.IP_SERVIDOR, Opciones.ipServidor);//read string in shared preference.
        int check=Preferencias.read(Preferencias.MODO, R.id.mano_der);
        radioGroup.check(check);
        gestionaCheck(check);//read string in shared preference);
        construirDatagram();

        int sensibilidad_priv_x=Preferencias.read(Preferencias.SENSIBILIDADX, sensibilidadX);
        sensibilidadX=sensibilidad_priv_x;
        txt_progresoX.setText(getString(R.string.titulo_sensibilidad, getString(R.string.izquierda),sensibilidad_priv_x)  );

        seekBarX.setProgress(sensibilidadX-min_value);

        int sensibilidad_priv_y=Preferencias.read(Preferencias.SENSIBILIDADY, sensibilidadY);
        sensibilidadY=sensibilidad_priv_y;
        txt_progresoY.setText(getString(R.string.titulo_sensibilidad, getString(R.string.centro),sensibilidad_priv_y)  );

        seekBarY.setProgress(sensibilidadY-min_value);

        int sensibilidad_priv_z=Preferencias.read(Preferencias.SENSIBILIDADZ, sensibilidadZ);
        sensibilidadZ=sensibilidad_priv_z;
        txt_progresoZ.setText(getString(R.string.titulo_sensibilidad, getString(R.string.derecha),sensibilidad_priv_z)  );

        seekBarZ.setProgress(sensibilidadZ-min_value);

        //calculaNumeros();


        try {
            if(!Opciones.cliente && !Opciones.offline)

              {
                chatserver = new com.fkoteam.anairdrum.udp2.Server(Opciones.puerto,mediaplayers);
                chatserver.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onResume();
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }




    public void checkButton(View v) {
        if(radioGroup.getCheckedRadioButtonId()==findViewById(R.id.pie_izq).getId() && Opciones.offline) {
            Toast.makeText(getApplicationContext(), R.string.pedal_offline, Toast.LENGTH_SHORT).show();

            radioGroup.check(Preferencias.read(Preferencias.MODO, R.id.mano_der));
        }
        else {

            Preferencias.write(Preferencias.MODO, radioGroup.getCheckedRadioButtonId());//save string in shared preference.
            gestionaCheck(radioGroup.getCheckedRadioButtonId());
        }




    }


    private void gestionaCheck(int checked)
    {
        if(checked == findViewById(R.id.mano_der).getId()) {

            izq=1;
            //calculaNumeros();
            findViewById(R.id.volver).setVisibility(View.GONE);
            findViewById(R.id.pedal_der).setVisibility(View.GONE);
            findViewById(R.id.pedal_izq).setVisibility(View.GONE);

            if(!started)
                start();
        }
        if(checked == findViewById(R.id.mano_izq).getId()) {

            izq=-1;
            //calculaNumeros();

            findViewById(R.id.volver).setVisibility(View.GONE);
            findViewById(R.id.pedal_der).setVisibility(View.GONE);
            findViewById(R.id.pedal_izq).setVisibility(View.GONE);

            if(!started)
                start();


        }
        if(checked == findViewById(R.id.pie_der).getId()) {
            findViewById(R.id.volver).setVisibility(View.VISIBLE);
            findViewById(R.id.pedal_der).setVisibility(View.VISIBLE);
            findViewById(R.id.pedal_izq).setVisibility(View.GONE);

            if (started)
                stop();

        }
        if(checked == findViewById(R.id.pie_izq).getId()) {
            findViewById(R.id.volver).setVisibility(View.VISIBLE);
            findViewById(R.id.pedal_der).setVisibility(View.GONE);
            findViewById(R.id.pedal_izq).setVisibility(View.VISIBLE);

            if (started)
                stop();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent1=new Intent(this,Opciones.class);
        this.startActivity(intent1);
        return true;

    }

    private void construirDatagram()
    {
        if(!Opciones.offline) {
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



            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void onSensorChanged(SensorEvent event) {

        float orientation[] = new float[3];



            float x = event.values[0];
        float z = event.values[2];
        if(!sonando1 && !sonando2 && !sonando3) {
            if (x > 20 + sensibilidadY) {
                sonando1 = true;
                v.vibrate(50);
                if(izq==1)
                 der3();
                else
                    izq3();
            } else if (z < -20 - sensibilidadZ) {
                v.vibrate(50);

                sonando2 = true;
                if(izq==1)
                der1();
                else
                    izq1();
            } else if (z > 20 + sensibilidadX) {
                v.vibrate(50);

                sonando3 = true;
                if(izq==1)
                der2();
                else
                    izq2();
            }
        }
        if(sonando1 && x<20+sensibilidadY)
            sonando1=false;
        if(sonando2 && z>-20-sensibilidadZ)
            sonando2=false;
        if(sonando3 && z<20+sensibilidadX)
            sonando3=false;


    }
}