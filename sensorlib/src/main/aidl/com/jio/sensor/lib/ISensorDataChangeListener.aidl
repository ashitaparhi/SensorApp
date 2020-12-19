// ISensorDataChangeListener.aidl
package com.jio.sensor.lib;


interface ISensorDataChangeListener {
    void onOrientationChange(float pitch, float roll);
}
