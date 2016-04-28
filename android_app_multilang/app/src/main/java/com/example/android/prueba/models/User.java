package com.example.android.prueba.models;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by Diego on 24/11/2015.
 */
public class User {
    private String user_id;
    private String name;
    private String lastname;
    private String idnumber;
    private String birthday;
    private String email;
    //private int height;
    private int gender;
    private int weight;
    private String secret;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurnames() {
        return lastname;
    }

    public void setSurnames(String surnames) {
        this.lastname = surnames;
    }

    public String getDni() {
        return idnumber;
    }

    public void setDni(String dni) {
        this.idnumber = dni;
    }

    public String getBirthdate() {
        return birthday;
    }

    public void setBirthdate(String birthdate) {
        this.birthday = birthdate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int g) {
        this.gender = g;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getPassword() {
        return secret;
    }

    public void setPassword(String password) {
        this.secret = password;
    }

    /**
     * This function builds the User from the json. It uses the Gson library
     * @param json
     * @return the User correctly built
     */
    public static User buildUser(String json) {
        Log.d("TAG", json);

        Gson gson = new Gson();
        Type type = new TypeToken<User>(){}.getType();
        return gson.fromJson(json, type);
    }
}
