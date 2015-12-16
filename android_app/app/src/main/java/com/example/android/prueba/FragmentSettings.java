package com.example.android.prueba;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Manuel on 24/11/2015.
 */
public class FragmentSettings extends PreferenceFragment{

    public FragmentSettings() {
        // Constructor Por Defecto
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
    }
}
