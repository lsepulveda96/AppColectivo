package com.stcu.appcolectivo.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Colectivo implements Parcelable {

    String unidad;
    String patente;
    String marca;


    public Colectivo(String unidad, String patente, String marca) {
        this.unidad = unidad;
        this.patente = patente;
        this.marca = marca;
    }

    public Colectivo(){}

    protected Colectivo(Parcel in) {
        unidad = in.readString();
        patente = in.readString();
        marca = in.readString();
    }

    public static final Creator<Colectivo> CREATOR = new Creator<Colectivo>() {
        @Override
        public Colectivo createFromParcel(Parcel in) {
            return new Colectivo(in);
        }

        @Override
        public Colectivo[] newArray(int size) {
            return new Colectivo[size];
        }
    };

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(unidad);
        parcel.writeString(patente);
        parcel.writeString(marca);
    }
}
