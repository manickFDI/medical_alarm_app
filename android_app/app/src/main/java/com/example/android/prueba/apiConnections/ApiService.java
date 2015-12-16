package com.example.android.prueba.apiConnections;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

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

    private static final String MY_IP = "192.168.1.33"; //OJO!! No usar la 127.0.0.1

    private static final String MY_URL = "http://" + MY_IP + ":8000/sensorValues/"; //OJO!! No usar la 127.0.0.1

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
        Map<String, String> sensorValuesMap = buildSensorValuesMap(bundle);
        //Map<String, String> sensorValuesMap = buildFakeMap();

        Log.d("TAG", "RUNNING Post...");
        excutePost(sensorValuesMap);
        /*HttpRequest ret = HttpRequest.post(MY_URL, sensorValuesMap, false); //post(url, Map<K,V>, bool encode)
        int responseCode = ret.code();
        Log.d("TAG", "(Response code: " + responseCode + ")");*/
        Log.d("TAG", "...FINISH Post");
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


    private static void excutePost(Map<String, String> sensorValues) {
        URL url = null;
        try {
            url = new URL(MY_URL);
            //HttpsURLConnection conn = (HttpsURLConnection) url.openConnection(); OJO!! descomentar cuando usemos Https
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true); // 'true' para POST y PUT

            Uri.Builder builder = buildUriBuilder(sensorValues); // añade los parametros de la query
            String query = builder.build().getEncodedQuery();   // creamos la query
            Log.d("TAG", "JEJEJEJEJEJE");
            OutputStream os = conn.getOutputStream();
            Log.d("TAG", "JAJAJAJAJAJAJA");
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            Log.d("TAG", "...connecting...");
            conn.connect();
            int responseCode = conn.getResponseCode(); // es aqui donde realmente se realiza el POST
            Log.d("TAG", "ResponseCode = " + responseCode);
            if (responseCode < HttpURLConnection.HTTP_BAD_REQUEST) { //HTTP_BAD_REQUEST = 400
                //inputStream = conn.getInputStream();
                Log.d("TAG", "Successful connection");
            } else {
                //inputStream = conn.getErrorStream();
                Log.d("TAG", "Failed connection");
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

}
