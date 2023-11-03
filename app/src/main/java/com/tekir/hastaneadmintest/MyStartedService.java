package com.tekir.hastaneadmintest;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import javax.annotation.Nullable;

public class MyStartedService extends Service {

    private static final String control = "MyStartedService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        CountDownTimer countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {

                Log.e(control, "onTick: " + l/1000);
            }

            @Override
            public void onFinish() {

                Log.e(control, "onFinish: ");
            }
        }.start();
        return START_STICKY;
    }
}