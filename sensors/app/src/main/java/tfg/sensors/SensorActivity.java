package tfg.sensors;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class SensorActivity extends AppCompatActivity {

    private static final String SENSOR_ACTIVITY = "SENSOR_ACTIVITY";
    private Handler mHandler;
    private Runnable runnable;
    private TextView mLatitudeTV;
    private TextView mLongitudeTV;
    private TextView mTimeTV;
    private TextView mMagnetometerTV;
    private TextView mMagnetometerAccuracyTV;
    private TextView mAccelerometerTV;
    private TextView mAccelerometerAccuracyTV;
    private TextView mLightTV;
    private TextView mLightAccuracyTV;
    private TextView mBatteryTV;
    private Timer timer;
    private boolean mBounded;
    private SensorService mSensorService;
    ServiceConnection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        setContentView(R.layout.activity_sensor);
        mTimeTV = (TextView) findViewById(R.id.value_time);
        mLatitudeTV = (TextView) findViewById(R.id.value_latitude);
        mLongitudeTV = (TextView) findViewById(R.id.value_longitude);
        mMagnetometerTV = (TextView) findViewById(R.id.value_magnetometer);
        mMagnetometerAccuracyTV = (TextView) findViewById(R.id.value_magnetometer_accuracy);
        mAccelerometerTV = (TextView) findViewById(R.id.value_accelerometer);
        mAccelerometerAccuracyTV = (TextView) findViewById(R.id.value_accelerometer_accuracy);
        mLightTV = (TextView) findViewById(R.id.value_light);
        mLightAccuracyTV = (TextView) findViewById(R.id.value_light_accuracy);
        mBatteryTV = (TextView) findViewById(R.id.value_battery);
        Intent intent = new Intent(this, SensorService.class);
        startService(intent);
        mBounded = false;
        mConnection = new ServiceConnection() {

            public void onServiceDisconnected(ComponentName name) {
                mBounded = false;
                Log.d(SENSOR_ACTIVITY, "service disconnected");
            }

            public void onServiceConnected(ComponentName name, IBinder service) {
                mBounded = true;
                Log.d(SENSOR_ACTIVITY, "service connected");
                SensorService.LocalBinder mLocalBinder = (SensorService.LocalBinder)service;
                mSensorService = mLocalBinder.getServerInstance();
            }
        };

        runnable = new Runnable() {
            public void run() {
                updateUI(mSensorService.getCurrentValues(), mSensorService.getNumUpdates());
            }
        };

        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.postDelayed(runnable, 1);
            }
        }, 10000, 10000);
    }

    @Override
    protected void onPause() {
        timer.cancel();
        timer.purge();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
    };

    @Override
    protected void onDestroy() {
        timer.cancel();
        timer.purge();
        super.onDestroy();
    }

    private void updateUI(SensorResult currentValues, int numUpdates) {
        Log.d(SENSOR_ACTIVITY, "new values update");
        mLatitudeTV.setText(String.valueOf(currentValues.getLocLat()));
        mLongitudeTV.setText(String.valueOf(currentValues.getLocLong()));
        mTimeTV.setText(currentValues.getTime().concat(" - ").concat(String.valueOf(numUpdates)));
        mMagnetometerTV.setText(String.valueOf(currentValues.getMgtVal()));
        mMagnetometerAccuracyTV.setText(String.valueOf(currentValues.getMgtAccuracy()));
        mAccelerometerTV.setText(String.valueOf(currentValues.getAccVal()));
        mAccelerometerAccuracyTV.setText(String.valueOf(currentValues.getAccAccuracy()));
        mLightTV.setText(String.valueOf(currentValues.getLightVal()));
        mLightAccuracyTV.setText(String.valueOf(currentValues.getLightAccuracy()));
        mBatteryTV.setText(currentValues.getBatVal().toString());
    }

}
