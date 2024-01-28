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

    // ip local actual
//    public static String ipv4 = "http://192.168.0.104:50004/stcu2service/v1/mobile/";

    //ip remoto actual
    public static String ipv4 =  "http://138.36.99.248:50004/stcu2service/v1/mobile/";

    static int timeOutRequest = 10; // en segundos
    Activity mActivity;
    Context mContext;
    RequestQueue requestQueue;
    private List<Coordenada> listaCoordenadasTrayecto;
    private List<Recorrido> listaRecorridosActivos;
    Coordenada coord;

    private MainInterface.Presenter presenter;

    public MainModel(MainInterface.Presenter presenter, Context mContext, Activity mActivity) {

        this.presenter = presenter;
        this.mContext = mContext;
        this.mActivity = mActivity;
        requestQueue = Volley.newRequestQueue(mContext);
    }

    @Override
    public List<Linea> consultaLineasActivas() throws ExecutionException, InterruptedException, TimeoutException {
        System.out.println("entra dentro de consulta lineas");

        String url = ipv4+"lineas/activas";

        List<Linea> lineasDisponibles = new ArrayList<>();
        RequestFuture<JSONObject> future = RequestFuture.newFuture();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, future, future);

        VolleySingleton.getmInstance(mActivity.getApplicationContext()).addToRequestQueue((jsonObjectRequest));

        JSONObject resp = future.get(timeOutRequest,TimeUnit.SECONDS);

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

            presenter.showResponseError("");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return lineasDisponibles;
    }



    //metodo para probar asyncTask
    @Override
    public List<Colectivo> consultaColectivosActivos() throws ExecutionException, InterruptedException, TimeoutException {
        System.out.println("consulta colectivos activos");

        String url = ipv4+"colectivos";

        List<Colectivo> colectivosDisponibles = new ArrayList<>();


        RequestFuture<JSONObject> future = RequestFuture.newFuture();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, future, future);

        VolleySingleton.getmInstance(mActivity.getApplicationContext()).addToRequestQueue((jsonObjectRequest));

        JSONObject resp = future.get(timeOutRequest,TimeUnit.SECONDS);
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


    /**
     * Metodo que recupera la lista de coordenadas a utilizar para simular el trayecto seleccionado
     * @param seleccionLineaDenom denominacion de la linea seleccionada
     * @param seleccionRecDenom seleccion de uno de los recorridos activos de esa linea seleccionada
     * @return lista de coordenadas del trayecto seleccionado
     */
    public List<Coordenada> consultaTrayectoASimular(String seleccionLineaDenom, String seleccionRecDenom) throws JSONException, ExecutionException, InterruptedException, TimeoutException {

        listaCoordenadasTrayecto = new ArrayList<Coordenada>();
        String url = ipv4 + "trayectos/simulacion/" + seleccionLineaDenom +"/"+ seleccionRecDenom;

        RequestFuture<JSONObject> future = RequestFuture.newFuture();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, future, future);

        VolleySingleton.getmInstance(mActivity.getApplicationContext()).addToRequestQueue((jsonObjectRequest));

        JSONObject resp = future.get(timeOutRequest,TimeUnit.SECONDS);

        JSONArray coordenadasSim = resp.getJSONArray("data"); // get the JSONArray

        for (int i = 0; i < coordenadasSim.length(); i++) {
            JSONObject coordSim = coordenadasSim.getJSONObject(i);

            String latitud = coordSim.getString("lat");
            String longitud = coordSim.getString("lng");

            System.out.println(" informacion recuperada de la nueva consulta trayectos a recorrer sim: " + latitud + " - " + longitud);
//            JSONObject coordLng = new JSONObject(coordSim.getString("parada")).getJSONObject("coordenada");
//            String longitud = coordLng.getString("lng");

//            JSONObject coordLat = new JSONObject(coordSim.getString("parada")).getJSONObject("coordenada");
//            String latitud = coordLat.getString("lat");

            coord = new Coordenada();
            coord.setLatitud(Double.parseDouble(latitud));
            coord.setLongitud(Double.parseDouble(longitud));
            listaCoordenadasTrayecto.add(coord);
        }

      /*  for (int i = 0; i < paradas.length(); i++) {
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
        }*/

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
    public void enviarInicioServicioAServidor(String seleccionLin, String seleccionCol, String seleccionRec, String lat, String lng) {

        final String url = ipv4+"inicio"; // uni
        final long fechaUbicacion = System.currentTimeMillis();
        Map<String, String> params = new HashMap();
        params.put("recorrido", seleccionRec);
        params.put("fechaUbicacion", String.valueOf(fechaUbicacion));
        params.put("linea", seleccionLin);
        params.put("colectivo", seleccionCol);
        params.put("latitud", lat);
        params.put("longitud", lng);

        JSONObject parameters = new JSONObject(params);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters,new Response.Listener<JSONObject>() {
            @Override
                    public void onResponse(JSONObject response) {
//                        response = response.replaceAll ("\"","");
                        presenter.showResponseInicioServicioOk(response.toString(),seleccionLin,seleccionCol,seleccionRec,fechaUbicacion,lat,lng);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        presenter.showResponseError("No se pudo iniciar el servicio");
                    }
                }
        );
        requestQueue.add(jsonRequest);
    }

    /**
     * Metodo utilizado para inicializar la simulacion del recorrido
     *
     * @param seleccionLin
     * @param seleccionCol
     * @param seleccionRec
     * @param latInicial
     * @param lngInicial
     * @param coordenadasSim
     */
    public void makeRequestPostSimulacion(final String seleccionLin, final String seleccionCol, final String seleccionRec, final String latInicial, final String lngInicial, List<Coordenada> coordenadasSim) {

        final String url = ipv4+"inicio";
        final long fechaUbicacion = System.currentTimeMillis();
        Map<String, String> params = new HashMap();
        params.put("linea",seleccionLin);
        params.put("colectivo", seleccionCol);
        params.put("recorrido", seleccionRec);
        params.put("latitud", latInicial);
        params.put("longitud", lngInicial);
        params.put("fechaUbicacion", String.valueOf(fechaUbicacion));
        // ver si agregar aca

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, parameters,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        presenter.showResponsePostSimulacionOk(response.toString(),seleccionLin,seleccionCol,seleccionRec,latInicial, lngInicial, coordenadasSim);
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
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        presenter.showResponseError("No se pudo realizar la operacion");
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


    public List<Recorrido> consultaRecorridosActivos(String denomLinea) throws ExecutionException, InterruptedException, TimeoutException {
        listaRecorridosActivos = new ArrayList<Recorrido>();
        String url = ipv4 + "recorridosActivos/" + denomLinea;

        RequestFuture<JSONObject> future = RequestFuture.newFuture();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, future, future);

        VolleySingleton.getmInstance(mActivity.getApplicationContext()).addToRequestQueue((jsonObjectRequest));

        JSONObject resp = future.get(timeOutRequest,TimeUnit.SECONDS);


        try {


            Boolean recorridosActivosIsEmpty = Boolean.parseBoolean(resp.getString("error"));



            if(!recorridosActivosIsEmpty) {

                JSONArray ja = resp.getJSONArray("data"); // get the JSONArray
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject recorridoActivo = ja.getJSONObject(i);
                    Long idRecorrido = Long.parseLong(recorridoActivo.getString("id"));
                    String denominacion = recorridoActivo.getString("denominacion");
                    boolean activo = Boolean.parseBoolean(recorridoActivo.getString("activo"));

                    listaRecorridosActivos.add(new Recorrido(idRecorrido, denominacion, activo));
                }

                System.out.println("+++++ consultaRecorridosActivos get first element: " + listaRecorridosActivos.get(0));
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return listaRecorridosActivos;
    }

}
