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
        void makeRequestPostFin(String linea, String colectivo, String recorrido) throws ExecutionException, InterruptedException, TimeoutException;
//
//        void showResponsePostFinOk(String response);
//
//        void showResponsePostFinError(String error);
//
        void makeRequestPostFinInforme(String linea, String colectivo, String valueOf, String valueOf1, String valueOf2, String s);
//
//        void showResponsePostFinInformeError(String error);
//
        void makeRequestPostFinDesvio(String linea, String colectivo, String recorrido) throws ExecutionException, InterruptedException, TimeoutException;
//
//        void showResponsePostFinDesvioError(String toString);

        void showResponse(String response);

        void makeRequestPostEnvio(String linea, String colectivo, String recorrido, String lat, String lng) throws ExecutionException, InterruptedException, TimeoutException;

        void makeRequestPostEnvioInforme(String linea, String colectivo, String valueOf, String valueOf1, String valueOf2, String s);

        void makeRequestPostActInforme(String linea, String colectivo, String lat, String lng, String fechaUbicacionStr, String difTotalStr);

        void makeRequestPostEnvioDesvio(String linea, String colectivo, String recorrido, Double latActual, Double lngActual) throws ExecutionException, InterruptedException, TimeoutException;

        void makeRequestPostColeEnParada(int codigo, String denomLinea, String unidad, String denomRecorrido) throws ExecutionException, InterruptedException, TimeoutException;

//        void enviarInicioServicioAServidor(String seleccionLin, String seleccionCol, Long fechaUbicacionI);
//
//        List<String> consultaLineasActivas();


    }

    interface Model {
        List<Coordenada> consultaParadasRecorrido(String linea, String recorrido) throws ExecutionException, InterruptedException, TimeoutException, JSONException;

//        void makeRequestPostFin(String linea, String colectivo, String latitud, String longitud);
        void makeRequestPostFin(String linea, String colectivo, String recorrido) throws ExecutionException, InterruptedException, TimeoutException;

        void makeRequestPostFinInforme(String linea, String colectivo, String latActual, String lngActual, String fechaUbicacionActual, String difTotal);

        void makeRequestPostFinDesvio(String linea, String colectivo, String recorrido) throws ExecutionException, InterruptedException, TimeoutException;

        void makeRequestPostEnvio(String linea, String colectivo, String recorrido, String lat, String lng) throws ExecutionException, InterruptedException, TimeoutException;

        void makeRequestPostEnvioInforme(String linea, String colectivo, String lat, String lng, String fechaUbicacionStr, String difTotalStr);

        void makeRequestPostActInforme(String linea, String colectivo, String lat, String lng, String fechaUbicacionStr, String difTotalStr);

        void makeRequestPostEnvioDesvio(String linea, String colectivo, String recorrido, Double latActual, Double lngActual) throws ExecutionException, InterruptedException, TimeoutException;

        void makeRequestPostColeEnParada(int codigo, String denomLinea, String unidad, String denomRecorrido) throws ExecutionException, InterruptedException, TimeoutException;
//        void enviarInicioServicioAServidor(String seleccionLin, String seleccionCol, Long fechaUbicacionI);
//
//        List<String> consultaLineasActivas();
//
//        void makeRequestPostSimulacion(String seleccionLin2, String seleccionCol2, String latitud, String longitud);
    }
}
