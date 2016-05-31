package com.example.android.prueba;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.prueba.sensors.SensorResult;
import com.example.android.prueba.sensors.SensorService;
import com.example.android.prueba.sensors.SensorService.LocalBinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentEstado.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentEstado#newInstance} factory method to
 * create an instance of this fragment.
 */
/**
 * Fragmento para la pestaña "TARJETAS" de la sección "Mi Cuenta"
 */
public class FragmentEstado extends Fragment {

    private SwipeRefreshLayout refreshLayout;
    private View view;
    private String dni;

    private CardView cardPersona;
    private CardView cardZona;
    private ImageView imgUser;
    private ImageView imgZone;
    private TextView titleUser;
    private TextView titleZone;
    private TextView descriptionUser;
    private TextView descriptionZone;

    private SensorService sensorService;
    private boolean mBound = false;
    private SensorResult sensorValues;

    public FragmentEstado() {
    }


    private ServiceConnection mConnection= new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            //Log.d("TAG", "ESTABLECIENDO LAS CONEXIONES");
            mBound = true;
            LocalBinder binder = (LocalBinder) service;
            sensorService = binder.getService();

            if(dni != null) {
                Log.d("TAG", "MBOUND 1");
                if (mBound) {
                    Log.d("TAG", "MBOUND 2");
                    if (sensorService == null) {
                        Log.d("TAG", "sensorService es null");
                    }

                    sensorValues = sensorService.getCurrentValues();
                    if(sensorValues != null) {
                        //Log.d("TAG", sensorValues.getLocLat() + "-" + sensorValues.getLocLong());
                        new UpdateUserStateTask().execute(dni, sensorValues.getLocLat(), sensorValues.getLocLong());
                    }
                    else {
                        //Log.d("TAG", "Obteniendo localizacion de LocationManager");
                        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                        // Sale error porque requiere minimo API 23 y nosotros tenemos minimo API 15
                        // No falla al ejecutarse
                        try {
                            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            //Log.d("TAG", location.getLatitude() + "-" + location.getLongitude());
                            new UpdateUserStateTask().execute(dni, location.getLatitude(), location.getLongitude());
                        }
                        catch (SecurityException e) {
                            Log.e("PERMISSION_EXCEPTION","PERMISSION_NOT_GRANTED");
                        }
                    }
                }
                else {
                    Log.d("TAG", "NO BOUND");
                }
            }
            else {
                Log.d("TAG", "DNI nulo");
            }

        }

        public void onServiceDisconnected(ComponentName className) {
            //Log.d("TAG", "onServiceDisconnected");
            sensorService = null;
            mBound = false;
        }
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(getActivity(), SensorService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(this.dni == null) {
            SharedPreferences prefs = this.getActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
            if(prefs.contains("DNI"))
                this.dni = prefs.getString("DNI", "");

        }

        view = inflater.inflate(R.layout.fragment_estado, container, false);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshEstado);
        refreshLayout.setColorSchemeResources(
                R.color.primaryColor,
                R.color.primaryDarkColor,
                R.color.accentColor);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);

                SensorResult sensorValues = sensorService.getCurrentValues();
                //Log.d("TAG", sensorValues.getLocLat() + "-" + sensorValues.getLocLong());
                new UpdateUserStateTask().execute(dni, sensorValues.getLocLat(), sensorValues.getLocLong());

                refreshLayout.setRefreshing(false);
            }
        });


        if(sensorService != null) {
            sensorValues = sensorService.getCurrentValues();
            new UpdateUserStateTask().execute(dni, sensorValues.getLocLat(), sensorValues.getLocLong());
        }



/*
        if(dni != null) {
            Log.d("TAG", "MBOUND 1");

            if (mBound) {
                Log.d("TAG", "MBOUND 2");
                if (sensorService == null) {
                    Log.d("TAG", "sensor service es NUUUUUUUULL");
                }

                SensorResult sensorValues = sensorService.getCurrentValues();
                Log.d("TAG", sensorValues.getLocLat() + "-" + sensorValues.getLocLong());
                new UpdateUserStateTask().execute(dni, sensorValues.getLocLat(), sensorValues.getLocLong());
            }
            else {
                //this.onCreateView(inflater, container, savedInstanceState);
            }
        }
*/
        return view;
    }


    public void loadUser(String dni) {
        this.dni = dni;
    }



    private class UpdateUserStateTask extends AsyncTask<Object, Void, List<List<String>>> {
        //private static final String MY_IP = "10.0.2.2";
        private static final String MY_IP = "147.96.80.89";
        private static final String MY_URL = "http://" + MY_IP + ":5000/malarm/api/user/status/"; //OJO!! No usar la 127.0.0.1

        @Override
        protected List<List<String>> doInBackground(Object... params) {
            //Log.d("TAG", "Updating status...");

            StringBuilder result = new StringBuilder();
            List<String> contagionsUserList = new ArrayList<>();
            List<String> contagionsZoneList = new ArrayList<>();
            List<List<String>> ret = new ArrayList<>();

            try {
                URL url = new URL(MY_URL + "?user_dni=" + params[0] + "&lat=" + params[1] + "&lng=" + params[2]);
                //Log.d("TAG", "URL actualizar estados: " + url.toString());

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String response = "";
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();

                    response = stringBuilder.toString();
                    //Log.d("TAG", "Response doInBackgound: " + response);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }

                try {
                    if(!response.equals("")) {
                        JSONObject json = (JSONObject) new JSONTokener(response).nextValue();
                        JSONObject jsonUser = (JSONObject) json.get("user");
                        JSONArray jsonZone = (JSONArray) json.get("zone");

                        int userStatus = jsonUser.getInt("state");
                        if(userStatus == 3) { // 3 = enfermo/infectado
                            JSONArray jsonContagions = (JSONArray) jsonUser.get("contagions");
                            for(int i=0; i<jsonContagions.length(); i++) {
                                JSONObject jsonDisease = (JSONObject) jsonContagions.getJSONObject(i).get("disease");
                                String diseaseName = jsonDisease.getString("name");
                                contagionsUserList.add(diseaseName);
                            }
                        }

                        if(jsonZone.length() > 0) { // si es mayor que cero es que sí hay contagios en la zona
                            for(int i=0; i< jsonZone.length(); i++) {
                                JSONObject jsonDisease = (JSONObject) jsonZone.getJSONObject(i).get("disease");
                                String diseaseName = jsonDisease.getString("name");
                                contagionsZoneList.add(diseaseName);
                            }
                        }

                        ret.add(contagionsUserList);
                        ret.add(contagionsZoneList);
                    }
                } catch (JSONException e) {
                    return ret;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return ret;
        }


        protected void onPostExecute(List<List<String>> response) {
            if(response.size() == 2) {
                Log.d("TAG", "onPostExecute FragmentEstado");
                List<String> contagionsUserList = response.get(0);
                List<String> contagionsZoneList = response.get(1);

                cardPersona = (CardView) view.findViewById(R.id.estadoPersona);
                cardZona = (CardView) view.findViewById(R.id.estadoZona);
                imgUser = (ImageView) view.findViewById(R.id.img_state_user);
                imgZone = (ImageView) view.findViewById(R.id.img_state_zone);
                titleUser = (TextView) view.findViewById(R.id.titulo_estado_individuo);
                titleZone = (TextView) view.findViewById(R.id.titulo_estado_zona);
                descriptionUser = (TextView) view.findViewById(R.id.info_estado_individuo);
                descriptionZone = (TextView) view.findViewById(R.id.info_estado);

                if(contagionsUserList.size() > 0) {
                    imgUser.setImageResource(R.drawable.verde_rojo_opt);
                    titleUser.setText(getString(R.string.etiqueta_contagio_persona));
                    //String descriptionString = R.string.etiqueta_contagio_persona_detallado;
                    String descriptionString = "";
                    for(int i=0; i<contagionsUserList.size(); i++) {
                        if(i == contagionsUserList.size()-1)
                            descriptionString += contagionsUserList.get(i);
                        else
                            descriptionString += contagionsUserList.get(i) + ", ";
                    }
                    descriptionUser.setText(getString(R.string.etiqueta_contagio_persona_detallado) + descriptionString);
                }
                else {
                    imgUser.setImageResource(R.drawable.ico_contact_opt);
                    titleUser.setText(getString(R.string.no_contagio_individuo));
                    descriptionUser.setText(getString(R.string.etiqueta_no_contagio_persona_detallado));
                }

                if(contagionsZoneList.size() > 0) {
                    imgZone.setImageResource(R.drawable.verde_rojo_opt);
                    titleZone.setText(getString(R.string.etiqueta_contagio_zona));
                    //String descriptionString = R.string.etiqueta_contagio_zona_detallado;
                    String descriptionString = "";
                    for(int i=0; i<contagionsZoneList.size(); i++) {
                        if(i == contagionsZoneList.size()-1)
                            descriptionString += contagionsZoneList.get(i);
                        else
                            descriptionString += contagionsZoneList.get(i) + ", ";
                    }
                    descriptionZone.setText(getString(R.string.etiqueta_contagio_zona_detallado) + descriptionString);
                }
                else {
                    imgZone.setImageResource(R.drawable.ico_contact_opt);
                    titleZone.setText(getString(R.string.no_contagio_zona));
                    descriptionZone.setText(getString(R.string.etiqueta_no_contagio_zona_detallado));
                }
            }
            else {
                Log.d("TAG", "ERROR en fragmentEstado");
            }

            cardPersona.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast toast = Toast.makeText(getActivity(), "Persona libre de contagio", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            });

            cardZona.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast toast = Toast.makeText(getActivity(), "Zona libre de contagio", Toast.LENGTH_SHORT);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            });



        }

    }


}