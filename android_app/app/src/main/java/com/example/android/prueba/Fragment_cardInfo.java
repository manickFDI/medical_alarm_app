package com.example.android.prueba;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class Fragment_cardInfo extends AppCompatActivity {

    public Fragment_cardInfo() {
        // Required empty public constructor
    }

    public static Fragment_cardInfo newInstance() {
        Fragment_cardInfo fragment = new Fragment_cardInfo();
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_cardInfo.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_cardInfo newInstance(String param1, String param2) {
        Fragment_cardInfo fragment = new Fragment_cardInfo();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }


    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("tab_index", 2);//you can put 1,2 or the index you want to
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
