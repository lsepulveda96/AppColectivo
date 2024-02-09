package com.stcu.appcolectivo.model;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.stcu.appcolectivo.interfaces.TrayectoARecorrerInterface;
import com.stcu.appcolectivo.util.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TrayectoARecorrerModel implements TrayectoARecorrerInterface.Model {

//     ip local actual
    public static String ipv4 = "http://192.168.0.105:50004/stcu2service/v1/mobile/";

//    public static String ipv4 = "http://192.168.1.40:50004/stcu2service/v1/mobile/";

    // ip remoto actual
//    public static String ipv4 =  "http://138.36.99.248:50004/stcu2service/v1/mobile/";


    static int timeOutRequest = 10; // en segundos
    Activity mActivity;
    Context mContext;
    RequestQueue requestQueue;
    Coordenada parada;
    private TrayectoARecorrerInterface.Presenter presenter;
    public TrayectoARecorrerModel(TrayectoARecorrerInterface.Presenter presenter, Context mContext, Activity mActivity) {

        this.presenter = this.presenter;
        this.mContext = mContext;
        this.mActivity = mActivity;
        requestQueue = Volley.newRequestQueue(mContext);
    }


    @Override
    public List<Coordenada> consultaParadasRecorrido(String linea, String recorrido) throws ExecutionException, InterruptedException, TimeoutException, JSONException {
        List<Coordenada> paradasRecorrido = new ArrayList<Coordenada>();
        String url = ipv4+"paradasParaApp/"+linea+"/"+recorrido;

        RequestFuture<JSONObject> future = RequestFuture.newFuture();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, future, future);

        VolleySingleton.getmInstance(mActivity.getApplicationContext()).addToRequestQueue((jsonObjectRequest));

        JSONObject resp = future.get(timeOutRequest, TimeUnit.SECONDS);

        JSONArray paradasServidor = resp.getJSONArray("data"); // get the JSONArray

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

        return paradasRecorrido;
    }


    public void makeRequestPostEnviarUbicacion(final String seleccionLin, final String seleccionCol, String seleccionRec, final String latitud, final String longitud) throws ExecutionException, InterruptedException, TimeoutException {

        final String url = ipv4+"enviarUbicacion";

        Map<String, String> params = new HashMap();
        params.put("linea",seleccionLin);
        params.put("colectivo", seleccionCol);
        params.put("recorrido", seleccionRec);
        params.put("latitud", latitud);
        params.put("longitud", longitud);
        JSONObject parameters = new JSONObject(params);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, future, future);
        VolleySingleton.getmInstance(mActivity.getApplicationContext()).addToRequestQueue((jsonObjectRequest));
        JSONObject resp = future.get(timeOutRequest,TimeUnit.SECONDS);


    }


    @Override
    public void makePostInformeColeDetenido(final String seleccionLin, final String seleccionCol, final String seleccionRec, final String latitud, final String longitud, final String segundosDetenidoStr) throws ExecutionException, InterruptedException, TimeoutException {
// final String fuActualD,
        final String url = ipv4 + "enviarNotificacionColectivoDetenido";

        Map<String, String> params = new HashMap();
        params.put("linea", seleccionLin);
        params.put("colectivo", seleccionCol);
        params.put("recorrido", seleccionRec);
        params.put("latitud", latitud);
        params.put("longitud", longitud);
//        params.put("fechaNotificacion",  String.valueOf(fechaUbicacion));
        params.put("segundosDetenidoStr", segundosDetenidoStr);
        JSONObject parameters = new JSONObject(params);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, future, future);
        VolleySingleton.getmInstance(mActivity.getApplicationContext()).addToRequestQueue((jsonObjectRequest));
        JSONObject resp = future.get(timeOutRequest, TimeUnit.SECONDS);

    }


    // trabajar en este metodo tambien. ver como estaba implementado
    @Override
    public void makePostActualizacionNotifColeDetenido(String seleccionLin, String seleccionCol, String seleccionRec, String latitud, String longitud, final String segundosDetenidoStr) throws ExecutionException, InterruptedException, TimeoutException {
        final String url = ipv4 + "actualizarNotificacionColeDetenido";
//        final long fechaUbicacion = System.currentTimeMillis();
        Map<String, String> params = new HashMap();
        params.put("linea", seleccionLin);
        params.put("colectivo", seleccionCol);
        params.put("recorrido", seleccionRec);
        params.put("latitud", latitud);
        params.put("longitud", longitud);
//        params.put("fechaNotificacion",  String.valueOf(fechaUbicacion));
        params.put("segundosDetenidoStr", segundosDetenidoStr);
        JSONObject parameters = new JSONObject(params);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, future, future);
        VolleySingleton.getmInstance(mActivity.getApplicationContext()).addToRequestQueue((jsonObjectRequest));
        JSONObject resp = future.get(timeOutRequest, TimeUnit.SECONDS);
    }



    public void makePostFinNotificacionColeDetenido(final String seleccionLin, final String seleccionCol, String seleccionRec, final String latitud, final String longitud, final String segundosDetenidoStr) throws ExecutionException, InterruptedException, TimeoutException {
        final String url = ipv4 + "finNotificacionColeDetenido";
        Map<String, String> params = new HashMap();
        params.put("linea", seleccionLin);
        params.put("colectivo", seleccionCol);
        params.put("recorrido", seleccionRec);
        params.put("latitud", latitud);
        params.put("longitud", longitud);
        params.put("segundosDetenidoStr", segundosDetenidoStr);
        JSONObject parameters = new JSONObject(params);

        System.out.println("los datos que envia al detener la notificacion cole detenido: ");
        System.out.println("linea: " + seleccionLin);

        params.put("colectivo", seleccionCol);
        params.put("recorrido", seleccionRec);
        params.put("latitud", latitud);
        params.put("longitud", longitud);
        params.put("segundosDetenidoStr", segundosDetenidoStr);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, future, future);
        VolleySingleton.getmInstance(mActivity.getApplicationContext()).addToRequestQueue((jsonObjectRequest));
        JSONObject resp = future.get(timeOutRequest, TimeUnit.SECONDS);
    }


    @Override
    public void makeRequestPostColeEnParada(final int codigo, final String denomLinea, final String unidad, final String denomRecorrido) throws ExecutionException, InterruptedException, TimeoutException {
        final String url = ipv4+"coleEnParada";


        Map<String, String> params = new HashMap();
        params.put("linea",denomLinea);
        params.put("colectivo", unidad);
        params.put("recorrido", denomRecorrido);
        params.put("codigo", String.valueOf( codigo ));


        JSONObject parameters = new JSONObject(params);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, future, future);

        VolleySingleton.getmInstance(mActivity.getApplicationContext()).addToRequestQueue((jsonObjectRequest));
        JSONObject resp = future.get(timeOutRequest,TimeUnit.SECONDS);

    }


    // metodo antiguo andando bien
    @Override
    public void makeRequestPostDetectarDesvio(final String linea, final String colectivo, final String recorrido, final Double lat, final Double lng) throws ExecutionException, InterruptedException, TimeoutException {
        final String url = ipv4+"detectarDesvio";

        Map<String, String> params = new HashMap();
        params.put("linea",linea);
        params.put("colectivo", colectivo);
        params.put("recorrido", recorrido);
        params.put("latitud", String.valueOf(lat));
        params.put("longitud", String.valueOf(lng));
        params.put("fechaNotificacion", String.valueOf(System.currentTimeMillis()));

        JSONObject parameters = new JSONObject(params);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, future, future);
        VolleySingleton.getmInstance(mActivity.getApplicationContext()).addToRequestQueue((jsonObjectRequest));
        JSONObject resp = future.get(timeOutRequest,TimeUnit.SECONDS);

    }

    // tomar de base este
    @Override
    public void makeRequestPostFinDesvio(final String linea, final String colectivo, String recorrido) throws ExecutionException, InterruptedException, TimeoutException {

        final String url = ipv4+"finDesvio";

        Map<String, String> params = new HashMap();
        params.put("linea",linea);
        params.put("colectivo", colectivo);
        params.put("recorrido", recorrido);

        JSONObject parameters = new JSONObject(params);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, future, future);
        VolleySingleton.getmInstance(mActivity.getApplicationContext()).addToRequestQueue((jsonObjectRequest));
        JSONObject resp = future.get(timeOutRequest,TimeUnit.SECONDS);
    }


    @Override
    public void makeRequestPostFinColectivoRecorrido(final String seleccionLin, final String seleccionCol, final String seleccionRec) throws ExecutionException, InterruptedException, TimeoutException {
        final String url = ipv4+"fin";


        Map<String, String> params = new HashMap();
        params.put("linea",seleccionLin);
        params.put("colectivo", seleccionCol);
        params.put("recorrido", seleccionRec);

        JSONObject parameters = new JSONObject(params);


        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, future, future);
        VolleySingleton.getmInstance(mActivity.getApplicationContext()).addToRequestQueue((jsonObjectRequest));
        JSONObject resp = future.get(timeOutRequest,TimeUnit.SECONDS);

    }
}
