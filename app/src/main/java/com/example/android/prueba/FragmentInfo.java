package com.example.android.prueba;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentInfo.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
/**
 * Fragmento para la pestaña "DIRECCIONES" De la sección "Mi Cuenta"
 */
public class FragmentInfo extends Fragment {

    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    public FragmentInfo() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_info, container, false);

        //Creamos los strings

        final String[] texto = {"Epidemia de Ébola en Madrid", "Dos casos de Tosferina hacen dar la alerta en España",
        "Posibles contagios de gripe A en Barcelona", "Epidemia de Fiebre amarilla en Zaragoza",
        "La gripe se extiende por el territorio español", "Erradicada la epidemia de Ébola en España"};

        final int[] codigoRiesgo = {0, 0, 2, 1, 1, 3};

        String fechaFormat = DatetoString();

        //final String[] riesgos = {"ALTO", "MEDIO", "BAJO", " - "};

        //final int[] iconos = {R.drawable.a, R.drawable.n, R.drawable.r, R.drawable.v};

        // creamos el arrayList vacio
        List<CardInfo> items = new ArrayList<>();

        for (int i = 0; i < texto.length; i++) {
            CardInfo card = new CardInfo();
            card.setTexto(texto[i]);
            card.setCodigoRiesgo(codigoRiesgo[i]);
            card.setFecha(fechaFormat);
            switch (codigoRiesgo[i]) {
                case 0:
                    card.setGravedad("ALTO");
                    card.setIdImagen(R.drawable.r);
                    break;
                case 1:
                    card.setGravedad("MEDIO");
                    card.setIdImagen(R.drawable.n);
                    break;
                case 2:
                    card.setGravedad("BAJO");
                    card.setIdImagen(R.drawable.a);
                    break;
                case 3:
                    card.setGravedad(" - ");
                    card.setIdImagen(R.drawable.v);
                    break;
                default: break;
            }

            items.add(card);

        }

        // Obtener el Recycler
        recycler = (RecyclerView)view.findViewById(R.id.recycler);
        //si el tamaño es fijo
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this.getContext());
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        adapter = new CardAdapter(items);
        recycler.setAdapter(adapter);
        return view;
    }

    public String DatetoString() {

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
