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

import java.util.Date;
import java.util.List;


public class SimulacionRecorridoActivity extends Activity implements TrayectoARecorrerInterface.View{

    //TODO esta clase si deberia tener una aparte para model, ir haciendo refactoring de los metodos
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

        paradasRecorrido = presenter.consultaParadasRecorrido(linea,recorrido);
        coordenadasSim = paradasRecorrido;

        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {

                // para indicar que esta en la parada inicial
                setParadaInicio(paradasRecorrido.get(0),linea,colectivo,recorrido);
                System.out.println("la primera parada a recorrer: " + paradasRecorrido.get(0).getDireccion() +", codigo:" + paradasRecorrido.get(0).getCodigo());
                InicioRecorrido inicioRecorrido = new InicioRecorrido();
                inicioRecorrido.execute();
            }
        };
        handler.postDelayed(r,4000);

    }



    public void finServicio(View view) {
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

    public void finServicioSimple() {
        //resetea el contador de desvio
        contDesvioAlIniciar = 0;
        //nuevo: resetea el cont de coord a simular
        contCoorSim = 0;
        //por si hay una notificacion de desvio activa // nose como puedo detenerla sino
        // TODO Arreglar
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
//                finish();
            }
        };
        handler.postDelayed( r, 3000 );


    }

    public void hilo(){

        try {
            Thread.sleep(6000); // cuanto tiempo duerme el hilo, esto deberia poder elegirse por el user
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void ejecutar(){
        InicioRecorrido inicioRecorrido = new InicioRecorrido();
        inicioRecorrido.execute();
    }


    public class InicioRecorrido extends AsyncTask<Void,Integer,Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            hilo();
            return true;
        }


        // todo trabajar en este metodo
        @Override
        protected void onPostExecute(Boolean aBoolean){
            if(enTransito){

                // porque ya es la segunda vez que entra
                if(contCoorSim < coordenadasSim.size()-1) {
                    contCoorSim++;
                }else{
                    finServicioSimple(); // nose si esta bien esto
                    finish();
                }

                obtenerUbicacion();
                ejecutar(); // ejecuta el hilo que duerme x segundos

                latActual = getLatActual();
                lngActual = getLngActual();
                fechaUbicacionActual = getFechaUbicacion(); // la que recive de la ubicacion actual

                //que envie la consulta de desvio la primera vez
                //consulta si esta desviado al iniciar el recorrido. por si no esta en parada de inicio
                if(contDesvioAlIniciar < 1) {
                    // si la primera vez esta parado y esta en una parada tambien detecta la parada
                    boolean esFin = esParadaFinal(paradasRecorrido,latActual,lngActual,linea,colectivo,recorrido);

                    final Handler handler = new Handler();
                    final Runnable r = new Runnable(){
                        public void run(){
                            if(esFin){
                                finServicioSimple();
                                finish();
                            }
                        }
                    };
                    handler.postDelayed(r,3000);

                    presenter.makeRequestPostEnvioDesvio(linea, colectivo, recorrido, latActual, lngActual);
                    Toast.makeText(SimulacionRecorridoActivity.this, "Detectando desvio..", Toast.LENGTH_SHORT).show();
                    contDesvioAlIniciar++;
                }

                // TODO inicio colectivo detenido

                final Handler handler = new Handler();
                final Runnable r = new Runnable(){
                    public void run(){
                        distancia = calcularDistancia(Double.parseDouble(latAntigua), Double.parseDouble(lngAntigua), latActual, lngActual);
                    }
                };
                handler.postDelayed(r,3500);

                Date fuAntiguaD = new Date(fechaUbicacionAntigua);
                Date fuActualD = new Date(fechaUbicacionActual);

                int difSeg = (int)(fuActualD.getTime()-fuAntiguaD.getTime())/1000; // en segundos

                // si esta detenido
                if(distancia < distanciaOffSetMov ){ // si es menor a 20.0 metros, esta detenido // que luego pueda ser configurable // 20.0
                    difTotal = difTotal + difSeg; // suma el tiempo total detenido // tambien se puede con cont++ cada 3 intentos envia
                    Toast.makeText(SimulacionRecorridoActivity.this, difTotal+" segundos detenido", Toast.LENGTH_SHORT).show();
                    tvEstado.setText("Unidad detenida");

                    if(contVerifParada < 1){
                        //si esta parado la primera vez detecta la parada
                        boolean esFin = esParadaFinal(paradasRecorrido,latActual,lngActual,linea,colectivo,recorrido);

                        final Handler handler1 = new Handler();
                        final Runnable r1 = new Runnable(){
                            public void run(){
                                if(esFin){
                                    finServicioSimple();
                                    finish();
                                }
                            }
                        };
                        handler1.postDelayed(r1,3000);
                        //por si es parada final
                        //detectarParadaFinal(getParadaFinal(),latActual,lngActual);
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

                    // vuelve a verificar si esta desviado
                    boolean esFin = esParadaFinal(paradasRecorrido,latActual,lngActual,linea,colectivo,recorrido);

                    final Handler handler1 = new Handler();
                    final Runnable r1 = new Runnable(){
                        public void run(){
                            if(esFin){
                                finServicioSimple();
                                finish();
                            } else{
                                presenter.makeRequestPostEnvioDesvio(linea, colectivo, recorrido, latActual, lngActual);
                                Toast.makeText( SimulacionRecorridoActivity.this, "Enviando ubicacion..", Toast.LENGTH_SHORT ).show();
                                presenter.makeRequestPostEnvio(linea, colectivo, recorrido, getLat(), getLng());

                                difTotal = 0; // resetea la suma
                                tvEstado.setText( "Unidad en circulacion" );
                            }
                        }
                    };
                    handler1.postDelayed(r1,3000);


                } // fin else colectivo circulando

                fechaUbicacionAntigua = fechaUbicacionActual;
//              TODO fin colectivo detenido
            }
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
    public void obtenerUbicacion(){
        //tvUbicacion.setText(coordenadasSim.get(contCoorSim).getLatitud()+""+coordenadasSim.get(contCoorSim).getLongitud());
        tvUbicacion.setText(coordenadasSim.get(contCoorSim).getDireccion() + " cod: " + coordenadasSim.get(contCoorSim).getCodigo());
        setLat(String.valueOf(coordenadasSim.get(contCoorSim).getLatitud()));
        setLng(String.valueOf(coordenadasSim.get(contCoorSim).getLongitud()));
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

    public boolean esParadaFinal(List<Coordenada> listaParadasRecorrido, Double latActual, Double lngActual, String denomLinea, String unidad, String denomRecorrido ) {
        boolean esFin = false;
        for(Coordenada parada: listaParadasRecorrido){
            double distancia = calcularDistancia(latActual,lngActual,parada.getLatitud(),parada.getLongitud());
            if (distancia < 20.0) {
                if(getParadaFinal().getCodigo() == parada.getCodigo()){
                    esFin = true;
                    Toast.makeText( SimulacionRecorridoActivity.this, "Colectivo en parada final", Toast.LENGTH_SHORT ).show();
                    presenter.makeRequestPostColeEnParada( parada.getCodigo(), denomLinea, unidad,denomRecorrido);
//                    finServicioSimple();
//                    finish();
                }else{
                    // es porque esta en o cerca de una parada de esa linea que esta en servicio
                    Toast.makeText( SimulacionRecorridoActivity.this, "Colectivo en parada", Toast.LENGTH_SHORT ).show();
//                    fragment.makeRequestPostColeEnParada( parada.getCodigo(), denom, unidad ); // a rest lineaColectivo
                    presenter.makeRequestPostColeEnParada( parada.getCodigo(), denomLinea, unidad, denomRecorrido);
                }
            }
        }
        return esFin;
    }


    public void setParadaInicio(Coordenada coordenada, String linea, String colectivo, String denomRecorrido) {
        MainFragment fragment = (MainFragment) getFragmentManager().findFragmentById(R.id.main_fragment);
        Toast.makeText( SimulacionRecorridoActivity.this, "Colectivo en parada inicio", Toast.LENGTH_SHORT ).show();
//        fragment.makeRequestPostColeEnParada( coordenada.getCodigo(), linea, colectivo ); // a rest lineaColectivo
        presenter.makeRequestPostColeEnParada(coordenada.getCodigo(), linea, colectivo, denomRecorrido);
    }

    // Si hay que cambiar la parada final, se hace aca
    public Coordenada getParadaFinal() {
        Coordenada paradaFinal = paradasRecorrido.get(paradasRecorrido.size()-1);
        System.out.println( "++++++++++++++++++ la parada final:" + paradasRecorrido.get(paradasRecorrido.size()-1).getDireccion() );
        return paradaFinal;
    }

    @Override
    public void showResponse(String response) {
        Toast.makeText( getApplicationContext() ,response, Toast.LENGTH_SHORT ).show();
    }

//
//    @Override
//    public void showResponsePostFinOk(String response) {
//        Toast.makeText( getApplicationContext() ,response, Toast.LENGTH_LONG ).show();
//    }
//
//    @Override
//    public void showResponsePostFinError(String error) {
//        //parametro error por si quiero usar la respuesta del volley
//        Toast.makeText( getApplicationContext() ,"No se pudo realizar la operacion", Toast.LENGTH_LONG ).show();
//        Toast.makeText( getApplicationContext() ,"Vuelva a intentarlo", Toast.LENGTH_LONG ).show();
//    }
//
//    //estos dos metodos son iguales!!! refactor
//    @Override
//    public void showResponsePostFinInformeError(String error) {
//        Toast.makeText( getApplicationContext() ,"No se pudo enviar la notificacion", Toast.LENGTH_LONG ).show();
//    }
//
//    @Override
//    public void showResponsePostFinDesvioError(String error) {
//        Toast.makeText( getApplicationContext() ,"No se pudo enviar la notificacion", Toast.LENGTH_LONG ).show();
//    }


}