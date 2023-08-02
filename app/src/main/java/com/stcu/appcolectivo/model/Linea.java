package com.stcu.appcolectivo.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Linea implements Parcelable {
    String denominacion;
    String descripcion;
    boolean enServicio;
    Long idLinea;

    public Linea(Long idLinea, String denominacion, String descripcion, boolean enServicio) {
        this.idLinea = idLinea;
        this.denominacion = denominacion;
        this.descripcion = descripcion;
        this.enServicio = enServicio;
    }

    public Linea(){}

    protected Linea(Parcel in) {
        idLinea = in.readLong();
        denominacion = in.readString();
        descripcion = in.readString();
        enServicio = in.readByte() != 0;
    }

    public static final Creator<Linea> CREATOR = new Creator<Linea>() {
        @Override
        public Linea createFromParcel(Parcel in) {
            return new Linea(in);
        }

        @Override
        public Linea[] newArray(int size) {
            return new Linea[size];
        }
    };

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isEnServicio() {
        return enServicio;
    }

    public void setEnServicio(boolean enServicio) {
        this.enServicio = enServicio;
    }

    public Long getIdLinea() {
        return idLinea;
    }

    public void setIdLinea(Long idLinea) {
        this.idLinea = idLinea;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeLong(idLinea);
        parcel.writeString(denominacion);
        parcel.writeString(descripcion);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.writeBoolean(enServicio);
        }
    }
}
