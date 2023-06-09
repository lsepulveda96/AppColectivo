package com.stcu.appcolectivo.presenter;

import android.app.Activity;
import android.content.Context;

import com.stcu.appcolectivo.interfaces.TrayectoARecorrerInterface;
import com.stcu.appcolectivo.model.TrayectoARecorrerModel;
import com.stcu.appcolectivo.ui.Coordenada;

import java.util.List;


public class TrayectoARecorrerPresenter implements TrayectoARecorrerInterface.Presenter {

    private TrayectoARecorrerInterface.View view;
    //model
    private TrayectoARecorrerInterface.Model model;

    public TrayectoARecorrerPresenter(TrayectoARecorrerInterface.View view, Context mContext, Activity mActivity){
        this.view = view;
        //nuevo model
        model = new TrayectoARecorrerModel(this,mContext, mActivity);
    }

    @Override
    public List<Coordenada> consultaParadasRecorrido(String linea) {
        List<Coordenada> paradasRecorrido = model.consultaParadasRecorrido(linea);
        return paradasRecorrido;
    }

    @Override
    public void showResponse(String response) {
        if(view!=null){
            view.showResponse(response);
        }
    }

    @Override
    public void makeRequestPostEnvio(String linea, String colectivo, String lat, String lng) {
        if(view != null){
            model.makeRequestPostEnvio(linea, colectivo, lat, lng);
        }
    }

    @Override
    public void makeRequestPostEnvioInforme(String linea, String colectivo, String lat, String lng, String fechaUbicacionStr, String difTotalStr) {
        if(view != null){
            model.makeRequestPostEnvioInforme(linea, colectivo, lat, lng, fechaUbicacionStr, difTotalStr);
        }
    }

    @Override
    public void makeRequestPostActInforme(String linea, String colectivo, String lat, String lng, String fechaUbicacionStr, String difTotalStr) {
        if(view != null){
            model.makeRequestPostActInforme(linea, colectivo, lat, lng, fechaUbicacionStr, difTotalStr);
        }
    }

    @Override
    public void makeRequestPostEnvioDesvio(String linea, String colectivo, Double latActual, Double lngActual) {
        if(view != null){
            model.makeRequestPostEnvioDesvio(linea, colectivo, latActual, lngActual);
        }
    }

    @Override
    public void makeRequestPostColeEnParada(int codigo, String denom, String unidad) {
        if(view != null){
            model.makeRequestPostColeEnParada(codigo, denom, unidad);
        }
    }

    @Override
    public void makeRequestPostFin(String linea, String colectivo, String latitud, String longitud) {
        if(view != null){
            model.makeRequestPostFin(linea, colectivo, latitud, longitud);
        }
    }

//    @Override
//    public void showResponsePostFinOk(String response) {
//        if(view!=null){
//            view.showResponsePostFinOk(response);
//        }
//    }
//
//    @Override
//    public void showResponsePostFinError(String error) {
//        if(view!=null){
//            view.showResponsePostFinError(error);
//        }
//    }

    @Override
    public void makeRequestPostFinInforme(String linea, String colectivo, String latActual, String lngActual, String fechaUbicacionActual, String difTotal) {
        if(view != null){
            model.makeRequestPostFinInforme(linea, colectivo, latActual, lngActual, fechaUbicacionActual, difTotal);
        }
    }

//    @Override
//    public void showResponsePostFinInformeError(String error) {
//        if(view!=null){
//            view.showResponsePostFinInformeError(error);
//        }
//    }

    @Override
    public void makeRequestPostFinDesvio(String linea, String colectivo, String latitud, String longitud, String fechaUbicacionActual) {
        if(view != null){
            model.makeRequestPostFinDesvio(linea, colectivo, latitud, longitud, fechaUbicacionActual);
        }
    }

//    @Override
//    public void showResponsePostFinDesvioError(String error) {
//        if(view!=null){
//            view.showResponsePostFinDesvioError(error);
//        }
//    }

//    @Override
//    public void showResponsePostSimulacionError(String error) {
//        if(view!=null){
//            view.showResponsePostSimulacionError(error);
//        }
//    }


//    @Override
//    public void enviarInicioServicioAServidor(String seleccionLin, String seleccionCol, Long fechaUbicacionI) {
//        if(view != null){
//            model.enviarInicioServicioAServidor(seleccionLin, seleccionCol, fechaUbicacionI);
//        }
//    }
//
//    @Override
//    public List<String> consultaLineasActivas() {
//        List<String> lineasActivas = model.consultaLineasActivas();
//        return lineasActivas;
//    }


}
