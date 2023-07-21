package com.stcu.appcolectivo.presenter;

import android.app.Activity;
import android.content.Context;
import android.net.NetworkInfo;

import com.stcu.appcolectivo.interfaces.MainInterface;
import com.stcu.appcolectivo.model.Colectivo;
import com.stcu.appcolectivo.model.Linea;
import com.stcu.appcolectivo.model.MainModel;
import com.stcu.appcolectivo.model.Recorrido;
import com.stcu.appcolectivo.ui.Coordenada;

import java.util.ArrayList;
import java.util.List;


public class MainPresenter implements MainInterface.Presenter {

    private MainInterface.View view;
    //model
    private MainInterface.Model model;

    public MainPresenter(MainInterface.View view, Context mContext, Activity mActivity){
        this.view = view;
        //nuevo model
        model = new MainModel(this,mContext, mActivity);
    }

    @Override
    public void enviarInicioServicioAServidor(String seleccionLin, String seleccionCol, Long fechaUbicacionI) {
        if(view != null){
            model.enviarInicioServicioAServidor(seleccionLin, seleccionCol, fechaUbicacionI);
        }
    }

//    @Override
//    public List<String> consultaLineasActivas() {
//        List<String> lineasActivas = model.consultaLineasActivas();
//        return lineasActivas;
//    }

    @Override
    public List<Linea> consultaLineasActivas() {
        List<Linea> lineasActivas = model.consultaLineasActivas();
        return lineasActivas;
    }

//    @Override
//    public List<String> consultaColectivosActivos() {
//        List<String> colectivosActivos = model.consultaColectivosActivos();
//        return colectivosActivos;
//    }
    @Override
    public List<Colectivo> consultaColectivosActivos() {
        List<Colectivo> colectivosActivos = model.consultaColectivosActivos();
        return colectivosActivos;
    }

    @Override
    public List<Recorrido> consultaRecorridoActivos(String denomLinea) {
        List<Recorrido> recorridosActivos = model.consultaRecorridosActivos(denomLinea);
        return recorridosActivos;
    }


    public NetworkInfo isNetAvailable() {
        return model.isNetAvailable();
    }

    @Override
    public List<Coordenada> consultaTrayectoASimular(String seleccionLin2) {
        List<Coordenada> coordenadasSim = new ArrayList<Coordenada>();
        if(view != null){
            coordenadasSim = model.consultaTrayectoASimular(seleccionLin2);
        }
        return coordenadasSim;
    }

    @Override
    public void showUbicacion(String strLatitud, String strLongitud) {
        if(view!=null){
            view.showUbicacion(strLatitud, strLongitud);
        }
    }

    @Override
    public void obtenerUbicacion() {
        if(view != null){
            model.obtenerUbicacion();
        }
    }

    @Override
    public void showResponseInicioServicioOk(String response, String seleccionLin, String seleccionCol, Long fechaUbicacionI) {
        if(view!=null){
            view.showResponseInicioServicioOk(response, seleccionLin, seleccionCol, fechaUbicacionI);
        }
    }

//    @Override
//    public void showResponseInicioServicioError(String error) {
//        if(view!=null){
//            view.showResponseInicioServicioError(error);
//        }
//    }

    @Override
    public void makeRequestPostSimulacion(String seleccionLin2, String seleccionCol2, String latitud, String longitud) {
        if(view != null){
            model.makeRequestPostSimulacion(seleccionLin2, seleccionCol2, latitud, longitud);
        }
    }

    @Override
    public void showResponsePostSimulacionOk(String response, String seleccionLin, String seleccionCol, String latInicial, String lngInicial) {
        if(view!=null){
            view.showResponsePostSimulacionOk( response,  seleccionLin,  seleccionCol,  latInicial,  lngInicial);
        }
    }

//    @Override
//    public void showResponsePostSimulacionError(String error) {
//        if(view!=null){
//            view.showResponsePostSimulacionError(error);
//        }
//    }

    @Override
    public void makeRequestPostFin(String seleccionLin, String seleccionCol, String lat, String lng) {
        if(view != null){
            model.makeRequestPostFin(seleccionLin, seleccionCol, lat, lng);
        }
    }

    @Override
    public void showResponse(String response) {
        if(view!=null){
            view.showResponse(response);
        }
    }

    @Override
    public void showResponseError(String error) {
        if(view!=null){
            view.showResponseError(error);
        }
    }

//    @Override
//    public void showListadoLineas(List<String> listadoLineas) {
//        if(view!=null){
//            view.showListadoLineas(listadoLineas);
//        }
//    }
//
//    @Override
//    public void showListadoColectivos(List<String> listadoColectivos) {
//        if(view!=null){
//            view.showListadoColectivos(listadoColectivos);
//        }
//    }

//    @Override
//    public boolean isOnlineNet() {
//        boolean result = false;
//
//        //este es cuando envia el dato que necesita
//        if(view != null){
//            result = model.isOnlineNet();
//        }
//        return result;
//    }
}
