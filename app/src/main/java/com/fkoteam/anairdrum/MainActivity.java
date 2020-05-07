package com.fkoteam.anairdrum;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fkoteam.anairdrum.udp2.Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements SensorEventListener  {
    SensorManager sensorManager;
    private Sensor mAccelerometer;
    TextView txt_mAzimuth;
    RadioGroup radioGroup;
    com.fkoteam.anairdrum.udp2.Server chatserver;
    DatagramPacket packet_izq1,packet_izq2,packet_der1cl,packet_der1op,packet_der2,packet_pie_der,packet_pie_izq_pulsado,packet_pie_izq_no_pulsado;

    MediaPlayers mediaplayers;

    Long timestamp_uno,timestamp_dos;
    double x_uno;
    double z_uno;


    double fuerza;
    boolean haveSensor = false;


    double min_z=99;
    double min_x=99;
    // - 1  mano izq, 1 mano derecha
    double izq=1;

    TextView txt_progreso;
    int min_value=1,sensibilidad=1;
    double primeroX,segundoX,terceroX,primeroZ,segundoZ,terceroZ;
    boolean started=false;
    SeekBar seekBar;


    SensorEventListener sensorEventListener;
    int whip = 0;
    int whip2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Preferencias.init(getApplicationContext());
        mediaplayers=new MediaPlayers(getApplicationContext());

        construirDatagram();

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
                calculaNumeros();
                findViewById(R.id.volver).setVisibility(View.GONE);
                findViewById(R.id.pedal_der).setVisibility(View.GONE);
                findViewById(R.id.pedal_izq).setVisibility(View.GONE);
                radioGroup.check(R.id.mano_der);
                Preferencias.write(Preferencias.MODO, R.id.mano_der);//save string in shared preference.

                if(!started)
                    start();
            }
        });
        seekBar = findViewById(R.id.seekBar);
        txt_progreso = findViewById(R.id.txt_progreso);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sensibilidad=progress+min_value;
                txt_progreso.setText(getString(R.string.titulo_sensibilidad, sensibilidad)  );

                calculaNumeros();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Preferencias.write(Preferencias.SENSIBILIDAD, sensibilidad);//save string in shared preference.


            }
        });
        calculaNumeros();
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

        int sensibilidad_priv=Preferencias.read(Preferencias.SENSIBILIDAD, sensibilidad);
        sensibilidad=sensibilidad_priv;
        txt_progreso.setText(getString(R.string.titulo_sensibilidad, sensibilidad_priv)  );

        seekBar.setProgress(sensibilidad-min_value);

        calculaNumeros();


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



    @Override
    public void onSensorChanged(SensorEvent event) {

            double x = event.values[0]*izq;
        float z = event.values[2];


            if(x<primeroX && whip == 0){
                whip++;
                min_x=99;

            }else if (x>segundoX && whip == 1 ){//&& event.values[1]<-5){
                whip++;
                timestamp_uno = System.currentTimeMillis();
                x_uno=x;
                z_uno=z;
                whip2=0;

            }else
            if(whip==2)
            {
                fuerza = x-x_uno/(System.currentTimeMillis()-timestamp_uno);
                txt_mAzimuth.setText(""+x+"\n"+z );
                if(Math.abs(Math.abs(x)-Math.abs(x_uno))>Math.abs(Math.abs(z)-Math.abs(z_uno))){

                    if (izq<0)
                        izq1();
                    else
                        der1();
                }else{

                    if (izq<0)
                        izq2();
                    else
                        der2();
                }

                whip++;
                whip2=0;

            } else if(whip==3 && x<terceroX)
            {
                whip=0;
                whip2=0;
                min_z=99;
                min_x=99;
            }


        if(z>primeroZ && whip2 == 0){
            whip2++;
            min_z=99;
        }else if (z<segundoZ && whip2 == 1 ){
            whip2++;
            timestamp_dos = System.currentTimeMillis();
            z_uno=z;
            whip=0;
            x_uno=x;

        }else
        if(whip2==2)
        {
            fuerza = z-z_uno/(System.currentTimeMillis()-timestamp_dos);
            txt_mAzimuth.setText(""+x+"\n"+z );

            if(Math.abs(Math.abs(x)-Math.abs(x_uno))>Math.abs(Math.abs(z)-Math.abs(z_uno))){
                if (izq<0)
                    izq1();
                else
                    der1();
            }else{
            if (izq<0)
                izq2();
            else
                der2();
            }
            whip2++;
            whip=0;

        }else if(whip2==3 && z>terceroZ)
        {
            whip=0;
            whip2=0;
            min_z=99;
            min_x=99;
        }

        if(whip==1 && min_x>x)min_x=x;
        if(whip2==1 && min_z>z)min_z=z;



    }

void calculaNumeros()
{
    primeroX=-5-sensibilidad;
    segundoX=6+sensibilidad;
    terceroX=6+sensibilidad;

    primeroZ=8+sensibilidad;
    segundoZ=-10-sensibilidad;
    terceroZ=-10-sensibilidad;

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
            calculaNumeros();
            findViewById(R.id.volver).setVisibility(View.GONE);
            findViewById(R.id.pedal_der).setVisibility(View.GONE);
            findViewById(R.id.pedal_izq).setVisibility(View.GONE);

            if(!started)
                start();
        }
        if(checked == findViewById(R.id.mano_izq).getId()) {

            izq=-1;
            calculaNumeros();

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




            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

}