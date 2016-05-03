package com.example.android.prueba.models;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by Diego on 29/04/2016.
 */
public class New {
    private String noticia_id;
    private int level;
    private String description;
    private String date;


    public String getNoticia_id() {
        return noticia_id;
    }

    public void setNoticia_id(String noticia_id) {
        this.noticia_id = noticia_id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    /**
     * This function builds the New from the json. It uses the Gson library
     * @param json
     * @return the New correctly built
     */
    public static New buildNew(String json) {
        Log.d("TAG", json);

        Gson gson = new Gson();
        Type type = new TypeToken<User>(){}.getType();
        return gson.fromJson(json, type);
    }
}
