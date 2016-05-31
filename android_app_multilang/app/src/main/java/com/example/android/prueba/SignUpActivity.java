package com.example.android.prueba;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.android.prueba.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Manuel on 20/12/2015.
 */
public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    //private static final String MY_IP = "192.168.1.60"; //OJO!! No usar la 127.0.0.1

    //private static final String MY_URL = "http://" + MY_IP + ":8000/users/"; //OJO!! No usar la 127.0.0.1

    SharedPreferences prefs;

    private EditText _nameText;
    private EditText _surnamesText;
    private EditText _dniText;
    private EditText _fechaText;
    private EditText _emailText;
    private RadioGroup _radioGroup;
    //private EditText _alturaText;
    private EditText _pesoText;
    private EditText _passwordText;
    private EditText _repasswordText;
    private Button _signupButton;
    private Button _loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        initFields(); //inicializamos los campos y cogemos los datos del usuario

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();//registro
            }
        });

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }


    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creando cuenta...");
        progressDialog.show();

        final String name = _nameText.getText().toString();
        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();
        final String dni = _dniText.getText().toString();

        // TODO: Implement your own signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess(name, email, password, dni); //guardamos el nuevo usuario en preferencias
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);

        //guardamos el nuevo usuario en la bbdd
        User nuevoUsuario = new User();
        nuevoUsuario.setUser_id("5");
        nuevoUsuario.setName(_nameText.getText().toString());
        nuevoUsuario.setSurnames(_surnamesText.getText().toString());
        nuevoUsuario.setDni(_dniText.getText().toString());
        nuevoUsuario.setBirthdate(_fechaText.getText().toString());
        nuevoUsuario.setEmail(_emailText.getText().toString());
        //nuevoUsuario.setHeight(Integer.parseInt(_alturaText.getText().toString()));
        int gender = 0;
        if(_radioGroup.getCheckedRadioButtonId() == R.id.radio_male) {
            gender = 0;
        }
        else if(_radioGroup.getCheckedRadioButtonId() == R.id.radio_female) {
            gender = 1;
        }
        nuevoUsuario.setGender(gender);
        nuevoUsuario.setWeight(Integer.parseInt(_pesoText.getText().toString()));
        nuevoUsuario.setSecret(_passwordText.getText().toString());

        Log.d("TAG", "RUNNING Post...");
        new PostUser().execute(nuevoUsuario);
        Log.d("TAG", "FINISH Post...");
    }


    public void onSignupSuccess(String name, String email, String pass, String dni) {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);

        prefs = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Nombre", name);
        editor.putString("Email", email);
        editor.putString("Password", pass);
        editor.putString("DNI", dni);
        editor.commit();

        finish();
    }

    public void onSignupFailed() {
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String repassword = _repasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("Al menos 3 caracteres");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("Contraseña inválida: entre 4 y 10 caracteres alfanuméricos");
            valid = false;
        } else if (password.compareTo(repassword) != 0) {
            _passwordText.setError("Las contraseñas no coinciden");
            _repasswordText.setError("Las contraseñas no coinciden");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private void initFields() {
        _nameText = (EditText) findViewById(R.id.input_name);
        _surnamesText = (EditText) findViewById(R.id.input_surname);
        _dniText = (EditText) findViewById(R.id.input_dni);
        _fechaText = (EditText) findViewById(R.id.input_fechaNac);
        _emailText = (EditText) findViewById(R.id.input_email);
        _radioGroup = (RadioGroup) findViewById(R.id.radioGroup_gender);
        //_alturaText = (EditText) findViewById(R.id.input_altura);
        _pesoText = (EditText) findViewById(R.id.input_peso);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _repasswordText = (EditText) findViewById(R.id.input_repassword);
        _signupButton = (Button) findViewById(R.id.btn_signup);
        _loginButton = (Button) findViewById(R.id.btn_login);

        _radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

            }
        });

    }

    private static Uri.Builder buildUriUser(User user) {
        Uri.Builder builder = new Uri.Builder();
        // builder.appendQueryParameter("id", "5");
        builder.appendQueryParameter("user_id", user.getUser_id());
        builder.appendQueryParameter("name", user.getName());
        builder.appendQueryParameter("surnames", user.getSurnames());
        builder.appendQueryParameter("dni", user.getDni());
        builder.appendQueryParameter("birthdate", user.getBirthdate());
        builder.appendQueryParameter("email", user.getEmail());
        //builder.appendQueryParameter("height", Integer.toString(user.getHeight()));
        builder.appendQueryParameter("weight", Integer.toString(user.getWeight()));
        builder.appendQueryParameter("password", user.getSecret());

        Uri.Builder builderf = new Uri.Builder();
        builderf.appendQueryParameter("user", builder.toString());
        //Log.d("TAG", builder.toString());


        return builderf;
    }

    /**
     * Class responsible for making the request to the ApiRest and print the user information
     */
    private class PostUser extends AsyncTask<User, Void, Void> {

        //private static final String MY_IP = "10.0.2.2";
        //private static final String MY_IP = "10.0.2.2:5000/malarm/api/";
        private static final String MY_IP = "147.96.80.89:5000/malarm/api/";
        private static final String MY_URL = "http://" + MY_IP + "users/"; //OJO!! No usar la 127.0.0.1

        /**
         * This function realizes the request (GET) with the correct URL
         * @param params
         * @return the response (the json with the user information)
         */
        @Override
        protected Void doInBackground(User... params) {
            URL url;
            try {
                url = new URL(MY_URL);
                //HttpsURLConnection conn = (HttpsURLConnection) url.openConnection(); OJO!! descomentar cuando usemos Https
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true); // 'true' para POST y PUT
                Log.d("TAG", "Conexion...");


                //Uri.Builder builder = buildUriUser(params[0]); // añade los parametros de la query
                //String query = builder.build().getEncodedQuery();   // creamos la query
                //String query = "user_id=5&name=manu&surnames=marsan&dni=11&birthdate=07-05-1992&email=prueba@ucm.es&height=11&weight=22&password=aaaa";

                String query = createJson(params[0]);

                OutputStream os = conn.getOutputStream();

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                //Log.d("TAG", "Query:" + query);
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
            return null;
        }



        private String createJson(User user) {
            JSONObject jo = new JSONObject();
            JSONObject jo2 = new JSONObject();
            try {
                jo.put("name", user.getName());
                jo.put("lastname", user.getSurnames());
                jo.put("idnumber", user.getDni());
                jo.put("birthday", user.getBirthdate());
                jo.put("email", user.getEmail());
                jo.put("gender", Integer.toString(user.getGender()));
                jo.put("weight", Integer.toString(user.getWeight()));
                jo.put("password", user.getSecret());

                jo.put("secret", user.getSecret()); // secret de prueba

                jo2.put("user", jo);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return jo2.toString();
        }
    }
}