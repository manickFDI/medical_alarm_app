package com.example.android.prueba;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class FragmentCardInfo extends AppCompatActivity {

    private TextView txtTitleNew;
    private TextView txtSeverity;
    private TextView txtDate;

    public FragmentCardInfo() {
        // Required empty public constructor
    }

    public static FragmentCardInfo newInstance() {
        FragmentCardInfo fragment = new FragmentCardInfo();
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentCardInfo.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCardInfo newInstance(String param1, String param2) {
        FragmentCardInfo fragment = new FragmentCardInfo();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_card_info);
        setupActionBar();

        this.txtTitleNew = (TextView)findViewById(R.id.new_title);
        this.txtSeverity = (TextView)findViewById(R.id.new_severity);
        this.txtDate = (TextView)findViewById(R.id.new_date);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.txtTitleNew.setText(extras.getString("text"));
            this.txtSeverity.setText("Riesgo: " + extras.getString("severity"));
            this.txtDate.setText(extras.getString("date"));
        }
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
            Log.d("TAG", "Pulsado boton Atras");
            //Intent intent = new Intent(this, MainActivity.class);
            //intent.putExtra("tab_index", 2);//you can put 1,2 or the index you want to
            //startActivity(intent);
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
