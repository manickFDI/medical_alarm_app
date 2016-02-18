package com.example.android.prueba;

/**
 * A public class to encapsulate a card with the associate information
 */
public class CardInfo {
    String texto;
    String gravedad;
    int idImagen;
    int codigoRiesgo; //0-> alto, 1-> medio, 2-> bajo, 3->nulo
    String fecha;

    public CardInfo() {}

    public CardInfo(String texto, String gravedad, int idImagen, String fecha) {
        this.texto = texto;
        this.gravedad = gravedad;
        this.idImagen = idImagen;
        this.fecha = fecha;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
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

    public int getCodigoRiesgo() {
        return codigoRiesgo;
    }

    public void setCodigoRiesgo(int codigoRiesgo) {
        this.codigoRiesgo = codigoRiesgo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
