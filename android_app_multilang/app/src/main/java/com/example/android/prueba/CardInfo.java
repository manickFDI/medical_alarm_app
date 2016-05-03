package com.example.android.prueba;

import android.util.Log;

import com.example.android.prueba.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * A public class to encapsulate a card with the associate information
 */
public class CardInfo {
    int news_id;
    String description;
    String gravedad;
    int idImagen;
    int level; //0-> alto, 1-> medio, 2-> bajo, 3->nulo
    String date;

    public CardInfo() {}

    public CardInfo(String description, String gravedad, int idImagen, String fecha) {
        this.description = description;
        this.gravedad = gravedad;
        this.idImagen = idImagen;
        this.date = fecha;
    }

    public CardInfo(int id, String descr, int lev, String fecha) {
        this.news_id = id;
        this.description = descr;
        this.level = lev;
        this.date = fecha;

        switch (this.level) {
            case 0:
                this.gravedad = "ALTO";
                this.idImagen = R.drawable.r;
                break;
            case 1:
                this.gravedad = "MEDIO";
                this.idImagen = R.drawable.n;
                break;
            case 2:
                this.gravedad = "BAJO";
                this.idImagen = R.drawable.a;
                break;
            case 3:
                this.gravedad = "-";
                this.idImagen = R.drawable.v;
                break;
            default: break;
        }
    }

    public int getNews_id() {
        return news_id;
    }

    public void setNews_id(int news_id) {
        this.news_id = news_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String texto) {
        this.description = texto;
    }

    public String getGravedad() {
        return gravedad;
    }

    public void setGravedad(String gravedad) {
        this.gravedad = gravedad;
    }

    public int getIdImagen() {
        return idImagen;
    }

    public void setIdImagen(int idImagen) {
        this.idImagen = idImagen;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    /**
     * This function builds the Card from the json. It uses the Gson library
     * @param json
     * @return the Card correctly built
     */
    public static CardInfo buildNew(String json) {
        Log.d("TAG", json);

        Gson gson = new Gson();
        Type type = new TypeToken<CardInfo>(){}.getType();
        return gson.fromJson(json, type);
    }
}
