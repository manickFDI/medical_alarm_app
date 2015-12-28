package com.example.android.prueba;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.View;

import com.example.android.prueba.sensors.SensorService;

/**
 * Created by Manuel on 24/11/2015.
 */
public class FragmentSettings extends PreferenceFragment{

    private static final String SETTINGS = "SETTINGS FRAGMENT";
    private SwitchPreference sensorsStatus; //on or off
    private SharedPreferences.Editor prefsEditor;
    private SharedPreferences prefs;
    private boolean mBounded;
    private SensorService mSensorService;
    private ServiceConnection mConnection;

    public FragmentSettings() {
        mBounded = false;
        mConnection = new ServiceConnection() {

            public void onServiceDisconnected(ComponentName name) {
                mBounded = false;
                mSensorService = null;
                Log.d(SETTINGS, "service disconnected");
            }

            public void onServiceConnected(ComponentName name, IBinder service) {
                mBounded = true;
                mSensorService = ((SensorService.LocalBinder)service).getService();
                Log.d(SETTINGS, "service connected");
            }
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        /*String settings = getArguments().getString("settings");
        if ("generales".equals(settings)) {
            addPreferencesFromResource(R.xml.settings_gen);
        } else if ("twitter".equals(settings)) {
            addPreferencesFromResource(R.xml.settings_twitter);
        } else if ("google".equals(settings)) {
            addPreferencesFromResource(R.xml.settings_google);
        }*/

        initFields();
        prefs = this.getActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE);

        Intent intent = new Intent(getActivity(), SensorService.class);
        getActivity().bindService(intent, mConnection, Activity.BIND_AUTO_CREATE);

        sensorsStatus.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {

                boolean sensorStatus = prefs.getBoolean("sensors_initialized", true);
                boolean newState = !((SwitchPreference) preference).isChecked();
                prefsEditor = prefs.edit();
                prefsEditor.putBoolean("sensors_status", newState);
                prefsEditor.commit();

                if(sensorStatus) {
                    if (mBounded) {
                        if (newState) {
                            mSensorService.start();
                        } else {
                            mSensorService.stop();
                        }
                    } else {
                        Log.d(SETTINGS, "Failed to bind SensorService [2]");
                    }
                }
                else if (newState) { //It cannot bind because its not running, starting form off
                    Intent intentSensor = new Intent(getActivity(), SensorService.class);
                    getActivity().startService(intentSensor);
                    prefsEditor.putBoolean("sensors_initialized", true);
                    prefsEditor.commit();
                    //mSensorService.start();
                }
                else {
                    Log.d(SETTINGS, "Failed to bind SensorService [1]");
                }

                return true;
            }

        });

    }

    @Override
    public void onStop(){
        if(mBounded)
            getActivity().unbindService(mConnection);
    }

    @Override
    public void onDestroy(){
        if(mBounded)
            getActivity().unbindService(mConnection);
    }

    private void initFields() {
        this.sensorsStatus = (SwitchPreference) findPreference("gps");
    }
}
