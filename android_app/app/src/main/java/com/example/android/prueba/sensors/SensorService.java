package com.example.android.prueba.sensors;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.android.prueba.apiConnections.ApiService;
import com.example.android.prueba.commons.Lock;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;


/**
 * Created by Light on 28/11/15.
 */
public class SensorService extends Service {

    protected static final String SENSOR_TAG = "SENSORS";
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private Lock valuesLock;
    private SensorResult currentValues;
    private IBinder mBinder;

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        mBinder = new LocalBinder();
        valuesLock = new Lock();
        currentValues = new SensorResult();
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    public SensorResult getCurrentValues(){
        SensorResult aux = new SensorResult();
        try {
            valuesLock.lock();
            aux = mServiceHandler.getCurrentValues();
            valuesLock.unlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return aux;
    }

    public int getNumUpdates() {
        int aux = -1;
        try {
            valuesLock.lock();
            aux = mServiceHandler.getNumUpdates();
            valuesLock.unlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return aux;
    }

    public void start(){
        if(!mServiceHandler.isStarted()) {
            this.mServiceHandler.startHandlerWork();
        }
        else{
            Log.d(SENSOR_TAG, "Sensors were already started");
        }
    }

    public void stop(){
        if(mServiceHandler.isStarted()) {
            this.mServiceHandler.stopHandlerWork();
        }
        else{
            Log.d(SENSOR_TAG, "Sensors were already stopped");
        }
    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

        private boolean started;
        private GoogleApiClient mGoogleApiClient;
        private LocationRequest mLocationRequest;
        private int mNumUpdates;
        private Location mCurrentLocation;
        private String mCurrentLocationTime;
        //Sensors
        private Magnetometer mMagnetometer;
        private Accelerometer mAccelerometer;
        private Light mLight;
        private Battery mBattery;
        private boolean mRequestUpdates;

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            started = false;
            mNumUpdates = 0;
            buildGoogleApiClient();
            mGoogleApiClient.connect();
            mRequestUpdates = true;
            createLocationRequest();
            mMagnetometer = new Magnetometer(getApplicationContext(), Sensor.TYPE_MAGNETIC_FIELD,
                    SensorManager.SENSOR_DELAY_NORMAL);
            mAccelerometer = new Accelerometer(getApplicationContext(), Sensor.TYPE_ACCELEROMETER,
                    SensorManager.SENSOR_DELAY_NORMAL);
            mLight = new Light(getApplicationContext(), Sensor.TYPE_LIGHT, SensorManager.SENSOR_DELAY_NORMAL);
            mBattery = new Battery();
            startHandlerWork();
        }

        public boolean isStarted(){
            return this.started;
        }

        public void startHandlerWork() {
            if(!mGoogleApiClient.isConnected()){
                mGoogleApiClient.connect();
            }
            if (!mRequestUpdates) {
                mMagnetometer.registerSensor();
                mAccelerometer.registerSensor();
                mLight.registerSensor();
                startLocationUpdates();
            }
            started = true;
            Log.d(SENSOR_TAG, "Sensors started");
        }

        public void stopHandlerWork() {
            mMagnetometer.unregisterSensor();
            mAccelerometer.unregisterSensor();
            mLight.unregisterSensor();
            stopLocationUpdates();
            if(mGoogleApiClient.isConnected()){
                mGoogleApiClient.disconnect();
            }
            started = false;
            Log.d(SENSOR_TAG, "Sensors stopped");
        }

        protected synchronized void buildGoogleApiClient() {
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        @Override
        public void onConnected(Bundle connectionHint) {
            Log.d(SENSOR_TAG, "Google Play Services API Connected");
            if (mRequestUpdates) {
                startLocationUpdates();
            }
        }


        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.e(SENSOR_TAG, connectionResult.getErrorMessage());
        }


        @Override
        public void onLocationChanged(Location location) {
            try {
                valuesLock.lock();
                Log.d(SENSOR_TAG, "new location update: " + mNumUpdates);
                mCurrentLocation = location;
                mCurrentLocationTime = DateFormat.getTimeInstance().format(new Date());
                if(mNumUpdates == 20){
                    stopHandlerWork();
                    stopSelf();
                }
                mNumUpdates++;
                valuesLock.unlock();

                Intent intent = new Intent(getApplicationContext(), ApiService.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude", this.getCurrentValues().getLocLat());
                bundle.putDouble("longitude", this.getCurrentValues().getLocLong());
                bundle.putDouble("altitude", this.getCurrentValues().getLocAlt());
                bundle.putString("time", this.getCurrentValues().getTime());
                bundle.putDouble("magnetometer", this.getCurrentValues().getMgtVal());
                bundle.putInt("mgt_accuracy", this.getCurrentValues().getMgtAccuracy());
                bundle.putDouble("accelerometer", this.getCurrentValues().getAccVal());
                bundle.putInt("acc_accuracy", this.getCurrentValues().getAccAccuracy());
                bundle.putDouble("light", this.getCurrentValues().getLightVal());
                bundle.putInt("light_accuracy", this.getCurrentValues().getLightAccuracy());
                bundle.putString("battery", this.getCurrentValues().getBatVal().toString());
                intent.putExtras(bundle);
                //startService(intent);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        protected void createLocationRequest() {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        }

        protected void startLocationUpdates() {
            if(!mGoogleApiClient.isConnected()){
                mGoogleApiClient.connect();
            }
            mRequestUpdates = true;
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        protected void stopLocationUpdates() {
            if(mGoogleApiClient.isConnected()) {
                mRequestUpdates = false;
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        }

        public SensorResult getCurrentValues() {
            //TODO: only one object and change through setters. Less memory consumption
            return new SensorResult(mAccelerometer.getSensorValue(), mAccelerometer.getSensorAccuracy(),
                                    mMagnetometer.getSensorValue(), mMagnetometer.getSensorAccuracy(),
                                    mLight.getSensorValue(), mLight.getSensorAccuracy(),
                                    mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),
                                    mCurrentLocation.getAltitude(), mCurrentLocationTime,
                                    mBattery.getBatteryStatus());
        }

        public int getNumUpdates() {
            return mNumUpdates;
        }
    }

    public class LocalBinder extends Binder {
        public SensorService getSensorServiceInstance() {
            return SensorService.this;
        }
    }
}