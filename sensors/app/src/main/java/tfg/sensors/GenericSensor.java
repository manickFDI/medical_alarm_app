package tfg.sensors;

import android.content.Context;
import android.hardware.SensorManager;

import tfg.commons.Lock;


/**
 * Created by Light on 28/11/15.
 */
public class GenericSensor {


    protected SensorManager mSensorManager;
    protected android.hardware.Sensor mSensor;
    protected int mSensorType;
    protected int mSensorDelay;
    protected int mSensorAccuracy;
    protected double mSensorValue;
    protected Lock valueLock;
    protected Lock accuracyLock;


    public int getSensorAccuracy(){
        int aux = -1;
        try {
            accuracyLock.lock();
            aux = mSensorAccuracy;
            accuracyLock.unlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return aux;
    }

    public double getSensorValue(){
        double aux = -1;
        try {
            accuracyLock.lock();
            aux = mSensorValue;
            accuracyLock.unlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return aux;
    }

    public GenericSensor(Context c, int type, int delay){

        //Initialize values
        valueLock = new Lock();
        accuracyLock = new Lock();
        mSensorDelay = delay;
        mSensorType = type;
        mSensor = null;
        mSensorAccuracy = -1;
        mSensorValue = -1;
        mSensorManager = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);
    }

}
