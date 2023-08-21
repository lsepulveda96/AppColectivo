package com.stcu.appcolectivo.model;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.stcu.appcolectivo.interfaces.TrayectoARecorrerInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrayectoARecorrerModel implements TrayectoARecorrerInterface.Model {
//    public static String ipv4 = "http://stcu.mdn.unp.edu.ar:50002/stcu_app/";

    // ip local casa
    public static String ipv4 = "http://192.168.0.106:50000/v1/mobile/";
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

   /* //metodo a refactorizar
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
    }*/


    @Override
    public List<Coordenada> consultaParadasRecorrido(String linea, String recorrido) {
        List<Coordenada> paradasRecorrido = new ArrayList<Coordenada>();
        String url = ipv4+"paradasParaApp/"+linea+"/"+recorrido;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray paradasServidor = response.getJSONArray("data"); // get the JSONArray

                            for(int i=0;i<paradasServidor.length();i++){

                                JSONObject paradaItem = paradasServidor.getJSONObject(i);


                                JSONObject coordLng = new JSONObject(paradaItem.getString("parada")).getJSONObject("coordenada");
                                String longitud = coordLng.getString("lng");

                                JSONObject coordLat = new JSONObject(paradaItem.getString("parada")).getJSONObject("coordenada");
                                String latitud = coordLat.getString("lat");

                                String direccion= new JSONObject(paradaItem.getString("parada")).getString("direccion");

                                String codigo= new JSONObject(paradaItem.getString("parada")).getString("codigo");

                                parada = new Coordenada();
                                parada.setLatitud(Double.parseDouble(latitud));
                                parada.setLongitud(Double.parseDouble(longitud));
                                parada.setDireccion(direccion);
                                parada.setCodigo(Integer.parseInt(codigo));

                                paradasRecorrido.add(parada);

                            }

                        }catch (JSONException e){
                            System.out.println("error JSONException al recuperar lista de paradas" + e.toString());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        System.out.println("error Volley al recuperar lista de paradas" + error.toString());
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
//        addToQueue(jsonArrayRequest);
        return paradasRecorrido;
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




    public void makeRequestPostEnvio(final String seleccionLin, final String seleccionCol, String seleccionRec, final String latitud, final String longitud) {
       /* final String url = ipv4+"rest/lineaColectivos/enviarUbicacion";
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

*/
        final String url = ipv4+"enviarUbicacion";

        Map<String, String> params = new HashMap();
        params.put("linea",seleccionLin);
        params.put("colectivo", seleccionCol);
        params.put("recorrido", seleccionRec);
        params.put("latitud", latitud);
        params.put("longitud", longitud);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        presenter.showResponse("No se pudo enviar la ubicacion");
                    }
                });

        requestQueue.add(jsonRequest);
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














    /*
    *
    *   final String url = ipv4+"inicio"; // uni
        final long fechaUbicacion = System.currentTimeMillis();
        Map<String, String> params = new HashMap();
        params.put("linea",seleccionLin);
        params.put("colectivo", seleccionCol);
        params.put("recorrido", seleccionRec);
        params.put("latitud", latInicial);
        params.put("longitud", lngInicial);
        params.put("fechaUbicacion", String.valueOf(fechaUbicacion));

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        presenter.showResponsePostSimulacionOk(response.toString(),seleccionLin,seleccionCol,seleccionRec,latInicial, lngInicial);
                    }
                },  new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        presenter.showResponseError("No se pudo iniciar el servicio");
                    }
    });
        requestQueue.add(jsonRequest);
        *
    * */





    @Override
    public void makeRequestPostEnvioDesvio(final String linea, final String colectivo, final String recorrido, final Double lat, final Double lng) {
        final String url = ipv4+"envioDesvio";

        Map<String, String> params = new HashMap();
        params.put("linea",linea);
        params.put("colectivo", colectivo);
        params.put("recorrido", recorrido);
        params.put("latitud", String.valueOf(lat));
        params.put("longitud", String.valueOf(lng));
        params.put("fechaNotificacion", String.valueOf(System.currentTimeMillis()));

        JSONObject parameters = new JSONObject(params);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters,new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                             presenter.showResponse("No se pudo enviar la notificacion");
                        }
                    });

        requestQueue.add(jsonRequest);
    }


    @Override
    public void makeRequestPostColeEnParada(final int codigo, final String denomLinea, final String unidad, final String denomRecorrido) {
        final String url = ipv4+"coleEnParada";
        //final long fechaUbicacion = System.currentTimeMillis();

        Map<String, String> params = new HashMap();
        params.put("linea",denomLinea);
        params.put("colectivo", unidad);
        params.put("recorrido", denomRecorrido);
        params.put("codigo", String.valueOf( codigo ));


        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        },  new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error Volley. Cole en parada: " + error.toString());
            }
        });
        requestQueue.add(jsonRequest);

      /*  StringRequest postRequest = new StringRequest(Request.Method.POST, url,
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

                params.put("linea", denomLinea);
                params.put("colectivo", unidad);
                params.put("codigo", String.valueOf( codigo ) );
                params.put("recorrido", denomRecorrido);


                return params;
            }
        };
        requestQueue.add(postRequest);*/
    }











    // Todo trabajar en este metodo
    //
    //
    //
    //

    @Override
    public void makeRequestPostFin(final String seleccionLin, final String seleccionCol, final String seleccionRec) {
        final String url = ipv4+"fin";

        Map<String, String> params = new HashMap();
        params.put("linea",seleccionLin);
        params.put("colectivo", seleccionCol);
        params.put("recorrido", seleccionRec);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // presenter.showResponse(response.getString("mensaje")); // ver bien que mensaje responde, con "mensaje" no anda
                System.out.println(" el mensaje de respuesta del servidor POST FIN: " + response);
            }
        },  new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error Volley. fin servicio: " + error.toString());
                presenter.showResponse("No se pudo realizar la operacion");
            }
        });
        requestQueue.add(jsonRequest);

      /*  final String url = ipv4+"rest/lineaColectivos/fin";
        final long fechaBaja = System.currentTimeMillis();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        response = response.replaceAll ("\"","");
                        presenter.showResponse(response);
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
                params.put("fecha", String.valueOf(fechaBaja));

                return params;
            }
        };
        requestQueue.add(postRequest);*/
    }



    // todo trabajar en este metodo tambien
    @Override
    public void makeRequestPostFinDesvio(final String linea, final String colectivo, String recorrido) {

        final String url = ipv4+"finDesvio";

        Map<String, String> params = new HashMap();
        params.put("linea",linea);
        params.put("colectivo", colectivo);
        params.put("recorrido", recorrido);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            }
        },  new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error Volley. fin desvio: " + error.toString());
                presenter.showResponse("No se pudo enviar la notificacion desvio");
            }
        });
        requestQueue.add(jsonRequest);

       /* final String url = ipv4+"rest/lineaColectivos/finDesvio";
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
                params.put("recorrido", recorrido);


                return params;
            }
        };
        requestQueue.add(postRequest);*/
    }
}
