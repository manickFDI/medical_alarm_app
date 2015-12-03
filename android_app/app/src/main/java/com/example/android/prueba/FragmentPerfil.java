package com.example.android.prueba;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.prueba.models.User;
import com.example.android.prueba.commons.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;


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

    private TextView txtName, txtDni, txtDate, txtEmail, txtHeight, txtWeight;
    private User user;

    public FragmentPerfil() {
        //new LoadUserInfo().execute(); // solicita al API los datos del usuario
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_perfil, container, false);
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        //this.prepareUI(view); // instanciamos los elementos de la vista...
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
        this.txtHeight = (TextView) view.findViewById(R.id.texto_altura);
        this.txtWeight = (TextView) view.findViewById(R.id.texto_peso);
    }

    /**
     * This function updates the UI fields
     */
    private void printUserInfo() {
        txtName.setText(user.getName() + " " + user.getSurnames());
        txtDni.setText(user.getDni());
        txtDate.setText(user.getBirthdate());
        txtEmail.setText(user.getEmail());
        txtHeight.setText(String.valueOf(user.getHeight()));
        txtWeight.setText(String.valueOf(user.getWeight()));
    }


    /**
     * Class responsible for making the request to the ApiRest and print the user information
     */
    private class LoadUserInfo extends AsyncTask<Void, Void, String> {

        private static final String URL = "http://10.0.2.2:8000/users/1/"; //OJO!! No usar la 127.0.0.1

        /**
         * This function realizes the request (GET) with the correct URL
         * @param params
         * @return the response (the json with the user information)
         */
        @Override
        protected String doInBackground(Void... params) {
            try {
                Log.d("TAG", "Peticion al API...");
                return HttpRequest.get(URL).accept("application/json").body();
            } catch (HttpRequest.HttpRequestException e) {
                //Log.d("TAG", "Error al realizar la peticion al API);
                return null;
            }
        }

        /**
         * After making the request, we create the user
         * @param response the api response (the json with the user information)
         */
        @Override
        protected void onPostExecute(String response) {
            user = buildUser(response);
        }

        /**
         * This function builds the User from the json. It uses the Gson library
         * @param json
         * @return the User correctly built
         */
        private User buildUser(String json) {
            Gson gson = new Gson();
            Type type = new TypeToken<User>(){}.getType();
            return gson.fromJson(json, type);
        }
    }
}
