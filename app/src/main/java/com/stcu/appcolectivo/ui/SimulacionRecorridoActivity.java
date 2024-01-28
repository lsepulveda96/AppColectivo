package com.stcu.appcolectivo.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

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


public class SimulacionRecorridoActivity extends Activity implements TrayectoARecorrerInterface.View{

    Boolean enTransito = true, notificacionActiva = false;
    public static double distanciaOffSetMov = 15.0;
    private static double distanciaOffSetParada = 60.0; // en mts
    public static int tiempoMaxDetenido = 30;
    public static int tiempoEnvioNuevaCoord = 5; // en segundos
//public static int tiempoEnvioNuevaCoord = 4; // en segundos para probar sim



    private TextView tvLinea ,tvColectivo, tvNombreParada, tvLongitud, tvUbicacion;
    TextView tvEstado;
    private String linea, colectivo, recorrido, latitud, longitud, fechaUbicacionInicialS, myLat, myLng, latAntigua, lngAntigua;
    List<Coordenada> coordsTrayectoASimular;
    private Long fechaUbicacionAntigua, fechaUbicacionInicial, fechaUbicacionActual;
    private Button finServicio;
    Double latActual, lngActual, distancia = 0.0;
    int segundosDetenidoStr = 0;
    private int contDesvioAlIniciar = 0, contVerifParada = 0;

    List<Coordenada> paradasRecorrido, coordenadasSim;
    // la empiezo en 0 para que se sume siempre (tambien la primera vez)
    private int contCoorSim = 0;

    private TrayectoARecorrerInterface.Presenter presenter;

    ImageView ivGifBus;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulacion);

        presenter = new TrayectoARecorrerPresenter(this, this, this);
        tvLinea = (TextView) findViewById(R.id.tvLinea);
        tvColectivo = (TextView) findViewById(R.id.tvColectivo);
        tvNombreParada = (TextView) findViewById(R.id.tvNombreParada);
        tvEstado = (TextView) findViewById(R.id.tvEstado);
        finServicio = (Button) findViewById(R.id.btnFinServicio);
        tvUbicacion = (TextView) findViewById(R.id.tvUbicacion);

        linea = getIntent().getExtras().getString("linea");
        colectivo = getIntent().getExtras().getString("colectivo");
        recorrido = getIntent().getExtras().getString("recorrido");
        latitud = getIntent().getExtras().getString("latitud");
        longitud = getIntent().getExtras().getString("longitud");
        fechaUbicacionInicialS = getIntent().getExtras().getString("fechaUbicacion");
        coordsTrayectoASimular = getIntent().getParcelableArrayListExtra("coordenadasSim");
        fechaUbicacionInicial = Long.valueOf(fechaUbicacionInicialS);
        fechaUbicacionAntigua = fechaUbicacionInicial;

        tvLinea.setText(linea);
        tvColectivo.setText(colectivo);

        setLat(latitud); // para que la primera vez no sea null, despues este valor se pisa
        setLng(longitud);

        latAntigua = latitud; // para que la primera vez no sea null, despues este valor se pisa
        lngAntigua = longitud;

        latActual = Double.parseDouble(latitud);
        lngActual = Double.parseDouble(longitud);

        ivGifBus = findViewById(R.id.gifbus);
        ivGifBus.setVisibility(View.GONE);

        new InicioRecorridoSim().execute();


//        final Handler handler = new Handler();
//        final Runnable r = new Runnable() {
//            public void run() {
//
//                // para indicar que esta en la parada inicial
//                setParadaInicio(paradasRecorrido.get(0),linea,colectivo,recorrido);
//                System.out.println("la primera parada a recorrer: " + paradasRecorrido.get(0).getDireccion() +", codigo:" + paradasRecorrido.get(0).getCodigo());
//                InicioRecorrido inicioRecorrido = new InicioRecorrido();
//                inicioRecorrido.execute();
//            }
//        };
//        handler.postDelayed(r,4000);

    } // fin onCreate




    // cambiar lo del return true
    public class InicioRecorridoSim extends AsyncTask<Void,Integer,Boolean> {


        @Override
        protected Boolean doInBackground(Void... voids) {
            // para indicar que esta en la parada inicial
            try {
                paradasRecorrido = presenter.consultaParadasRecorrido(linea,recorrido);
//                coordenadasSim = presenter.consultaTrayectoASimular(recorrido);
                System.out.println(" Coordenadas trayecto a simular ++++++++++++++++++++++++++++++++++++: " );
                for (Coordenada coord: coordsTrayectoASimular) {System.out.println("lat : " + coord.getLatitud() + ".  lng : "  + coord.getLongitud());}
                coordenadasSim = coordsTrayectoASimular;
//                coordenadasSim = paradasRecorrido;
                // todo aca traer recorrido a simular

//                System.out.println("el resultado de la consulta paradas recorrido");
//                for (Coordenada coord: paradasRecorrido) {System.out.println("Parada recorrido- id: " + coord.getCodigo() + ".  direccion: "  + coord.getDireccion());}
            }  catch (ExecutionException | InterruptedException | TimeoutException | JSONException e) {
                System.out.println("Error en consulta paradas recorridos: " + e);
                Toaster.get().showToast(getApplicationContext(), "Error en consulta paradas recorridos - 140" , Toast.LENGTH_SHORT);
//                throw new RuntimeException(e);
            }

            try{
                setParadaInicio(paradasRecorrido.get(0),linea,colectivo,recorrido);
            }  catch (ExecutionException | InterruptedException | TimeoutException e) {
                System.out.println("Error en set parada inicio: " + e);
                Toaster.get().showToast(getApplicationContext(), "Error en set parada inicio - 148" , Toast.LENGTH_SHORT);
//                throw new RuntimeException(e);
            }
            System.out.println("la primera parada a recorrer: " + paradasRecorrido.get(0).getDireccion() +", codigo:" + paradasRecorrido.get(0).getCodigo());


            // TODO ojo con este for cuando cambie el recorrido a simular, deberia ser for coordenadasRecorridoASimular.size()
//            for (int i=0;i<paradasRecorrido.size();i++) {
            for (int i=0;i<coordsTrayectoASimular.size();i++) {


            if(enTransito){

                obtenerUbicacion();

                // porque ya es la segunda vez que entra
                // esto creo que es redundante
                if(contCoorSim < coordenadasSim.size()) {
                    contCoorSim++;
                }else{
                    try {
                        finServicioSimple(); // nose si esta bien esto
                        finish();
                    } catch (ExecutionException | InterruptedException | TimeoutException e) {
                        System.out.println("error al finalizar servicio");
                        Toaster.get().showToast(getApplicationContext(), "error al finalizar servicio - 173" , Toast.LENGTH_SHORT);
                        //                        throw new RuntimeException(e);
                    }
                }

                try {
                    Thread.sleep(tiempoEnvioNuevaCoord*1000 );
                } catch (InterruptedException e) {
                    System.out.println("error durmiendo el hilo");
//                            throw new RuntimeException(e);
                }
//                ejecutar(); // ejecuta el hilo que duerme x segundos

                latActual = getLatActual();
                lngActual = getLngActual();
                fechaUbicacionActual = getFechaUbicacion(); // la que recive de la ubicacion actual

                //que envie la consulta de desvio la primera vez
                //consulta si esta desviado al iniciar el recorrido. por si no esta en parada de inicio
                if(contDesvioAlIniciar < 1) {
                    // si la primera vez esta parado y esta en una parada tambien detecta la parada
                    boolean esFin = false;
                    try {
                        esFin = detectarParada(paradasRecorrido,latActual,lngActual,linea,colectivo,recorrido);
                        System.out.println("es parada final?: " + esFin);
                    } catch (ExecutionException | InterruptedException | TimeoutException e) {
                        Toaster.get().showToast(getApplicationContext(), "error al detectar parada - 199" , Toast.LENGTH_SHORT);
//                        throw new RuntimeException(e);
                    }

                    if(esFin){
                        try {
                            System.out.println("Entra a fin servicio simple");
                            finServicioSimple();
                            finish();
                        } catch (ExecutionException | InterruptedException | TimeoutException e) {
                            Toaster.get().showToast(getApplicationContext(), "error al finalizar servicio - 209" , Toast.LENGTH_SHORT);
//                            throw new RuntimeException(e);
                        }
                    }


                    try {
                        presenter.makeRequestPostDetectarDesvio(linea, colectivo, recorrido, latActual, lngActual);
                        System.out.println("Detectando desvio..");
                        contDesvioAlIniciar++;
                    } catch (ExecutionException | InterruptedException | TimeoutException e) {
                        Toaster.get().showToast(getApplicationContext(), "error posta detectar desvio - 220" , Toast.LENGTH_SHORT);
//                        throw new RuntimeException(e);
                    }

//                    Toast.makeText(SimulacionRecorridoActivity.this, "Detectando desvio..", Toast.LENGTH_SHORT).show();

                } // fin if cont desvio al inciar

                // TODO inicio colectivo detenido

                distancia = calcularDistancia(Double.parseDouble(latAntigua), Double.parseDouble(lngAntigua), latActual, lngActual);


                Date fuAntiguaD = new Date(fechaUbicacionAntigua);
                Date fuActualD = new Date(fechaUbicacionActual);

                int difSeg = (int)(fuActualD.getTime()-fuAntiguaD.getTime())/1000; // en segundos

                // si esta detenido
                if(distancia < distanciaOffSetMov ){ // si es menor a 20.0 metros, esta detenido // que luego pueda ser configurable // 20.0
//                    System.out.println("entra en colectivo detenido");
                    segundosDetenidoStr = segundosDetenidoStr + difSeg; // suma el tiempo total detenido // tambien se puede con cont++ cada 3 intentos envia
//                    Toast.makeText(SimulacionRecorridoActivity.this, segundosDetenidoStr +" segundos detenido", Toast.LENGTH_SHORT).show();
                    tvEstado.setText("Unidad detenida");

                    if(contVerifParada < 1){
                        //si esta parado la primera vez detecta la parada
                        boolean esFin;
                        try {
                            esFin = detectarParada(paradasRecorrido,latActual,lngActual,linea,colectivo,recorrido);
                            System.out.println("es parada final?: " + esFin);
                            if(esFin) {
                                finServicioSimple();
                                finish();
                            }
                        } catch (ExecutionException | InterruptedException | TimeoutException e) {
//                            throw new RuntimeException(e);
                            Toaster.get().showToast(getApplicationContext(), "error detectar parada- 257" , Toast.LENGTH_SHORT);
                        }

                        //por si es parada final
                        contVerifParada++;
                    }

                    //si esta detenido por mas de 'x' tiempo
                    if(segundosDetenidoStr > tiempoMaxDetenido){ // si el tiempo que esta detenido es mayor a 17 seg envia el informe (configurable) // 17

                        // solo si el tiempo detenido es mayor a x tiempo, envia aviso y muestra cartel aviso
                        SimulacionRecorridoActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvEstado.setText( "Unidad detenida" );
                                ivGifBus.setVisibility(View.VISIBLE);
                                Glide.with(SimulacionRecorridoActivity.this)
                                        .load(R.drawable.bus_animation_fondo_violeta_alerta)
                                        .into(ivGifBus);
                            }
                        });

                        // si el colectivo todavia sigue parado, actualiza la notificacion
                        if(notificacionActiva){
                            try {
                                // deberia cambiar este por el de abajo
//                                presenter.makePostInformeColeDetenido(linea, colectivo, recorrido, String.valueOf(latActual), String.valueOf(lngActual), "" + segundosDetenidoStr);
                                presenter.makePostActualizacionNotifColeDetenido(linea, colectivo, recorrido, String.valueOf(latActual), String.valueOf(lngActual), "" + segundosDetenidoStr);
                            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                                Toaster.get().showToast(getApplicationContext(), "error makePostActualizacionNotifColeDetenido - 258" , Toast.LENGTH_SHORT);
                                Toaster.get().showToast(getApplicationContext(), "error " + e.getMessage(), Toast.LENGTH_SHORT);
                            }
//                            Toast.makeText(SimulacionRecorridoActivity.this, "unidad detenida, actualizando informe..", Toast.LENGTH_SHORT).show();
                            System.out.println( "unidad detenida, actualizando informe..");
                        }else{
                            // sino es la primera vez que el colectivo se para y crea la nueva notificacion y setea bander en true
                            try {
                                presenter.makePostInformeColeDetenido(linea, colectivo, recorrido, String.valueOf(latActual), String.valueOf(lngActual), "" + segundosDetenidoStr);
                            }catch (ExecutionException | InterruptedException | TimeoutException e) {
                                Toaster.get().showToast(getApplicationContext(), "error makePostInformeColeDetenido - 293" , Toast.LENGTH_SHORT);
                                Toaster.get().showToast(getApplicationContext(), "error " + e.getMessage(), Toast.LENGTH_SHORT);
//                                throw new RuntimeException(e);
                            }
                            //Toast.makeText(SimulacionRecorridoActivity.this, "unidad detenida, enviando informe..", Toast.LENGTH_SHORT).show();
                            System.out.println(  "unidad detenida, enviando informe..");
                            notificacionActiva = true;
                        }
                    }

                }else{ //fin if si el colectivo estaba parado

                    // la distancia entre paradas fue MAYOR de 20 mts, el colectivo ESTA circulando

                    // si el colectivo estaba parado, y empezo a circular, actualiza la notificacion(con el tiempo final) y cambia la bander a false
                    if(notificacionActiva == true) {
                      /*  try {
                          // todo  presenter.makePostFinNotificacionColeDetenido(linea, colectivo, recorrido, String.valueOf(latActual), String.valueOf(lngActual), "" + segundosDetenidoStr);
                        } catch (ExecutionException | InterruptedException | TimeoutException e) {
//                            throw new RuntimeException(e);
                            Toaster.get().showToast(getApplicationContext(), "error makePostFinNotificacionColeDetenido - 312" , Toast.LENGTH_SHORT);
                        } */
                        notificacionActiva = false; // resetea la bander
                    }

                    // contador en 0 para que cuando se vuelve a parar la primera vez verifique si es parada
                    contVerifParada = 0;
                    try {
                        // vuelve a verificar si esta desviado
                        boolean esFin;
                        esFin = detectarParada(paradasRecorrido,latActual,lngActual,linea,colectivo,recorrido);
                        System.out.println("es parada final?: " + esFin);
                        if(esFin) {
//                            System.out.println("llama a fin de servicio simple");
                            finServicioSimple();
                            finish();
                        }else{
                            System.out.println("no es parada final");
                            presenter.makeRequestPostDetectarDesvio(linea, colectivo, recorrido, latActual, lngActual);
//                        Toast.makeText( SimulacionRecorridoActivity.this, "Enviando ubicacion..", Toast.LENGTH_SHORT ).show();
                            System.out.println("enviando ubicacion..");
                            presenter.makeRequestPostEnviarUbicacion(linea, colectivo, recorrido, getLat(), getLng());
                            segundosDetenidoStr = 0; // resetea la suma
                            System.out.println("unidad en circulacion");
                            SimulacionRecorridoActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvEstado.setText( "Unidad en circulacion" );


                                    ivGifBus.setVisibility(View.VISIBLE);
                                    //inserte gif cole en parada. posible error x estar fuera del hilo principal
                                    Glide.with(SimulacionRecorridoActivity.this)
                                            .load(R.drawable.bus_animation_fondo_violeta_circulando)
                                            .into(ivGifBus);


                                }
                            });


                        }

                    } catch (ExecutionException | InterruptedException | TimeoutException e) {
                        Toaster.get().showToast(getApplicationContext(), "error verificacion desvio, envio nueva ubicacion - 356" , Toast.LENGTH_SHORT);
//                        throw new RuntimeException(e);
                    }



                } // fin else colectivo circulando

                fechaUbicacionAntigua = fechaUbicacionActual;
                latAntigua = String.valueOf(latActual);
                lngAntigua = String.valueOf(lngActual);
//              TODO fin colectivo detenido

            }else{ //fin if en transito
                break; // para salir del for
            }

            } // fin for

            return true;
        } // fin doInBackGround


        @Override
        protected void onPostExecute(Boolean aBoolean){
            System.out.println("recorrido sim terminado");
            finish();
        }

    } // fin thread inicio recorrido




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
    public void obtenerUbicacion(){
        setLat(String.valueOf(coordenadasSim.get(contCoorSim).getLatitud()));
        setLng(String.valueOf(coordenadasSim.get(contCoorSim).getLongitud()));

        System.out.println("la nueva coordenada obtenida: " +coordenadasSim.get(contCoorSim).getLatitud() + " - " + coordenadasSim.get(contCoorSim).getLongitud());
        setLatActual(coordenadasSim.get(contCoorSim).getLatitud()); // se pueden sacar por las dos de arriba
        setLngActual(coordenadasSim.get(contCoorSim).getLongitud());

        fechaUbicacionActual = System.currentTimeMillis();
        setFechaUbicacion(fechaUbicacionActual);
//        runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//
//                tvUbicacion.setText(coordenadasSim.get(contCoorSim).getDireccion() + " cod: " + coordenadasSim.get(contCoorSim).getCodigo());
//
//            }
//        });
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

    public boolean detectarParada(List<Coordenada> listaParadasRecorrido, Double latActual, Double lngActual, String denomLinea, String unidad, String denomRecorrido ) throws ExecutionException, InterruptedException, TimeoutException {
        boolean esFin = false;
        for(Coordenada parada: listaParadasRecorrido){
//            System.out.println("Parada actual: " + parada.getCodigo() + " - " + parada.getDireccion());
//            System.out.println("Parada final: " + getParadaFinal().getCodigo());
            double distancia = calcularDistancia(latActual,lngActual,parada.getLatitud(),parada.getLongitud());
            if (distancia < distanciaOffSetParada) {
                SimulacionRecorridoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvEstado.setText( "Unidad en parada" );
                        tvNombreParada.setText(parada.getDireccion());
                        ivGifBus.setVisibility(View.VISIBLE);
                        //inserte gif cole en parada. posible error x estar fuera del hilo principal
                        Glide.with(SimulacionRecorridoActivity.this)
                                .load(R.drawable.bus_animation_fondo_violeta_parada)
                                .into(ivGifBus);
                    }
                });

                if(getParadaFinal().getCodigo() == parada.getCodigo()){
//                    System.out.println("getParadaFinal es igual a parada getCodigo actual");
                    esFin = true;
//                    Toast.makeText( SimulacionRecorridoActivity.this, "Colectivo en parada final", Toast.LENGTH_SHORT ).show();
                    System.out.println("++++ colectivo en parada final");
                    presenter.makeRequestPostColeEnParada( parada.getCodigo(), denomLinea, unidad,denomRecorrido);
                    try {
                        Thread.sleep(5000 ); // para que muestre gif ultima parada
                        Toaster.get().showToast(getApplicationContext(), "Servicio finalizado" , Toast.LENGTH_SHORT);
                    } catch (InterruptedException e) {}
                    enTransito = false;

                }else{
//                    System.out.println("getParada actual no era final");
                    // es porque esta en o cerca de una parada de esa linea que esta en servicio
//                    Toast.makeText( SimulacionRecorridoActivity.this, "Colectivo en parada", Toast.LENGTH_SHORT ).show();
                    System.out.println("++++ Colectivo en parada");
                    presenter.makeRequestPostColeEnParada( parada.getCodigo(), denomLinea, unidad, denomRecorrido);
                }
            }
        }
        return esFin;
    }


    public void setParadaInicio(Coordenada coordenada, String linea, String colectivo, String denomRecorrido) throws ExecutionException, InterruptedException, TimeoutException {
//        Toast.makeText( SimulacionRecorridoActivity.this, "Colectivo en parada inicio", Toast.LENGTH_SHORT ).show();
        System.out.println("Colectivo en parada inicio");
        presenter.makeRequestPostColeEnParada(coordenada.getCodigo(), linea, colectivo, denomRecorrido);
    }

    // Si hay que cambiar la parada final, se hace aca
    public Coordenada getParadaFinal() {
        Coordenada paradaFinal = paradasRecorrido.get(paradasRecorrido.size()-1);
        return paradaFinal;
    }

    public void finServicio(View view) throws ExecutionException, InterruptedException, TimeoutException {
        enTransito = false;
//        finish();
//        FinServicio finServicio1 = new FinServicio();
//        finServicio1.execute();

        //resetea el contDesvio
      /*  contDesvioAlIniciar = 0;

        //nueva, resetea cont coordenada a simular
        contCoorSim = 0;

        //por si hay una notificacion de desvio activa // nose como puedo detenerla sino
        presenter.makeRequestPostFinDesvio(linea, colectivo, recorrido);
        presenter.makeRequestPostFinColectivoRecorrido(linea,colectivo,recorrido);

        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                enTransito = false;
                if(notificacionActiva) {
                    try {
                        presenter.makePostFinNotificacionColeDetenido(linea, colectivo, recorrido, String.valueOf(latActual), String.valueOf(lngActual), "" + segundosDetenidoStr);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (TimeoutException e) {
                        throw new RuntimeException(e);
                    }
                }
                notificacionActiva = false; // resetea la bander
                Toast.makeText( getApplicationContext(), "Servicio finalizado", Toast.LENGTH_SHORT ).show();
                finish();
            }
        };
        handler.postDelayed( r, 3000 );*/

    }

    public void finServicioSimple() throws ExecutionException, InterruptedException, TimeoutException {
          enTransito = false;
//        finish();
//        FinServicio finServicio1 = new FinServicio();
//        finServicio1.execute();

//        //resetea el contador de desvio
//        contDesvioAlIniciar = 0;
//        //nuevo: resetea el cont de coord a simular
//        contCoorSim = 0;
//        //por si hay una notificacion de desvio activa // nose como puedo detenerla sino
//        // TODO Arreglar
//        presenter.makeRequestPostFinDesvio(linea, colectivo, recorrido);
//        presenter.makeRequestPostFinColectivoRecorrido(linea,colectivo,recorrido);
//
//        enTransito = false;
//        if(notificacionActiva) {
//            //refactorizarlo
//            presenter.makePostFinNotificacionColeDetenido(linea, colectivo, recorrido, String.valueOf(latActual), String.valueOf(fechaUbicacionActual), "" + segundosDetenidoStr);
//        }
//        notificacionActiva = false; // resetea la bander
////        Toast.makeText( getApplicationContext(), "Servicio finalizado", Toast.LENGTH_SHORT ).show();
//        System.out.println("servicio finalizado+++++++++++++++");

    }



    @Override
    public void showResponse(String response) {
//        Toast.makeText( getApplicationContext() ,response, Toast.LENGTH_SHORT ).show();
        System.out.println("show response en simulacion recorrido activity: " + response);
    }


        @Override
    protected void onDestroy() {
//        try {
            FinServicio finServicio1 = new FinServicio();
            finServicio1.execute();

//            finServicioSimple();
//        } catch (ExecutionException | InterruptedException | TimeoutException e) {
////            System.out.println("error: " + e);
//            throw new RuntimeException(e);
//        }
//        finish();
        super.onDestroy();
    }

    public class FinServicio extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            //resetea el contador de desvio
            contDesvioAlIniciar = 0;
            //nuevo: resetea el cont de coord a simular
            contCoorSim = 0;
            //por si hay una notificacion de desvio activa // nose como puedo detenerla sino
            // TODO Arreglar
            try {
               // todo  presenter.makeRequestPostFinDesvio(linea, colectivo, recorrido);

            presenter.makeRequestPostFinColectivoRecorrido(linea,colectivo,recorrido);

            enTransito = false;
            if(notificacionActiva) {
                //refactorizarlo
               // todo presenter.makePostFinNotificacionColeDetenido(linea, colectivo, recorrido, String.valueOf(latActual), String.valueOf(fechaUbicacionActual), "" + segundosDetenidoStr);
            }
                notificacionActiva = false; // resetea la bander
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                Toaster.get().showToast(getApplicationContext(), "error makeRequestPostFinColectivoRecorrido - 631" , Toast.LENGTH_SHORT);
            }

//        Toaster.makeText( getApplicationContext(), "Servicio finalizado", Toast.LENGTH_SHORT ).show(); // daba error
            System.out.println("servicio finalizado+++++++++++++++");
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            finish();
            super.onPostExecute(aBoolean);
        }
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
        public static SimulacionRecorridoActivity.Toaster get() {
            return INSTANCE;
        }
    }
}



/*
* backup
*     public void finServicioSimple() throws ExecutionException, InterruptedException, TimeoutException {

        //resetea el contador de desvio
        contDesvioAlIniciar = 0;
        //nuevo: resetea el cont de coord a simular
        contCoorSim = 0;
        //por si hay una notificacion de desvio activa // nose como puedo detenerla sino
        // TODO Arreglar
        presenter.makeRequestPostFinDesvio(linea, colectivo, recorrido);
        presenter.makeRequestPostFinColectivoRecorrido(linea,colectivo,recorrido);

        enTransito = false;
        if(notificacionActiva) {
            //refactorizarlo
            presenter.makePostFinNotificacionColeDetenido(linea, colectivo, recorrido, String.valueOf(latActual), String.valueOf(fechaUbicacionActual), "" + segundosDetenidoStr);
        }
        notificacionActiva = false; // resetea la bander
//        Toast.makeText( getApplicationContext(), "Servicio finalizado", Toast.LENGTH_SHORT ).show();
        System.out.println("servicio finalizado+++++++++++++++");

    }
    *
    * */