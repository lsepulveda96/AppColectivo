package com.stcu.appcolectivo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.gpmess.example.volley.app.R;
import com.stcu.appcolectivo.ui.BaseVolleyFragment;
import com.stcu.appcolectivo.ui.Coordenada;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFragment extends BaseVolleyFragment {

    //borrar esta clase!!!
//        public static String ipv4 = "http://stcu.mdn.unp.edu.ar:50002/stcu_app/";
//
//    List<String> listadoLineas = new ArrayList<String>();
//    List<String> listadoColectivos = new ArrayList<String>();
//    List<Coordenada> listaParadas;
//    private List<Coordenada> listaCoordenadasTrayecto;
//    Coordenada parada;
//    Coordenada coord;
//
//    String myLat;
//    String myLng;
//
//    Long fechaUbicacion;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_main, container, false);

//        consultaLineasActivas(); // carga las lineas en la lista de opciones
//        consultaColectivosActivos(); // carga los colectivos en la lista de opciones

        return v;
    }


//    public void makeRequestPostEnvio(final String seleccionLin, final String seleccionCol, final String latitud, final String longitud) {
//        final String url = ipv4+"rest/lineaColectivos/enviarUbicacion";
//        final long fechaUbicacion = System.currentTimeMillis();
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>()
//                {
//                    @Override
//                    public void onResponse(String response) {
//
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                        Toast toast1 = Toast.makeText(getActivity(),"No se pudo enviar la ubicacion", Toast.LENGTH_SHORT);
//                        toast1.show();
//
//                    }
//                }
//        ) {
//
//            @Override
//            protected Map<String, String> getParams()
//            {
//                Map<String, String>  params = new HashMap<String, String>();
//
//                params.put("linea", seleccionLin);
//                params.put("colectivo", seleccionCol);
//                params.put("latitud", latitud);
//                params.put("longitud", longitud);
//                params.put("fechaUbicacion", String.valueOf(fechaUbicacion));
//
//                return params;
//            }
//        };
//        addToQueue(postRequest);
//    }


//    public void makeRequestPostEnvioInforme(final String linea, final String colectivo, final String lat, final String lng, final String fuActualD, final String descripcion) {
//        final String url = ipv4+"rest/lineaColectivos/enviarNotificacion";
//        final long fechaUbicacion = System.currentTimeMillis();
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>()
//                {
//                    @Override
//                    public void onResponse(String response) {
//
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                        Toast toast1 = Toast.makeText(getActivity(),"No se pudo enviar la notificacion", Toast.LENGTH_SHORT);
//                        toast1.show();
//                    }
//                }
//        ) {
//
//            @Override
//            protected Map<String, String> getParams()
//            {
//                Map<String, String>  params = new HashMap<String, String>();
//
//                params.put("linea", linea);
//                params.put("colectivo", colectivo);
//                params.put("latitud", lat);
//                params.put("longitud", lng);
//                params.put("fechaNotificacion",  String.valueOf(fechaUbicacion));
//                params.put("descripcion", descripcion);
//
//                return params;
//            }
//        };
//        addToQueue(postRequest);
//    }

//    public void makeRequestPostActInforme(final String linea, final String colectivo, final String lat, final String lng, final String fuActualD, final String descripcion) {
//        final String url = ipv4+"rest/lineaColectivos/actNotificacion";
//        final long fechaUbicacion = System.currentTimeMillis();
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>()
//                {
//                    @Override
//                    public void onResponse(String response) {
//
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                        Toast toast1 = Toast.makeText(getActivity(),"No se pudo actualizar la notificacion", Toast.LENGTH_SHORT);
//                        toast1.show();
//                    }
//                }
//        ) {
//
//            @Override
//            protected Map<String, String> getParams()
//            {
//
//                Map<String, String>  params = new HashMap<String, String>();
//
//                params.put("linea", linea);
//                params.put("colectivo", colectivo);
//                params.put("latitud", lat);
//                params.put("longitud", lng);
//                params.put("fechaNotificacion",  String.valueOf(fechaUbicacion));
//                params.put("descripcion", descripcion);
//
//                return params;
//            }
//        };
//        addToQueue(postRequest);
//    }


//    public void makeRequestPostEnvioDesvio(final String linea, final String colectivo, final Double lat, final Double lng) {
//        final String url = ipv4+"rest/lineaColectivos/envioDesvio";
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>()
//                {
//                    @Override
//                    public void onResponse(String response) {
//
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                        Toast toast1 = Toast.makeText(getActivity(),"No se pudo enviar la notificacion", Toast.LENGTH_SHORT);
//                        toast1.show();
//                    }
//                }
//        ) {
//
//            @Override
//            protected Map<String, String> getParams()
//            {
//
//                Map<String, String>  params = new HashMap<String, String>();
//
//                params.put("linea", linea);
//                params.put("colectivo", colectivo);
//                params.put("latitud", String.valueOf(lat));
//                params.put("longitud", String.valueOf(lng));
//                params.put("fechaNotificacion", String.valueOf(System.currentTimeMillis()));
//
//                return params;
//            }
//        };
//        addToQueue(postRequest);
//    }

//    public void makeRequestPostColeEnParada(final int codigo, final String denom, final String unidad) {
//        final String url = ipv4+"rest/lineaColectivos/coleEnParada";
//        //final long fechaUbicacion = System.currentTimeMillis();
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>()
//                {
//                    @Override
//                    public void onResponse(String response) {
//
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                        Toast toast1 = Toast.makeText(getActivity(),"No se pudo dar aviso sobre colectivo en parada", Toast.LENGTH_SHORT);
//                        toast1.show();
//                    }
//                }
//        ) {
//
//            @Override
//            protected Map<String, String> getParams()
//            {
//
//                Map<String, String>  params = new HashMap<String, String>();
//
//                params.put("linea", denom);
//                params.put("colectivo", unidad);
//                params.put("codigo", String.valueOf( codigo ) );
//
//                return params;
//            }
//        };
//        addToQueue(postRequest);
//    }

//    public List<String> getListadoLineas() {
//        return this.listadoLineas;
//    }
//
//    public List<String> getListadoColectivos() {
//        return this.listadoColectivos;
//    }

//    public void enviarInicioServicioAServidor(final String seleccionLin, final String seleccionCol, final Long fechaUbicacionI) {
//        final String url = ipv4+"rest/lineaColectivos/inicio"; // uni
//        final Long fechaUbicacion = System.currentTimeMillis();
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>()
//                {
//                    @Override
//                    public void onResponse(String response) {
//                        response = response.replaceAll ("\"","");
//                        Toast toast1 =
//                                Toast.makeText(getActivity(),
//                                        response, Toast.LENGTH_LONG);
//                        toast1.show();
//
//                        if(response.equals("Servicio iniciado")) {
//
//                            MainActivity activity2 = (MainActivity) getActivity();
//                            String myLat2 = activity2.getLat();
//                            String myLng2 = activity2.getLng();
//
//                            Intent ma2 = new Intent(getActivity(), ColectivoEnServicioActivity.class);
//                            ma2.putExtra("linea", seleccionLin);
//                            ma2.putExtra("colectivo", seleccionCol);
//                            ma2.putExtra("latitud", myLat2);
//                            ma2.putExtra("longitud", myLng2);
//
//                            ma2.putExtra("fechaUbicacion", String.valueOf(System.currentTimeMillis()));
//                            getActivity().startActivity(ma2);
//                        }
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast toast1 = Toast.makeText(getActivity(),"No se pudo iniciar el servicio", Toast.LENGTH_SHORT);
//                        toast1.show();
//                        Toast.makeText( getActivity(),"Vuelva a intentarlo",Toast.LENGTH_SHORT ).show();
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams()
//            {
//
//                Map<String, String>  params = new HashMap<String, String>();
//
//                MainActivity activity = (MainActivity) getActivity();
//                myLat = activity.getLat();
//                myLng = activity.getLng();
//
//                params.put("linea", seleccionLin);
//                params.put("colectivo", seleccionCol);
//                params.put("latitud", myLat);
//                params.put("longitud", myLng);
//                params.put("fechaUbicacion", String.valueOf(fechaUbicacion));
//
//                return params;
//            }
//        };
//        addToQueue(postRequest);
//    }


//    //este no iria mas aca, iria en el model
//    public void makeRequestPostSimulacion(final String seleccionLin, final String seleccionCol, final String latInicial, final String lngInicial) {
//
//        final String url = ipv4+"rest/lineaColectivos/inicio"; // uni
//        final Long fechaUbicacion = System.currentTimeMillis();
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>()
//                {
//                    @Override
//                    public void onResponse(String response) {
//                        response = response.replaceAll ("\"","");
//                        Toast toast1 =
//                                Toast.makeText(getActivity(),
//                                        response, Toast.LENGTH_LONG);
//                        toast1.show();
//
//                        if(response.equals("Servicio iniciado")) {
//                            //TODO tiene que ir al model a hacer request y volver al main activity para que llame a simulacion
//                            Intent ma2 = new Intent(getActivity(), SimulacionRecorridoActivity.class);
//                            ma2.putExtra("linea", seleccionLin);
//                            ma2.putExtra("colectivo", seleccionCol);
//                            ma2.putExtra("latitud", latInicial);
//                            ma2.putExtra("longitud", lngInicial);
//                            ma2.putExtra("fechaUbicacion", String.valueOf(System.currentTimeMillis()));
//                            getActivity().startActivity(ma2);
//                        }
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                        Toast toast1 = Toast.makeText(getActivity(),"No se pudo iniciar el servicio", Toast.LENGTH_SHORT);
//                        toast1.show();
//                        Toast.makeText( getActivity(),"Vuelva a intentarlo",Toast.LENGTH_SHORT ).show();
//
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams()
//            {
//                Map<String, String>  params = new HashMap<String, String>();
//
//                params.put("linea",seleccionLin);
//                params.put("colectivo", seleccionCol);
//                params.put("latitud", latInicial);
//                params.put("longitud", lngInicial);
//                params.put("fechaUbicacion", String.valueOf(fechaUbicacion));
//
//                return params;
//            }
//        };
//        addToQueue(postRequest);
//    }


//    public void makeRequestPostFin(final String seleccionLin, final String seleccionCol, final String lat, final String lng) {
//        final String url = ipv4+"rest/lineaColectivos/fin";
//        final long fechaUbicacion = System.currentTimeMillis();
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>()
//                {
//                    @Override
//                    public void onResponse(String response) {
//                        response = response.replaceAll ("\"","");
//                        Toast toast1 =
//                                Toast.makeText(getActivity(),
//                                        response, Toast.LENGTH_LONG);
//                        toast1.show();
//
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                        Toast toast1 = Toast.makeText(getActivity(),"No se pudo realizar la operacion", Toast.LENGTH_SHORT);
//                        toast1.show();
//                        Toast.makeText( getActivity(),"Vuelva a intentarlo",Toast.LENGTH_SHORT ).show();
//
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams()
//            {
//                Map<String, String>  params = new HashMap<String, String>();
//
//                params.put("linea", seleccionLin);
//                params.put("colectivo", seleccionCol);
//                params.put("latitud", lat);
//                params.put("longitud", lng);
//                params.put("fechaUbicacion", String.valueOf(fechaUbicacion));
//
//                return params;
//            }
//        };
//        addToQueue(postRequest);
//    }




//    public void makeRequestPostFinDesvio(final String linea, final String colectivo, final String lat, final String lng, String fuActualID) {
//        final String url = ipv4+"rest/lineaColectivos/finDesvio";
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>()
//                {
//                    @Override
//                    public void onResponse(String response) {
//
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                        Toast toast1 = Toast.makeText(getActivity(),"No se pudo enviar la notificacion", Toast.LENGTH_SHORT);
//                        toast1.show();
//                    }
//                }
//        ) {
//
//            @Override
//            protected Map<String, String> getParams()
//            {
//
//                Map<String, String>  params = new HashMap<String, String>();
//
//                params.put("linea", linea);
//                params.put("colectivo", colectivo);
//                params.put("latitud", lat);
//                params.put("longitud", lng);
//                params.put("fechaNotificacion", String.valueOf(System.currentTimeMillis()));
//
//                return params;
//            }
//        };
//        addToQueue(postRequest);
//    }

//    public void makeRequestPostFinInforme(final String linea, final String colectivo, final String lat, final String lng, final String fuActualD, final String descripcion) {
//        final String url = ipv4+"rest/lineaColectivos/finNotificacion";
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>()
//                {
//                    @Override
//                    public void onResponse(String response) {
//
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                        Toast toast1 = Toast.makeText(getActivity(),"No se pudo enviar la notificacion", Toast.LENGTH_SHORT);
//                        toast1.show();
//                    }
//                }
//        ) {
//
//            @Override
//            protected Map<String, String> getParams()
//            {
//
//                Map<String, String>  params = new HashMap<String, String>();
//
//                params.put("linea", linea);
//                params.put("colectivo", colectivo);
//                params.put("latitud", lat);
//                params.put("longitud", lng);
//                params.put("fechaNotificacion", String.valueOf(System.currentTimeMillis()));
//                params.put("descripcion", descripcion);
//
//                return params;
//            }
//        };
//        addToQueue(postRequest);
//    }

    ///////////////////////////////////////////////////////////////////////////////////////////



// TODO se movio al model
//    public void consultaLineasActivas(){
//        String url = ipv4+"rest/lineas/activas"; // ip uni
//
//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
//                Request.Method.GET,
//                url,
//                (String) null,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        try{
//                            for(int i=0;i<response.length();i++){
//                                JSONObject linea = response.getJSONObject(i);
//                                String id = linea.getString("id");
//                                String denominacion = linea.getString("denominacion");
//                                String enServicio = linea.getString("enServicio");
//
//                                listadoLineas.add(denominacion);
//                            }
//                        }catch (JSONException e){
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener(){
//                    @Override
//                    public void onErrorResponse(VolleyError error){
//                    }
//                }
//        );
//        addToQueue(jsonArrayRequest);
//    }

    // TODO se movio a model
//    public void consultaColectivosActivos(){
//        String url = ipv4+"rest/colectivos/activos";
//
//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
//                Request.Method.GET,
//                url,
//                (String) null,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        try{
//                            for(int i=0;i<response.length();i++){
//                                JSONObject colectivo = response.getJSONObject(i);
//                                String id = colectivo.getString("id");
//                                String patente = colectivo.getString("patente");
//                                String unidad = colectivo.getString("unidad");
//
//                                listadoColectivos.add(unidad);
//
//                            }
//                        }catch (JSONException e){
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener(){
//                    @Override
//                    public void onErrorResponse(VolleyError error){
//                    }
//                }
//        );
//        addToQueue(jsonArrayRequest);
//    }



//public List<Coordenada> consultaParadasRecorrido(String linea){
//    listaParadas = new ArrayList<Coordenada>();
//    String url = ipv4+"rest/paradasRecorrido/paradasParaApp/"+linea;
//
//    JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
//            Request.Method.GET,
//            url,
//            (String) null,
//            new Response.Listener<JSONArray>() {
//                @Override
//                public void onResponse(JSONArray response) {
//                    try{
//                        for(int i=0;i<response.length();i++){
//                            JSONObject paradaServidor = response.getJSONObject(i);
//                            String latitud = paradaServidor.getString("latitud");
//                            String longitud = paradaServidor.getString("longitud");
//                            String direccion = paradaServidor.getString("direccion");
//                            String codigo = paradaServidor.getString("codigo");
//
//                            parada = new Coordenada();
//                            parada.setLatitud(Double.parseDouble(latitud));
//                            parada.setLongitud(Double.parseDouble(longitud));
//                            parada.setDireccion(direccion);
//                            parada.setCodigo(Integer.parseInt(codigo));
//
//                            listaParadas.add(parada);
//
//                        }
//
//                    }catch (JSONException e){
//                        e.printStackTrace();
//                    }
//                }
//            },
//            new Response.ErrorListener(){
//                @Override
//                public void onErrorResponse(VolleyError error){
//
//                }
//            }
//    );
//    addToQueue(jsonArrayRequest);
//    return listaParadas;
//}

//TODO se movio a model
//    public List<Coordenada> consultaTrayectoASimular(String denom) {
//        listaCoordenadasTrayecto = new ArrayList<Coordenada>();
//        String url = ipv4+"rest/lineaColectivos/trayectos/"+denom;
//
//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
//                Request.Method.GET,
//                url,
//                (String) null,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        try{
//
//                            for(int i=0;i<response.length();i++){
//                                JSONObject trayecto = response.getJSONObject(i);
//                                String latitud = trayecto.getString("latitud");
//                                String longitud = trayecto.getString("longitud");
//                                coord = new Coordenada();
//                                coord.setLatitud(Double.parseDouble(latitud));
//                                coord.setLongitud(Double.parseDouble(longitud));
//                                listaCoordenadasTrayecto.add(coord);
//                            }
//
//                        }catch (JSONException e){
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener(){
//                    @Override
//                    public void onErrorResponse(VolleyError error){
//
//                    }
//                }
//        );
//        addToQueue(jsonArrayRequest);
//        return listaCoordenadasTrayecto;
//    }
}
