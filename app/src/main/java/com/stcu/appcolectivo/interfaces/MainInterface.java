package com.stcu.appcolectivo.interfaces;

import android.net.NetworkInfo;

import com.stcu.appcolectivo.model.Colectivo;
import com.stcu.appcolectivo.model.Linea;
import com.stcu.appcolectivo.model.Recorrido;
import com.stcu.appcolectivo.model.Coordenada;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface MainInterface {
    interface View {
        void showUbicacion(String strLatitud, String strLongitud);

        void showResponseInicioServicioOk(String response, String seleccionLin, String seleccionCol, Long fechaUbicacionI);

//        void showResponseInicioServicioError(String error);

        void showResponsePostSimulacionOk(String response, String seleccionLin, String seleccionCol, String seleccionRec, String latInicial, String lngInicial);

//        void showResponsePostSimulacionError(String error);

        void showResponse(String response);

        void showResponseError(String error);

//        void showListadoLineas(List<String> listadoLineas);

//        void showListadoColectivos(List<String> listadoColectivos);
    }

    interface Presenter {
        void enviarInicioServicioAServidor(String seleccionLin, String seleccionCol, Long fechaUbicacionI);

//        List<String> consultaLineasActivas();
        List<Linea> consultaLineasActivas() throws ExecutionException, InterruptedException, TimeoutException;

//        List<String> consultaColectivosActivos();
        List<Colectivo> consultaColectivosActivos() throws ExecutionException, InterruptedException, TimeoutException;

        List<Recorrido> consultaRecorridoActivos(String denomLinea) throws ExecutionException, InterruptedException, TimeoutException;

        NetworkInfo isNetAvailable();

        List<Coordenada> consultaTrayectoASimular(String seleccionLin2, String seleccionRec2);

        //para mostar ubicacion pedida anteriormente
        void showUbicacion(String strLatitud, String strLongitud);

        void obtenerUbicacion();

        void showResponseInicioServicioOk(String response, String seleccionLin, String seleccionCol, Long fechaUbicacionI);

//        void showResponseInicioServicioError(String error);

        void makeRequestPostSimulacion(String seleccionLin2, String seleccionCol2, String seleccionRec2, String latitud, String longitud);

        void showResponsePostSimulacionOk(String response, String seleccionLin, String seleccionCol, String seleccionRec, String latInicial, String lngInicial);

//        void showResponsePostSimulacionError(String toString);

        void makeRequestPostFin(String seleccionLin, String seleccionCol, String lat, String lng);

        void showResponse(String response);

        void showResponseError(String error);

//        void showListadoLineas(List<String> listadoLineas);
//
//        void showListadoColectivos(List<String> listadoColectivos);
    }

    interface Model {
        void enviarInicioServicioAServidor(String seleccionLin, String seleccionCol, Long fechaUbicacionI);

        //List<String> consultaLineasActivas();
        List<Linea> consultaLineasActivas() throws ExecutionException, InterruptedException, TimeoutException;

//        List<String> consultaColectivosActivos();
        List<Colectivo> consultaColectivosActivos() throws ExecutionException, InterruptedException, TimeoutException;

        List<Recorrido> consultaRecorridosActivos(String denomLinea) throws ExecutionException, InterruptedException, TimeoutException;

        NetworkInfo isNetAvailable();

//        boolean isOnlineNet();

        List<Coordenada> consultaTrayectoASimular(String seleccionLin2, String seleccionRec2);

        void obtenerUbicacion();

        void makeRequestPostSimulacion(String seleccionLin2, String seleccionCol2, String seleccionRec2, String latitud, String longitud);

        void makeRequestPostFin(String seleccionLin, String seleccionCol, String lat, String lng);

    }
}
