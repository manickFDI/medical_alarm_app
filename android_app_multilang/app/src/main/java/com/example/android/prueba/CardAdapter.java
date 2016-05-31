package com.example.android.prueba;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Adapter for the Info View with two fields:
 * a textView with the info
 * a textView with the gravedad
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<CardInfo> items;
    private String dni;
    //private FragmentManager manager;

    private static ClickListener clickListener; //ClickListener es una interfaz creada al final de esta clase

    public CardAdapter(List<CardInfo> items, String dni) {
        this.items = items;
        this.dni = dni;
    }

    /*
    public CardAdapter(List<CardInfo> items, FragmentManager m) {
        this.items = items;
        this.manager = m;
    }
    */


    @Override
    public int getItemCount() {
        if(items != null)
            return items.size();
        else
            return 0;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card, viewGroup, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CardViewHolder viewHolder, int i) {
        viewHolder.info.setText(items.get(i).getDescription());
        viewHolder.gravedad.setText("Riesgo: " + items.get(i).getGravedad());
        viewHolder.fecha.setText(items.get(i).getDate());
        viewHolder.imagen.setImageResource(items.get(i).getIdImagen());
        //viewHolder.setFragmentManager(this.manager);
    }

    /*
   Delete an element from the item list
    */
    public void removeItem(int position) {
        items.remove(position);
        notifyDataSetChanged();

        //new DeleteNewFromUser().execute(this.dni);
    }

    public CardInfo getCardByPosition(int position) {
        return items.get(position);
    }


    /*
    Clear all the list
     */
    public void clear(){
        items.clear();
        notifyDataSetChanged();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Campos respectivos de un item
        public TextView info;
        public TextView gravedad;
        public TextView fecha;
        public ImageView imagen;
        public ImageButton icon;
        //private FragmentManager manager;

        public CardViewHolder(View v) {
            super(v);
            info = (TextView) v.findViewById(R.id.infoCard);
            gravedad = (TextView) v.findViewById(R.id.gravedad);
            fecha = (TextView) v.findViewById(R.id.textFecha);
            imagen = (ImageView) v.findViewById(R.id.color_info);
            icon = (ImageButton) v.findViewById(R.id.btnEliminar);
            icon.setOnClickListener(this);

            //onClick Listener
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //FragmentCardInfo fragment = new FragmentCardInfo();
                    //FragmentManager fragmentManager = getFragmentManager();

                    /*
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.contenedor_principal, FragmentCardInfo.newInstance());
                    transaction.addToBackStack("cardAdapter");
                    transaction.commit();
                    */

                    clickListener.onItemClick(getAdapterPosition(), v);

                    //Toast toast = Toast.makeText(v.getContext(), gravedad.getText(), Toast.LENGTH_SHORT);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    //toast.show();

                }
            });
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            int new_id = getCardByPosition(getAdapterPosition()).getNews_id();
            new DeleteNewTask().execute(new_id);

            removeItem(getAdapterPosition());
            Toast toast = Toast.makeText(v.getContext(), "Noticia eliminada correctamente", Toast.LENGTH_LONG);
            //toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        /*
        public void setFragmentManager(FragmentManager m) {
            this.manager = m;
        }
        */
    }


    public void setOnItemClickListener(ClickListener clickListener) {
        CardAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }



    private class DeleteNewTask extends AsyncTask<Integer, Void, Void> {

        //private static final String MY_IP = "10.0.2.2";
        private static final String MY_IP = "147.96.80.89";
        private static final String MY_URL = "http://" + MY_IP + ":5000/malarm/api/news/"; //OJO!! No usar la 127.0.0.1

        @Override
        protected Void doInBackground(Integer... params) {
            Log.d("TAG", "Deleting new...");

            try {
                URL url = new URL(MY_URL);
                Log.d("TAG", "URL: " + url.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("DELETE");

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream ());

                JSONObject jsonParam = new JSONObject();
                JSONObject jsonAux = new JSONObject();
                jsonAux.put("user_dni", dni);
                jsonAux.put("news_id", params[0]);
                jsonParam.put("user_news", jsonAux);

                wr.writeBytes(jsonParam.toString());

                wr.flush();
                wr.close();

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    //Log.d("TAG", "Response doInBackgound: " + response);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

    }
}
