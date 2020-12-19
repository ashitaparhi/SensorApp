package com.jio.sensor;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.TextView;

import com.jio.sensor.lib.IOrientationChangeListener;
import com.jio.sensor.lib.SensorManager;

public class MainActivity extends AppCompatActivity implements IOrientationChangeListener {

    private TextView mSensorPitchTextView;
    private TextView mSensorRollTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorPitchTextView = findViewById(R.id.pitch);
        mSensorRollTextView = findViewById(R.id.roll);
        SensorManager sensorManager = SensorManager.getInstance(getApplicationContext());
        sensorManager.addOrientationChangeListener(this);
    }

    @Override
    public void onOrientationChange(float pitch, float roll) {
        mSensorPitchTextView.setText("Pitch: " + pitch);
        mSensorRollTextView.setText("Roll: " + roll);
    }
}