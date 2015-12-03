package com.example.android.prueba;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentEstado.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentEstado#newInstance} factory method to
 * create an instance of this fragment.
 */
/**
 * Fragmento para la pestaña "TARJETAS" de la sección "Mi Cuenta"
 */
public class FragmentEstado extends Fragment {

    public FragmentEstado() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estado, container, false);

        CardView cardPersona = (CardView)view.findViewById(R.id.estadoPersona);

        CardView cardZona = (CardView)view.findViewById(R.id.estadoZona);

        cardPersona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getActivity(), "Persona libre de contagio", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });

        cardZona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getActivity(), "Zona libre de contagio", Toast.LENGTH_SHORT);
                //toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });

        return view;
    }


}