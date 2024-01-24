package com.stcu.appcolectivo.ui;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

    Boolean enTransito = true, notificacionActiva = false, coleEstaDesviado = false;
    public static double distanciaOffSetMov = 15.0; // en mts. mientras menor es el numero, mas detectara que se mueve
    public static int tiempoMaxDetenido = 20; // equivale a 4 veces el envio de la ubicacion en el mismo lugar
    private static double distanciaOffSetParada = 75.0; // en mts
    public static int tiempoEnvioNuevaCoord = 5; // en segundos

    private TextView tvLinea, tvColectivo, tvEstado, tvNombreParada;
    private String linea, colectivo, recorrido, latitud, longitud, fechaUbicacionInicialS, latAntigua, lngAntigua;
    private Long fechaUbicacionAntigua, fechaUbicacionInicial, fechaUbicacionActual;
    private Button finServicio;
    Double latActual, lngActual, distancia = 0.0;
    int segundosDetenidoStr = 0, contVerifParada = 0;
    List<Coordenada> paradasRecorrido;

    private TrayectoARecorrerInterface.Presenter presenter;

    ImageView ivGifBus;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servicio_colectivo_activity);

        presenter = new TrayectoARecorrerPresenter(this, this, this);
        tvLinea = findViewById(R.id.tvLinea);
        tvColectivo = findViewById(R.id.tvColectivo);
        tvNombreParada = findViewById(R.id.tvNombreParada);
        tvEstado = findViewById(R.id.tvEstado);
        finServicio = findViewById(R.id.btnFinServicio);
        linea = getIntent().getExtras().getString("linea");
        colectivo = getIntent().getExtras().getString("colectivo");
        recorrido = getIntent().getExtras().getString("recorrido");
        latitud = getIntent().getExtras().getString("latitud");
        longitud = getIntent().getExtras().getString("longitud");
        fechaUbicacionInicialS = getIntent().getExtras().getString("fechaUbicacion");
        fechaUbicacionInicial = Long.valueOf(fechaUbicacionInicialS);
        fechaUbicacionAntigua = fechaUbicacionInicial;

        ivGifBus = findViewById(R.id.gifbus);
        ivGifBus.setVisibility(View.GONE);

        tvLinea.setText(linea);
        tvColectivo.setText(colectivo);

        latAntigua = latitud; // para que la primera vez no sea null, despues este valor se pisa
        lngAntigua = longitud; // para que la primera vez no sea null, despues este valor se pisa
        latActual = Double.parseDouble(latitud);
        lngActual = Double.parseDouble(longitud);

        new InicioRecorrido().execute();

    } // fin onCreate

    public class InicioRecorrido extends AsyncTask<Void, Integer, Boolean> {

        @SuppressLint("SuspiciousIndentation")
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                paradasRecorrido = presenter.consultaParadasRecorrido(linea, recorrido);
            } catch (ExecutionException | InterruptedException | TimeoutException | JSONException e) {
                System.out.println("Error en consulta paradas recorridos: " + e);
                Toaster.get().showToast(getApplicationContext(), "Error en consulta paradas recorridos -linea 106", Toast.LENGTH_SHORT);
//                throw new RuntimeException(e);
            }

//        TODO si es parada final, setea 'enTransito' en false, y termina recorrido
            try {
//                boolean esFinRecorrido = detectarParada(paradasRecorrido, latActual, lngActual, linea, colectivo, recorrido);
//                enTransito = esFinRecorrido;
                detectarParada(paradasRecorrido, latActual, lngActual, linea, colectivo, recorrido);
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                Toaster.get().showToast(getApplicationContext(), "Error al detectar parada - linea 115", Toast.LENGTH_SHORT);
//                throw new RuntimeException(e);
            }

            while (enTransito){ // mientras este en transito sigue, sino sale

                obtenerUbicacion();

                try {
                    Thread.sleep(tiempoEnvioNuevaCoord*1000 );
                } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
                }

                latActual = getLatActual();
                lngActual = getLngActual();
                fechaUbicacionActual = getFechaUbicacion(); // la que recive de la ubicacion actual


                // TODO por si es parada final
                //que envie la consulta de desvio la primera vez
                if (!coleEstaDesviado) {
                    // si la primera vez esta parado y esta en una parada tambien detecta la parada
                    try {
                        System.out.println("detectando desvio..");
                        detectarParada(paradasRecorrido, latActual, lngActual, linea, colectivo, recorrido);
                        presenter.makeRequestPostDetectarDesvio(linea, colectivo, recorrido, latActual, lngActual);
                        // aca estaba thread sleep
                    } catch (ExecutionException | InterruptedException | TimeoutException e) {
                        Toaster.get().showToast(getApplicationContext(), "Error en envio desvio -linea 136", Toast.LENGTH_SHORT);
//                            throw new RuntimeException(e);
                    }
//                    Toaster.get().showToast(getApplicationContext(), "Detectando desvio..", Toast.LENGTH_SHORT);
                    coleEstaDesviado = true;
                }



                distancia = calcularDistancia(Double.parseDouble(latAntigua), Double.parseDouble(lngAntigua), latActual, lngActual);
                Date fuAntiguaD = new Date(fechaUbicacionAntigua);
                Date fuActualD = new Date(fechaUbicacionActual);

                int difSeg = (int) (fuActualD.getTime() - fuAntiguaD.getTime()) / 1000; // en segundos

                // TODO Si es menor a 20.0 metros, esta detenido
                if (distancia < distanciaOffSetMov) {
                    segundosDetenidoStr = segundosDetenidoStr + difSeg; // suma el tiempo total detenido // tambien se puede con cont++ cada 3 intentos envia
                    System.out.println(segundosDetenidoStr + " segundos detenido");

                    if (contVerifParada < 1) {
                        //si esta parado la primera vez detecta la parada
                        try {
                            detectarParada(paradasRecorrido, latActual, lngActual, linea, colectivo, recorrido);
                        } catch (ExecutionException | InterruptedException | TimeoutException e) {
//                                Toast.makeText(ColectivoEnServicioActivity.this, "error en detectar parada. -linea 189", Toast.LENGTH_LONG).show();
                            Toaster.get().showToast(getApplicationContext(), "error en detectar parada. -linea 189", Toast.LENGTH_SHORT);
//                                throw new RuntimeException(e);
                        }
                        //por si es parada final
                        contVerifParada++;
                    }

                    // TODO Si esta detenido por mas de 'x' tiempo, envia el informe
                    if (segundosDetenidoStr > tiempoMaxDetenido) {

                        // solo si el tiempo detenido es mayor a x tiempo, envia aviso y muestra cartel aviso
                        ColectivoEnServicioActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvEstado.setText( "Unidad detenida" );
                                ivGifBus.setVisibility(View.VISIBLE);
                                Glide.with(ColectivoEnServicioActivity.this)
                                        .load(R.drawable.bus_animation_fondo_violeta_alerta)
                                        .into(ivGifBus);
                            }
                        });

                        // si el colectivo todavia sigue parado, actualiza la notificacion
                        if (notificacionActiva) {
                            try {
                                presenter.makePostActualizacionNotifColeDetenido(linea, colectivo, recorrido, String.valueOf(latActual), String.valueOf(lngActual), "" + segundosDetenidoStr);
                            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                                Toaster.get().showToast(getApplicationContext(),  "error makePostActualizacionNotifDetenido - linea 204", Toast.LENGTH_SHORT);
//                                    throw new RuntimeException(e);
                            }
//                            Toaster.get().showToast(getApplicationContext(),  "unidad detenida, actualizando informe..", Toast.LENGTH_SHORT);
                            System.out.println("unidad detenida, actualizando informe..");
                        } else {
                            // sino es la primera vez que el colectivo se para y crea la nueva notificacion y set notificacionActiva en true
                            try {
                                presenter.makePostInformeColeDetenido(linea, colectivo, recorrido, String.valueOf(latActual), String.valueOf(lngActual), "" + segundosDetenidoStr);
                            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                                Toaster.get().showToast(getApplicationContext(),  "error makePostInformeColeDetenido - linea 214", Toast.LENGTH_SHORT);
//                                    throw new RuntimeException(e);
                            }
//                            Toaster.get().showToast(getApplicationContext(),  "unidad detenida, enviando informe..", Toast.LENGTH_SHORT);
                            System.out.println("unidad detenida, enviando informe..");
                            notificacionActiva = true;
                        }
                    }
                    // TODO fin si esta detenido





                }else {
                    // TODO Colectivo en circulacion

                    // si el colectivo estaba parado, y empezo a circular, actualiza la notificacion(con el tiempo final) y cambia la bandera a false
                    if (notificacionActiva == true) {
                        try {
                            presenter.makePostFinNotificacionColeDetenido(linea, colectivo, recorrido, String.valueOf(latActual), String.valueOf(lngActual),"" + segundosDetenidoStr);
                        }catch (ExecutionException | InterruptedException | TimeoutException e) {
//                                Toast.makeText(ColectivoEnServicioActivity.this, "Error makePostFinNotificacionColeDetenido -linea 229", Toast.LENGTH_LONG).show();
                            Toaster.get().showToast(getApplicationContext(),  "Error makePostFinNotificacionColeDetenido -linea 229", Toast.LENGTH_SHORT);
//                                throw new RuntimeException(e);
                        }
                        notificacionActiva = false; // resetea la bandera
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
                        Toaster.get().showToast(getApplicationContext(),  "Error en detectar parada - linea 245", Toast.LENGTH_SHORT);
//                            throw new RuntimeException(e);
                    }
                    if (esFin) {
                        enTransito = false;
//                        finServicioSimple();
//                        finish();
                    }else { // else por si no es fin de servicio, continua circulando
                        try {
                            presenter.makeRequestPostDetectarDesvio(linea, colectivo, recorrido, latActual, lngActual);
                        } catch (ExecutionException | InterruptedException | TimeoutException e) {
                            Toaster.get().showToast(getApplicationContext(),   "error makeRequestPostDetectarDesvio -linea 255", Toast.LENGTH_SHORT);
//                                throw new RuntimeException(e);
                        }
//                        Toaster.get().showToast(getApplicationContext(),   "enviando ubicacion, detectando desvio..", Toast.LENGTH_SHORT);
                        System.out.println("enviando ubicacion, detectando desvio..");
                        try {
                            presenter.makeRequestPostEnviarUbicacion(linea, colectivo, recorrido, String.valueOf(latActual), String.valueOf(lngActual));
                        } catch (ExecutionException | InterruptedException | TimeoutException e) {
                            Toaster.get().showToast(getApplicationContext(),   "error makeRequestPostEnvio - linea 265", Toast.LENGTH_SHORT);
//                                throw new RuntimeException(e);
                        }

                        segundosDetenidoStr = 0; // resetea la suma
                        ColectivoEnServicioActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvEstado.setText( "Unidad en circulacion" );
                                ivGifBus.setVisibility(View.VISIBLE);
                                Glide.with(getApplicationContext())
                                        .load(R.drawable.bus_animation_fondo_violeta_circulando)
                                        .into(ivGifBus);
                            }
                        });
                    } // fin else colectivo circulando
                }
                fechaUbicacionAntigua = fechaUbicacionActual;
                latAntigua = String.valueOf(latActual);
                lngAntigua = String.valueOf(lngActual);

            } // fin while en transito



//            // hacer aca lo del boton cuando sale. el boton solo deberia poner la variable enTransito en false.
//            coleEstaDesviado = false;
//            if (notificacionActiva) {
//                try {
//                    presenter.makePostFinNotificacionColeDetenido(linea, colectivo, recorrido, String.valueOf(latActual), String.valueOf(lngActual), "" + segundosDetenidoStr);
//                } catch (ExecutionException | InterruptedException | TimeoutException e) {
////                            Toast.makeText(ColectivoEnServicioActivity.this, "error makePostFinNotificacionColeDetenido - linea 284", Toast.LENGTH_LONG).show();
//                    Toaster.get().showToast(getApplicationContext(),   "error makePostFinNotificacionColeDetenido - linea 284", Toast.LENGTH_SHORT);
////                            throw new RuntimeException(e);
//                }
//            }
//            notificacionActiva = false; // resetea la bandera
//            //por si hay una notificacion de desvio activa // nose como puedo detenerla sino
//            try {
//                presenter.makeRequestPostFinDesvio(linea, colectivo, recorrido);
//            } catch (ExecutionException | InterruptedException | TimeoutException e) {
////                        Toast.makeText(ColectivoEnServicioActivity.this, "error makeRequestPostFinDesvio - linea 293", Toast.LENGTH_LONG).show();
//                Toaster.get().showToast(getApplicationContext(),   "error makeRequestPostFinDesvio - linea 293", Toast.LENGTH_SHORT);
////                        throw new RuntimeException(e);
//            }
//            try {
//                presenter.makeRequestPostFinColectivoRecorrido(linea, colectivo, recorrido);
//            } catch (ExecutionException | InterruptedException | TimeoutException e) {
////                        Toast.makeText(ColectivoEnServicioActivity.this, "error makeRequestPostFinColectivoRecorrido - linea 299", Toast.LENGTH_LONG).show();
//                Toaster.get().showToast(getApplicationContext(),    "error makeRequestPostFinColectivoRecorrido - linea 299", Toast.LENGTH_SHORT);
////                        throw new RuntimeException(e);
//            }

            return true;
        } // fin doInBackGround

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            System.out.println("recorrido terminado");
//            Toast.makeText(ColectivoEnServicioActivity.this, "recorrido terminado", Toast.LENGTH_LONG).show();
//            Toaster.get().showToast(getApplicationContext(),   "recorrido terminado", Toast.LENGTH_SHORT);
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

//                Toaster.get().showToast(getApplicationContext(),   "Ubicacion obtenida", Toast.LENGTH_SHORT);
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



        this.runOnUiThread(new Runnable() {
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        });

    }



    public boolean detectarParada(List<Coordenada> listitaParadas, Double latActual, Double lngActual, String denom, String unidad, String denomRecorrido) throws ExecutionException, InterruptedException, TimeoutException {
        boolean esFin = false;
        for(Coordenada parada: listitaParadas){
            double distancia = calcularDistancia(latActual,lngActual,parada.getLatitud(),parada.getLongitud());
            if (distancia < distanciaOffSetParada) {
//                Toaster.get().showToast(getApplicationContext(),   "Colectivo en parada", Toast.LENGTH_SHORT);
                ColectivoEnServicioActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvEstado.setText( "Unidad en parada" );
                        tvNombreParada.setText(parada.getDireccion());
                        ivGifBus.setVisibility(View.VISIBLE);
                        Glide.with(ColectivoEnServicioActivity.this)
                                .load(R.drawable.bus_animation_fondo_violeta_parada)
                                .into(ivGifBus);
                    }
                });



                if(getParadaFinal().getCodigo() == parada.getCodigo()){
                    esFin = true;
//                        Toast.makeText( this, "Colectivo en parada final", Toast.LENGTH_LONG ).show();
                    Toaster.get().showToast(getApplicationContext(),   "Colectivo en parada final", Toast.LENGTH_SHORT);
                    presenter.makeRequestPostColeEnParada( parada.getCodigo(), denom, unidad, denomRecorrido); // a rest lineaColectivo

                    try {
                        Thread.sleep(3000 ); // para que muestre gif ultima parada
                    } catch (InterruptedException e) {}

//                    finServicioSimple();
                    enTransito = false;
//                    finish();
                }else {
                    // es porque esta en o cerca de una parada de esa linea que esta en servicio
//                        Toast.makeText( this, "Colectivo en parada", Toast.LENGTH_LONG ).show();
//                    Toaster.get().showToast(getApplicationContext(),   "Colectivo en parada", Toast.LENGTH_SHORT);
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

    public Double getLatActual() { return latActual; }

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
        Toast.makeText( getApplicationContext() ,response, Toast.LENGTH_LONG ).show();
    }


    /**
     * Detiene el servicio mediante el boton "Fin Servicio"
     *
     * @param  view    boton fin servicio
     */
    public void finServicio(View view) {
        enTransito = false;
    }

    public enum Toaster {
        INSTANCE;
        private final Handler handler = new Handler(Looper.getMainLooper());
        public void showToast(final Context context, final String message, final int length) {
            handler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, message, length).show();
                        }
                    }
            );
        }
        public static Toaster get() {
            return INSTANCE;
        }
    }


//    public boolean detectarParadaFinal(List<Coordenada> listitaParadas, Double latActual, Double lngActual, String denom, String unidad, String denomRecorrido) throws ExecutionException, InterruptedException, TimeoutException {
//        boolean esFin = false;
//        for(Coordenada parada: listitaParadas){
//            double distancia = calcularDistancia(latActual,lngActual,parada.getLatitud(),parada.getLongitud());
//            if (distancia < distanciaOffSetParada) {
//                if(getParadaFinal().getCodigo() == parada.getCodigo()){
//                    esFin = true;
//                    Toaster.get().showToast(getApplicationContext(),   "Colectivo en parada final", Toast.LENGTH_SHORT);
//                    presenter.makeRequestPostColeEnParada( parada.getCodigo(), denom, unidad, denomRecorrido); // a rest lineaColectivo
//                    enTransito = false;
////                    finServicioSimple();
////                    finish();
//                }
//            }
//        }
//        return esFin;
//    }

    protected void onDestroy() {
        FinServicio finServicio1 = new FinServicio();
        finServicio1.execute();
        super.onDestroy();
    }


    public class FinServicio extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {

            System.out.println(" +++++++++++++++++++++++++++ entra en fin servicio thread ++++++++++++++++++++++++");
            //resetea el contDesvio
            coleEstaDesviado = false;
            enTransito = false;
            try {

                System.out.println("datos para finalizar el servicio: " + linea + " - " + colectivo + " - " + recorrido);

                //por si hay una notificacion de desvio activa // nose como puedo detenerla sino
                presenter.makeRequestPostFinDesvio(linea, colectivo,recorrido);

                if (notificacionActiva) {
                    presenter.makePostFinNotificacionColeDetenido(linea, colectivo, recorrido, String.valueOf(latActual), String.valueOf(lngActual), "" + segundosDetenidoStr);
                }
                notificacionActiva = false; // resetea la bandera

                presenter.makeRequestPostFinColectivoRecorrido(linea, colectivo, recorrido);

            }catch (ExecutionException | InterruptedException | TimeoutException e){
                System.out.println("error al finalizar servicio" + e);
                Toaster.get().showToast(getApplicationContext(),   "error al finalzar servicio - linea 520", Toast.LENGTH_SHORT);
            }
            System.out.println("servicio finalizado");
            Toaster.get().showToast(getApplicationContext(),   "servicio finalizado", Toast.LENGTH_SHORT);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            finish();
            super.onPostExecute(aBoolean);
        }
    } // fin Thread finServicio

} // fin clase