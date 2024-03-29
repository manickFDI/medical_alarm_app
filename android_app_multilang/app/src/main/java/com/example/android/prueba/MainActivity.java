package com.example.android.prueba;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.prueba.sensors.SensorService;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ListView lista;
    private ArrayAdapter<String> adapter;
    private Locale myLocale; // Locale es una clase de android que trabaja con la configuracion de la app en temas como lenaguaje, localizacion etc

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);

        // Comprobamos el idioma en el que debe estar la app
        if(prefs.contains("Language")) { // si no lo contiene pilla el que este predefinido en el movil
            String locale = prefs.getString("Language", ""); //getString(key, defaulValue)
            this.changeMyLocale(locale);
            Log.d("TAG", "Idioma de la app: " + locale);
        }

        setContentView(R.layout.activity_main);


        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        //progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Iniciando...");
        progressDialog.show();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 3000);

        if(!prefs.contains("DNI")) {
            Intent intent = new Intent(this, LoginActivity.class);
            //startActivity(intent);
            startActivityForResult(intent, 1);
        }

        agregarToolbar();//encapsulación

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        // Cargar valores por defecto
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        if (navigationView != null) {
            prepararDrawer(navigationView);
            seleccionarItem(navigationView.getMenu().getItem(0)); // Seleccionar item por defecto
        }


        // PABLO
        Intent intent = new Intent(this, SensorService.class);
        startService(intent);


        /*SharedPreferences.Editor prefsEditor = prefs.edit();
        boolean initialized = prefs.getBoolean("sensors_status", false);
        if(initialized){
            Intent intentSensor = new Intent(this, SensorService.class);
            startService(intentSensor);
        }
        prefsEditor.putBoolean("sensors_initialized", initialized);
        prefsEditor.commit();*/


        // PRUEBA POST sensores
        //Intent intent = new Intent(this, ApiService.class);
        //startService(intent);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //si no queremos que salgan los puntitos quitamos esto
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void agregarToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // Poner ícono del drawer toggle
            ab.setHomeAsUpIndicator(R.drawable.drawer_toggle);
            ab.setDisplayHomeAsUpEnabled(true);
        }

    }

    private void prepararDrawer(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        seleccionarItem(menuItem);
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });

    }

    private void seleccionarItem(MenuItem itemDrawer) {
        Fragment fragmentoGenerico = null;
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (itemDrawer.getItemId()) {
            case R.id.item_miCuenta:
                fragmentoGenerico = new FragmentCuenta();
                break;
            case R.id.item_ajustes:
                // Fragmento para la sección ajustes
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.item_sobreNosotros:
                // Fragmento para la sección sobreNosotros
                fragmentoGenerico = new FragmentSobreNosotros();
                break;
            case R.id.item_soporte:
                // Iniciar actividad de soporte
                fragmentoGenerico = new FragmentSoporte();
                break;
            case R.id.item_desconexion:
                // Iniciar actividad de desconexion
                fragmentoGenerico = new FragmentDesconexion();
                break;
        }
        //cambiamos el contenido principal y ponemos en el RelativeLayout nuestro fragmento
        if (fragmentoGenerico != null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.contenedor_principal, fragmentoGenerico)
                    .commit();
        }

        // Setear título actual
        setTitle(itemDrawer.getTitle());
    }


    private void changeMyLocale(String locale) {
        //Configuramos el idioma cargado desde SharedPreferences
        myLocale = new Locale(locale);
        Locale.setDefault(myLocale);
        //Configuration config = new Configuration();
        Configuration config =getBaseContext().getResources().getConfiguration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }


}
