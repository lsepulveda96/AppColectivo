package com.stcu.appcolectivo.interfaces;

import com.stcu.appcolectivo.model.Coordenada;

import org.json.JSONException;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface TrayectoARecorrerInterface {

    interface View {
//        void showResponsePostFinOk(String response);
//
//        void showResponsePostFinError(String error);
//
//        void showResponsePostFinInformeError(String error);
//
//        void showResponsePostFinDesvioError(String error);

        void showResponse(String response);
//        void showUbicacion(String strLatitud, String strLongitud);


    }

    interface Presenter {
        List<Coordenada> consultaParadasRecorrido(String linea, String recorrido) throws JSONException, ExecutionException, InterruptedException, TimeoutException;

//        void makeRequestPostFin(String linea, String colectivo, String latitud, String longitud);
        void makeRequestPostFinColectivoRecorrido(String linea, String colectivo, String recorrido) throws ExecutionException, InterruptedException, TimeoutException;
//
//        void showResponsePostFinOk(String response);
//
//        void showResponsePostFinError(String error);
//
        void makePostFinNotificacionColeDetenido(String linea, String colectivo, String recorrido, String latitud, String longitud, String segundosDetenidoStr) throws ExecutionException, InterruptedException, TimeoutException;
//
//        void showResponsePostFinInformeError(String error);
//
        void makeRequestPostFinDesvio(String linea, String colectivo, String recorrido) throws ExecutionException, InterruptedException, TimeoutException;
//
//        void showResponsePostFinDesvioError(String toString);

        void showResponse(String response);

        void makeRequestPostEnvio(String linea, String colectivo, String recorrido, String lat, String lng) throws ExecutionException, InterruptedException, TimeoutException;

        void makePostInformeColeDetenido(String seleccionLin, String seleccionCol, String seleccionRec, String latitud, String longitud, String segundosDetenidoStr) throws ExecutionException, InterruptedException, TimeoutException;

        void makePostActualizacionNotifColeDetenido(String seleccionLin, String seleccionCol, String seleccionRec, String latitud, String longitud, String segundosDetenidoStr) throws ExecutionException, InterruptedException, TimeoutException;

        void makeRequestPostEnvioDesvio(String linea, String colectivo, String recorrido, Double latActual, Double lngActual) throws ExecutionException, InterruptedException, TimeoutException;

        void makeRequestPostColeEnParada(int codigo, String denomLinea, String unidad, String denomRecorrido) throws ExecutionException, InterruptedException, TimeoutException;

//        void enviarInicioServicioAServidor(String seleccionLin, String seleccionCol, Long fechaUbicacionI);
//
//        List<String> consultaLineasActivas();


    }

    interface Model {
        List<Coordenada> consultaParadasRecorrido(String linea, String recorrido) throws ExecutionException, InterruptedException, TimeoutException, JSONException;

//        void makeRequestPostFin(String linea, String colectivo, String latitud, String longitud);
        void makeRequestPostFinColectivoRecorrido(String linea, String colectivo, String recorrido) throws ExecutionException, InterruptedException, TimeoutException;

        void makePostFinNotificacionColeDetenido(String linea, String colectivo, String recorrido, String latActual, String lngActual, String segundosDetenidoStr) throws ExecutionException, InterruptedException, TimeoutException;

        void makeRequestPostFinDesvio(String linea, String colectivo, String recorrido) throws ExecutionException, InterruptedException, TimeoutException;

        void makeRequestPostEnvio(String linea, String colectivo, String recorrido, String lat, String lng) throws ExecutionException, InterruptedException, TimeoutException;

//        void makeRequestPostEnvioInforme(String linea, String colectivo, String lat, String lng, String fechaUbicacionStr, String segundosDetenidoStr);
        void makePostInformeColeDetenido(final String seleccionLin, final String seleccionCol, final String seleccionRec, final String latitud, final String longitud, final String segundosDetenidoStr) throws ExecutionException, InterruptedException, TimeoutException;

        void makePostActualizacionNotifColeDetenido(final String seleccionLin, final String seleccionCol, final String seleccionRec, final String latitud, final String longitud, final String segundosDetenidoStr) throws ExecutionException, InterruptedException, TimeoutException;

        void makeRequestPostEnvioDesvio(String linea, String colectivo, String recorrido, Double latActual, Double lngActual) throws ExecutionException, InterruptedException, TimeoutException;

        void makeRequestPostColeEnParada(int codigo, String denomLinea, String unidad, String denomRecorrido) throws ExecutionException, InterruptedException, TimeoutException;
//        void enviarInicioServicioAServidor(String seleccionLin, String seleccionCol, Long fechaUbicacionI);
//
//        List<String> consultaLineasActivas();
//
//        void makeRequestPostSimulacion(String seleccionLin2, String seleccionCol2, String latitud, String longitud);
    }
}
