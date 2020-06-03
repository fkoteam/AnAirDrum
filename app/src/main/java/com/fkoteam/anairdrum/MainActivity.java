package com.fkoteam.anairdrum;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.StrictMode;
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

import com.esotericsoftware.kryo.Kryo;
import com.fkoteam.anairdrum.objects.UpdateObject;


import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import ru.maklas.mnet2.BroadcastProcessor;
import ru.maklas.mnet2.BroadcastServlet;
import ru.maklas.mnet2.Connection;
import ru.maklas.mnet2.ServerAuthenticator;
import ru.maklas.mnet2.ServerResponse;
import ru.maklas.mnet2.ServerSocket;
import ru.maklas.mnet2.Socket;
import ru.maklas.mnet2.SocketImpl;
import ru.maklas.mnet2.SocketProcessor;
import ru.maklas.mnet2.serialization.KryoSerializer;
import ru.maklas.mnet2.serialization.Serializer;

public class MainActivity extends AppCompatActivity implements SensorEventListener , ServerAuthenticator {
    float[] rMat = new float[9];

    ServerSocket serverSocket;

    SensorManager sensorManager;
    private Sensor mAccelerometer;
    TextView txt_mAzimuth;
    RadioGroup radioGroup;

    public static MediaPlayers mediaplayers;
    ru.maklas.mnet2.Socket client;



    Vibrator v;
    boolean arriba = true;


    int compararDer = -1;
    int compararIzq = -1;
    boolean sonandoA = false, sonandoB = false, sonandoC = false;


    double fuerza;
    boolean haveSensor = false, haveGravity = false;


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
    public static DataOutputStream out;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Preferencias.init(getApplicationContext());
        if (mediaplayers == null)
            mediaplayers = new MediaPlayers(getApplicationContext());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
          /*  solo para log de la respuesta
          BufferedReader in =
                    new BufferedReader( new InputStreamReader( socket.getInputStream() ) );*/



        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        radioGroup = findViewById(R.id.radioGroup);

        final Button button = findViewById(R.id.pedal_der);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!Opciones.cliente || Opciones.offline)
                    mediaplayers.pie_der();
                else
                {
                    /*if (Opciones.modo_tcp) {
                            clienteTCP.enviar("6");
                    }
                    else
                    {
                        if (Opciones.modo_thread)
                            //new Client(packet_pie_der).start();
                            clienteUDP.enviar(packet_pie_der);
                        else
                            cl_pie_der.enviar();
                    }*/
                    client.sendUnreliable(new EntityUpdate(6)); ;// sends data unreliably and unordered.

                }


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
                        {
                           /* if (Opciones.modo_tcp)
                                clienteTCP.enviar("7");
                            else {
                                if (Opciones.modo_thread)
                                    //new Client(packet_pie_izq_pulsado).start();
                                    clienteUDP.enviar(packet_pie_izq_pulsado);
                                else
                                    cl_pie_izq_pulsado.enviar();
                            }*/
                            client.sendUnreliable(new EntityUpdate(7)) ;// sends data unreliably and unordered.

                        }


                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        mediaplayers.setPie_izq_pulsado(false);
                        if (!Opciones.cliente) {
                            mediaplayers.pie_izq();
                        } else
                        {
                            /*if (Opciones.modo_tcp)
                                clienteTCP.enviar("8");
                            else {
                                if (Opciones.modo_thread)
                                    //new Client(packet_pie_izq_no_pulsado).start();
                                    clienteUDP.enviar(packet_pie_izq_no_pulsado);
                                else
                                    cl_pie_izq_no_pulsado.enviar();
                            }*/
                            client.sendUnreliable(new EntityUpdate(8)) ;// sends data unreliably and unordered.


                        }
                    }

                }
                return true;
            }


        });
        final Button volver = findViewById(R.id.volver);
        volver.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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


      /*solo en onresume try {
            if (!Opciones.cliente && chatserver == null && !Opciones.offline) {
                if(Opciones.modo_tcp && serverTcp==null)
                {
                    System.out.println("puertooo2 "+Opciones.puerto);

                    serverTcp = new ServerTcp(Opciones.puerto);
                    serverTcp.start();
                }
                if(!Opciones.modo_tcp && chatserver==null)

                {
                    System.out.println("puertooo3 "+Opciones.puerto);

                    chatserver = new Server(Opciones.puerto);
               chatserver.start();
           }

            }
           if (Opciones.cliente && clienteUDP == null && !Opciones.offline && !Opciones.modo_tcp) {
               clienteUDP = new Client2();
               clienteUDP.start();
           }
               if (Opciones.cliente && clienteTCP == null && !Opciones.offline && Opciones.modo_tcp) {
                   clienteTCP = new ClientTcp(Opciones.ipServidor,Opciones.puerto);
                   clienteTCP.start();


           }

        } catch (IOException e) {
            //e.printStackTrace();
        }*/

    }


    private void izq1() {
        if (!Opciones.cliente || Opciones.offline) {
            mediaplayers.izq1();
        } else
        {
            /*if (Opciones.modo_tcp)
                clienteTCP.enviar("4");
            else
            {
                if (Opciones.modo_thread)

                    //new Client(packet_izq1).start();
                    clienteUDP.enviar(packet_izq1);
                else
                    cl_izq1.enviar();
            }*/
            client.sendUnreliable(new EntityUpdate(4)) ;// sends data unreliably and unordered.

        }


    }

    private void izq2() {
        if (!Opciones.cliente || Opciones.offline) {
            mediaplayers.izq2();
        } else
        {
            /*if (Opciones.modo_tcp)
                clienteTCP.enviar("5");
            else
            {
                if (Opciones.modo_thread)
                    //new Client(packet_izq2).start();
                    clienteUDP.enviar(packet_izq2);
                else
                    cl_izq2.enviar();
            }*/
            client.sendUnreliable(new EntityUpdate(5)) ;// sends data unreliably and unordered.

        }

    }


    private void der1() {
        if (!Opciones.cliente || Opciones.offline) {
            if (mediaplayers.isPie_izq_pulsado())
                mediaplayers.der1_op();
            else
                mediaplayers.der1_cl();

        } else {
            /*if (Opciones.modo_tcp)
                clienteTCP.enviar("2");
            else
            {
                if (Opciones.modo_thread)
                    clienteUDP.enviar(packet_der1op);
                else
                    cl_der1op.enviar();
            }*/
            client.sendUnreliable(new EntityUpdate(2)) ;// sends data unreliably and unordered.

        }


    }

    private void der2() {
        if (!Opciones.cliente || Opciones.offline) {
            mediaplayers.der2();

        } else
        {
            /*if (Opciones.modo_tcp)
                clienteTCP.enviar("3");
            else
            {
                if (Opciones.modo_thread)
                    clienteUDP.enviar(packet_der2);


                else
                    cl_der2.enviar();
            }*/
            client.sendUnreliable(new EntityUpdate(3)) ;// sends data unreliably and unordered.

        }


    }

    private void der3() {
        if (!Opciones.cliente || Opciones.offline) {
                mediaplayers.der3();


        } else {
            /*if (Opciones.modo_tcp)
                clienteTCP.enviar("9");
            else
            {
                if (Opciones.modo_thread)
                    clienteUDP.enviar(packet_der3);
                else
                    cl_der3.enviar();
            }*/
            client.sendUnreliable(new EntityUpdate(9)) ;// sends data unreliably and unordered.

        }


    }

    private void izq3() {
        if (!Opciones.cliente || Opciones.offline) {
            mediaplayers.izq3();

        } else
        {
            /*if (Opciones.modo_tcp)
                clienteTCP.enviar("0");
            else
            {
                if (Opciones.modo_thread)
                    //asi se hacia antes: new Client(packet_izq3).start();
                    clienteUDP.enviar(packet_izq3);
                else
                    cl_izq3.enviar();
            }*/
            client.sendUnreliable(new EntityUpdate(0)) ;// sends data unreliably and unordered.

        }


    }


    private void start() {
        if (!started) {
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

        if (haveGravity)
            sensorManager.unregisterListener(this, mGravity);


        started = false;

    }

    @Override
    protected void onPause() {
        stop();
        super.onPause();

if(serverSocket!=null)
    serverSocket.close();
if(client!=null)
    client.close();

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
        if (check != R.id.mano_der && check != R.id.mano_izq && check != R.id.pie_der && check != R.id.pie_izq) {
            check = R.id.mano_der;
            Preferencias.write(Preferencias.MODO, check);//save string in shared preference.

        }

        radioGroup.check(check);
        gestionaCheck(check);//read string in shared preference);


        int sensibilidad_priv_a = Preferencias.read(Preferencias.SENSIBILIDADA, sensibilidadA);
        sensibilidadA = sensibilidad_priv_a;
        txt_progresoA.setText(getString(R.string.titulo_sensibilidad, getString(R.string.izquierda), sensibilidad_priv_a));

        seekBarA.setProgress(sensibilidadA - min_value);

        int sensibilidad_priv_b = Preferencias.read(Preferencias.SENSIBILIDADB, sensibilidadB);
        sensibilidadB = sensibilidad_priv_b;
        txt_progresoB.setText(getString(R.string.titulo_sensibilidad, getString(R.string.centro), sensibilidad_priv_b));

        seekBarB.setProgress(sensibilidadB - min_value);

        int sensibilidad_priv_c = Preferencias.read(Preferencias.SENSIBILIDADC, sensibilidadC);
        sensibilidadC = sensibilidad_priv_c;
        txt_progresoC.setText(getString(R.string.titulo_sensibilidad, getString(R.string.derecha), sensibilidad_priv_c));

        seekBarC.setProgress(sensibilidadC - min_value);

        //calculaNumeros();

        try {
            if(!Opciones.cliente && !Opciones.offline)
            {
                ServerSocket serverSocket = TestUtils.newServerSocket(TestUtils.udp(Opciones.puerto, 0, 0), this);
                TestUtils.startUpdating(serverSocket, 1); //TODO probar diferentes valores. por defecto, 16
               // BroadcastServlet servlet = new BroadcastServlet(7368, 512, "uuid", TestUtils.serializerSupplier.get(), this);


            }
            if(Opciones.cliente && !Opciones.offline)
            {
                client = new SocketImpl(InetAddress.getByName(Opciones.ipServidor), Opciones.puerto, TestUtils.serializerSupplier.get());


                ServerResponse response = client.connect(new ConnectionRequest("maklas"), 5_000);

                //Here is our response object that Server replied with. Check it for being NULL just in case.
                ConnectionResponse connResp = (ConnectionResponse) response.getResponse();

//There is 4 types of possible outcomes during connection.
//The only time we can be sure to be connected is when ResponseType == ACCEPTED.
//In any other case, socket is not connected.
                switch (response.getType()){
                    case ACCEPTED:
                        System.out.println("Successfully connected with message " + connResp.getMessage());
                        break;
                    case REJECTED:
                        System.out.println("Server rejected our request with message " + connResp.getMessage());
                        break;
                    case NO_RESPONSE:
                        System.out.println("Server doesn't respond");
                        break;
                    case WRONG_STATE:
                        System.out.println("Socket was closed or was already connected");
                        break;
                }
            }
            /*if (!Opciones.cliente && !Opciones.offline) {
                if (!Opciones.modo_tcp) {
                    chatserver = new ServerUdp(Opciones.puerto);
                    chatserver.start();
                }
                if (Opciones.modo_tcp) {

                    serverTcp = new ServerTcp(Opciones.puerto);
                    serverTcp.start();
                }


            }
            if (Opciones.cliente && !Opciones.offline && !Opciones.modo_tcp) {

                clienteUDP = new ClientThread();
                clienteUDP.start();

            }
            if (Opciones.cliente && !Opciones.offline && Opciones.modo_tcp ) {
                clienteTCP = new ClientTcp(Opciones.ipServidor, Opciones.puerto);
                clienteTCP.start();

            }*/


        } catch (IOException e) {
            e.printStackTrace();

        }
        String log = (haveGravity ? getString(R.string.usando_gravedad) : getString(R.string.no_usando_gravedad));

        if (!Opciones.offline && Opciones.cliente)
            log = log + " " + getString(R.string.aviso_cliente);
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float z = event.values[2];


            if (z < -20 - sensibilidadA && !sonandoA && !sonandoB && !sonandoC && (!arriba || !haveGravity)) {

                sonandoA = true;
                if (izq == 1)
                    der1();
                else
                    izq1();

                if (!Opciones.no_vibracion)
                    v.vibrate(50);

            }


            if (x > 20 + sensibilidadB && !sonandoA && !sonandoB && !sonandoC) {
                sonandoB = true;
                if (izq == 1)
                    der3();
                else
                    izq3();

                if (!Opciones.no_vibracion)
                    v.vibrate(50);
            }
            if (z > 20 + sensibilidadC && !sonandoA && !sonandoB && !sonandoC && (arriba || !haveGravity)) {

                sonandoC = true;
                if (izq == 1)
                    der2();
                else
                    izq2();

                if (!Opciones.no_vibracion)
                    v.vibrate(50);

            }

            if (sonandoA && z > 0)
                sonandoA = false;
            if (sonandoB && x < 0)
                sonandoB = false;
            if (sonandoC && z < 0)
                sonandoC = false;


        } else if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            if (event.values[2] > 0) arriba = true;
            else arriba = false;


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



}
