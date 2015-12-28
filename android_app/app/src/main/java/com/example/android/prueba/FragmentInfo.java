package com.example.android.prueba;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.prueba.apiConnections.ApiService;
import com.example.android.prueba.commons.Utilities;

import java.util.ArrayList;
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
    private CardAdapter adapter;
    private RecyclerView.LayoutManager lManager;
    private TextView fecha;
    private SwipeRefreshLayout refreshLayout;

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

        String fechaFormat = Utilities.DatetoString();

        //final String[] riesgos = {"ALTO", "MEDIO", "BAJO", " - "};

        //final int[] iconos = {R.drawable.a, R.drawable.n, R.drawable.r, R.drawable.v};

        // creamos el arrayList vacio
        final List<CardInfo> items = new ArrayList<>();

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

        //Obtener el campo fecha para setearla
        fecha = (TextView)view.findViewById(R.id.fechaActual);
        fecha.setText(fechaFormat);

        // Obtener el Recycler
        recycler = (RecyclerView)view.findViewById(R.id.recycler);
        //si el tamaño es fijo
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this.getContext());
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        adapter = new CardAdapter(items);

        adapter.setOnItemClickListener(new CardAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.d("TAG", "onItemClick position: " + position);

                //Lanzar startActivity con un intent que sea el de mostrar info detallada
                CardInfo card = adapter.getCardByPosition(position);
                Intent intent = new Intent(getActivity(), Fragment_cardInfo.class);
                //Bundle bundle = new Bundle();
                //bundle.putDouble("latitude", this.getCurrentValues().getLocLat());
                //intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        recycler.setAdapter(adapter);



        /*// Obtener el refreshLayout
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);

        // Seteamos los colores que se usarán a lo largo de la animación
        refreshLayout.setColorSchemeResources(
                R.color.primaryColor,
                R.color.primaryDarkColor,
                R.color.accentColor);

        // Iniciar la tarea asíncrona al revelar el indicador
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new HackingBackgroundTask().execute();
                    }
                }
        );*/

        return view;
    }

    /*private class HackingBackgroundTask extends AsyncTask<Void, Void, List<CardInfo>> {

        static final int DURACION = 3 * 1000; // 3 segundos de carga

        @Override
        protected List<CardInfo> doInBackground(Void... params) {
            // Simulación de la carga de items
            try {
                Thread.sleep(DURACION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Retornar en nuevos elementos para el adaptador
            try {
                return this.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<CardInfo> result) {
            super.onPostExecute(result);

            // Parar la animación del indicador
            refreshLayout.setRefreshing(false);
        }

    }*/
}
