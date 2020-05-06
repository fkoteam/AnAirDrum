package com.fkoteam.anairdrum;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

import java.io.IOException;

public class MediaPlayers {
    Context mContext;
    public static boolean  pie_izq_pulsado = false;

    MediaPlayer izq1_1, izq1_2, izq1_3, izq2_1, izq2_2, izq2_3, der1op_1, der1op_2, der1op_3, der1cl_1, der1cl_2, der1cl_3, der2_1, der2_2, der2_3, pie_der1, pie_der2, pie_der3, pie_izq_cerr1, pie_izq_cerr2, pie_izq_cerr3;



    public MediaPlayers(Context applicationContext) {
        mContext = applicationContext;

        izq1_1 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("izq1", "raw", mContext.getPackageName()));
        izq1_2 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("izq1", "raw", mContext.getPackageName()));
        izq1_3 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("izq1", "raw", mContext.getPackageName()));
        der1op_1 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("der1op", "raw", mContext.getPackageName()));
        der1op_2 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("der1op", "raw", mContext.getPackageName()));
        der1op_3 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("der1op", "raw", mContext.getPackageName()));
        der1cl_1 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("der1cl", "raw", mContext.getPackageName()));
        der1cl_2 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("der1cl", "raw", mContext.getPackageName()));
        der1cl_3 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("der1cl", "raw", mContext.getPackageName()));
        izq2_1 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("izq2", "raw", mContext.getPackageName()));
        izq2_2 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("izq2", "raw", mContext.getPackageName()));
        izq2_3 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("izq2", "raw", mContext.getPackageName()));
        der2_1 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("der2", "raw", mContext.getPackageName()));
        der2_2 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("der2", "raw", mContext.getPackageName()));
        der2_3 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("der2", "raw", mContext.getPackageName()));
        pie_der1 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("pie_der", "raw", mContext.getPackageName()));
        pie_der2 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("pie_der", "raw", mContext.getPackageName()));
        pie_der3 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("pie_der", "raw", mContext.getPackageName()));
        pie_izq_cerr1 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("pie_izq_cer", "raw", mContext.getPackageName()));
        pie_izq_cerr2 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("pie_izq_cer", "raw", mContext.getPackageName()));
        pie_izq_cerr3 = MediaPlayer.create(mContext, mContext.getResources().getIdentifier("pie_izq_cer", "raw", mContext.getPackageName()));


    }


    public void pie_der() {

        if (pie_der1.isPlaying()) {
            if (pie_der2.isPlaying())
                pie_der3.start();
            else
                pie_der2.start();
        } else
            pie_der1.start();


    }

    public void izq1() {

        if (izq1_1.isPlaying()) {
            if (izq1_2.isPlaying())
                izq1_3.start();
            else
                izq1_2.start();
        } else
            izq1_1.start();


    }

    public void izq2() {

        if (izq2_1.isPlaying()) {
            if (izq2_2.isPlaying())
                izq2_3.start();
            else
                izq2_2.start();
        } else
            izq2_1.start();


    }

    public void pie_izq() {

        if (pie_izq_cerr1.isPlaying()) {
            if (pie_izq_cerr2.isPlaying())
                pie_izq_cerr3.start();
            else
                pie_izq_cerr2.start();
        } else
            pie_izq_cerr1.start();
        try {
        if(der1op_1.isPlaying()) {
            der1op_1.stop();
            der1op_1.prepare();

        }
        if(der1op_2.isPlaying()) {
            der1op_2.stop();
            der1op_2.prepare();

        }
        if(der1op_3.isPlaying()) {
            der1op_3.stop();
            der1op_3.prepare();
        }
            } catch (IOException e) {
                e.printStackTrace();
            }


    }


    public void der1_op() {
if(pie_izq_pulsado) {
    if (der1op_1.isPlaying()) {
        if (der1op_2.isPlaying())
            der1op_3.start();
        else
            der1op_2.start();
    } else
        der1op_1.start();
}
else
{
    if (der1cl_1.isPlaying()) {
        if (der1cl_2.isPlaying())
            der1cl_3.start();
        else
            der1cl_2.start();
    } else
        der1cl_1.start();
}


    }

    public void der1_cl() {

            if (der1cl_1.isPlaying()) {
                if (der1cl_2.isPlaying())
                    der1cl_3.start();
                else
                    der1cl_2.start();
            } else
                der1cl_1.start();


    }


    public void der2(){

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

}