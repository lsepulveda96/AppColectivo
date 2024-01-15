package com.stcu.appcolectivo.presenter;

import android.app.Activity;
import android.content.Context;

import com.stcu.appcolectivo.interfaces.TrayectoARecorrerInterface;
import com.stcu.appcolectivo.model.TrayectoARecorrerModel;
import com.stcu.appcolectivo.model.Coordenada;

import org.json.JSONException;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


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
    public List<Coordenada> consultaParadasRecorrido(String linea, String recorrido) throws JSONException, ExecutionException, InterruptedException, TimeoutException {
        List<Coordenada> paradasRecorrido = model.consultaParadasRecorrido(linea,recorrido);
        return paradasRecorrido;
    }

    @Override
    public void showResponse(String response) {
        if(view!=null){
            view.showResponse(response);
        }
    }

    @Override
    public void makeRequestPostEnviarUbicacion(String linea, String colectivo, String recorrido, String lat, String lng) throws ExecutionException, InterruptedException, TimeoutException {
        if(view != null){
            model.makeRequestPostEnviarUbicacion(linea, colectivo, recorrido, lat, lng);
        }
    }

    @Override
    public void makePostInformeColeDetenido(String seleccionLin, String seleccionCol, String seleccionRec, String latitud, String longitud, String segundosDetenidoStr) throws ExecutionException, InterruptedException, TimeoutException {
        if(view != null){
            model.makePostInformeColeDetenido(seleccionLin, seleccionCol, seleccionRec, latitud, longitud, segundosDetenidoStr);
        }
    }

    @Override
    public void makePostActualizacionNotifColeDetenido(String seleccionLin, String seleccionCol, String seleccionRec, String latitud, String longitud, String segundosDetenidoStr) throws ExecutionException, InterruptedException, TimeoutException {
        if(view != null){
            model.makePostActualizacionNotifColeDetenido( seleccionLin, seleccionCol, seleccionRec, latitud, longitud, segundosDetenidoStr);
        }
    }

    @Override
    public void makeRequestPostDetectarDesvio(String linea, String colectivo, String recorrido, Double latActual, Double lngActual) throws ExecutionException, InterruptedException, TimeoutException {
        if(view != null){
            model.makeRequestPostDetectarDesvio(linea, colectivo, recorrido, latActual, lngActual);
        }
    }

    @Override
    public void makeRequestPostColeEnParada(int codigo, String denomLinea, String unidad, String denomRecorrido) throws ExecutionException, InterruptedException, TimeoutException {
        if(view != null){
            model.makeRequestPostColeEnParada(codigo, denomLinea, unidad, denomRecorrido);
        }
    }

    @Override
    public void makeRequestPostFinColectivoRecorrido(String linea, String colectivo, String recorrido) throws ExecutionException, InterruptedException, TimeoutException {
        if(view != null){
//            model.makeRequestPostFin(linea, colectivo, latitud, longitud);
            model.makeRequestPostFinColectivoRecorrido(linea, colectivo, recorrido);
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
    public void makePostFinNotificacionColeDetenido(String linea, String colectivo, String recorrido, String latActual, String lngActual, String segundosDetenidoStr) throws ExecutionException, InterruptedException, TimeoutException {
        if(view != null){
            model.makePostFinNotificacionColeDetenido(linea, colectivo, recorrido, latActual, lngActual, segundosDetenidoStr);
        }
    }

//    @Override
//    public void showResponsePostFinInformeError(String error) {
//        if(view!=null){
//            view.showResponsePostFinInformeError(error);
//        }
//    }

    @Override
    public void makeRequestPostFinDesvio(String linea, String colectivo, String recorrido) throws ExecutionException, InterruptedException, TimeoutException {
        if(view != null){
            model.makeRequestPostFinDesvio(linea, colectivo, recorrido);
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
