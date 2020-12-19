package com.jio.sensor.lib.service;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.jio.sensor.lib.IOrientationChangeListener;
import com.jio.sensor.lib.ISensorDataChangeListener;
import com.jio.sensor.lib.ISensorManager;
import com.jio.sensor.lib.wrapper.OrientationSensorWrapper;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class SensorManagerNativeImpl extends ISensorManager.Stub implements IOrientationChangeListener {

    private static final String TAG = SensorManagerNativeImpl.class.getSimpleName();

    private final Context mContext;
    private final Map<IBinder, ISensorDataChangeListener> mSensorDataChangeListenerMap = Collections.synchronizedMap(new WeakHashMap<IBinder, ISensorDataChangeListener>());

    public SensorManagerNativeImpl(Context context) {
        mContext = context;
        OrientationSensorWrapper orientationSensorWrapper = new OrientationSensorWrapper(context);
        orientationSensorWrapper.registerOrientationListener(this);
    }

    @Override
    public void registerSensorDataChangeListener(ISensorDataChangeListener listener) throws RemoteException {
        if (listener == null) {
            Log.w(TAG, "Listener can not be null !!!");
            return;
        }
        IBinder token = listener.asBinder();
        mSensorDataChangeListenerMap.put(token, listener);
    }

    @Override
    public void unregisterSensorDataChangeListener(ISensorDataChangeListener listener) throws RemoteException {
        if (listener == null) {
            Log.w(TAG, "Listener can not be null !!!");
            return;
        }
        IBinder token = listener.asBinder();
        mSensorDataChangeListenerMap.remove(token);
    }

    private void notifyOrientationChange(float pitch, float roll) {
        for (Map.Entry<IBinder, ISensorDataChangeListener> listener : mSensorDataChangeListenerMap.entrySet()) {
            try {
                listener.getValue().onOrientationChange(pitch, roll);
            } catch (RemoteException exp) {
                Log.w(TAG, " error on callback exp " + exp.getMessage());
                mSensorDataChangeListenerMap.remove(listener.getKey());
            }
        }
    }

    @Override
    public void onOrientationChange(float pitch, float roll) {
        notifyOrientationChange(pitch, roll);
    }
}
