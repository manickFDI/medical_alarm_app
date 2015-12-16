package com.example.android.prueba.sensors;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Created by Light on 28/11/15.
 */
public class Accelerometer extends GenericSensor implements SensorEventListener {


    public Accelerometer(Context c, int type, int delay) {
        super(c, type, delay);
        //Get sensor and subscribe to events if not null
        if(mSensorManager != null) { //In case there is no sensor manager
            mSensor = mSensorManager.getDefaultSensor(mSensorType);
        }
        if (mSensor != null){
            mSensorManager.registerListener(this, mSensor, mSensorDelay);
        }
        else {
            // Failure! No magnetometer.
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Should we take into account the gravity
        try {
            valueLock.lock();
            //It is needed to do the square root of the values because they come as X,Y,Z axis and we want the absolute value
            mSensorValue = Math.sqrt(Math.pow(event.values[0], 2) + Math.pow(event.values[0], 2)
                    + Math.pow(event.values[0],2));
            valueLock.unlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
        try {
            accuracyLock.lock();
            mSensorAccuracy = accuracy;
            accuracyLock.unlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void unregisterSensor(){
        mSensorManager.unregisterListener(this);
    }

    public void registerSensor(){
        mSensorManager.registerListener(this, mSensor, mSensorDelay);
    }
}
