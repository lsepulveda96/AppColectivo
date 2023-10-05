package com.stcu.appcolectivo.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gpmess.example.volley.app.R;
import com.stcu.appcolectivo.interfaces.TrayectoARecorrerInterface;
import com.stcu.appcolectivo.model.Coordenada;
import com.stcu.appcolectivo.presenter.TrayectoARecorrerPresenter;

import org.json.JSONException;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


public class ColectivoEnServicioActivity extends Activity implements TrayectoARecorrerInterface.View {

    Boolean enTransito = true, notActiva = false;
    public static double distanciaOffSetMov = 20.0;
    public static int tiempoMaxDetenido = 17;

    private TextView tvLinea, tvColectivo, tvLatitud, tvLongitud, tvUbicacion, tvEstado;
    private String linea, colectivo, recorrido, latitud, longitud, fechaUbicacionInicialS, myLat, myLng, latAntigua, lngAntigua;
    private Long fechaUbicacionAntigua, fechaUbicacionInicial, fechaUbicacionActual;
    private Button finServicio;
    Double latActual, lngActual, distancia = 0.0;
    int difTotal = 0, contDesvio = 0, contVerifParada = 0;
    List<Coordenada> paradasRecorrido;

    private TrayectoARecorrerInterface.Presenter presenter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servicio_colectivo_activity);

        presenter = new TrayectoARecorrerPresenter(this, this, this);
        tvLinea = (TextView) findViewById(R.id.tvLinea);
        tvColectivo = (TextView) findViewById(R.id.tvColectivo);
        tvLatitud = (TextView) findViewById(R.id.tvLatitud);
        tvEstado = (TextView) findViewById(R.id.tvEstado);
        tvLongitud = (TextView) findViewById(R.id.tvLongitud);
        finServicio = (Button) findViewById(R.id.btnFinServicio);
        tvUbicacion = (TextView) findViewById(R.id.tvUbicacion);

        linea = getIntent().getExtras().getString("linea");
        colectivo = getIntent().getExtras().getString("colectivo");
        recorrido = getIntent().getExtras().getString("recorrido");
        latitud = getIntent().getExtras().getString("latitud");
        longitud = getIntent().getExtras().getString("longitud");
        fechaUbicacionInicialS = getIntent().getExtras().getString("fechaUbicacion");
        fechaUbicacionInicial = Long.valueOf(fechaUbicacionInicialS);
        fechaUbicacionAntigua = fechaUbicacionInicial;

        tvLinea.setText(linea);
        tvColectivo.setText(colectivo);
        tvLatitud.setText(latitud);
        tvLongitud.setText(longitud);

        setLat(latitud); // para que la primera vez no sea null, despues este valor se pisa
        setLng(longitud);

        latAntigua = latitud; // para que la primera vez no sea null, despues este valor se pisa
        lngAntigua = longitud;

        latActual = Double.parseDouble(latitud);
        lngActual = Double.parseDouble(longitud);

        new InicioRecorrido().execute();

    } // fin onCreate

    public class InicioRecorrido extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                paradasRecorrido = presenter.consultaParadasRecorrido(linea, recorrido);
            } catch (ExecutionException | InterruptedException | TimeoutException | JSONException e) {
                System.out.println("Error en consulta paradas recorridos: " + e);
                throw new RuntimeException(e);
            }

//        TODO por si es parada final
            try {
                Thread.sleep(3000);
                detectarParada(paradasRecorrido, latActual, lngActual, linea, colectivo, recorrido);
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                throw new RuntimeException(e);
            }

            for (int i=0;i<paradasRecorrido.size();i++) {


                if (enTransito) {
                    obtenerUbicacion();

                    latActual = getLatActual();
                    lngActual = getLngActual();
                    fechaUbicacionActual = getFechaUbicacion(); // la que recive de la ubicacion actual

                    // TODO por si es parada final
                    //que envie la consulta de desvio la primera vez
                    if (contDesvio < 1) {
                        // si la primera vez esta parado y esta en una parada tambien detecta la parada
                        try {
                            detectarParada(paradasRecorrido, latActual, lngActual, linea, colectivo, recorrido);
                            presenter.makeRequestPostEnvioDesvio(linea, colectivo, recorrido, latActual, lngActual);
                            Thread.sleep(2000);
                        } catch (ExecutionException | InterruptedException | TimeoutException e) {
                            throw new RuntimeException(e);
                        }
//                        Toast.makeText(ColectivoEnServicioActivity.this, "Detectando desvio..", Toast.LENGTH_SHORT).show();
                        System.out.println("detectanto desvio..");
                        contDesvio++;
                    }

                    // TODO inicio colectivo detenido
                    distancia = calcularDistancia(Double.parseDouble(latAntigua), Double.parseDouble(lngAntigua), latActual, lngActual);
                    Date fuAntiguaD = new Date(fechaUbicacionAntigua);
                    Date fuActualD = new Date(fechaUbicacionActual);

                    int difSeg = (int) (fuActualD.getTime() - fuAntiguaD.getTime()) / 1000; // en segundos

                    // si esta detenido
                    if (distancia < distanciaOffSetMov) { // si es menor a 20.0 metros, esta detenido // que luego pueda ser configurable // 20.0
                        difTotal = difTotal + difSeg; // suma el tiempo total detenido // tambien se puede con cont++ cada 3 intentos envia
//                        Toast.makeText(ColectivoEnServicioActivity.this, difTotal + " segundos detenido", Toast.LENGTH_SHORT).show();
                        System.out.println(difTotal + " segundos detenido");
                        tvEstado.setText("Unidad detenida");

                        if (contVerifParada < 1) {
                            //si esta parado la primera vez detecta la parada
                            try {
                                detectarParada(paradasRecorrido, latActual, lngActual, linea, colectivo, recorrido);
                            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                                throw new RuntimeException(e);
                            }
                            //por si es parada final
                            contVerifParada++;
                        }

                        //si esta detenido por mas de 'x' tiempo
                        if (difTotal > tiempoMaxDetenido) { // si el tiempo que esta detenido es mayor a 17 seg envia el informe (configurable) // 17

                            // si el colectivo todavia sigue parado, actualiza la notificacion
                            if (notActiva == true) {
                                presenter.makeRequestPostActInforme(linea, colectivo, String.valueOf(latActual), String.valueOf(lngActual), String.valueOf(fechaUbicacionActual), "" + difTotal);
//                                Toast.makeText(ColectivoEnServicioActivity.this, "unidad detenida, actualizando informe..", Toast.LENGTH_SHORT).show();
                                System.out.println("unidad detenida, actualizando informe..");
                            } else {
                                // sino es la primera vez que el colectivo se para y crea la nueva notificacion y setea bandera en true
                                presenter.makeRequestPostEnvioInforme(linea, colectivo, String.valueOf(latActual), String.valueOf(lngActual), String.valueOf(fechaUbicacionActual), "" + difTotal);
                                Toast.makeText(ColectivoEnServicioActivity.this, "unidad detenida, enviando informe..", Toast.LENGTH_SHORT).show();
                                System.out.println("unidad detenida, enviando informe..");
                                notActiva = true;
                            }
                        }

                    } else {
                        // si el colectivo estaba parado, y empezo a circular, actualiza la notificacion(con el tiempo final) y cambia la bandera a false
                        if (notActiva == true) {
                            presenter.makeRequestPostFinInforme(linea, colectivo, String.valueOf(latActual), String.valueOf(lngActual), String.valueOf(fechaUbicacionActual), "" + difTotal);
                            notActiva = false; // resetea la bandera
                        }

                        // contador en 0 para que cuando se vuelve a parar la primera vez verifique si es parada
                        contVerifParada = 0;
                        // aca realiza las tres acciones (detecta parada, detecta desvio y envia ubicacion)
                        //por si es parada final

//                   vuelve a verificar si esta desviado
                        boolean esFin = false;
                        try {
                            esFin = detectarParada(paradasRecorrido, latActual, lngActual, linea, colectivo, recorrido);
                        } catch (ExecutionException | InterruptedException | TimeoutException e) {
                            throw new RuntimeException(e);
                        }
                        // vuelve a verificar si esta desviado
                        if (esFin) {
                            finish();
                        } else {
                            try {
                                presenter.makeRequestPostEnvioDesvio(linea, colectivo, recorrido, latActual, lngActual);
                            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                                throw new RuntimeException(e);
                            }
//                            Toast.makeText(ColectivoEnServicioActivity.this, "Detectando desvio..", Toast.LENGTH_SHORT).show();

//                            Toast.makeText(ColectivoEnServicioActivity.this, "enviando ubicacion..", Toast.LENGTH_SHORT).show();

                            System.out.println("Detectando desvio..");
                            System.out.println( "enviando ubicacion..");
                            try {
                                presenter.makeRequestPostEnvio(linea, colectivo, recorrido, getLat(), getLng());
                            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                                throw new RuntimeException(e);
                            }

                            difTotal = 0; // resetea la suma
                            tvEstado.setText("Unidad en circulacion");
                        }
                    }
                    fechaUbicacionAntigua = fechaUbicacionActual;
//                TODO fin colectivo detenido
                } // fin if en transito
            }
            return true;
        } // fin doInBackGround

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            System.out.println("recorrido terminado");
            finish();
        }

    }

    private double calcularDistancia(Double latAntigua, Double lngAntigua, Double latActual, Double lngActual) {
        Location locAntigua = new Location("location antigua");
        locAntigua.setLatitude(latAntigua);
        locAntigua.setLongitude(lngAntigua);
        Location locActual = new Location("location actual");
        locActual.setLatitude(latActual);
        locActual.setLongitude(lngActual);
        double distance = locAntigua.distanceTo(locActual);
        return distance;
    }

    // ya iniciado el servicio
    public void obtenerUbicacion() {
        LocationManager locationManager = (LocationManager) ColectivoEnServicioActivity.this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                tvUbicacion.setText(location.getLatitude() + "" + location.getLongitude());
                setLat(String.valueOf(location.getLatitude()));
                setLng(String.valueOf(location.getLongitude()));

                setLatActual(location.getLatitude()); // se pueden sacar por las dos de arriba
                setLngActual(location.getLongitude());
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

        fechaUbicacionActual = System.currentTimeMillis();
        setFechaUbicacion(fechaUbicacionActual);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }



        public boolean detectarParada(List<Coordenada> listitaParadas, Double latActual, Double lngActual, String denom, String unidad, String denomRecorrido) throws ExecutionException, InterruptedException, TimeoutException {
            boolean esFin = false;
            for(Coordenada parada: listitaParadas){
                double distancia = calcularDistancia(latActual,lngActual,parada.getLatitud(),parada.getLongitud());
                if (distancia < 20.0) {
                    if(getParadaFinal().getCodigo() == parada.getCodigo()){
                        esFin = true;
                        Toast.makeText( this, "Colectivo en parada final", Toast.LENGTH_SHORT ).show();
                        presenter.makeRequestPostColeEnParada( parada.getCodigo(), denom, unidad, denomRecorrido); // a rest lineaColectivo
                        finServicioSimple();
                        finish();
                    }else {
                        // es porque esta en o cerca de una parada de esa linea que esta en servicio
                        Toast.makeText( this, "Colectivo en parada", Toast.LENGTH_SHORT ).show();
                        presenter.makeRequestPostColeEnParada( parada.getCodigo(), denom, unidad, denomRecorrido ); // a rest lineaColectivo
                    }
                }
            }
            return esFin;
        }

    public Coordenada getParadaFinal() {
        Coordenada paradaFinal = paradasRecorrido.get(paradasRecorrido.size()-1);
        return paradaFinal;
    }

    public String getLat(){
        return this.myLat;
    }

    public String getLng(){
        return this.myLng;
    }

    public void setLat(String lat){
        this.myLat = lat;
    }

    public void setLng(String lng){
        this.myLng = lng;
    }

    public Double getLatActual() {
        return latActual;
    }

    public void setLatActual(Double latActual) {
        this.latActual = latActual;
    }

    public Double getLngActual() {
        return lngActual;
    }

    public void setLngActual(Double lngActual) {
        this.lngActual = lngActual;
    }

    public long getFechaUbicacion() {
        return fechaUbicacionActual;
    }

    public void setFechaUbicacion(long fechaUbicacion) {
        this.fechaUbicacionActual = fechaUbicacion;
    }
    @Override
    public void showResponse(String response) {
        Toast.makeText( getApplicationContext() ,response, Toast.LENGTH_SHORT ).show();
    }

    /**
     * Detiene el servicio mediante el boton "Fin Servicio"
     *
     * @param  view    boton fin servicio
     */
    public void finServicio(View view) throws ExecutionException, InterruptedException, TimeoutException {
        //resetea el contDesvio
        contDesvio = 0;

        presenter.makeRequestPostFin(linea, colectivo, recorrido);
        enTransito = false;
        if (notActiva) {
            presenter.makeRequestPostFinInforme(linea, colectivo, String.valueOf(latActual), String.valueOf(lngActual), String.valueOf(fechaUbicacionActual), "" + difTotal);
        }
        notActiva = false; // resetea la bandera

        //por si hay una notificacion de desvio activa // nose como puedo detenerla sino
        presenter.makeRequestPostFinDesvio(linea, colectivo, recorrido);

        Toast toast1 =
                Toast.makeText(this,
                        "Servicio finalizado", Toast.LENGTH_SHORT);
        toast1.show();
        finish();
    }

    /**
     * Finaliza el servicio por haber llegado a la parada final del recorrido
     */
    public void finServicioSimple() throws ExecutionException, InterruptedException, TimeoutException {
        //resetea el contDesvio
        contDesvio = 0;
        presenter.makeRequestPostFin(linea, colectivo, recorrido);
        enTransito = false;
        if (notActiva) {
            presenter.makeRequestPostFinInforme(linea, colectivo, String.valueOf(latActual), String.valueOf(lngActual), String.valueOf(fechaUbicacionActual), "" + difTotal);
        }
        notActiva = false; // resetea la bandera

        //por si hay una notificacion de desvio activa // nose como puedo detenerla sino
        presenter.makeRequestPostFinDesvio(linea, colectivo,recorrido);

        Toast toast1 =
                Toast.makeText(this,
                        "Servicio finalizado", Toast.LENGTH_SHORT);
        toast1.show();
        finish();
    }
}