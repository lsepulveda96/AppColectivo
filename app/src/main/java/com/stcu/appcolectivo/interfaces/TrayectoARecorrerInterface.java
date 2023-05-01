package com.stcu.appcolectivo.interfaces;

import com.stcu.appcolectivo.ui.Coordenada;

import java.util.List;

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
        List<Coordenada> consultaParadasRecorrido(String linea);

        void makeRequestPostFin(String linea, String colectivo, String latitud, String longitud);
//
//        void showResponsePostFinOk(String response);
//
//        void showResponsePostFinError(String error);
//
        void makeRequestPostFinInforme(String linea, String colectivo, String valueOf, String valueOf1, String valueOf2, String s);
//
//        void showResponsePostFinInformeError(String error);
//
        void makeRequestPostFinDesvio(String linea, String colectivo, String latitud, String longitud, String fechaUbicacionActual);
//
//        void showResponsePostFinDesvioError(String toString);

        void showResponse(String response);

        void makeRequestPostEnvio(String linea, String colectivo, String lat, String lng);

        void makeRequestPostEnvioInforme(String linea, String colectivo, String valueOf, String valueOf1, String valueOf2, String s);

        void makeRequestPostActInforme(String linea, String colectivo, String lat, String lng, String fechaUbicacionStr, String difTotalStr);

        void makeRequestPostEnvioDesvio(String linea, String colectivo, Double latActual, Double lngActual);

        void makeRequestPostColeEnParada(int codigo, String denom, String unidad);

//        void enviarInicioServicioAServidor(String seleccionLin, String seleccionCol, Long fechaUbicacionI);
//
//        List<String> consultaLineasActivas();


    }

    interface Model {
        List<Coordenada> consultaParadasRecorrido(String linea);

        void makeRequestPostFin(String linea, String colectivo, String latitud, String longitud);

        void makeRequestPostFinInforme(String linea, String colectivo, String latActual, String lngActual, String fechaUbicacionActual, String difTotal);

        void makeRequestPostFinDesvio(String linea, String colectivo, String latitud, String longitud, String fechaUbicacionActual);

        void makeRequestPostEnvio(String linea, String colectivo, String lat, String lng);

        void makeRequestPostEnvioInforme(String linea, String colectivo, String lat, String lng, String fechaUbicacionStr, String difTotalStr);

        void makeRequestPostActInforme(String linea, String colectivo, String lat, String lng, String fechaUbicacionStr, String difTotalStr);

        void makeRequestPostEnvioDesvio(String linea, String colectivo, Double latActual, Double lngActual);

        void makeRequestPostColeEnParada(int codigo, String denom, String unidad);
//        void enviarInicioServicioAServidor(String seleccionLin, String seleccionCol, Long fechaUbicacionI);
//
//        List<String> consultaLineasActivas();
//
//        void makeRequestPostSimulacion(String seleccionLin2, String seleccionCol2, String latitud, String longitud);
    }
}
