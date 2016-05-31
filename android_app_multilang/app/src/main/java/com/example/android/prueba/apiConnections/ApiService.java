package com.example.android.prueba.apiConnections;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Diego on 10/12/2015.
 */
public class ApiService extends IntentService {

    private static final String MY_IP = "147.96.80.89"; //OJO!! No usar la 127.0.0.1

    private static final String MY_URL = "http://" + MY_IP + ":5000/malarm/api/sensors/"; //OJO!! No usar la 127.0.0.1

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ApiService(String name) {
        super(name);
    }

    public ApiService() {
        super("ApiService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        //Map<String, String> sensorValuesMap = buildSensorValuesMap(bundle);
        //Map<String, String> sensorValuesMap = buildFakeMap();
        String json = createJson(bundle);

        //Log.d("TAG", "RUNNING Post (sensorValues)...");
        excutePost(json);
        //Log.d("TAG", "...FINISH Post (sensorValues)");
    }


    private static Map<String, String> buildSensorValuesMap(Bundle bundle) {
        Map<String, String> ret = new HashMap<>();
        ret.put("latitude", String.valueOf(bundle.getDouble("latitude")));
        ret.put("longitude", String.valueOf(bundle.getDouble("longitude")));
        ret.put("altitude", String.valueOf(bundle.getDouble("altitude")));
        ret.put("time", bundle.getString("time"));
        ret.put("magnetometer", String.valueOf(bundle.getDouble("magnetometer")));
        ret.put("mgt_accuracy", String.valueOf(bundle.getInt("mgt_accuracy")));
        ret.put("accelerometer", String.valueOf(bundle.getDouble("accelerometer")));
        ret.put("acc_accuracy", String.valueOf(bundle.getInt("acc_accuracy")));
        ret.put("light", String.valueOf(bundle.getDouble("light")));
        ret.put("light_accuracy", String.valueOf(bundle.getInt("light_accuracy")));
        ret.put("battery", bundle.getString("battery"));

        /*Log.d("TAG", "-------------------------------------------");
        Log.d("TAG", "latitude=" + bundle.getDouble("latitude"));
        Log.d("TAG", "longitude=" + bundle.getDouble("longitude"));
        Log.d("TAG", "altitude=" + bundle.getDouble("altitude"));
        Log.d("TAG", "time=" + bundle.getString("time"));
        Log.d("TAG", "magnetometer=" + bundle.getDouble("magnetometer"));
        Log.d("TAG", "mgt_accuracy=" + bundle.getInt("mgt_accuracy"));
        Log.d("TAG", "accelerometer=" + bundle.getDouble("accelerometer"));
        Log.d("TAG", "acc_accuracy=" + bundle.getInt("acc_accuracy"));
        Log.d("TAG", "light=" + bundle.getDouble("light"));
        Log.d("TAG", "light_accuracy=" + bundle.getInt("light_accuracy"));
        Log.d("TAG", "battery=" + bundle.getString("battery"));
        Log.d("TAG", "-------------------------------------------");*/
        return ret;
    }

    private static Map<String, String> buildFakeMap() {
        Map<String, String> ret = new HashMap<>();
        ret.put("latitude", String.valueOf(777.00));
        ret.put("longitude", String.valueOf(4.00));
        ret.put("altitude", String.valueOf(6.00));
        ret.put("time", "11:10:00");
        ret.put("magnetometer", String.valueOf(8.00));
        ret.put("mgt_accuracy", String.valueOf(10.00));
        ret.put("accelerometer", String.valueOf(12.00));
        ret.put("acc_accuracy", String.valueOf(14.00));
        ret.put("light", String.valueOf(16.00));
        ret.put("light_accuracy", String.valueOf(18.00));
        ret.put("battery", "BAT_VAL");

        return ret;
    }


    //private static void excutePost(Map<String, String> sensorValues) {
    private static void excutePost(String json) {
        URL url = null;
        try {
            url = new URL(MY_URL);
            //HttpsURLConnection conn = (HttpsURLConnection) url.openConnection(); OJO!! descomentar cuando usemos Https
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true); // 'true' para POST y PUT

            //Uri.Builder builder = buildUriBuilder(sensorValues); // añade los parametros de la query
            //String query = builder.build().getEncodedQuery();   // creamos la query
            String query = json;

            OutputStream os = conn.getOutputStream();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            //Log.d("TAG", "...connecting (sensorValues)...");
            conn.connect();
            int responseCode = conn.getResponseCode(); // es aqui donde realmente se realiza el POST
            //Log.d("TAG", "ResponseCode (sensorValues) = " + responseCode);
            if (responseCode < HttpURLConnection.HTTP_BAD_REQUEST) { //HTTP_BAD_REQUEST = 400
                //Log.d("TAG", "Successful connection (SensorValues)");
            } else {
                //Log.d("TAG", "Failed connection (SensorValues)");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static Uri.Builder buildUriBuilder(Map<String, String> sensorValues) {
        Uri.Builder builder = new Uri.Builder();
        builder.appendQueryParameter("latitude", sensorValues.get("latitude"));
        builder.appendQueryParameter("longitude", sensorValues.get("longitude"));
        builder.appendQueryParameter("altitude", sensorValues.get("altitude"));
        builder.appendQueryParameter("time", sensorValues.get("time"));
        builder.appendQueryParameter("magnetometer", sensorValues.get("magnetometer"));
        builder.appendQueryParameter("mgt_accuracy", sensorValues.get("mgt_accuracy"));
        builder.appendQueryParameter("accelerometer", sensorValues.get("accelerometer"));
        builder.appendQueryParameter("acc_accuracy", sensorValues.get("acc_accuracy"));
        builder.appendQueryParameter("light", sensorValues.get("light"));
        builder.appendQueryParameter("light_accuracy", sensorValues.get("light_accuracy"));
        builder.appendQueryParameter("battery", sensorValues.get("battery"));

        return builder;
    }


    private String createJson(Bundle bundle) {
        String dni = null;
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        if(prefs.contains("DNI"))
            dni = prefs.getString("DNI", "");

        JSONObject jo = new JSONObject();
        JSONObject ret = new JSONObject();

        try {
            jo.put("user_dni", dni);
            jo.put("latitude", String.valueOf(bundle.getDouble("latitude")));
            jo.put("longitude", String.valueOf(bundle.getDouble("longitude")));
            jo.put("altitude", String.valueOf(bundle.getDouble("altitude")));
            jo.put("timestamp", bundle.getString("time"));
            jo.put("magnetometer", String.valueOf(bundle.getDouble("magnetometer")));
            jo.put("mgt_accuracy", String.valueOf(bundle.getInt("mgt_accuracy")));
            jo.put("accelerometer", String.valueOf(bundle.getDouble("accelerometer")));
            jo.put("acc_accuracy", String.valueOf(bundle.getInt("acc_accuracy")));
            jo.put("light", String.valueOf(bundle.getDouble("light")));
            jo.put("light_accuracy", String.valueOf(bundle.getInt("light_accuracy")));
            jo.put("battery", bundle.getString("battery"));

            ret.put("sensor", jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Log.d("TAG", "JSON: " + ret.toString());
        return ret.toString();
    }


}
