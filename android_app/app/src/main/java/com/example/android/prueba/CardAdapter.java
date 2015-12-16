package com.example.android.prueba;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Adapter for the Info View with two fields:
 * a textView with the info
 * a textView with the gravedad
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<CardInfo> items;

    public CardAdapter(List<CardInfo> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card, viewGroup, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CardViewHolder viewHolder, int i) {
        viewHolder.info.setText(items.get(i).getTexto());
        viewHolder.gravedad.setText("Riesgo: " + String.valueOf(items.get(i).getGravedad()));
        viewHolder.fecha.setText(items.get(i).getFecha());
        viewHolder.imagen.setImageResource(items.get(i).getIdImagen());
    }

    /*
   Delete an element from the item list
    */
    public void removeItem(int position) {
        items.remove(position);
        notifyDataSetChanged();
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
                    /*Fragment_cardInfo fragment = new Fragment_cardInfo();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.swipeRefresh, this)
                            .commit();*/
                    Toast toast = Toast.makeText(v.getContext(), gravedad.getText(), Toast.LENGTH_SHORT);
                    //toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

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
            removeItem(getAdapterPosition());
            Toast toast = Toast.makeText(v.getContext(), "Card eliminada", Toast.LENGTH_LONG);
            //toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }
}
