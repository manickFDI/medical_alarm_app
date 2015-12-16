package com.example.android.prueba.sensors;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
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

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private Lock valuesLock;
    private SensorResult currentValues;

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
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
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p/>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process</em>.  More information about the main thread can be found in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

        private static final String SENSOR_TAG = "SENSORS";
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

        private void startHandlerWork() {
            if(!mGoogleApiClient.isConnected()){
                mGoogleApiClient.connect();
            }
            mMagnetometer.registerSensor();
            mAccelerometer.registerSensor();
            mLight.registerSensor();
            if (!mRequestUpdates) {
                startLocationUpdates();
            }
        }

        private void stopHandlerWork() {
            mMagnetometer.unregisterSensor();
            mAccelerometer.unregisterSensor();
            mLight.unregisterSensor();
            stopLocationUpdates();
            if(mGoogleApiClient.isConnected()){
                mGoogleApiClient.disconnect();
            }
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
                if(mNumUpdates == 2){
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
                startService(intent);

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
}