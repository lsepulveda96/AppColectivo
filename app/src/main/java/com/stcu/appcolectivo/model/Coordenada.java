package com.stcu.appcolectivo.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Coordenada implements Parcelable {

    private double latitud;
    private double longitud;
    private String direccion;
    private int codigo;

    public Coordenada() {}

    public Coordenada(double latitud, double longitud ) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    protected Coordenada(Parcel in) {
        latitud = in.readDouble();
        longitud = in.readDouble();
        direccion = in.readString();
        codigo = in.readInt();
    }

    public static final Creator<Coordenada> CREATOR = new Creator<Coordenada>() {
        @Override
        public Coordenada createFromParcel(Parcel in) {
            return new Coordenada(in);
        }

        @Override
        public Coordenada[] newArray(int size) {
            return new Coordenada[size];
        }
    };

    public double getLatitud() { return this.latitud; }
    public void setLatitud( double l ) { this.latitud = l; }

    public double getLongitud() { return this.longitud; }
    public void setLongitud( double l ) { this.longitud = l; }

    public String getDireccion() { return direccion; }
    public void setDireccion( String direccion ) { this.direccion = direccion; }

    public int getCodigo() { return this.codigo; }
    public void setCodigo( int cod ) { this.codigo = cod; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeDouble(latitud);
        parcel.writeDouble(longitud);
        parcel.writeString(direccion);
        parcel.writeInt(codigo);
    }
}