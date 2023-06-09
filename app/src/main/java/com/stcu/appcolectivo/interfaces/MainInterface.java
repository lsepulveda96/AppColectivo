package com.stcu.appcolectivo.interfaces;

import android.net.NetworkInfo;

import com.stcu.appcolectivo.ui.Coordenada;

import java.util.List;

public interface MainInterface {
    interface View {
        void showUbicacion(String strLatitud, String strLongitud);

        void showResponseInicioServicioOk(String response, String seleccionLin, String seleccionCol, Long fechaUbicacionI);

//        void showResponseInicioServicioError(String error);

        void showResponsePostSimulacionOk(String response, String seleccionLin, String seleccionCol, String latInicial, String lngInicial);

//        void showResponsePostSimulacionError(String error);

        void showResponse(String response);

        void showResponseError(String error);

//        void showListadoLineas(List<String> listadoLineas);

//        void showListadoColectivos(List<String> listadoColectivos);
    }

    interface Presenter {
        void enviarInicioServicioAServidor(String seleccionLin, String seleccionCol, Long fechaUbicacionI);

        List<String> consultaLineasActivas();

        List<String> consultaColectivosActivos();

//        boolean isOnlineNet();

        NetworkInfo isNetAvailable();

        List<Coordenada> consultaTrayectoASimular(String seleccionLin2);

        //para mostar ubicacion pedida anteriormente
        void showUbicacion(String strLatitud, String strLongitud);

        void obtenerUbicacion();

        void showResponseInicioServicioOk(String response, String seleccionLin, String seleccionCol, Long fechaUbicacionI);

//        void showResponseInicioServicioError(String error);

        void makeRequestPostSimulacion(String seleccionLin2, String seleccionCol2, String latitud, String longitud);

        void showResponsePostSimulacionOk(String response, String seleccionLin, String seleccionCol, String latInicial, String lngInicial);

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

        List<String> consultaLineasActivas();

        List<String> consultaColectivosActivos();

        NetworkInfo isNetAvailable();

//        boolean isOnlineNet();

        List<Coordenada> consultaTrayectoASimular(String seleccionLin2);

        void obtenerUbicacion();

        void makeRequestPostSimulacion(String seleccionLin2, String seleccionCol2, String latitud, String longitud);

        void makeRequestPostFin(String seleccionLin, String seleccionCol, String lat, String lng);
    }
}
