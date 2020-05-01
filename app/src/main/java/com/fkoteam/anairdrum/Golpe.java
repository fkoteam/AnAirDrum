package com.fkoteam.anairdrum;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class Golpe extends Service {
    //creating a mediaplayer object
    private MediaPlayer player;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //getting systems default ringtone
        if( "2".compareTo(intent.getStringExtra("parametro"))==0)// will return "FirstKeyValue"
            player = MediaPlayer.create(this, R.raw.der1);
        if( "3".compareTo(intent.getStringExtra("parametro"))==0)// will return "FirstKeyValue"
        player = MediaPlayer.create(this, R.raw.izq1);
        //setting loop play to true
        //this will make the ringtone continuously playing
        player.setLooping(false);

        //staring the player
        player.start();

        //we have some options for service
        //start sticky means service will be explicity started and stopped
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //stopping the player when service is destroyed
        player.stop();
    }
}