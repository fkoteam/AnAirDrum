package com.fkoteam.anairdrum;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.ArrayList;

class MediaPlayers {
    private Context mContext;



    private static boolean  pie_izq_pulsado = false;

    private SoundPool soundpool;
    private  int izq1;
    private  int izq2;
    private  int der1cl;
    private  int der1op;
    private  int der2;
    private  int pie_der;
    private  int pie_izq_cer;
    private  int der3;
    private  int izq3;
    private ArrayList<Integer> list_der1op = new ArrayList<>();



    MediaPlayers(Context applicationContext) {
        mContext = applicationContext;
        soundpool = new SoundPool(50, AudioManager.STREAM_MUSIC, 0);

        izq1 = soundpool.load(mContext, mContext.getResources().getIdentifier("izq1", "raw", mContext.getPackageName()), 1);
        izq2 = soundpool.load(mContext, mContext.getResources().getIdentifier("izq2", "raw", mContext.getPackageName()), 1);
        der1op = soundpool.load(mContext, mContext.getResources().getIdentifier("der1op", "raw", mContext.getPackageName()), 1);
        der1cl = soundpool.load(mContext, mContext.getResources().getIdentifier("der1cl", "raw", mContext.getPackageName()), 1);
        pie_der = soundpool.load(mContext, mContext.getResources().getIdentifier("pie_der", "raw", mContext.getPackageName()), 1);
        pie_izq_cer = soundpool.load(mContext, mContext.getResources().getIdentifier("pie_izq_cer", "raw", mContext.getPackageName()), 1);
        der2 = soundpool.load(mContext, mContext.getResources().getIdentifier("der2", "raw", mContext.getPackageName()), 1);
        der3 = soundpool.load(mContext, mContext.getResources().getIdentifier("der3", "raw", mContext.getPackageName()), 1);
        izq3 = soundpool.load(mContext, mContext.getResources().getIdentifier("izq3", "raw", mContext.getPackageName()), 1);


    }

    void pie_der() {
        soundpool.play(pie_der, 1f, 1f, 1, 0, 1f);
    }

    void izq1() {
        soundpool.play(izq1, 1f, 1f, 1, 0, 1f);

    }

    void izq2() {
        soundpool.play(izq2, 1f, 1f, 1, 0, 1f);
    }
    void izq3() {
        soundpool.play(izq3, 1f, 1f, 1, 0, 1f);
    }
    void der3() {
        soundpool.play(der3, 1f, 1f, 1, 0, 1f);
    }

    void pie_izq() {

        soundpool.play(pie_izq_cer, 1f, 1f, 1, 0, 1f);
        for(Integer i : list_der1op) {
            soundpool.stop(i.intValue());


        }
        list_der1op.clear();
    }


    void der1_op() {
        if(pie_izq_pulsado) {
            Integer i=new Integer(soundpool.play(der1op, 1f, 1f, 1, 0, 1f));
            list_der1op.add(i);

        }
        else
        {
            soundpool.play(der1cl, 1f, 1f, 1, 0, 1f);
        }


    }

    void der1_cl() {

        soundpool.play(der1cl, 1f, 1f, 1, 0, 1f);



    }


    void der2(){

        soundpool.play(der2, 1f, 1f, 1, 0, 1f);



    }


    static boolean isPie_izq_pulsado() {
        return pie_izq_pulsado;
    }

    static void setPie_izq_pulsado(boolean pie_izq_pulsado) {
        MediaPlayers.pie_izq_pulsado = pie_izq_pulsado;
    }

}