package com.fkoteam.anairdrum;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.AsyncSocket;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.Util;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.ConnectCallback;
import com.koushikdutta.async.callback.DataCallback;

import java.net.InetSocketAddress;

public class Preferencias {
    public static final String IP_SERVIDOR = "192.168.1.1";
    public static final String PUERTO = "7001";
    public static final String OFFLINE = "IS_ONLINE";
    public static final String CLIENTE = "IS_CLIENTE";
    public static final String MODO = "";
    public static final String SENSIBILIDADA = "SENSIBILIDADA";
    public static final String SENSIBILIDADB = "SENSIBILIDADB";
    public static final String SENSIBILIDADC = "SENSIBILIDADC";
    public static final String NO_GRAVITY = "NO_GRAVITY";
    public static final String NO_VIBRACION = "NO_VIBRACION";




    private static SharedPreferences mSharedPref;

    private Preferencias()
    {

    }

    public static void init(Context context)
    {
        if(mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    public static void write(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    public static Integer read(String key, int defValue) {
        return mSharedPref.getInt(key, defValue);
    }

    public static void write(String key, Integer value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putInt(key, value).commit();
    }
}