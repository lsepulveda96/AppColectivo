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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.stcu.appcolectivo.MainActivity;
import com.stcu.appcolectivo.interfaces.MainInterface;
import com.stcu.appcolectivo.ui.Coordenada;

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
    public static String ipv4 = "http://192.168.0.103:50000/v1/mobile/";

    Activity mActivity;
    Context mContext;
    RequestQueue requestQueue;
    List<String> listadoLineas = new ArrayList<String>();
    List<String> listadoColectivos = new ArrayList<String>();
    List<Coordenada> listaParadas;
    private List<Coordenada> listaCoordenadasTrayecto;
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


    public List<String> consultaLineasActivas() {

        requestQueue = Volley.newRequestQueue(mContext);

        String url = ipv4+"lineas/activas";

        List<String> lineasDisponibles = new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray ja = response.getJSONArray("data"); // get the JSONArray

                            for(int i=0;i<ja.length();i++){
                                JSONObject linea = ja.getJSONObject(i);
                                String denominacion = linea.getString("denominacion");
                                lineasDisponibles.add(denominacion);
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

    //metodo que anda bien
//    public List<String> consultaLineasActivas() {
//        List<String> lineasActivas = new ArrayList<>();
//
//        String url = ipv4 + "rest/lineas/activas"; // ip uni
//
//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
//                Request.Method.GET,
//                url,
//                (String) null,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        try {
//                            for (int i = 0; i < response.length(); i++) {
//                                JSONObject linea = response.getJSONObject(i);
//                                String id = linea.getString("id");
//                                String denominacion = linea.getString("denominacion");
//                                String enServicio = linea.getString("enServicio");
//
//                                listadoLineas.add(denominacion);
////                                presenter.showListadoLineas(listadoLineas);
//
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                    }
//                }
//        );
//        requestQueue.add(jsonArrayRequest);
//        return lineasActivas;
//    }

    @Override
    public List<String> consultaColectivosActivos() {
        requestQueue = Volley.newRequestQueue(mContext);

        String url = ipv4+"colectivos";

        List<String> colectivos = new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray ja = response.getJSONArray("data"); // get the JSONArray

                            for(int i=0;i<ja.length();i++){
                                JSONObject colectivo = ja.getJSONObject(i);
                                String id = colectivo.getString("id");
                                String patente = colectivo.getString("patente");
                                String unidad = colectivo.getString("unidad");
                                colectivos.add(unidad);
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
    }




//    JSONObject colectivo = response.getJSONObject(i);
//    String id = colectivo.getString("id");
//    String patente = colectivo.getString("patente");
//    String unidad = colectivo.getString("unidad");
//















    public NetworkInfo isNetAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        //for airplane mode, networkinfo is null
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        return activeNetwork;
   }

    public List<Coordenada> consultaTrayectoASimular(String denom) {
        listaCoordenadasTrayecto = new ArrayList<Coordenada>();
        String url = ipv4 + "rest/lineaColectivos/trayectos/" + denom;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                (String) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            //ver donde usa esto!! cuando ande el servidor
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject trayecto = response.getJSONObject(i);
                                String latitud = trayecto.getString("latitud");
                                String longitud = trayecto.getString("longitud");
                                coord = new Coordenada();
                                coord.setLatitud(Double.parseDouble(latitud));
                                coord.setLongitud(Double.parseDouble(longitud));
                                listaCoordenadasTrayecto.add(coord);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
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


/*                        Toast toast1 =
                                Toast.makeText(getActivity(),
                                        response, Toast.LENGTH_LONG);
                        toast1.show();

                        if(response.equals("Servicio iniciado")) {


                            //pasar devuelta el control al mainActivity para que llame al nuevo activity
                            //presenter.showResult() o servicioIniciado() o response()
                            //los parametros ya los tenia del activity del que vino
                            //
                            MainActivity activity2 = (MainActivity) getActivity();
                            String myLat2 = activity2.getLat();
                            String myLng2 = activity2.getLng();

                            //esto deberia hacerlo en el mainActivity, aca solo llama a la api para iniciar el servicio
                            Intent ma2 = new Intent(getActivity(), ColectivoEnServicioActivity.class);
                            ma2.putExtra("linea", seleccionLin);
                            ma2.putExtra("colectivo", seleccionCol);
                            ma2.putExtra("latitud", myLat2);
                            ma2.putExtra("longitud", myLng2);

                            ma2.putExtra("fechaUbicacion", String.valueOf(System.currentTimeMillis()));
                            getActivity().startActivity(ma2);
                        }*/
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        presenter.showResponseInicioServicioError(error.toString());
                        presenter.showResponseError("No se pudo iniciar el servicio");
//                        Toast toast1 = Toast.makeText(getActivity(),"No se pudo iniciar el servicio", Toast.LENGTH_SHORT);
//                        toast1.show();
//                        Toast.makeText( getActivity(),"Vuelva a intentarlo",Toast.LENGTH_SHORT ).show();
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

    public void makeRequestPostSimulacion(final String seleccionLin, final String seleccionCol, final String latInicial, final String lngInicial) {

        final String url = ipv4+"rest/lineaColectivos/inicio"; // uni
        final Long fechaUbicacion = System.currentTimeMillis();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        response = response.replaceAll ("\"","");
                        presenter.showResponsePostSimulacionOk(response,seleccionLin,seleccionCol,latInicial, lngInicial);

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
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        presenter.showResponseError("No se pudo iniciar el servicio");
//                        presenter.showResponsePostSimulacionError(error.toString());
//                        Toast toast1 = Toast.makeText(getActivity(),"No se pudo iniciar el servicio", Toast.LENGTH_SHORT);
//                        toast1.show();
//                        Toast.makeText( getActivity(),"Vuelva a intentarlo",Toast.LENGTH_SHORT ).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                params.put("linea",seleccionLin);
                params.put("colectivo", seleccionCol);
                params.put("latitud", latInicial);
                params.put("longitud", lngInicial);
                params.put("fechaUbicacion", String.valueOf(fechaUbicacion));

                return params;
            }
        };
        requestQueue.add(postRequest);
//        addToQueue(postRequest);
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
}
