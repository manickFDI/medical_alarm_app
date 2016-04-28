package com.example.android.prueba;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
 * Created by Manuel on 20/12/2015.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    private EditText _dniText;
    private EditText _passwordText;
    private Button _loginButton;
    private TextView _signupButton;

    private User user;
    private boolean valid = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initFields();

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });


        _signupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        validate();
        /*if (!this.valid) {
            onLoginFailed();
            return;
        }*/

        //_loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        //saveSharedPreferences(this.user.getDni(), this.user.getPassword());

        //String dni = _dniText.getText().toString();
        //String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    private void saveSharedPreferences(String dni, String password) {
        SharedPreferences prefs = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("DNI", dni);
        editor.putString("Password", password);
        editor.commit();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        _loginButton.setEnabled(true);
    }

    public void validate() {
        //return true;

        //boolean valid = true;

        String dni = _dniText.getText().toString();
        String password = _passwordText.getText().toString();

        if (dni.isEmpty()){
            _dniText.setError("Introduzca un DNI");
            valid = false;
        }
        if (password.isEmpty()) {
            _passwordText.setError("Introduzca la contraseña");
            valid = false;
        }

        if (password.length() < 4 || password.length() > 10) {
            _passwordText.setError("Contraseña inválida: entre 4 y 10 caracteres alfanuméricos");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        //peticion API
        new LoadUserInfo().execute(_dniText.getText().toString());


        /*if((this.user.getPassword().compareTo(password) != 0)) {
            _passwordText.setError("Contraseña o email incorrectos");
            valid = false;
        }*/

        //return valid;

    }

    private void initFields(){
        _loginButton = (Button) findViewById(R.id.btn_login);
        _signupButton = (Button) findViewById(R.id.btn_signup);
        _dniText = (EditText) findViewById(R.id.input_dni);
        _passwordText = (EditText) findViewById(R.id.input_password);
    }


    private void validateResponse(String pass) {
        if((this.user.getPassword().compareTo(pass) != 0)) {
            _passwordText.setError("Contraseña o email incorrectos");
            valid = false;
        }
    }


    private class LoadUserInfo extends AsyncTask<String, Void, String> {

        private static final String MY_IP = "10.0.2.2";
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
                Log.d("TAG", "Peticion al API...");

                //String url = URL + params[0];
                String urlAux = MY_URL + params[0];
                Log.d("TAG", urlAux);

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
                    Log.d("TAG", "DNI: " + user.getDni());

                    Log.d("TAG", "PASS: " + user.getPassword());
                    validateResponse(user.getPassword());
                    saveSharedPreferences(user.getDni(), user.getPassword());

                    //_loginButton.setEnabled(false);

                    /*final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Cargando...");
                    progressDialog.show();*/

                    //saveSharedPreferences(this.user.getDni(), this.user.getPassword());

                    //String dni = _dniText.getText().toString();
                    //String password = _passwordText.getText().toString();

                    // TODO: Implement your own authentication logic here.

                    /*new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    // On complete call either onLoginSuccess or onLoginFailed
                                    onLoginSuccess();
                                    // onLoginFailed();
                                    progressDialog.dismiss();
                                }
                            }, 3000);*/



                    //validateResponse(user.getPassword());

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
        }

    }
}
