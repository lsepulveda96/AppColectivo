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
import com.stcu.appcolectivo.model.Parada;
import com.stcu.appcolectivo.presenter.TrayectoARecorrerPresenter;

import org.json.JSONException;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


public class SimulacionRecorridoActivity extends Activity implements TrayectoARecorrerInterface.View{

    Boolean enTransito = true;
    //public static double distanciaOffSetMov = 15.0;
    // private static double distanciaOffSetParada = 20.0; // en mts
    //public static int tiempoEnvioNuevaCoord = 9; // en segundos

    public static double distanciaOffSetMovLarga = 25.0;
    public static double distanciaOffSetMov = 10.0; // si la distancia mov es menor a este num, envia coordenada cada menos tiempo
    private static double distanciaOffSetParada = 25.0; // en mts // no tocar mas. 30. hay paradas que no estan justo en la calle y el trayecto si lo esta.
    public static int tiempoMaxDetenido = 30; // no lo estoy usando aca. saque lo de que este detenido en simulacion
    public static int tiempoEnvioNuevaCoord = 9; // en segundos // recomendable 9
    boolean coleEnParada = false;



    private TextView tvLinea ,tvColectivo, tvNombreParada, tvLongitud, tvUbicacion;
    TextView tvEstado;
    private String linea, colectivo, recorrido, latitud, longitud, fechaUbicacionInicialS, myLat, myLng, latAntigua, lngAntigua;
    List<Parada> coordsTrayectoASimular;
    private Long fechaUbicacionAntigua, fechaUbicacionInicial, fechaUbicacionActual;
    private Button finServicio;
    Double latActual, lngActual, distancia = 0.0;
    int segundosDetenidoStr = 0;
    private int contDesvioAlIniciar = 0, contVerifParada = 0;

    List<Parada> paradasRecorrido, coordenadasSim;
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

    } // fin onCreate


    // cambiar lo del return true
    public class InicioRecorridoSim extends AsyncTask<Void,Integer,Boolean> {


        @Override
        protected Boolean doInBackground(Void... voids) {
            // para indicar que esta en la parada inicial
            try {
                paradasRecorrido = presenter.consultaParadasRecorrido(linea,recorrido);
                System.out.println(" Coordenadas trayecto a simular ++++++++++++++++++++++++++++++++++++: " );
                for (Parada coord: coordsTrayectoASimular) {System.out.println("lat : " + coord.getLatitud() + ".  lng : "  + coord.getLongitud());}
                coordenadasSim = coordsTrayectoASimular;

            }  catch (ExecutionException | InterruptedException | TimeoutException | JSONException e) {
                System.out.println("Error en consulta paradas recorridos: " + e);
                Toaster.get().showToast(getApplicationContext(), "Error en consulta paradas recorridos - 140" , Toast.LENGTH_SHORT);
                Toaster.get().showToast(getApplicationContext(), "error " + e.getMessage(), Toast.LENGTH_SHORT);
            }

            try{
                // todo avisa al servicor que esta en parada inicio
                setParadaInicio(paradasRecorrido.get(0), linea,colectivo,recorrido);

            }  catch (ExecutionException | InterruptedException | TimeoutException e) {
                System.out.println("Error en set parada inicio: " + e);
                Toaster.get().showToast(getApplicationContext(), "Error en set parada inicio - 148" , Toast.LENGTH_SHORT);
                Toaster.get().showToast(getApplicationContext(), "error " + e.getMessage(), Toast.LENGTH_SHORT);
            }

            for (int i=0;i<coordsTrayectoASimular.size();i++) {

                if(enTransito){
                    coleEnParada = false;
                    obtenerUbicacion();
                    // porque ya es la segunda vez que entra esto creo que es redundante
                    if(contCoorSim < coordenadasSim.size()) {
                        contCoorSim++;
                    }else{
                        try {
                            finServicioSimple(); // nose si esta bien esto
                            finish();
                        } catch (ExecutionException | InterruptedException | TimeoutException e) {
                            System.out.println("error al finalizar servicio");
                            Toaster.get().showToast(getApplicationContext(), "error al finalizar servicio - 173" , Toast.LENGTH_SHORT);
                            Toaster.get().showToast(getApplicationContext(), "error " + e.getMessage(), Toast.LENGTH_SHORT);
                        }
                    }

            /*        try {
                        Thread.sleep(tiempoEnvioNuevaCoord*1000 );
                    } catch (InterruptedException e) {
                        System.out.println("error durmiendo el hilo");
                    }*/

                    latActual = getLatActual();
                    lngActual = getLngActual();
                    fechaUbicacionActual = getFechaUbicacion(); // la que recibe de la ubicacion actual

                    // TODO inicio colectivo detenido
                    distancia = calcularDistancia(Double.parseDouble(latAntigua), Double.parseDouble(lngAntigua), latActual, lngActual);

                    Date fuAntiguaD = new Date(fechaUbicacionAntigua);
                    Date fuActualD = new Date(fechaUbicacionActual);

                    int difSeg = (int)(fuActualD.getTime()-fuAntiguaD.getTime())/1000; // en segundos

                    // si esta detenido
                    // si la distancia entre coordenadas es menor a distanciaOffSetMov no envia la coordenada
                    if(distancia < distanciaOffSetMov ){ // si es menor a 20.0 metros, esta detenido // que luego pueda ser configurable // 20.0
                        segundosDetenidoStr = segundosDetenidoStr + difSeg; // suma el tiempo total detenido // tambien se puede con cont++ cada 3 intentos envia
//                        tvEstado.setText("Unidad detenida");
                        //si esta detenido por mas de 'x' tiempo
                   /*     if(segundosDetenidoStr > tiempoMaxDetenido){ // si el tiempo que esta detenido es mayor a 17 seg envia el informe (configurable)
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
                        }*/



                        try {
                            // envia coordenada cada menos tiempo
                            long tiempoCoordMasRapido = Math.round(tiempoEnvioNuevaCoord / 3);
                            System.out.println("envia coordenada cada: " +tiempoCoordMasRapido);
                            Thread.sleep(tiempoCoordMasRapido * 1000);
                        } catch (InterruptedException e) {
                            System.out.println("error durmiendo el hilo");
                        }
                    }

                    else { //fin if si el colectivo estaba parado

                        try {
                            System.out.println("+++++++++++++++++++++++++++++++ distancia recorrida" + distancia);

                            if(distancia > distanciaOffSetMovLarga ){
                                long tiempoCoordMasLento = Math.round(tiempoEnvioNuevaCoord * 1.4);
                                System.out.println("envia coordenada cada: " +tiempoCoordMasLento);
                                // enviar coordenada cada mucho mas tiempo
                                Thread.sleep(tiempoCoordMasLento * 1000);
                            }else{
                                // enviar coordenada cada mucho mas tiempo
                                System.out.println("envia coordenada cada: " +tiempoEnvioNuevaCoord);
                                Thread.sleep(tiempoEnvioNuevaCoord * 1000);
                            }
                        } catch (InterruptedException e) {
                            System.out.println("error durmiendo el hilo");
                        }
                    }

                    // la distancia entre paradas fue MAYOR de 20 mts, el colectivo ESTA circulando
                    // contador en 0 para que cuando se vuelve a parar la primera vez verifique si es parada
                    contVerifParada = 0;
                    try {
                        // vuelve a verificar si esta desviado
                        boolean esFin;
                        esFin = detectarParada(paradasRecorrido,latActual,lngActual,linea,colectivo,recorrido);
                        System.out.println("es parada final?: " + esFin);
                        if(esFin) {
                            finServicioSimple();
                            finish();
                        }else {
                            System.out.println("no es parada final");
                            presenter.makeRequestPostDetectarDesvio(linea, colectivo, recorrido, latActual, lngActual);
                            System.out.println("enviando ubicacion..");
                            presenter.makeRequestPostEnviarUbicacion(linea, colectivo, recorrido, getLat(), getLng());
                            segundosDetenidoStr = 0; // resetea la suma

                            // para que no se superpongan gif
                            if (!coleEnParada) {
                                System.out.println("unidad en circulacion");
                                SimulacionRecorridoActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvEstado.setText("Unidad en circulacion");
                                        ivGifBus.setVisibility(View.VISIBLE);
                                        Glide.with(SimulacionRecorridoActivity.this)
                                                .load(R.drawable.bus_animation_fondo_violeta_circulando)
                                                .into(ivGifBus);
                                    }
                                });
                            }
                        }
                    } catch (ExecutionException | InterruptedException | TimeoutException e) {
                        Toaster.get().showToast(getApplicationContext(), "error verificacion desvio, envio nueva ubicacion - 356" , Toast.LENGTH_SHORT);
                        Toaster.get().showToast(getApplicationContext(), "error " + e.getMessage(), Toast.LENGTH_SHORT);
                    }


//                    } // fin else colectivo circulando. este lo saque de cuando esta detenido

                    fechaUbicacionAntigua = fechaUbicacionActual;
                    latAntigua = String.valueOf(latActual);
                    lngAntigua = String.valueOf(lngActual);


                    // TODO fin colectivo detenido
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

    public boolean detectarParada(List<Parada> listaParadasRecorrido, Double latActual, Double lngActual, String denomLinea, String unidad, String denomRecorrido ) throws ExecutionException, InterruptedException, TimeoutException {
        boolean esFin = false;
        for(Parada parada: listaParadasRecorrido){
//            System.out.println("Parada actual: " + parada.getCodigo() + " - " + parada.getDireccion());
//            System.out.println("Parada final: " + getParadaFinal().getCodigo());
            double distancia = calcularDistancia(latActual,lngActual,parada.getLatitud(),parada.getLongitud());
            if (distancia < distanciaOffSetParada) {
                SimulacionRecorridoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        coleEnParada = true;
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
                    esFin = true;
                    System.out.println("++++ colectivo en parada final");
                    presenter.makeRequestPostColeEnParada( parada.getCodigo(), denomLinea, unidad,denomRecorrido);
                    try {
                        Thread.sleep(5000 ); // para que muestre gif ultima parada
                        Toaster.get().showToast(getApplicationContext(), "Servicio finalizado" , Toast.LENGTH_SHORT);
                    } catch (InterruptedException e) {}
                    enTransito = false;
                }else{
                    System.out.println("++++ Colectivo en parada");
                    presenter.makeRequestPostColeEnParada( parada.getCodigo(), denomLinea, unidad, denomRecorrido);
                }
            }
        }
        return esFin;
    }


    public void setParadaInicio(Parada coordenada, String linea, String colectivo, String denomRecorrido) throws ExecutionException, InterruptedException, TimeoutException {
        SimulacionRecorridoActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                coleEnParada = true;
                tvEstado.setText( "Unidad en parada" );
                tvNombreParada.setText(paradasRecorrido.get(0).getDireccion());
                ivGifBus.setVisibility(View.VISIBLE);
                //inserte gif cole en parada. posible error x estar fuera del hilo principal
                Glide.with(SimulacionRecorridoActivity.this)
                        .load(R.drawable.bus_animation_fondo_violeta_parada)
                        .into(ivGifBus);
            }
        });
        System.out.println("Colectivo en parada inicio");
        presenter.makeRequestPostColeEnParada(coordenada.getCodigo(), linea, colectivo, denomRecorrido);
    }

    // Si hay que cambiar la parada final, se hace aca
    public Parada getParadaFinal() {
        Parada paradaFinal = paradasRecorrido.get(paradasRecorrido.size()-1);
        return paradaFinal;
    }

    public void finServicio(View view) throws ExecutionException, InterruptedException, TimeoutException {
        enTransito = false;
    }

    public void finServicioSimple() throws ExecutionException, InterruptedException, TimeoutException {
        enTransito = false;
    }


    @Override
    public void showResponse(String response) {
        System.out.println("show response en simulacion recorrido activity: " + response);
    }


    @Override
    protected void onDestroy() {
        FinServicio finServicio1 = new FinServicio();
        finServicio1.execute();
        super.onDestroy();
    }

    public class FinServicio extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            //resetea el contador de desvio
            contDesvioAlIniciar = 0;
            //nuevo: resetea el cont de coord a simular
            contCoorSim = 0;

            try {
                presenter.makeRequestPostFinColectivoRecorrido(linea,colectivo,recorrido);
                enTransito = false;
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                Toaster.get().showToast(getApplicationContext(), "error makeRequestPostFinColectivoRecorrido - 631" , Toast.LENGTH_SHORT);
            }

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



