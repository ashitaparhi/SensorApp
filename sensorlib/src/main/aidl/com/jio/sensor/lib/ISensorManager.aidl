// ISensorManager.aidl
package com.jio.sensor.lib;

import com.jio.sensor.lib.ISensorDataChangeListener;

interface ISensorManager {

    void registerSensorDataChangeListener(ISensorDataChangeListener listener);

    void unregisterSensorDataChangeListener(ISensorDataChangeListener listener);
}
