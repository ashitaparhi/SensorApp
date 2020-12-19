package com.jio.sensor.lib.wrapper;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.jio.sensor.lib.IOrientationChangeListener;


public class OrientationSensorWrapper implements SensorEventListener {

    private static final String TAG = OrientationSensorWrapper.class.getSimpleName();
    private static final int SENSOR_DELAY = 8;
    private static final int MSG_NOTIFY_ORIENTATION_DATA = 1000;
    private static final int DELAY_NOTIFY_ORIENTATION = 8;

    private final Handler mUIHandler;

    private IOrientationChangeListener mOrientationChangeListener;

    public OrientationSensorWrapper(Context context) {
        mUIHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (mOrientationChangeListener != null) {
                    OrientationInfo info = (OrientationInfo) msg.obj;
                    mOrientationChangeListener.onOrientationChange(info.pitch, info.roll);
                }
                return false;
            }
        });
        SensorManager sensorManager = (SensorManager) context.getSystemService(Activity.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SENSOR_DELAY);
    }

    public void registerOrientationListener(IOrientationChangeListener listener) {
        if (listener == null) {
            Log.w(TAG, "Listener can not be null !!!");
            return;
        }
        mOrientationChangeListener = listener;
    }

    public void unregisterOrientationListener() {
        mOrientationChangeListener = null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            if (event.values.length > 4) {
                float[] truncatedRotationVector = new float[4];
                System.arraycopy(event.values, 0, truncatedRotationVector, 0, 4);
                update(truncatedRotationVector);
            } else {
                update(event.values);
            }
        }
    }

    private void update(float[] vectors) {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, vectors);
        int worldAxisX = SensorManager.AXIS_X;
        int worldAxisZ = SensorManager.AXIS_Z;
        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);
        float[] orientation = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientation);
        float pitch = (float) Math.toDegrees(orientation[1]);
        float roll = (float) Math.toDegrees(orientation[2]);
        if (!mUIHandler.hasMessages(MSG_NOTIFY_ORIENTATION_DATA)) {
            Message msg = Message.obtain();
            msg.what = MSG_NOTIFY_ORIENTATION_DATA;
            msg.obj = new OrientationInfo(pitch, roll);
            mUIHandler.sendMessageDelayed(msg, DELAY_NOTIFY_ORIENTATION);
        }
    }

    private class OrientationInfo {
        float pitch;
        float roll;

        public OrientationInfo(float pitch, float roll) {
            this.pitch = pitch;
            this.roll = roll;
        }
    }
}
