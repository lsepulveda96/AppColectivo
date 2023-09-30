package com.stcu.appcolectivo.ui;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

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
    public static double distanciaOffSetMov = 20.0;
    public static int tiempoMaxDetenido = 17;

    private TextView tvLinea ,tvColectivo, tvLatitud, tvLongitud, tvUbicacion;
    TextView tvEstado;
    private String linea, colectivo, recorrido, latitud, longitud, fechaUbicacionInicialS, myLat, myLng, latAntigua, lngAntigua;
    private Long fechaUbicacionAntigua, fechaUbicacionInicial, fechaUbicacionActual;
    private Button finServicio;
    Double latActual, lngActual, distancia = 0.0;
    int difTotal = 0;
    private int contDesvioAlIniciar = 0, contVerifParada = 0;

    List<Coordenada> paradasRecorrido, coordenadasSim;
    // la empiezo en 0 para que se sume siempre (tambien la primera vez)
    private int contCoorSim = 0;

    private TrayectoARecorrerInterface.Presenter presenter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulacion);

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

        //seguir trabajando aca
        //.



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





//    public void hilo(){
//
//        try {
//            Thread.sleep(6000); // cuanto tiempo duerme el hilo, esto deberia poder elegirse por el user
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    public void ejecutar(){
        InicioRecorridoSim inicioRecorrido = new InicioRecorridoSim();
        inicioRecorrido.execute();
    }


    // cambiar lo del return true
    public class InicioRecorridoSim extends AsyncTask<Void,Integer,Boolean> {


        @Override
        protected Boolean doInBackground(Void... voids) {
//            hilo();
            // para indicar que esta en la parada inicial
            try {
                paradasRecorrido = presenter.consultaParadasRecorrido(linea,recorrido);
                coordenadasSim = paradasRecorrido;
                System.out.println("el resultado de la consulta paradas recorrido");
//                for (Coordenada coord: paradasRecorrido
//                ) {
//                    System.out.println("Parada recorrido- id: " + coord.getCodigo() + ".  direccion: "  + coord.getDireccion());
//                }
            }  catch (ExecutionException | InterruptedException | TimeoutException | JSONException e) {
                System.out.println("Error en consulta paradas recorridos: " + e);
                throw new RuntimeException(e);
            }

            try{
                setParadaInicio(paradasRecorrido.get(0),linea,colectivo,recorrido);
            }  catch (ExecutionException | InterruptedException | TimeoutException e) {
                System.out.println("Error en set parada inicio: " + e);
                throw new RuntimeException(e);
            }
            System.out.println("la primera parada a recorrer: " + paradasRecorrido.get(0).getDireccion() +", codigo:" + paradasRecorrido.get(0).getCodigo());


            for (int i=0;i<paradasRecorrido.size();i++) {




            if(enTransito){

                // porque ya es la segunda vez que entra
                if(contCoorSim < coordenadasSim.size()-1) {
                    contCoorSim++;
                }else{
                    try {
                        finServicioSimple(); // nose si esta bien esto
                        finish();
                    } catch (ExecutionException | InterruptedException | TimeoutException e) {
                        throw new RuntimeException(e);
                    }
                }

                obtenerUbicacion();
                try {
                    Thread.sleep(6000); // para dejar pasar tiempo entre cada parada
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
//                ejecutar(); // ejecuta el hilo que duerme x segundos

                latActual = getLatActual();
                lngActual = getLngActual();
                fechaUbicacionActual = getFechaUbicacion(); // la que recive de la ubicacion actual

                //que envie la consulta de desvio la primera vez
                //consulta si esta desviado al iniciar el recorrido. por si no esta en parada de inicio
                if(contDesvioAlIniciar < 1) {
                    // si la primera vez esta parado y esta en una parada tambien detecta la parada
                    boolean esFin;
                    try {
                        esFin = esParadaFinal(paradasRecorrido,latActual,lngActual,linea,colectivo,recorrido);
                        Thread.sleep(3000);
                        System.out.println("es parada final?: " + esFin);
                    } catch (ExecutionException | InterruptedException | TimeoutException e) {
                        throw new RuntimeException(e);
                    }

                    if(esFin){
                        try {
                            System.out.println("Entra a fin servicio simple");
                            finServicioSimple();
                            finish();
                        } catch (ExecutionException | InterruptedException | TimeoutException e) {
                            throw new RuntimeException(e);
                        }
                    }


                    try {
                        presenter.makeRequestPostEnvioDesvio(linea, colectivo, recorrido, latActual, lngActual);
                        System.out.println("Detectando desvio..");
                        contDesvioAlIniciar++;
                    } catch (ExecutionException | InterruptedException | TimeoutException e) {
                        throw new RuntimeException(e);
                    }

//                    Toast.makeText(SimulacionRecorridoActivity.this, "Detectando desvio..", Toast.LENGTH_SHORT).show();

                } // fin if cont desvio al inciar

                // TODO inicio colectivo detenido

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                distancia = calcularDistancia(Double.parseDouble(latAntigua), Double.parseDouble(lngAntigua), latActual, lngActual);


                Date fuAntiguaD = new Date(fechaUbicacionAntigua);
                Date fuActualD = new Date(fechaUbicacionActual);

                int difSeg = (int)(fuActualD.getTime()-fuAntiguaD.getTime())/1000; // en segundos

                // si esta detenido
                if(distancia < distanciaOffSetMov ){ // si es menor a 20.0 metros, esta detenido // que luego pueda ser configurable // 20.0
//                    System.out.println("entra en colectivo detenido");
                    difTotal = difTotal + difSeg; // suma el tiempo total detenido // tambien se puede con cont++ cada 3 intentos envia
                    Toast.makeText(SimulacionRecorridoActivity.this, difTotal+" segundos detenido", Toast.LENGTH_SHORT).show();
                    tvEstado.setText("Unidad detenida");

                    if(contVerifParada < 1){
                        //si esta parado la primera vez detecta la parada
                        boolean esFin;
                        try {
                            esFin = esParadaFinal(paradasRecorrido,latActual,lngActual,linea,colectivo,recorrido);

                            Thread.sleep(3000);
                            System.out.println("es parada final?: " + esFin);
                            if(esFin) {
                                finServicioSimple();
                                finish();
                            }
                        } catch (ExecutionException | InterruptedException | TimeoutException e) {
                            throw new RuntimeException(e);
                        }

                        //por si es parada final
                        contVerifParada++;
                    }

                    //si esta detenido por mas de 'x' tiempo
                    if(difTotal > tiempoMaxDetenido){ // si el tiempo que esta detenido es mayor a 17 seg envia el informe (configurable) // 17

                        // si el colectivo todavia sigue parado, actualiza la notificacion
                        if(notificacionActiva == true){
                            presenter.makeRequestPostActInforme(linea, colectivo, String.valueOf(latActual), String.valueOf(lngActual), String.valueOf(fechaUbicacionActual), "" + difTotal);
                            Toast.makeText(SimulacionRecorridoActivity.this, "unidad detenida, actualizando informe..", Toast.LENGTH_SHORT).show();
                        }else{
                            // sino es la primera vez que el colectivo se para y crea la nueva notificacion y setea bander en true
                            presenter.makeRequestPostEnvioInforme(linea, colectivo, String.valueOf(latActual), String.valueOf(lngActual), String.valueOf(fechaUbicacionActual), "" + difTotal);
                            Toast.makeText(SimulacionRecorridoActivity.this, "unidad detenida, enviando informe..", Toast.LENGTH_SHORT).show();
                            notificacionActiva = true;
                        }
                    }

                }else{ //fin if si el colectivo estaba parado

                    // la distancia entre paradas fue MAYOR de 20 mts, el colectivo ESTA circulando

                    // si el colectivo estaba parado, y empezo a circular, actualiza la notificacion(con el tiempo final) y cambia la bander a false
                    if(notificacionActiva == true) {
                        presenter.makeRequestPostFinInforme(linea, colectivo, String.valueOf(latActual), String.valueOf(lngActual), String.valueOf(fechaUbicacionActual), "" + difTotal);
                        notificacionActiva = false; // resetea la bander
                    }

                    // contador en 0 para que cuando se vuelve a parar la primera vez verifique si es parada
                    contVerifParada = 0;
                    try {
                        // vuelve a verificar si esta desviado
                        boolean esFin;
                        esFin = esParadaFinal(paradasRecorrido,latActual,lngActual,linea,colectivo,recorrido);
                        Thread.sleep(3000);
                        System.out.println("es parada final?: " + esFin);
                        if(esFin) {
//                            System.out.println("llama a fin de servicio simple");
                            finServicioSimple();
                            finish();
                        }else{
                            System.out.println("no es parada final");
                            presenter.makeRequestPostEnvioDesvio(linea, colectivo, recorrido, latActual, lngActual);
//                        Toast.makeText( SimulacionRecorridoActivity.this, "Enviando ubicacion..", Toast.LENGTH_SHORT ).show();
                            System.out.println("enviando ubicacion..");
                            presenter.makeRequestPostEnvio(linea, colectivo, recorrido, getLat(), getLng());
                            difTotal = 0; // resetea la suma
                            System.out.println("unidad en circulacion");
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    tvEstado.setText( "Unidad en circulacion" );
//                                }
//                            });

                        }

                    } catch (ExecutionException | InterruptedException | TimeoutException e) {
                        throw new RuntimeException(e);
                    }



                } // fin else colectivo circulando

                fechaUbicacionAntigua = fechaUbicacionActual;
//              TODO fin colectivo detenido
            } //fin if en transito

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

    public boolean esParadaFinal(List<Coordenada> listaParadasRecorrido, Double latActual, Double lngActual, String denomLinea, String unidad, String denomRecorrido ) throws ExecutionException, InterruptedException, TimeoutException {
        boolean esFin = false;

        for(Coordenada parada: listaParadasRecorrido){

//            System.out.println("Parada actual: " + parada.getCodigo() + " - " + parada.getDireccion());
//            System.out.println("Parada final: " + getParadaFinal().getCodigo());

            double distancia = calcularDistancia(latActual,lngActual,parada.getLatitud(),parada.getLongitud());

            if (distancia < 20.0) {
                if(getParadaFinal().getCodigo() == parada.getCodigo()){
//                    System.out.println("getParadaFinal es igual a parada getCodigo actual");
                    esFin = true;
//                    Toast.makeText( SimulacionRecorridoActivity.this, "Colectivo en parada final", Toast.LENGTH_SHORT ).show();
                    System.out.println("++++ colectivo en parada final");
                    presenter.makeRequestPostColeEnParada( parada.getCodigo(), denomLinea, unidad,denomRecorrido);

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
        //resetea el contDesvio
        contDesvioAlIniciar = 0;

        //nueva, resetea cont coordenada a simular
        contCoorSim = 0;

        //por si hay una notificacion de desvio activa // nose como puedo detenerla sino
        presenter.makeRequestPostFinDesvio(linea, colectivo, recorrido);
        presenter.makeRequestPostFin(linea,colectivo,recorrido);

        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                enTransito = false;
                if(notificacionActiva) {
                    presenter.makeRequestPostFinInforme(linea, colectivo, String.valueOf(latActual), String.valueOf(lngActual), String.valueOf(fechaUbicacionActual), "" + difTotal);
                }
                notificacionActiva = false; // resetea la bander
                Toast.makeText( getApplicationContext(), "Servicio finalizado", Toast.LENGTH_SHORT ).show();
                finish();
            }
        };
        handler.postDelayed( r, 3000 );

    }

    public void finServicioSimple() throws ExecutionException, InterruptedException, TimeoutException {

        //resetea el contador de desvio
        contDesvioAlIniciar = 0;
        //nuevo: resetea el cont de coord a simular
        contCoorSim = 0;
        //por si hay una notificacion de desvio activa // nose como puedo detenerla sino
        // TODO Arreglar
        presenter.makeRequestPostFinDesvio(linea, colectivo, recorrido);
        presenter.makeRequestPostFin(linea,colectivo,recorrido);

        enTransito = false;
        if(notificacionActiva) {
            //refactorizarlo
//            presenter.makeRequestPostFinInforme(linea, colectivo, String.valueOf(latActual), String.valueOf(lngActual), String.valueOf(fechaUbicacionActual), "" + difTotal);
        }
        notificacionActiva = false; // resetea la bander
//        Toast.makeText( getApplicationContext(), "Servicio finalizado", Toast.LENGTH_SHORT ).show();
        System.out.println("servicio finalizado+++++++++++++++");

    }



    @Override
    public void showResponse(String response) {
        Toast.makeText( getApplicationContext() ,response, Toast.LENGTH_SHORT ).show();
    }



}