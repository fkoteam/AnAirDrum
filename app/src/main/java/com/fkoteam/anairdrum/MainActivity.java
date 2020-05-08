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
    boolean enCalibracion=false;
    com.fkoteam.anairdrum.udp2.Server chatserver;
    DatagramPacket packet_izq1,packet_izq2,packet_izq3,packet_der1cl,packet_der1op,packet_der2,packet_der3,packet_pie_der,packet_pie_izq_pulsado,packet_pie_izq_no_pulsado;

    MediaPlayers mediaplayers;

    Long timestamp_uno,timestamp_dos;
    double x_uno;
    double z_uno;

    Vibrator v ;

    double calibrado_der1x=-9999,calibrado_der1y=-9999,calibrado_der1z=-9999;
    double calibrado_der2x=-9999,calibrado_der2y=-9999,calibrado_der2z=-9999;
    double calibrado_izq1x=-9999,calibrado_izq1y=-9999,calibrado_izq1z=-9999;
    double calibrado_izq2x=-9999,calibrado_izq2y=-9999,calibrado_izq2z=-9999;

    int compararDer=-1;
    int compararIzq=-1;
    boolean sonando1=false,sonando2=false,sonando3=false;


    double fuerza;
    boolean haveSensor = false;
    boolean haveSensorMagnet = false;
    boolean haveSensorRotation = false;

    View mView;


    double min_z=99;
    double min_x=99;
    // - 1  mano izq, 1 mano derecha
    double izq=1;

    int xx, yy,zz;


    TextView txt_progreso;
    int min_value=1,sensibilidad=1;
    double primeroX,segundoX,terceroX,primeroZ,segundoZ,terceroZ;
    boolean started=false;
    LayoutInflater layoutInflaterAndroid;
    SeekBar seekBar;
    private float[] mRotationVector = new float[5];

    float[] gData = new float[3]; // accelerometer
    float[] mData = new float[3]; // magnetometer
    float[] iMat = new float[9];


    private Button mButtonCalibracion;
    final Context c = this;

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


/*

        mButtonCalibracion = (Button) findViewById(R.id.openUserInputDialog);
        mButtonCalibracion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                enCalibracion=true;
                 layoutInflaterAndroid = LayoutInflater.from(c);
                 mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
                 construirChecks();
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
                alertDialogBuilderUserInput.setView(mView);

                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                // ToDo get user input her
                                enCalibracion=false;

                            }
                        })

                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        enCalibracion=false;
                                        dialogBox.cancel();
                                    }
                                });

                 alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();
                alertDialogAndroid.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
        });
*/

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

       // mRotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

    /*    if(mRotationVectorSensor==null) {
            mMagnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
            if(mMagnetometerSensor!=null)
                haveSensorMagnet = sensorManager.registerListener(this, mMagnetometerSensor, SensorManager.SENSOR_DELAY_FASTEST);
            else
                Toast.makeText(getApplicationContext(), "Este dispositivo no dispone de los sensores necesarios", Toast.LENGTH_SHORT).show();
        }

        else
        {
            haveSensorRotation = sensorManager.registerListener(this, mRotationVectorSensor, SensorManager.SENSOR_DELAY_FASTEST);

        }*/

        started=true;


    }

    private void stop(){

        sensorManager.unregisterListener(sensorEventListener);
        sensorManager.unregisterListener(this,mAccelerometer);
        if(haveSensorMagnet)
            sensorManager.unregisterListener(this,mMagnetometerSensor);
        if(haveSensorRotation)
            sensorManager.unregisterListener(this,mRotationVectorSensor);

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


/*
    @Override
    public void onSensorChanged(SensorEvent event) {



        float orientation[] = new float[3];
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // Only use rotation vector sensor if it is working on this device
            if (!mUseRotationVectorSensor) {
                mUseRotationVectorSensor = true;
            }
            // calculate th rotation matrix
            SensorManager.getRotationMatrixFromVector( rMat, event.values );
            // get the azimuth value (orientation[0]) in degree
            mAzimuth =  (Math.toDegrees( SensorManager.getOrientation( rMat, orientation )[0] ) + 360 ) % 360;

        } else if (!mUseRotationVectorSensor && event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED){

            xx=Math.round(event.values[0]);
            yy=Math.round(event.values[1]);
            zz=Math.round(event.values[2]);

        }
        else if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //gData = event.values.clone();


            float x = event.values[0];


            if (x < primeroX && whip == 0) {
                whip++;
                min_x = 99;

                //getWindow().getDecorView().setBackgroundColor(Color.BLUE);
            } else if (x > segundoX && whip == 1) {//&& event.values[1]<-5){
                whip++;
                timestamp_uno = System.currentTimeMillis();
                x_uno = x;
                whip2 = 0;

                //getWindow().getDecorView().setBackgroundColor(Color.RED);
            } else if (whip == 2) {
                fuerza = x - x_uno / (System.currentTimeMillis() - timestamp_uno);
                txt_mAzimuth.setText("" + xx + "," + yy+","+zz);
                //if(min_x<-15) {

                    if (izq < 0) {
                        if(compararIzq==0)
                        {
                           if(Math.abs( Math.abs(xx)-Math.abs(calibrado_izq1x)) <Math.abs( Math.abs(xx)-Math.abs(calibrado_izq2x)))
                            izq1();
                           else
                               izq2();
                        }
                        if(compararIzq==1)
                        {
                            if(Math.abs( Math.abs(yy)-Math.abs(calibrado_izq1y)) <Math.abs( Math.abs(yy)-Math.abs(calibrado_izq2y)))
                                izq1();
                            else
                                izq2();
                        }
                        if(compararIzq==2)
                        {
                            if(Math.abs( Math.abs(zz)-Math.abs(calibrado_izq1z)) <Math.abs( Math.abs(zz)-Math.abs(calibrado_izq2z)))
                                izq1();
                            else
                                izq2();
                        }
                    }
                    else {
                        if(compararDer==0)
                        {
                            if(Math.abs( Math.abs(xx)-Math.abs(calibrado_der1x)) <Math.abs( Math.abs(xx)-Math.abs(calibrado_der2x)))
                                der1();
                            else
                                der2();
                        }
                        if(compararDer==1)
                        {
                            if(Math.abs( Math.abs(yy)-Math.abs(calibrado_der1y)) <Math.abs( Math.abs(yy)-Math.abs(calibrado_der2y)))
                                der1();
                            else
                                der2();
                        }
                        if(compararDer==2)
                        {
                            if(Math.abs( Math.abs(zz)-Math.abs(calibrado_der1z)) <Math.abs( Math.abs(zz)-Math.abs(calibrado_der2z)))
                                der1();
                            else
                                der2();
                        }
                    }


                whip++;
                whip2 = 0;

            } else if (whip == 3 && x < terceroX) {
                whip = 0;
                whip2 = 0;
                min_z = 99;
                min_x = 99;
            }
        }


    }*/

void calculaNumeros()
{
    primeroX=-5-sensibilidad;
    segundoX=6+sensibilidad;
    terceroX=6+sensibilidad;

    primeroZ=-8-sensibilidad;
    segundoZ=10+sensibilidad;
    terceroZ=10+sensibilidad;

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
                packet_der3 = new DatagramPacket("9".getBytes(), "9".length(), InetAddress.getByName(Opciones.ipServidor), Opciones.puerto);
                packet_izq3 = new DatagramPacket("0".getBytes(), "0".length(), InetAddress.getByName(Opciones.ipServidor), Opciones.puerto);



            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

private void afterCalibrate()
{
    alertDialogAndroid.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);


    double diff_izqx=Math.abs(Math.abs(calibrado_izq1x)-Math.abs(calibrado_izq2x));
    double diff_izqy=Math.abs(Math.abs(calibrado_izq1y)-Math.abs(calibrado_izq2y));
    double diff_izqz=Math.abs(Math.abs(calibrado_izq1z)-Math.abs(calibrado_izq2z));

    if (diff_izqx >= diff_izqy && diff_izqx >= diff_izqz)
        compararIzq=0;
    else if (diff_izqy >= diff_izqx && diff_izqy >= diff_izqz)
        compararIzq=1;
    else
        compararIzq=2;


    double diff_derx=Math.abs(Math.abs(calibrado_der1x)-Math.abs(calibrado_der2x));
    double diff_dery=Math.abs(Math.abs(calibrado_der1y)-Math.abs(calibrado_der2y));
    double diff_derz=Math.abs(Math.abs(calibrado_der1z)-Math.abs(calibrado_der2z));

    if (diff_derx >= diff_dery && diff_derx >= diff_derz)
        compararDer=0;
    else if (diff_dery >= diff_derx && diff_dery >= diff_derz)
        compararDer=1;
    else
        compararDer=2;


}


void construirChecks()
{


    CheckBox calibrar_der1 = ( CheckBox ) mView.findViewById( R.id.calibrar_der1 );
    calibrar_der1.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (((CheckBox) v).isChecked()) {
                calibrado_der1x=xx;
                calibrado_der1y=yy;
                calibrado_der1z=zz;
            }
            else
            {
                calibrado_der1x=-9999;
                calibrado_der1y=-9999;
                calibrado_der1z=-9999;
            }
            if(calibrado_der1x>-9998 && calibrado_der2x>-9998 &&calibrado_izq1x>-9998 &&calibrado_izq2x>-9998)
            {
                afterCalibrate();

            }
        }


    });
    CheckBox calibrar_der2 = ( CheckBox ) mView.findViewById( R.id.calibrar_der2 );
    calibrar_der2.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (((CheckBox) v).isChecked()) {
                calibrado_der2x=xx;
                calibrado_der2y=yy;
                calibrado_der2z=zz;
            }
            else
            {
                calibrado_der2x=-9999;
                calibrado_der2y=-9999;
                calibrado_der2z=-9999;
            }
            if(calibrado_der1x>-9998 && calibrado_der2x>-9998 &&calibrado_izq1x>-9998 &&calibrado_izq2x>-9998)
            {
                afterCalibrate();

            }
        }
    });
    CheckBox calibrar_izq1 = ( CheckBox ) mView.findViewById( R.id.calibrar_izq1 );
    calibrar_izq1.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (((CheckBox) v).isChecked()) {
                calibrado_izq1x=xx;
                calibrado_izq1y=yy;
                calibrado_izq1z=zz;
            }
            else
            {
                calibrado_izq1x=-9999;
                calibrado_izq1y=-9999;
                calibrado_izq1z=-9999;
            }
            if(calibrado_der1x>-9998 && calibrado_der2x>-9998 &&calibrado_izq1x>-9998 &&calibrado_izq2x>-9998)
            {
                afterCalibrate();

            }
        }
    });
    CheckBox calibrar_izq2 = ( CheckBox ) mView.findViewById( R.id.calibrar_izq2 );
    calibrar_izq2.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (((CheckBox) v).isChecked()) {
                calibrado_izq2x=xx;
                calibrado_izq2y=yy;
                calibrado_izq2z=zz;
            }
            else
            {
                calibrado_izq2x=-9999;
                calibrado_izq2y=-9999;
                calibrado_izq2z=-9999;
            }
            if(calibrado_der1x>-9998 && calibrado_der2x>-9998 &&calibrado_izq1x>-9998 &&calibrado_izq2x>-9998)
            {
                afterCalibrate();

            }
        }
    });


}


    @Override
    public void onSensorChanged(SensorEvent event) {

        float orientation[] = new float[3];



            float x = event.values[0];
        float z = event.values[2];
        if(!sonando1 && !sonando2 && !sonando3) {
            if (x > 20 + sensibilidad) {
                sonando1 = true;
                v.vibrate(50);
                if(izq==1)
                 der3();
                else
                    izq3();
            } else if (z < -20 - sensibilidad) {
                v.vibrate(50);

                sonando2 = true;
                if(izq==1)
                der1();
                else
                    izq1();
            } else if (z > 20 + sensibilidad) {
                v.vibrate(50);

                sonando3 = true;
                if(izq==1)
                der2();
                else
                    izq2();
            }
        }
        if(sonando1 && x<20+sensibilidad)
            sonando1=false;
        if(sonando2 && z>-20-sensibilidad)
            sonando2=false;
        if(sonando3 && z<20+sensibilidad)
            sonando3=false;

/*
            if (x < primeroX && whip == 0) {
                whip++;
                min_x = 99;

                //getWindow().getDecorView().setBackgroundColor(Color.BLUE);
            } else if (x > segundoX && whip == 1) {//&& event.values[1]<-5){
                whip++;
                timestamp_uno = System.currentTimeMillis();
                x_uno = x;
                whip2 = 0;

                //getWindow().getDecorView().setBackgroundColor(Color.RED);
            } else if (whip == 2) {
                fuerza = x - x_uno / (System.currentTimeMillis() - timestamp_uno);
                txt_mAzimuth.setText("" + xx + "," + yy+","+zz);
                //if(min_x<-15) {

                if (izq < 0) {
                    if(compararIzq==0)
                    {
                        if(Math.abs( Math.abs(xx)-Math.abs(calibrado_izq1x)) <Math.abs( Math.abs(xx)-Math.abs(calibrado_izq2x)))
                            izq1();
                        else
                            izq2();
                    }
                    if(compararIzq==1)
                    {
                        if(Math.abs( Math.abs(yy)-Math.abs(calibrado_izq1y)) <Math.abs( Math.abs(yy)-Math.abs(calibrado_izq2y)))
                            izq1();
                        else
                            izq2();
                    }
                    if(compararIzq==2)
                    {
                        if(Math.abs( Math.abs(zz)-Math.abs(calibrado_izq1z)) <Math.abs( Math.abs(zz)-Math.abs(calibrado_izq2z)))
                            izq1();
                        else
                            izq2();
                    }
                }
                else {
                    if(compararDer==0)
                    {
                        if(Math.abs( Math.abs(xx)-Math.abs(calibrado_der1x)) <Math.abs( Math.abs(xx)-Math.abs(calibrado_der2x)))
                            der1();
                        else
                            der2();
                    }
                    if(compararDer==1)
                    {
                        if(Math.abs( Math.abs(yy)-Math.abs(calibrado_der1y)) <Math.abs( Math.abs(yy)-Math.abs(calibrado_der2y)))
                            der1();
                        else
                            der2();
                    }
                    if(compararDer==2)
                    {
                        if(Math.abs( Math.abs(zz)-Math.abs(calibrado_der1z)) <Math.abs( Math.abs(zz)-Math.abs(calibrado_der2z)))
                            der1();
                        else
                            der2();
                    }
                }


                whip++;
                whip2 = 0;

            } else if (whip == 3 && x < terceroX) {
                whip = 0;
                whip2 = 0;
                min_z = 99;
                min_x = 99;
            }

*/

    }
}