package com.jio.sensor.lib.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class SensorService extends Service {

    private SensorManagerNativeImpl mSensorManagerNative;

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManagerNative = new SensorManagerNativeImpl(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mSensorManagerNative;
    }
}
