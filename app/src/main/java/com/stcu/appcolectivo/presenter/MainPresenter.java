package com.stcu.appcolectivo.presenter;

import android.app.Activity;
import android.content.Context;
import android.net.NetworkInfo;

import com.stcu.appcolectivo.interfaces.MainInterface;
import com.stcu.appcolectivo.model.Colectivo;
import com.stcu.appcolectivo.model.Linea;
import com.stcu.appcolectivo.model.MainModel;
import com.stcu.appcolectivo.model.Recorrido;
import com.stcu.appcolectivo.model.Parada;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


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
    public void enviarInicioServicioAServidor(String seleccionLin, String seleccionCol, String seleccionRec, String lat, String lng) {
        if(view != null){
            model.enviarInicioServicioAServidor(seleccionLin, seleccionCol, seleccionRec, lat, lng);
        }
    }

//    @Override
//    public List<String> consultaLineasActivas() {
//        List<String> lineasActivas = model.consultaLineasActivas();
//        return lineasActivas;
//    }

    @Override
    public List<Linea> consultaLineasActivas() throws ExecutionException, InterruptedException, TimeoutException {
        List<Linea> lineasActivas = model.consultaLineasActivas();
        return lineasActivas;
    }

//    @Override
//    public List<String> consultaColectivosActivos() {
//        List<String> colectivosActivos = model.consultaColectivosActivos();
//        return colectivosActivos;
//    }
    @Override
    public List<Colectivo> consultaColectivosActivos() throws ExecutionException, InterruptedException, TimeoutException {
        List<Colectivo> colectivosActivos = model.consultaColectivosActivos();
        return colectivosActivos;
    }

    @Override
    public List<Recorrido> consultaRecorridoActivos(String denomLinea) throws ExecutionException, InterruptedException, TimeoutException {
        List<Recorrido> recorridosActivos = model.consultaRecorridosActivos(denomLinea);
        return recorridosActivos;
    }


    public NetworkInfo isNetAvailable() {
        return model.isNetAvailable();
    }

    @Override
    public List<Parada> consultaTrayectoASimular(String seleccionLin2, String seleccionRec2) throws JSONException, ExecutionException, InterruptedException, TimeoutException {
        List<Parada> coordenadasSim = new ArrayList<Parada>();
        if(view != null){
            coordenadasSim = model.consultaTrayectoASimular(seleccionLin2, seleccionRec2);
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
    public void showResponseInicioServicioOk(String response, String seleccionLin, String seleccionCol, String seleccionRec, Long fechaUbicacionI, String lat, String lng) {
        if(view!=null){
            view.showResponseInicioServicioOk(response, seleccionLin, seleccionCol, seleccionRec, fechaUbicacionI, lat, lng);
        }
    }

//    @Override
//    public void showResponseInicioServicioError(String error) {
//        if(view!=null){
//            view.showResponseInicioServicioError(error);
//        }
//    }

    @Override
    public void makeRequestPostSimulacion(String seleccionLin2, String seleccionCol2, String seleccionRec2, String latitud, String longitud, List<Parada> coordenadasSim) {
        if(view != null){
            model.makeRequestPostSimulacion(seleccionLin2, seleccionCol2, seleccionRec2, latitud, longitud, coordenadasSim);
        }
    }

    @Override
    public void showResponsePostSimulacionOk(String response, String seleccionLin, String seleccionCol, String seleccionRec, String latInicial, String lngInicial, List<Parada> coordenadasSim) {
        if(view!=null){
            view.showResponsePostSimulacionOk( response,  seleccionLin,  seleccionCol,  seleccionRec, latInicial,  lngInicial, coordenadasSim);
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
