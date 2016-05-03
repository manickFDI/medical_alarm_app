package com.example.android.prueba;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.prueba.commons.HttpRequest;
import com.example.android.prueba.models.User;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentPerfil.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentPerfil#newInstance} factory method to
 * create an instance of this fragment.
 */
/**
 * Fragmento para la pestaña "PERFIL" De la sección "Mi Cuenta"
 */
public class FragmentPerfil extends Fragment {

    private TextView txtName, txtDni, txtDate, txtEmail, txtGenger, txtWeight;
    private User user;
    private String dni;

    public FragmentPerfil() {
        //SharedPreferences prefs = this.getActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        //String dni = prefs.getString("DNI", "");
        //loadDni();
        //new LoadUserInfo().execute(this.dni); // solicita al API los datos del usuario
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        /*if(user == null) {
            loadDni();
            new LoadUserInfo().execute(this.dni); // solicita al API los datos del usuario
            printUserInfo();
        }*/



        super.onCreate(savedInstanceState);
    }


    /*private void loadDni() {
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            String dni = bundle.getString("dni");
            //Log.d("TAG", "DNI: " + dni);
            //new LoadUserInfo().execute(dni); // solicita al API los datos del usuario
            this.dni = dni;
        }
        else
            Log.d("TAG", "Error bundle null");
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_perfil, container, false);
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        this.prepareUI(view); // instanciamos los elementos de la vista...

        if(user == null) {
            SharedPreferences prefs = this.getActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
            String dni = prefs.getString("DNI", "");
            //Log.d("TAG", "onCreate dni: " + dni);
            //this.dni = dni;
            //new LoadUserInfo().execute(this.dni); // solicita al API los datos del usuario

            this.loadUser(dni);
            printUserInfo();
        }
        else
            this.printUserInfo();

        //this.printUserInfo(); // ... y los rellenamos con la informacion del usuario
        return view;
    }


    /**
     * This function initializes the view's elements
     * @param view
     */
    private void prepareUI(View view) {
        this.txtName = (TextView) view.findViewById(R.id.texto_nombre_apellidos);
        this.txtDni = (TextView) view.findViewById(R.id.texto_dni);
        this.txtDate = (TextView) view.findViewById(R.id.texto_fecha_nacimiento);
        this.txtEmail = (TextView) view.findViewById(R.id.texto_email);
        this.txtGenger = (TextView) view.findViewById(R.id.texto_genero);
        this.txtWeight = (TextView) view.findViewById(R.id.texto_peso);
    }

    /**
     * This function updates the UI fields
     */
    private void printUserInfo() {
        while(user == null) {}
        if(user != null) {
            txtName.setText(user.getName() + " " + user.getSurnames());
            txtDni.setText(user.getDni());
            txtDate.setText(user.getBirthdate());
            txtEmail.setText(user.getEmail());
            if (user.getGender() == 0) {
                txtGenger.setText("Hombre");
            } else {
                txtGenger.setText("Mujer");
            }
            txtWeight.setText(String.valueOf(user.getWeight()));
        }
        else {
            Log.d("TAG", "Fallo user es null");
        }
    }


    public void loadUser(String dni) {
        this.dni = dni;
        new LoadUserInfo().execute(this.dni); // solicita al API los datos del usuario
    }


    /**
     * Class responsible for making the request to the ApiRest and print the user information
     */
    private class LoadUserInfo extends AsyncTask<String, Void, String> {

        //private static final String MY_IP = "10.0.2.2";
        private static final String MY_IP = "192.168.1.33";
        //private static final String MY_URL = "http://" + MY_IP + ":5000/malarm/api/users/1/"; //OJO!! No usar la 127.0.0.1
        private static final String MY_URL = "http://" + MY_IP + ":5000/malarm/api/user/"; //OJO!! No usar la 127.0.0.1

        /**
         * This function realizes the request (GET) with the correct URL
         * @param params
         */
        @Override
        protected String doInBackground(String... params) {
            StringBuilder result = new StringBuilder();
            try {
                //Log.d("TAG", "Peticion al API FragmentPerfil...");

                //String url = URL + params[0];
                String urlAux = MY_URL + dni + "/";
                //Log.d("TAG", urlAux);

                //return HttpRequest.get(url).accept("application/json").body();
                URL url = new URL(urlAux);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    // Acciones a realizar con el flujo de datos
                    //Log.d("TAG", in.toString());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    user = User.buildUser(result.toString());

                } catch (HttpRequest.HttpRequestException e) {
                    //Log.d("TAG", "Error al realizar la peticion al API);
                } finally {
                    urlConnection.disconnect();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            //printUserInfo();
        }

    }
}
