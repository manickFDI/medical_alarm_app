package com.example.android.prueba;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Manuel on 20/12/2015.
 */
public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    private EditText _nameText;
    private EditText _dniText;
    private EditText _fechaText;
    private EditText _emailText;
    private EditText _alturaText;
    private EditText _pesoText;
    private EditText _passwordText;
    private EditText _repasswordText;
    private Button _signupButton;
    private Button _loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        initFields();

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
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

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
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
        _dniText = (EditText) findViewById(R.id.input_dni);
        _fechaText = (EditText) findViewById(R.id.input_fechaNac);
        _emailText = (EditText) findViewById(R.id.input_email);
        _alturaText = (EditText) findViewById(R.id.input_altura);
        _pesoText = (EditText) findViewById(R.id.input_peso);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _repasswordText = (EditText) findViewById(R.id.input_repassword);
        _signupButton = (Button) findViewById(R.id.btn_signup);
        _loginButton = (Button) findViewById(R.id.btn_login);

    }
}