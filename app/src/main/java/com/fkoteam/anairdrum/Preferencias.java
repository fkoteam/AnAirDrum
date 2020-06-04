package com.fkoteam.anairdrum;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

class Preferencias {
    static final String IP_SERVIDOR = "192.168.1.1";
    static final String PUERTO = "7001";
    static final String OFFLINE = "IS_ONLINE";
    static final String CLIENTE = "IS_CLIENTE";
    static final String MODO = "";
    static final String SENSIBILIDADA = "SENSIBILIDADA";
    static final String SENSIBILIDADB = "SENSIBILIDADB";
    static final String SENSIBILIDADC = "SENSIBILIDADC";
    static final String NO_GRAVITY = "NO_GRAVITY";
    static final String NO_VIBRACION = "NO_VIBRACION";




    private static SharedPreferences mSharedPref;

    private Preferencias()
    {

    }

    static void init(Context context)
    {
        if(mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }

    static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    static void write(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    static Integer read(String key, int defValue) {
        return mSharedPref.getInt(key, defValue);
    }

    static void write(String key, Integer value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putInt(key, value).commit();
    }
}