package com.jio.sensor.lib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.jio.sensor.lib.service.SensorService;

import java.util.concurrent.CopyOnWriteArrayList;

public class SensorManager {

    private static final String TAG = SensorManager.class.getSimpleName();

    private static volatile SensorManager mSensorManager;
    private volatile boolean mIsServiceConnected = false;
    private ISensorManager mSensorBinderService;

    private final Context mContext;
    private final CopyOnWriteArrayList<IOrientationChangeListener> mOrientationChangeListeners = new CopyOnWriteArrayList<>();

    public SensorManager(Context context) {
        mContext = context;
        connect();
    }

    public static SensorManager getInstance(Context context) {
        if (mSensorManager == null) {
            synchronized (SensorManager.class) {
                if (mSensorManager == null) {
                    mSensorManager = new SensorManager(context);
                }
            }
        }
        return mSensorManager;
    }

    private void connect() {
        if (!mIsServiceConnected) {
            Intent presetServiceBindIntent = new Intent(mContext, SensorService.class);
            mContext.bindService(presetServiceBindIntent, mSensorServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void disconnect() {
        if (mIsServiceConnected) {
            try {
                mSensorBinderService.unregisterSensorDataChangeListener(mSensorDataChangeListener);
            } catch (RemoteException e) {
                Log.w(TAG, "Unable to unregister listener!!!");
            }
            mContext.unbindService(mSensorServiceConnection);
        }
    }

    private final ServiceConnection mSensorServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i(TAG, "onServiceConnected  ");
            try {
                mSensorBinderService = ISensorManager.Stub.asInterface(service);
                mSensorBinderService.registerSensorDataChangeListener(mSensorDataChangeListener);
            } catch (RemoteException e) {
                Log.w(TAG, "Exception in onServiceConnected :: ", e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.i(TAG, "onServiceDisconnected  ");
        }
    };

    private final ISensorDataChangeListener.Stub mSensorDataChangeListener = new ISensorDataChangeListener.Stub() {
        @Override
        public void onOrientationChange(float pitch, float roll) throws RemoteException {
            notifyOrientationChange(pitch, roll);
        }
    };

    public void addOrientationChangeListener(IOrientationChangeListener listener) {
        if (listener == null) {
            Log.w(TAG, "Listener can not be null !!!");
            return;
        }
        mOrientationChangeListeners.addIfAbsent(listener);
    }

    public void removeOrientationChangeListener(IOrientationChangeListener listener) {
        if (listener == null) {
            Log.w(TAG, "Listener can not be null !!!");
            return;
        }
        mOrientationChangeListeners.remove(listener);
    }

    private void notifyOrientationChange(float pitch, float roll) {
        for (IOrientationChangeListener listener : mOrientationChangeListeners) {
            listener.onOrientationChange(pitch, roll);
        }
    }
}
