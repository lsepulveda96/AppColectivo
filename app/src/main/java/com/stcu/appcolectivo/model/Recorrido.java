package com.stcu.appcolectivo.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Recorrido implements Parcelable {

    private long id;
    private String denominacion;
    private boolean activo;

    public Recorrido(){};

    public Recorrido(long id, String denominacion, boolean activo) {
        this.id = id;
        this.denominacion = denominacion;
        this.activo = activo;
    }

    protected Recorrido(Parcel in) {
        id = in.readLong();
        denominacion = in.readString();
        activo = in.readByte() != 0;
    }

    public static final Creator<Recorrido> CREATOR = new Creator<Recorrido>() {
        @Override
        public Recorrido createFromParcel(Parcel in) {
            return new Recorrido(in);
        }

        @Override
        public Recorrido[] newArray(int size) {
            return new Recorrido[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(denominacion);
        parcel.writeByte((byte) (activo ? 1 : 0));
    }
}
