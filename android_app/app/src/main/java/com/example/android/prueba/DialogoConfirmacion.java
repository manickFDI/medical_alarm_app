package com.example.android.prueba;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * This class has been used for the calls of the pop ups dialogs
 * extends DialogFragment and create the message
 */
public class DialogoConfirmacion extends DialogFragment {

    private boolean logout;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("¿Está seguro que desea salir de la aplicación?")
                .setTitle("Logout")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("Dialogos", "Se ha desconectado con éxito");
                        logout = true;
                        getActivity().finish(); //duda
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("Dialogos", "");
                        logout = false;
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public boolean isLogout() {
        return logout;
    }
}
