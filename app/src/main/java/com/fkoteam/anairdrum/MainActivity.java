package com.fkoteam.anairdrum;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;

import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity implements SensorEventListener  {
    SensorManager sensorManager;
    private Sensor mRotationV, mAccelerometer, mMagnetometer;
    TextView txt_mAzimuthIni;
    TextView txt_mAzimuth;
    RadioGroup radioGroup;
    RadioButton radioButton;
    MediaPlayer izq1_1,izq1_2,izq1_3,izq2_1,izq2_2,izq2_3,der1_1,der1_2,der1_3,der2_1,der2_2,der2_3,pie_der1,pie_der2,pie_der3;

    Long timestamp_uno,timestamp_dos;
    double x_uno,x_dos;
    double z_uno,z_dos;
    int mAzimuth;
    int mAzimuthIni=-9999;
    double mAzimuth_f=0.0, mAzimuth_f_media,mAzimuth_f_1,mAzimuth_f_2,mAzimuth_f_3;
    float mAzimuthIni_f=-9999;
    double mAzimuth_f_arr[]=new double[4];
    double fuerza;
    boolean haveSensor = false, haveSensor2 = false, haveSensor3 = false;
    float[] rMat = new float[9];
    float[] orientation = new float[3];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    int index=0;
    double min_z=99,max_z=0;
    double min_x=99,max_x=0;
    // - 1  mano izq, 1 mano derecha
    double izq=1;
    TextView txt_progreso;
    double min_value=1,sensibilidad=1,primeroX,segundoX,terceroX,primeroZ,segundoZ,terceroZ;
    boolean started=false;

    String log="";
    String parametro="2";

    SensorEventListener sensorEventListener;
    int whip = 0;
    int whip2 = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        radioGroup = findViewById(R.id.radioGroup);
        izq1_1 = MediaPlayer.create(this, R.raw.izq1);
        izq1_2 = MediaPlayer.create(this, R.raw.izq1);
        izq1_3 = MediaPlayer.create(this, R.raw.izq1);
        der1_1 = MediaPlayer.create(this, R.raw.der1);
        der1_2 = MediaPlayer.create(this, R.raw.der1);
        der1_3 = MediaPlayer.create(this, R.raw.der1);
        izq2_1 = MediaPlayer.create(this, R.raw.izq2);
        izq2_2 = MediaPlayer.create(this, R.raw.izq2);
        izq2_3 = MediaPlayer.create(this, R.raw.izq2);
        der2_1 = MediaPlayer.create(this, R.raw.der2);
        der2_2 = MediaPlayer.create(this, R.raw.der2);
        der2_3 = MediaPlayer.create(this, R.raw.der2);
        pie_der1 = MediaPlayer.create(this, R.raw.pie_der);
        pie_der2 = MediaPlayer.create(this, R.raw.pie_der);
        pie_der3 = MediaPlayer.create(this, R.raw.pie_der);

        final Button button = findViewById(R.id.pedal);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(!Opciones.cliente)
                    pie_der();
                else
                    if(!Opciones.tcp)
                        new com.fkoteam.anairdrum.udp.Client(getApplicationContext(),Opciones.ipServidor, Opciones.puerto).send("pie_der");
                    else
                        new com.fkoteam.anairdrum.tcp.Client(Opciones.ipServidor, Opciones.puerto);



            }
        });
        final Button volver = findViewById(R.id.volver);
        volver.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                izq=1;
                calculaNumeros();
                findViewById(R.id.volver).setVisibility(View.GONE);
                findViewById(R.id.pedal).setVisibility(View.GONE);
                radioGroup.check(R.id.mano_der);
                if(!started)
                    start();
            }
        });
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        txt_progreso = (TextView) findViewById(R.id.txt_progreso);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sensibilidad=progress+min_value;
                txt_progreso.setText("" + sensibilidad );

                calculaNumeros();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        calculaNumeros();
        txt_mAzimuth = (TextView) findViewById(R.id.txt_mAzimuth);
        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);

        /*sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(sensor==null)
            finish();*/



/*
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float x = sensorEvent.values[0];
                if(x<-5 && whip == 0){
                    whip++;
                    getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                }else if (x>5 && whip == 1){
                    whip++;
                    getWindow().getDecorView().setBackgroundColor(Color.RED);
                }

                if(whip==2){
                    song();
                    whip=0;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };*/
        start();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                //TCP client and server (Client will automatically send welcome message after setup and server will respond)
                //new com.fkoteam.anairdrum.tcp.Server("localhost", 7000);
                //new com.fkoteam.anairdrum.tcp.Client("localhost", 7000);

                if(!Opciones.cliente && Opciones.tcp)
                    new com.fkoteam.anairdrum.tcp.Server("localhost", Opciones.puerto);


                    //UDP client and server (Here the client explicitly sends a message)
                if(!Opciones.cliente && !Opciones.tcp)
                    new com.fkoteam.anairdrum.udp.Server(getApplicationContext(),"localhost", Opciones.puerto);
                return null;
            }
        }.execute();
    }

    private void pie_der(){

        if(pie_der1.isPlaying())
        {
            if(pie_der2.isPlaying())
                pie_der3.start();
            else
                pie_der2.start();
        }
        else
            pie_der1.start();


    }
    private void izq1(){

        if(izq1_1.isPlaying())
        {
            if(izq1_2.isPlaying())
                izq1_3.start();
            else
                izq1_2.start();
        }
        else
            izq1_1.start();


    }

    private void izq2(){

        if(izq2_1.isPlaying())
        {
            if(izq2_2.isPlaying())
                izq2_3.start();
            else
                izq2_2.start();
        }
        else
            izq2_1.start();


    }

    private void der1(){

        if(der1_1.isPlaying())
        {
            if(der1_2.isPlaying())
                der1_3.start();
            else
                der1_2.start();
        }
        else
            der1_1.start();


    }

    private void der2(){

        if(der2_1.isPlaying())
        {
            if(der2_2.isPlaying())
                der2_3.start();
            else
                der2_2.start();
        }
        else
            der2_1.start();


    }

   // private void song2(){
     /*   if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
        mediaPlayer.start();*/
  /*   Intent pp=new Intent(this, Golpe.class);
        pp.putExtra("parametro",parametro);
        startService(pp);*/

      /*  if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.dos);
        mediaPlayer.start();*/
       /* if(mediaPlayer.isPlaying())
            mediaPlayer2.start();
        else
            mediaPlayer.start();*/


  //  }

    private void start(){
     //   sensorManager.registerListener(sensorEventListener, mAccelerometer, sensorManager.SENSOR_DELAY_FASTEST);
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        haveSensor = sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        started=true;

/*
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            if ((sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) || (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null)) {
                noSensorsAlert();
            }
            else {
                mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                haveSensor = sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
                haveSensor2 = sensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_FASTEST);
            }
        }
        else{
            mRotationV = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor = sensorManager.registerListener(this, mRotationV, SensorManager.SENSOR_DELAY_FASTEST);
            mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            haveSensor3 = sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        }*/

    }

    private void stop(){

        sensorManager.unregisterListener(sensorEventListener);
        sensorManager.unregisterListener(this,mAccelerometer);
        started=false;
        /*
        sensorManager.unregisterListener(this,mMagnetometer);

        if(haveSensor && haveSensor2){
            sensorManager.unregisterListener(this,mAccelerometer);
            sensorManager.unregisterListener(this,mMagnetometer);
        }
        else{
            if(haveSensor)
                sensorManager.unregisterListener(this,mRotationV);
                sensorManager.unregisterListener(this,mAccelerometer);

        }*/
    }

    @Override
    protected void onPause() {
        stop();
        super.onPause();
    }

    @Override
    protected void onResume() {
        start();
        super.onResume();
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
/*
    public void noSensorsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Your device doesn't support the Compass.")
                .setCancelable(false)
                .setNegativeButton("Close",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        alertDialog.show();
    }*/


    @Override
    public void onSensorChanged(SensorEvent event) {
        //if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
           // SensorManager.getRotationMatrixFromVector(rMat, event.values);
           // mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;

         /*   mAzimuth_f=event.values[2]*100.0;
            mAzimuth_f_1=event.values[1]*100.0;
            log =log+(mAzimuth_f_1-mAzimuth_f)+"\n";*/
          /*  mAzimuth_f_arr[index]=event.values[2]*100.0;
            mAzimuth_f_arr[index+2]=event.values[1]*100.0;

            if(index<1)
            {
                index++;
            }
            else
            {
                index=0;

            }*/
        // log =log+"r: "+mAzimuth_f+"\n";
      //  }

     //   if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

           // System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
            double x = event.values[0]*izq;
        float z = event.values[2];
          //  log =log+"a: "+x+"\n";


            if(x<primeroX && whip == 0){
                whip++;
                min_x=99;

                //getWindow().getDecorView().setBackgroundColor(Color.BLUE);
            }else if (x>segundoX && whip == 1 ){//&& event.values[1]<-5){
                whip++;
                timestamp_uno = System.currentTimeMillis();
                x_uno=x;
                z_uno=z;
                whip2=0;

                //getWindow().getDecorView().setBackgroundColor(Color.RED);
            }else
            if(whip==2)
            {
                fuerza = x-x_uno/(System.currentTimeMillis()-timestamp_uno);
                txt_mAzimuth.setText(""+x+"\n"+z );
                //if(min_x<-15) {
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
            //getWindow().getDecorView().setBackgroundColor(Color.BLUE);
        }else if (z<segundoZ && whip2 == 1 ){
            whip2++;
            timestamp_dos = System.currentTimeMillis();
            z_uno=z;
            whip=0;
            x_uno=x;

            //getWindow().getDecorView().setBackgroundColor(Color.RED);
        }else
        if(whip2==2)
        {
            fuerza = z-z_uno/(System.currentTimeMillis()-timestamp_dos);
            txt_mAzimuth.setText(""+x+"\n"+z );
            //if(min_x>-15) {

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



  /*bueno          if(whip>0) {

                if (max_x < Math.abs(event.values[0]))
                    max_x = Math.abs(event.values[0]);

                if (max_z < Math.abs(event.values[2]))
                    max_z = Math.abs(event.values[2]);
            } fin bueno*/




           /* float z = event.values[2];


            if(z<-8 && whip2 == 0 && !tratado){
                whip2++;
                tratado=true;
                log =log+"a: "+z+"-1\n";
                //getWindow().getDecorView().setBackgroundColor(Color.BLUE);
            }
            if(z<-8 && whip2 == 1 && ! tratado) {
                whip2++;
                tratado=true;
                log =log+"a: "+z+"-2\n";

            }

            if (z>10 && whip2 == 2 && !tratado){
                parametro="2";
                whip2=0;
                tratado=true;
                log =log+"a: "+z+"-3\n";


            }

                if (z >10 && whip2 == 0 && !tratado) {
                    whip2=3;
                    tratado=true;
                    log =log+"a: "+z+"-4\n";

                    //getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                }
            if (z >10 && whip2 == 3 && !tratado) {
                whip2++;
                tratado=true;
                log =log+"a: "+z+"-5\n";

                //getWindow().getDecorView().setBackgroundColor(Color.BLUE);
            }

            if (z <-8 && whip2 == 4 && !tratado) {
                    parametro = "3";
                    whip2 = 0;
                tratado=true;
                log =log+"a: "+z+"-6 \n";


            }
           /* if(!tratado)
            {
                whip2=0;
            }*/
     /*   } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            log =log+"m: "+event.values[0]+"\n";

            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(rMat, orientation);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }*/
     /*   if(mAzimuthIni_f<-9998 && (mAzimuth_f>0.001 || mAzimuth_f<-0.001))
        {
            //mAzimuthIni_f=mAzimuth_f;
            txt_mAzimuthIni.setText("Inicial: "+mAzimuthIni_f + "° " );
        }*/
        //txt_mAzimuth.setText(mAzimuth_f + "° " );




     /*   if(whip==3){


            whip2=0;

            log =log+"min_x: "+ min_x+" max_x: "+max_x+"\n";
            log =log+"min_z: "+ min_z+" max_z: "+max_z+"\n";

            txt_mAzimuth.setText(log );

            if(max_z>max_x)
                song1();
            else
                song2();
            whip=0;
            log="";
            index=0;
            mAzimuth_f=0.0;
             min_z=100;
             max_z=-99;
             min_x=100;
             max_x=-99;
        }*/
   /*  if(whip==3)
     {
         song1();
         whip=0;
         whip2=0;
     }else
         if(whip2==3)
         {
             song2();
             whip=0;
             whip2=0;
         }*/

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
        if(radioGroup.getCheckedRadioButtonId() == findViewById(R.id.mano_der).getId()) {

            izq=1;
            calculaNumeros();
            findViewById(R.id.volver).setVisibility(View.GONE);
            findViewById(R.id.pedal).setVisibility(View.GONE);
            if(!started)
                start();
        }
        if(radioGroup.getCheckedRadioButtonId() == findViewById(R.id.mano_izq).getId()) {

            izq=-1;
            calculaNumeros();

            findViewById(R.id.volver).setVisibility(View.GONE);
            findViewById(R.id.pedal).setVisibility(View.GONE);
            if(!started)
                start();


        }
        if(radioGroup.getCheckedRadioButtonId() == findViewById(R.id.pie_der).getId()) {
            findViewById(R.id.volver).setVisibility(View.VISIBLE);
            findViewById(R.id.pedal).setVisibility(View.VISIBLE);
            if(started)
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
}