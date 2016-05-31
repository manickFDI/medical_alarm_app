package com.example.android.prueba;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.prueba.commons.Utilities;
import com.example.android.prueba.models.New;
import com.example.android.prueba.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
    private ImageButton deleteNewButton;
    private View view;

    private List<CardInfo> newsList;
    private String dni;

    public FragmentInfo() {
        this.newsList = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(this.dni == null) {
            SharedPreferences prefs = this.getActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
            if(prefs.contains("DNI"))
                this.dni = prefs.getString("DNI", "");
        }

        this.view = inflater.inflate(R.layout.fragment_info, container, false);

        /*refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);

                for(int i = 0; i < 10000; i++) {
                    Log.d("TAG", "prueba swipe info " + i);
                }
                refreshLayout.setRefreshing(false);
            }
        });*/

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        refreshLayout.setColorSchemeResources(
                R.color.primaryColor,
                R.color.primaryDarkColor,
                R.color.accentColor);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);

                //SharedPreferences prefs = getActivity().getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
                //String dni = prefs.getString("DNI", "");
                newsList.clear();
                new UpdateNewsTask().execute(dni);

                refreshLayout.setRefreshing(false);
            }
        });

        //Creamos los strings

        /*final String[] texto = {"Epidemia de Ébola en Madrid", "Dos casos de Tosferina hacen dar la alerta en España",
        "Posibles contagios de gripe A en Barcelona", "Epidemia de Fiebre amarilla en Zaragoza",
        "La gripe se extiende por el territorio español", "Erradicada la epidemia de Ébola en España"};

        final int[] codigoRiesgo = {0, 0, 2, 1, 1, 3};

        String fechaFormat = Utilities.DatetoString();*/

        // creamos el arrayList vacio
        /*final List<CardInfo> items = new ArrayList<>();

        for (int i = 0; i < newsList.size(); i++) {
            CardInfo card = new CardInfo();
            card.setDescription(newsList.get(i).getDescription());
            card.setLevel(newsList.get(i).getLevel());
            card.setDate(newsList.get(i).getDate());
            switch (newsList.get(i).getLevel()) {
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

        }*/

        //Obtener el campo fecha para setearla
        //fecha = (TextView)view.findViewById(R.id.fechaActual);
        //fecha.setText(fechaFormat);




        // Obtener el Recycler
        recycler = (RecyclerView)view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true); //si el tamaño es fijo

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this.getContext());
        recycler.setLayoutManager(lManager);

/*

        // Crear un nuevo adaptador
        //while(newsList.size() == 0) {}
        /*adapter = new CardAdapter(this.newsList, this.dni);

        adapter.setOnItemClickListener(new CardAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.d("TAG", "onItemClick position: " + position);
                //Lanzar startActivity con un intent que sea el de mostrar info detallada
                CardInfo card = adapter.getCardByPosition(position);
                Intent intent = new Intent(getActivity(), FragmentCardInfo.class);
                Bundle bundle = new Bundle();
                bundle.putString("text", card.getDescription());
                bundle.putString("severity", card.getGravedad());
                bundle.putString("date", card.getDate());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        recycler.setAdapter(adapter);*/


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

        //newsList.clear();
        //if(dni != null) {
            //Log.d("TAG", "user es distinto de nuuuuuulllll");
            new UpdateNewsTask().execute(this.dni);
        //}


        return view;
    }

    public void loadUser(String dni) {
        this.dni = dni;
        //new UpdateNewsTask().execute(this.dni);
    }




    private class UpdateNewsTask extends AsyncTask<String, Void, List<CardInfo>> {

        //private static final String MY_IP = "10.0.2.2";
        private static final String MY_IP = "147.96.80.89";
        private static final String MY_URL = "http://" + MY_IP + ":5000/malarm/api/news/"; //OJO!! No usar la 127.0.0.1

        protected List<CardInfo> doInBackground(String... params) {

            //Log.d("TAG", "Updating news...");

            StringBuilder result = new StringBuilder();
            List<CardInfo> ret = new ArrayList<>();

            try {

                URL url = new URL(MY_URL + "?user_dni=" + params[0]);
                //Log.d("TAG", "URL: " + url.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String response = "";
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    response = stringBuilder.toString();
                    //Log.d("TAG", "Response doInBackgound: " + response);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }

                /*if (!response.equals("")) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    // Acciones a realizar con el flujo de datos
                    //Log.d("TAG", in.toString());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    CardInfo curNew;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                        curNew = CardInfo.buildNew(line);
                        ret.add(curNew);
                    }
                }*/

                try {
                    String json = new JSONTokener(response).next(2); // Cogemos los 2 primeros caracteres
                    if(!(json.equals("{}"))) { // json vacio (nuevo usuario o usuario sin noticias)
                        JSONArray jsonNews = (JSONArray) new JSONTokener(response).nextValue();
                        for(int i = 0; i < jsonNews.length(); ++i) {
                            JSONObject obj = jsonNews.getJSONObject(i);
                            int new_id = obj.getInt("news_id");
                            String description = obj.getString("description");
                            JSONObject contagion = obj.getJSONObject("contagion");
                            int level = contagion.getInt("level");
                            String date = contagion.getString("date");

                            CardInfo card = new CardInfo(new_id, description, level, date);
                            ret.add(card);
                        }
                    }
                    else {
                        Log.d("TAG", "response nuevo usuario vacio");
                    }
                    newsList = ret;
                } catch (JSONException e) {
                    return ret;
                }

            } catch (MalformedURLException e) {
                return ret;
            } catch (IOException e) {
                return ret;
            }
            return ret;
        }

        protected void onPostExecute(List<CardInfo> response) {
            adapter = new CardAdapter(newsList, dni);

            adapter.setOnItemClickListener(new CardAdapter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    //Log.d("TAG", "onItemClick position: " + position);
                    //Lanzar startActivity con un intent que sea el de mostrar info detallada
                    CardInfo card = adapter.getCardByPosition(position);
                    Intent intent = new Intent(getActivity(), FragmentCardInfo.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("text", card.getDescription());
                    bundle.putString("severity", card.getGravedad());
                    bundle.putString("date", card.getDate());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

/*
            // Obtener el Recycler
            recycler = (RecyclerView)view.findViewById(R.id.recycler);
            recycler.setHasFixedSize(true); //si el tamaño es fijo

            // Usar un administrador para LinearLayout
            lManager = new LinearLayoutManager(getContext());
            recycler.setLayoutManager(lManager);
*/
            recycler.setAdapter(adapter);
        }
    }
}
