package com.stcu.appcolectivo.model;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.stcu.appcolectivo.interfaces.TrayectoARecorrerInterface;
import com.stcu.appcolectivo.ui.Coordenada;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrayectoARecorrerModel implements TrayectoARecorrerInterface.Model {
    public static String ipv4 = "http://stcu.mdn.unp.edu.ar:50002/stcu_app/";
    Activity mActivity;
    Context mContext;
    RequestQueue requestQueue;
    String myLat;
    String myLng;
    Coordenada parada;
    private TrayectoARecorrerInterface.Presenter presenter;
    public TrayectoARecorrerModel(TrayectoARecorrerInterface.Presenter presenter, Context mContext, Activity mActivity) {

        this.presenter = this.presenter;
        this.mContext = mContext;
        this.mActivity = mActivity;
        requestQueue = Volley.newRequestQueue(mContext);
    }

    @Override
    public List<Coordenada> consultaParadasRecorrido(String linea) {
        List<Coordenada> paradasRecorrido = new ArrayList<Coordenada>();
        String url = ipv4+"rest/paradasRecorrido/paradasParaApp/"+linea;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                (String) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            for(int i=0;i<response.length();i++){
                                JSONObject paradaServidor = response.getJSONObject(i);
                                String latitud = paradaServidor.getString("latitud");
                                String longitud = paradaServidor.getString("longitud");
                                String direccion = paradaServidor.getString("direccion");
                                String codigo = paradaServidor.getString("codigo");

                                parada = new Coordenada();
                                parada.setLatitud(Double.parseDouble(latitud));
                                parada.setLongitud(Double.parseDouble(longitud));
                                parada.setDireccion(direccion);
                                parada.setCodigo(Integer.parseInt(codigo));

                                paradasRecorrido.add(parada);

                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){

                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
//        addToQueue(jsonArrayRequest);
        return paradasRecorrido;
    }

    @Override
    public void makeRequestPostFin(final String seleccionLin, final String seleccionCol, final String lat, final String lng) {
        final String url = ipv4+"rest/lineaColectivos/fin";
        final long fechaUbicacion = System.currentTimeMillis();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        response = response.replaceAll ("\"","");
                        presenter.showResponse(response);
//                        presenter.showResponsePostFinOk(response);
//                        Toast toast1 =
//                                Toast.makeText(getActivity(),
//                                        response, Toast.LENGTH_LONG);
//                        toast1.show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        presenter.showResponse("No se pudo realizar la operacion");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                params.put("linea", seleccionLin);
                params.put("colectivo", seleccionCol);
                params.put("latitud", lat);
                params.put("longitud", lng);
                params.put("fechaUbicacion", String.valueOf(fechaUbicacion));

                return params;
            }
        };
        requestQueue.add(postRequest);
    }


    public void makeRequestPostFinInforme(final String linea, final String colectivo, final String lat, final String lng, final String fuActualD, final String descripcion) {
        final String url = ipv4+"rest/lineaColectivos/finNotificacion";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                          presenter.showResponse("No se pudo enviar la notificacion");
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams()
            {

                Map<String, String>  params = new HashMap<String, String>();

                params.put("linea", linea);
                params.put("colectivo", colectivo);
                params.put("latitud", lat);
                params.put("longitud", lng);
                params.put("fechaNotificacion", String.valueOf(System.currentTimeMillis()));
                params.put("descripcion", descripcion);

                return params;
            }
        };
        requestQueue.add(postRequest);
    }

    @Override
    public void makeRequestPostFinDesvio(final String linea, final String colectivo, final String lat, final String lng, String fuActualID) {
        final String url = ipv4+"rest/lineaColectivos/finDesvio";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        presenter.showResponse("No se pudo enviar la notificacion");
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams()
            {

                Map<String, String>  params = new HashMap<String, String>();

                params.put("linea", linea);
                params.put("colectivo", colectivo);
                params.put("latitud", lat);
                params.put("longitud", lng);
                params.put("fechaNotificacion", String.valueOf(System.currentTimeMillis()));

                return params;
            }
        };
        requestQueue.add(postRequest);
    }

    public void makeRequestPostEnvio(final String seleccionLin, final String seleccionCol, final String latitud, final String longitud) {
        final String url = ipv4+"rest/lineaColectivos/enviarUbicacion";
        final long fechaUbicacion = System.currentTimeMillis();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        presenter.showResponse("No se pudo enviar la ubicacion");

                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                params.put("linea", seleccionLin);
                params.put("colectivo", seleccionCol);
                params.put("latitud", latitud);
                params.put("longitud", longitud);
                params.put("fechaUbicacion", String.valueOf(fechaUbicacion));

                return params;
            }
        };
        requestQueue.add(postRequest);
    }

    @Override
    public void makeRequestPostEnvioInforme(final String linea, final String colectivo, final String lat, final String lng, final String fuActualD, final String descripcion) {
        final String url = ipv4+"rest/lineaColectivos/enviarNotificacion";
        final long fechaUbicacion = System.currentTimeMillis();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        presenter.showResponse("No se pudo enviar la notificacion");
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                params.put("linea", linea);
                params.put("colectivo", colectivo);
                params.put("latitud", lat);
                params.put("longitud", lng);
                params.put("fechaNotificacion",  String.valueOf(fechaUbicacion));
                params.put("descripcion", descripcion);

                return params;
            }
        };
        requestQueue.add(postRequest);
    }

    @Override
    public void makeRequestPostActInforme(final String linea, final String colectivo, final String lat, final String lng, final String fuActualD, final String descripcion) {
        final String url = ipv4+"rest/lineaColectivos/actNotificacion";
        final long fechaUbicacion = System.currentTimeMillis();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                          presenter.showResponse("No se pudo actualizar la notificacion");
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams()
            {

                Map<String, String>  params = new HashMap<String, String>();

                params.put("linea", linea);
                params.put("colectivo", colectivo);
                params.put("latitud", lat);
                params.put("longitud", lng);
                params.put("fechaNotificacion",  String.valueOf(fechaUbicacion));
                params.put("descripcion", descripcion);

                return params;
            }
        };
        requestQueue.add(postRequest);
    }

    @Override
    public void makeRequestPostEnvioDesvio(final String linea, final String colectivo, final Double lat, final Double lng) {
        final String url = ipv4+"rest/lineaColectivos/envioDesvio";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        presenter.showResponse("No se pudo enviar la notificacion");
//                        Toast toast1 = Toast.makeText(getActivity(),"No se pudo enviar la notificacion", Toast.LENGTH_SHORT);
//                        toast1.show();
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams()
            {

                Map<String, String>  params = new HashMap<String, String>();

                params.put("linea", linea);
                params.put("colectivo", colectivo);
                params.put("latitud", String.valueOf(lat));
                params.put("longitud", String.valueOf(lng));
                params.put("fechaNotificacion", String.valueOf(System.currentTimeMillis()));

                return params;
            }
        };
        requestQueue.add(postRequest);
    }

    @Override
    public void makeRequestPostColeEnParada(final int codigo, final String denom, final String unidad) {
        final String url = ipv4+"rest/lineaColectivos/coleEnParada";
        //final long fechaUbicacion = System.currentTimeMillis();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        presenter.showResponse("No se pudo dar aviso sobre colectivo en parada");
//                        Toast toast1 = Toast.makeText(getActivity(),"No se pudo dar aviso sobre colectivo en parada", Toast.LENGTH_SHORT);
//                        toast1.show();
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams()
            {

                Map<String, String>  params = new HashMap<String, String>();

                params.put("linea", denom);
                params.put("colectivo", unidad);
                params.put("codigo", String.valueOf( codigo ) );

                return params;
            }
        };
        requestQueue.add(postRequest);
    }
}
