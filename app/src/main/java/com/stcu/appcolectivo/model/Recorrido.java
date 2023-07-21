package com.stcu.appcolectivo.model;

public class Recorrido {

    private long id;
    private String denominacion;
    private boolean activo;

    public Recorrido(){};

    public Recorrido(long id, String denominacion, boolean activo) {
        this.id = id;
        this.denominacion = denominacion;
        this.activo = activo;
    }

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
}
