package com.stcu.appcolectivo.model;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.stcu.appcolectivo.interfaces.MainInterface;
import com.stcu.appcolectivo.ui.MainActivity;
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

public class MainModel implements MainInterface.Model {
//    public static String ipv4 = "http://stcu.mdn.unp.edu.ar:50002/stcu_app/";
    public static String ipv4 = "http://192.168.0.108:50000/v1/mobile/";

    Activity mActivity;
    Context mContext;
    RequestQueue requestQueue;
    List<String> listadoLineas = new ArrayList<String>();
    List<String> listadoColectivos = new ArrayList<String>();
    List<Coordenada> listaParadas;
    private List<Coordenada> listaCoordenadasTrayecto;
    private List<Recorrido> listaRecorridosActivos;
    Coordenada parada;
    Coordenada coord;

    String myLat;
    String myLng;
    private MainInterface.Presenter presenter;

    public MainModel(MainInterface.Presenter presenter, Context mContext, Activity mActivity) {

        this.presenter = presenter;
        this.mContext = mContext;
        this.mActivity = mActivity;
        requestQueue = Volley.newRequestQueue(mContext);
    }

    //metodo que anda bien consultaLineasActivas
    /*
    public List<Linea> consultaLineasActivas() {
        System.out.println("entra dentro de consulta lineas");
        requestQueue = Volley.newRequestQueue(mContext);

        String url = ipv4+"lineas/activas";

        //List<String> lineasDisponibles = new ArrayList<>();
        List<Linea> lineasDisponibles = new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray ja = response.getJSONArray("data"); // get the JSONArray

                            for(int i=0;i<ja.length();i++){
                                JSONObject linea = ja.getJSONObject(i);
                                String denominacion = linea.getString("denominacion");
                                String descripcion = linea.getString("descripcion");
                                boolean enServicio = linea.getBoolean("enServicio");
                                Long idLinea = Long.parseLong(linea.getString("id"));
                                lineasDisponibles.add(new Linea(idLinea, denominacion,descripcion,enServicio));
                            }

//                            presenter.showLineasDisponibles(lineasDisponibles);
                            System.out.println("Respuesta del servidor ok: " + lineasDisponibles.get(0));



                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Respuesta del servidor con error: " + error.toString());

                    }
                });

        requestQueue.add(jsonObjectRequest);

        return lineasDisponibles;
    }
     */

    //metodo que anda bien consultaColectivosActivos
    /*    @Override
    public List<Colectivo> consultaColectivosActivos() {
        System.out.println("entra dentro de consulta colectivos");
        requestQueue = Volley.newRequestQueue(mContext);

        String url = ipv4+"colectivos";

        List<Colectivo> colectivos = new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {


                            JSONArray ja = response.getJSONArray("data"); // get the JSONArray

                            for(int i=0;i<ja.length();i++){
                                JSONObject colectivo = ja.getJSONObject(i);
                                Long idColectivo = Long.parseLong(colectivo.getString("id"));
                                String patente = colectivo.getString("patente");
                                String unidad = colectivo.getString("unidad");
                                String marca = colectivo.getString("marca");
                                colectivos.add(new Colectivo(idColectivo,unidad,patente,marca));
                            }

//                            presenter.showLineasDisponibles(lineasDisponibles);

                            System.out.println("Respuesta del servidor ok: " + colectivos.get(0));



                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Respuesta del servidor con error: " + error.toString());

                    }
                });

        requestQueue.add(jsonObjectRequest);
        return colectivos;
    }*/


    //metodo para probar asyncTask
    @Override
    public List<Linea> consultaLineasActivas() throws ExecutionException, InterruptedException, TimeoutException {
        System.out.println("entra dentro de consulta lineas");

        String url = ipv4+"lineas/activas";

        List<Linea> lineasDisponibles = new ArrayList<>();
        RequestFuture<JSONObject> future = RequestFuture.newFuture();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, future, future);

        VolleySingleton.getmInstance(mActivity.getApplicationContext()).addToRequestQueue((jsonObjectRequest));

        JSONObject resp = future.get(5,TimeUnit.SECONDS);

        try {
            JSONArray ja = resp.getJSONArray("data"); // get the JSONArray
            for(int i=0;i<ja.length();i++){
                JSONObject linea = ja.getJSONObject(i);
                String denominacion = linea.getString("denominacion");
                String descripcion = linea.getString("descripcion");
                boolean enServicio = linea.getBoolean("enServicio");
                Long idLinea = Long.parseLong(linea.getString("id"));
                lineasDisponibles.add(new Linea(idLinea, denominacion,descripcion,enServicio));
            }
            System.out.println("Respuesta del servidor ok: " + lineasDisponibles.get(0));

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return lineasDisponibles;
    }



    //metodo para probar asyncTask
    @Override
    public List<Colectivo> consultaColectivosActivos() throws ExecutionException, InterruptedException, TimeoutException {
        System.out.println("entra dentro de consulta colectivos");

        String url = ipv4+"colectivos";

        List<Colectivo> colectivosDisponibles = new ArrayList<>();


        RequestFuture<JSONObject> future = RequestFuture.newFuture();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, future, future);

        VolleySingleton.getmInstance(mActivity.getApplicationContext()).addToRequestQueue((jsonObjectRequest));

        JSONObject resp = future.get(5,TimeUnit.SECONDS);
        try {
            JSONArray ja = resp.getJSONArray("data"); // get the JSONArray
            for(int i=0;i<ja.length();i++){
                JSONObject colectivo = ja.getJSONObject(i);
                Long idColectivo = Long.parseLong(colectivo.getString("id"));
                String patente = colectivo.getString("patente");
                String unidad = colectivo.getString("unidad");
                String marca = colectivo.getString("marca");
                colectivosDisponibles.add(new Colectivo(idColectivo,unidad,patente,marca));
            }
            System.out.println("Respuesta del servidor ok: " + colectivosDisponibles.get(0));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return colectivosDisponibles;
    }





    public NetworkInfo isNetAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        //for airplane mode, networkinfo is null
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        return activeNetwork;
   }

   // metodo antiguo sin future.get. andando
/*    *//**
     * Metodo que recupera la lista de coordenadas a utilizar para simular el trayecto seleccionado
     * @param denom denominacion de la linea seleccionada
     * @param seleccionRec2 seleccion de uno de los recorridos activos de esa linea seleccionada
     * @return lista de coordenadas del trayecto seleccionado
     *//*
    public List<Coordenada> consultaTrayectoASimular(String denom, String seleccionRec2) {
        listaCoordenadasTrayecto = new ArrayList<Coordenada>();
        String url = ipv4 + "trayectos/" + denom +"/"+ seleccionRec2;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray paradas = response.getJSONArray("data"); // get the JSONArray

                            for (int i = 0; i < paradas.length(); i++) {
                                JSONObject parada = paradas.getJSONObject(i);

                                JSONObject coordLng = new JSONObject(parada.getString("parada")).getJSONObject("coordenada");
                                String longitud = coordLng.getString("lng");

                                JSONObject coordLat = new JSONObject(parada.getString("parada")).getJSONObject("coordenada");
                                String latitud = coordLat.getString("lat");

                                String direccion= new JSONObject(parada.getString("parada")).getString("direccion");

                                // esto quizas se deba llamar Parada, o paradaRecorrido en vez de coordenada
                                coord = new Coordenada();
                                coord.setLatitud(Double.parseDouble(latitud));
                                coord.setLongitud(Double.parseDouble(longitud));
                                coord.setDireccion(direccion);
                                listaCoordenadasTrayecto.add(coord);
                            }

//                            System.out.println("Respuesta del servidor ok: " + colectivos.get(0));

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Respuesta del servidor con error: " + error.toString());
                    }
                });

        requestQueue.add(jsonObjectRequest);
        return listaCoordenadasTrayecto;
    }*/






    /**
     * Metodo que recupera la lista de coordenadas a utilizar para simular el trayecto seleccionado
     * @param denom denominacion de la linea seleccionada
     * @param seleccionRec2 seleccion de uno de los recorridos activos de esa linea seleccionada
     * @return lista de coordenadas del trayecto seleccionado
     */
    public List<Coordenada> consultaTrayectoASimular(String denom, String seleccionRec2) throws JSONException, ExecutionException, InterruptedException, TimeoutException {

        listaCoordenadasTrayecto = new ArrayList<Coordenada>();
        String url = ipv4 + "trayectos/" + denom +"/"+ seleccionRec2;

        RequestFuture<JSONObject> future = RequestFuture.newFuture();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, future, future);

        VolleySingleton.getmInstance(mActivity.getApplicationContext()).addToRequestQueue((jsonObjectRequest));

        JSONObject resp = future.get(5,TimeUnit.SECONDS);

        JSONArray paradas = resp.getJSONArray("data"); // get the JSONArray

        for (int i = 0; i < paradas.length(); i++) {
            JSONObject parada = paradas.getJSONObject(i);

            JSONObject coordLng = new JSONObject(parada.getString("parada")).getJSONObject("coordenada");
            String longitud = coordLng.getString("lng");

            JSONObject coordLat = new JSONObject(parada.getString("parada")).getJSONObject("coordenada");
            String latitud = coordLat.getString("lat");

            String direccion= new JSONObject(parada.getString("parada")).getString("direccion");

            // esto quizas se deba llamar Parada, o paradaRecorrido en vez de coordenada
            coord = new Coordenada();
            coord.setLatitud(Double.parseDouble(latitud));
            coord.setLongitud(Double.parseDouble(longitud));
            coord.setDireccion(direccion);
            listaCoordenadasTrayecto.add(coord);
        }

//      System.out.println("Respuesta del servidor ok: " + colectivos.get(0));
        return listaCoordenadasTrayecto;
    }










    public void obtenerUbicacion() {

        LocationManager locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                presenter.showUbicacion(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

    }

    @Override
    public void enviarInicioServicioAServidor(String seleccionLin, String seleccionCol, Long fechaUbicacionI) {
        //llamo al presenter para enviar el resultado devuelta
        //TODO 4 -cuando tiene el dato no se lo envia directo a la vista, pasa devuelta por el presenter
//        presenter.showResultPresenter(String.valueOf(resultado));


        final String url = ipv4+"rest/lineaColectivos/inicio"; // uni
        final Long fechaUbicacion = System.currentTimeMillis();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    //                    @Override
                    public void onResponse(String response) {

                        response = response.replaceAll ("\"","");
                        presenter.showResponseInicioServicioOk(response,seleccionLin,seleccionCol,fechaUbicacionI);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        presenter.showResponseError("No se pudo iniciar el servicio");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

//                MainActivity activity = (MainActivity) getActivity();
                //esto lo puedo pasar por paramtros
                MainActivity activity = (MainActivity) mActivity;
                myLat = activity.getLat();
                myLng = activity.getLng();

                params.put("linea", seleccionLin);
                params.put("colectivo", seleccionCol);
                params.put("latitud", myLat);
                params.put("longitud", myLng);
                params.put("fechaUbicacion", String.valueOf(fechaUbicacion));

                return params;
            }
        };
        requestQueue.add(postRequest);
//        addToQueue(postRequest);
    }

    // todo esto trabajando en este para simulacion recorrido
    /**
     * Metodo utilizado para inicializar la simulacion del recorrido
     *
     * @param seleccionLin
     * @param seleccionCol
     * @param seleccionRec
     * @param latInicial
     * @param lngInicial
     */
    public void makeRequestPostSimulacion(final String seleccionLin, final String seleccionCol, final String seleccionRec, final String latInicial, final String lngInicial) {

        final String url = ipv4+"inicio"; // uni
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
    }


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

                        presenter.showResponseError("No se pudo realizar la operacion");
//                        Toast toast1 = Toast.makeText(getActivity(),"No se pudo realizar la operacion", Toast.LENGTH_SHORT);
//                        toast1.show();
//                        Toast.makeText( getActivity(),"Vuelva a intentarlo",Toast.LENGTH_SHORT ).show();

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
//        addToQueue(postRequest);
    }



    // consultaRecorridosActivos andando antigua. sin future get
   /* public List<Recorrido> consultaRecorridosActivos(String denomLinea) {
        listaRecorridosActivos = new ArrayList<Recorrido>();
        String url = ipv4 + "recorridosActivos/" + denomLinea;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray ja = response.getJSONArray("data"); // get the JSONArray

                            for(int i=0;i<ja.length();i++){
                                JSONObject recorridoActivo = ja.getJSONObject(i);
                                Long idRecorrido = Long.parseLong(recorridoActivo.getString("id"));
                                String denominacion = recorridoActivo.getString("denominacion");
                                boolean activo = Boolean.parseBoolean(recorridoActivo.getString("activo"));

                                listaRecorridosActivos.add(new Recorrido(idRecorrido,denominacion,activo));
                            }

//                            presenter.showLineasDisponibles(lineasDisponibles);

                            System.out.println("Respuesta del servidor ok: " + listaRecorridosActivos.get(0));

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Respuesta del servidor con error: " + error.toString());

                    }
                });
        requestQueue.add(jsonObjectRequest);
        return listaRecorridosActivos;
    }*/

    public List<Recorrido> consultaRecorridosActivos(String denomLinea) throws ExecutionException, InterruptedException, TimeoutException {
        listaRecorridosActivos = new ArrayList<Recorrido>();
        String url = ipv4 + "recorridosActivos/" + denomLinea;

        RequestFuture<JSONObject> future = RequestFuture.newFuture();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, future, future);

        VolleySingleton.getmInstance(mActivity.getApplicationContext()).addToRequestQueue((jsonObjectRequest));
//        requestQueue.add(jsonObjectRequest);

        JSONObject resp = future.get(5,TimeUnit.SECONDS);

        try {
            JSONArray ja = resp.getJSONArray("data"); // get the JSONArray

            for(int i=0;i<ja.length();i++){
                JSONObject recorridoActivo = ja.getJSONObject(i);
                Long idRecorrido = Long.parseLong(recorridoActivo.getString("id"));
                String denominacion = recorridoActivo.getString("denominacion");
                boolean activo = Boolean.parseBoolean(recorridoActivo.getString("activo"));

                listaRecorridosActivos.add(new Recorrido(idRecorrido,denominacion,activo));
            }

//        presenter.showLineasDisponibles(lineasDisponibles);

            System.out.println("Respuesta del servidor ok: " + listaRecorridosActivos.get(0));

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return listaRecorridosActivos;
    }


}
