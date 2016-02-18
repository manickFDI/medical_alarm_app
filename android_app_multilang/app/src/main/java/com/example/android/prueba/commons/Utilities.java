package com.example.android.prueba.commons;

import android.app.ActivityManager;
import android.content.Context;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Manuel on 13/12/2015.
 */
public class Utilities {

    public static String DatetoString() {

        String fecha ="";
        Calendar c = Calendar.getInstance();
        int anio = c.get(Calendar.YEAR); //obtenemos el año
        int mes = c.get(Calendar.MONTH); //obtenemos el mes
        //Los meses se presentan del 0 al 11 por lo cual para su presentación sumaremos 1 a la variable entera MES.
        mes = mes + 1;
        int dia = c.get(Calendar.DAY_OF_MONTH); // obtemos el día.
        fecha = (String.valueOf(dia) + "-" + String.valueOf(mes) + "-" + String.valueOf(anio));

        return fecha;
    }

}
