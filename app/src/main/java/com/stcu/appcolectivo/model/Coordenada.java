package com.stcu.appcolectivo.model;

public class Coordenada {

    private double latitud;
    private double longitud;
    private String direccion;
    private int codigo;

    public Coordenada() {}

    public Coordenada(double latitud, double longitud ) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public double getLatitud() { return this.latitud; }
    public void setLatitud( double l ) { this.latitud = l; }

    public double getLongitud() { return this.longitud; }
    public void setLongitud( double l ) { this.longitud = l; }

    public String getDireccion() { return direccion; }
    public void setDireccion( String direccion ) { this.direccion = direccion; }

    public int getCodigo() { return this.codigo; }
    public void setCodigo( int cod ) { this.codigo = cod; }
}