package com.stcu.appcolectivo.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gpmess.example.volley.app.R;
import com.stcu.appcolectivo.interfaces.MainInterface;
import com.stcu.appcolectivo.model.Colectivo;
import com.stcu.appcolectivo.model.Coordenada;
import com.stcu.appcolectivo.model.Linea;
import com.stcu.appcolectivo.model.Recorrido;
import com.stcu.appcolectivo.presenter.MainPresenter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


public class MainActivity extends Activity implements MainInterface.View {

    private MainInterface.Presenter presenter;

    List<Colectivo> colectivosDisponibles;
    List<Linea> lineasDisponibles;
    List<Recorrido> recorridosDisponibles;
    List<Coordenada> coordenadasSim;
    Boolean listasOkThread, listasEstanCargadas;
    Button btnGPS, finServicio, btnIniciarServicio;
    TextView tvUbicacion, tvNetwork, tvColectivoSeleccionado, tvLineaSeleccionada;
    private String myLat, myLng;
    private Spinner itemSeleccionLinea, itemSeleccionColectivo, itemSeleccionRecorrido;
    ArrayAdapter<String> adapterSeleccionLinea, adapterSeleccionColectivo, adapterSeleccionRecorrido;
    Long fechaUbicacionI;
    private ProgressDialog dialog4, dialog3, dialog1;
    private SwipeRefreshLayout swipe;



    //necesario para comprobar internet en tiempo real
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkStatus();
        }
    };

    //necesario para comprobar internet en tiempo real
    private void checkStatus(){
        NetworkInfo activeNetwork = presenter.isNetAvailable();
        if (null != activeNetwork) {

            switch (activeNetwork.getType()){
                case ConnectivityManager.TYPE_WIFI:
                case ConnectivityManager.TYPE_MOBILE:
                    tvNetwork.setVisibility(View.GONE);
                    break;
            }
        }else {
            tvNetwork.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //necesario para comprobar internet en tiempo real
        IntentFilter intentFilter =new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mBroadcastReceiver,intentFilter);

        presenter = new MainPresenter(this, this, this);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        setLat("0");
        tvUbicacion = findViewById(R.id.tvUbicacion);
        btnGPS = findViewById(R.id.button);
        btnIniciarServicio = findViewById(R.id.btnIniciarServicio);
        finServicio = findViewById(R.id.fin_button);
        finServicio.setEnabled(false); // se habilita cuando se inicia servicio
        btnIniciarServicio.setEnabled(false); // para que no pueda seleccionar una lista vacia
        adapterSeleccionLinea = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapterSeleccionColectivo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapterSeleccionRecorrido = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        itemSeleccionLinea = findViewById(R.id.spinnerSelLinea);
        itemSeleccionColectivo = findViewById(R.id.spinnerSelColectivo);
        itemSeleccionRecorrido = findViewById(R.id.spinnerSelRecorrido);
        tvLineaSeleccionada = findViewById(R.id.tvLineaSeleccionada);
        tvColectivoSeleccionado = findViewById(R.id.tvColectivoSeleccionado);
        tvNetwork = findViewById(R.id.tv_network);

        // if(listasEstanCargadas) // llamo a lo normal, sino hago un cartel con las op. reintentar. cancelar
        listasEstanCargadas = getIntent().getExtras().getBoolean("listasEstanCargadas");

        if(listasEstanCargadas){

            colectivosDisponibles = getIntent().getParcelableArrayListExtra("listaColectivos");
            lineasDisponibles = getIntent().getParcelableArrayListExtra("listaLineas");
            recorridosDisponibles = getIntent().getParcelableArrayListExtra("listaRecorridos");

            adapterSeleccionLinea.clear();
            for(Linea opcion: lineasDisponibles){
                adapterSeleccionLinea.add(opcion.getDenominacion());
            }
            adapterSeleccionColectivo.clear();

            for(Colectivo opcion2: colectivosDisponibles){
                adapterSeleccionColectivo.add(opcion2.getUnidad());
            }
            adapterSeleccionLinea.setDropDownViewResource(R.layout.textview_spinner_selected);
            adapterSeleccionColectivo.setDropDownViewResource(R.layout.textview_spinner_selected);
            itemSeleccionLinea.setAdapter(adapterSeleccionLinea);
            itemSeleccionColectivo.setAdapter(adapterSeleccionColectivo);
            itemSeleccionRecorrido.setAdapter(adapterSeleccionRecorrido);

            adapterSeleccionRecorrido.clear();
            for(Recorrido opcionRecorrido: recorridosDisponibles){
                adapterSeleccionRecorrido.add(opcionRecorrido.getDenominacion());
            }
            itemSeleccionRecorrido.setAdapter(adapterSeleccionRecorrido);
            adapterSeleccionRecorrido.setDropDownViewResource(R.layout.textview_spinner_selected);


            // si los dos estan vacios
            if(adapterSeleccionLinea.isEmpty() || adapterSeleccionColectivo.isEmpty()){
                Toast.makeText( getApplicationContext() ,"No se pudo cargar el listado de lineas y colectivos", Toast.LENGTH_SHORT ).show();
            }else {
                btnIniciarServicio.setEnabled( true ); // para que pueda guardar la eleccion
                finServicio.setEnabled( true );
            }


        }else{
            AlertDialog.Builder alertaListasVacias = new AlertDialog.Builder(MainActivity.this);
            alertaListasVacias.setMessage("No es posible cargar listado de colectivos-lineas")
                    .setCancelable(false) // para que no se pueda clickear afuera de el
                    .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.cancel();
                            new ThreadRecargaListaAsyncTask(getApplicationContext(),MainActivity.this).execute();

                        }
                    })
                    .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            unregisterReceiver(mBroadcastReceiver);
                            finish();

                        }
                    });
            AlertDialog titulo = alertaListasVacias.create();
            titulo.setTitle("Error");
            titulo.show();

        }


        // ante un cambio en la linea, llama nuevamente a sus recorrido correspondientes
        itemSeleccionLinea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                new ThreadActualizaRecorridoAsyncTask(getApplicationContext(), itemSeleccionLinea.getSelectedItem().toString()).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // escribir codigo..
            }
        }); // fin Listener cambio de linea



        //desplaza para abajo para actualizar, reemplaza boton actualizar
        swipe = findViewById(R.id.swipe);
        swipe.setOnRefreshListener(() -> {

            new ThreadRecargaListaAsyncTask(this,this).execute();

        }); // fin swipe actualiza listas

    } // fin onCreate


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // para menu desplegable - item SIMULACION RECORRIDO
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        // si uno de los dos es vacio
        if(adapterSeleccionLinea.isEmpty() || adapterSeleccionColectivo.isEmpty()){
            Toast.makeText( getApplicationContext() ,"Seleccione linea y colectivo para continuar", Toast.LENGTH_SHORT ).show();
        }else{
            new ThreadConsultaTrayectoASimular(this).execute();
        }
        return itemId == R.id.action_settings || super.onOptionsItemSelected(item);

    }  // fin menu desplegable




    //boton iniciar servicio
    public void iniciarServicio(View view) {

        // TODO 5to refactoring
        presenter.obtenerUbicacion();

        dialog3 = new ProgressDialog( this );
        dialog3.setMessage( "Detectando ubicación.." );
        dialog3.show();

        final Handler handler = new Handler();
        final Runnable r = new Runnable(){
            public void run(){
                dialog3.cancel();
                if(getLat().equals("0")){
                    Toast toast1 = Toast.makeText(getApplicationContext(),"No se pudo obtener su ubicación actual", Toast.LENGTH_LONG);
                    toast1.show();
                }else{
                    String seleccionLin = itemSeleccionLinea.getSelectedItem().toString();
                    String seleccionCol = itemSeleccionColectivo.getSelectedItem().toString();
                    tvLineaSeleccionada.setText("Linea: " + seleccionLin);
                    tvColectivoSeleccionado.setText("Colectivo: " + seleccionCol);

                    fechaUbicacionI = System.currentTimeMillis();
                    setFechaUbicacionI(fechaUbicacionI);
                    presenter.enviarInicioServicioAServidor(seleccionLin, seleccionCol, fechaUbicacionI);
                }
            }
        };
        handler.postDelayed(r,4500);

    } // fin boton iniciar servicio


    // boton fin servicio
    public void finServicio(View view) {
        presenter.obtenerUbicacion();
        dialog4 = new ProgressDialog( this );
        dialog4.setMessage( "Detectando ubicación.." );
        dialog4.show();

        final Handler handler = new Handler();
        final Runnable r = new Runnable(){
            public void run(){
                dialog4.cancel();

                if(getLat().equals("0")) {
                    Toast toast1 = Toast.makeText(getApplicationContext(),"No se pudo obtener la ubicación actual", Toast.LENGTH_LONG);
                    toast1.show();
                }else{
                    String seleccionLin = itemSeleccionLinea.getSelectedItem().toString();
                    String seleccionCol = itemSeleccionColectivo.getSelectedItem().toString();
                    tvLineaSeleccionada.setText("Linea: " + seleccionLin);
                    tvColectivoSeleccionado.setText("Colectivo: " + seleccionCol);
                    presenter.makeRequestPostFin(seleccionLin, seleccionCol, getLat(), getLng());
                }
            }
        };
        handler.postDelayed(r,2500);
    } // fin boton Fin servicio


    @Override
    public void showUbicacion(String strLatitud, String strLongitud) {
        tvUbicacion.setText(strLatitud + "" + strLongitud);
        setLat(strLatitud);
        setLng(strLongitud);
    }



    @Override
    public void showResponseInicioServicioOk(String response, String seleccionLin, String seleccionCol, Long fechaUbicacionI) {

        Toast.makeText( getApplicationContext() ,response, Toast.LENGTH_LONG ).show();

        if(response.equals("Servicio iniciado")) {
            //esto deberia hacerlo en el mainActivity, aca solo llama a la api para iniciar el servicio
            Intent ma2 = new Intent(getApplicationContext(), ColectivoEnServicioActivity.class);
            ma2.putExtra("linea", seleccionLin);
            ma2.putExtra("colectivo", seleccionCol);
            ma2.putExtra("latitud", getLat());
            ma2.putExtra("longitud", getLng());
            ma2.putExtra("fechaUbicacion", String.valueOf(fechaUbicacionI));
            getApplicationContext().startActivity(ma2);
        }
    }


    // todo trabajando en este metodo para simulacion recorrido
    @Override
    public void showResponsePostSimulacionOk(String response, String seleccionLin, String seleccionCol, String seleccionRec, String latInicial, String lngInicial) {
        try {
            JSONObject obj = new JSONObject(response);
            String respuesta = obj.getString("mensaje");

            Toast.makeText( MainActivity.this, respuesta, Toast.LENGTH_LONG ).show();
            if(respuesta.equals("Servicio iniciado")) {
                //TODO tiene que ir al model a hacer request y volver al main activity para que llame a simulacion
                Intent intentSimRecorridoActivity = new Intent(MainActivity.this, SimulacionRecorridoActivity.class);
                intentSimRecorridoActivity.putExtra("linea", seleccionLin);
                intentSimRecorridoActivity.putExtra("colectivo", seleccionCol);
                intentSimRecorridoActivity.putExtra("recorrido", seleccionRec);
                intentSimRecorridoActivity.putExtra("latitud", latInicial);
                intentSimRecorridoActivity.putExtra("longitud", lngInicial);
                intentSimRecorridoActivity.putExtra("fechaUbicacion", String.valueOf(System.currentTimeMillis()));
                this.startActivity(intentSimRecorridoActivity);
            }

        } catch (JSONException e) {
            System.out.println("error al cargar la simulacion del recorrido" + e);
            throw new RuntimeException(e);
        }

    } // fin showResponsePostSimulacion



    @Override
    public void showResponse(String response) {
        Toast.makeText( getApplicationContext(),response,Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void showResponseError(String error) {
        Toast.makeText( getApplicationContext(),error,Toast.LENGTH_SHORT ).show();
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

    public void setFechaUbicacionI(Long fechaUbicacionI) {
        this.fechaUbicacionI = fechaUbicacionI;
    }


    // thread para recargar listas colectivos lineas
    public class ThreadRecargaListaAsyncTask extends AsyncTask<String,String, ArrayList<Object>> implements MainInterface.View {

        private Context ctx;
        private MainInterface.Presenter presenter;

        public ThreadRecargaListaAsyncTask(Context hostContext, Activity mActivity)
        {
            ctx = hostContext;
            presenter = new MainPresenter(this, ctx, mActivity);
        }

        @Override
        protected ArrayList<Object> doInBackground(String... params) {

            // Method runs on a separate thread, make all the network calls you need
            try {

                listasOkThread = true;
                ArrayList<Object> listaLineasColectivosRecorridos = new ArrayList();
                List<Linea> lineasActivas = presenter.consultaLineasActivas();
                listaLineasColectivosRecorridos.add(presenter.consultaColectivosActivos());
                listaLineasColectivosRecorridos.add(lineasActivas);
                listaLineasColectivosRecorridos.add(presenter.consultaRecorridoActivos(lineasActivas.get(0).getDenominacion()));

                return listaLineasColectivosRecorridos;


            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                listasOkThread = false;
                System.out.println(" error en hilo " + e);
                //return del metodo doInBackground
                return null;
//                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Object> result){

            if(listasOkThread) {
                // para mostrar resultado
                for (Object itemLista : result) {
                    System.out.println(" resultado onPostExcecute MainActivity listas cole linea " + itemLista.toString());
                }

                List<Colectivo> listaColectivos = (List<Colectivo>) result.get(0);
                List<Linea> listaLineas = (List<Linea>) result.get(1);
                List<Recorrido> listaRecorridos = (List<Recorrido>) result.get(2);

                swipe.setRefreshing(false);

                adapterSeleccionLinea.clear();
                for(Linea opcion: listaLineas){
                    adapterSeleccionLinea.add(opcion.getDenominacion());
                }
                adapterSeleccionColectivo.clear();
                for(Colectivo opcion2: listaColectivos){
                    adapterSeleccionColectivo.add(opcion2.getUnidad());
                }

                itemSeleccionLinea.setAdapter(adapterSeleccionLinea);
                adapterSeleccionLinea.setDropDownViewResource(R.layout.textview_spinner_selected);
                itemSeleccionColectivo.setAdapter(adapterSeleccionColectivo);
                adapterSeleccionColectivo.setDropDownViewResource(R.layout.textview_spinner_selected);

                adapterSeleccionRecorrido.clear();
                for(Recorrido opcionRecorrido: listaRecorridos){
                    adapterSeleccionRecorrido.add(opcionRecorrido.getDenominacion());
                }
                itemSeleccionRecorrido.setAdapter(adapterSeleccionRecorrido);
                adapterSeleccionRecorrido.setDropDownViewResource(R.layout.textview_spinner_selected);

                // si los dos estan vacios
                if(adapterSeleccionLinea.isEmpty() || adapterSeleccionColectivo.isEmpty()){
                    Toast.makeText( getApplicationContext() ,"No se pudo cargar el listado de lineas y colectivos", Toast.LENGTH_SHORT ).show();
                }else {
                    btnIniciarServicio.setEnabled( true ); // para que pueda guardar la eleccion
                    finServicio.setEnabled( true );
                }
                // aca deberia llamar a los recorridos de la linea


            }else{

                // refactor en funcion comun
                AlertDialog.Builder alertaListasVacias = new AlertDialog.Builder(MainActivity.this);
                alertaListasVacias.setMessage("No es posible cargar listado de colectivos-lineas")
                        .setCancelable(false) // no se puede clickear afuera de el
                        .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.cancel();
                                // aca tiene que haber una ruedita de cargando
                                Toast.makeText(MainActivity.this, "Volviendo a cargar listas", Toast.LENGTH_SHORT).show();
                                new ThreadRecargaListaAsyncTask(ctx,MainActivity.this).execute();

                            }
                        })
                        .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                unregisterReceiver(mBroadcastReceiver);
                                finish();

                            }
                        });
                AlertDialog titulo = alertaListasVacias.create();
                titulo.setTitle("Error");
                titulo.show();

            } // cierra else si las listas no fueron cargadas

        } // cierra onPostExcecute thread


        @Override
        public void showUbicacion(String strLatitud, String strLongitud) {}

        @Override
        public void showResponseInicioServicioOk(String response, String seleccionLin, String seleccionCol, Long fechaUbicacionI) {}

        @Override
        public void showResponsePostSimulacionOk(String response, String seleccionLin, String seleccionCol, String seleccionRec, String latInicial, String lngInicial) {}

        @Override
        public void showResponse(String response) {}

        @Override
        public void showResponseError(String error) {}

    } // fin thread para recargar listas colectivos lineas





    // thread para actulizar recorrido al modificar item linea
    public class ThreadActualizaRecorridoAsyncTask extends AsyncTask<String, String, List<Recorrido>>{

        private Context ctx;
        private String lineaSeleccionadaLocal;

        public ThreadActualizaRecorridoAsyncTask(Context hostContext, String lineaSeleccionada)
        {
            ctx = hostContext;
            lineaSeleccionadaLocal = lineaSeleccionada;
        }
        @Override
        protected List<Recorrido> doInBackground(String... params) {
            try {
                List<Recorrido> recorridos = presenter.consultaRecorridoActivos(lineaSeleccionadaLocal);
                return recorridos;
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                System.out.println("error al traer recorridos activos: " + e);
                return null;
//                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(List<Recorrido> result){
            List<Recorrido> listaRecorridos = result;
            adapterSeleccionRecorrido.clear();
            for(Recorrido opcionRecorrido: listaRecorridos){
                adapterSeleccionRecorrido.add(opcionRecorrido.getDenominacion());
            }
            itemSeleccionRecorrido.setAdapter(adapterSeleccionRecorrido);
            adapterSeleccionRecorrido.setDropDownViewResource(R.layout.textview_spinner_selected);

            System.out.println("los recorridos activos que recupero en onPostExcecute MainActiviy:");
            for (Recorrido recorrido:listaRecorridos) {
                System.out.println("recorrido: " + recorrido.getDenominacion());
            }

        }
    } // fin thread para actulizar recorrido al modificar item linea


    public class ThreadConsultaTrayectoASimular extends AsyncTask<String, String, List<Coordenada>> {

        private Context ctx;
        final String seleccionLin2 = itemSeleccionLinea.getSelectedItem().toString();
        final String seleccionCol2 = itemSeleccionColectivo.getSelectedItem().toString();
        final String seleccionRec2 = itemSeleccionRecorrido.getSelectedItem().toString();

        public ThreadConsultaTrayectoASimular(Context hostContext)
        {
            ctx = hostContext;
        }
        @Override
        protected List<Coordenada> doInBackground(String... strings) {

            coordenadasSim = new ArrayList<Coordenada>();
            try {
                coordenadasSim = presenter.consultaTrayectoASimular(seleccionLin2, seleccionRec2);

                if (coordenadasSim.size() != 0) {
//                    Toast.makeText( getApplicationContext() ,"Iniciando simulacion..", Toast.LENGTH_SHORT ).show();
                    System.out.println("iniciando simulacion..");
                    Coordenada coordInicial = coordenadasSim.get( 0 ); // para cargar la coordenada inicial
                    presenter.makeRequestPostSimulacion( seleccionLin2, seleccionCol2, seleccionRec2, String.valueOf( coordInicial.getLatitud() ), String.valueOf( coordInicial.getLongitud() ) );
                    //aca tiene que venir la respuesta con "servicio iniciado" llamada desde el model presenter
                    //cambiar de activity a simulacionRecorrido
                } else {
                    Toast.makeText( getApplicationContext(), "No se pudo cargar el recorrido a simular", Toast.LENGTH_LONG ).show();
                }


            } catch (JSONException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            }
            return coordenadasSim;
        } // fin doInBackground


        @Override
        protected void onPostExecute(List<Coordenada> result){

            for (Coordenada coor: result) {
                System.out.println("la lista de coordenadas a simular: " + coor.getDireccion());
            }

//            dialog1 = new ProgressDialog( this );
//            dialog1.setMessage( "Cargando recorrido a simular" );
//            dialog1.show();
//            dialog1.cancel();



        } // fin onPostExcecute

    } // fin thread trayecto a simular


}


