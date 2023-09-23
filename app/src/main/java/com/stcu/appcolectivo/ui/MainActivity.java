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

    public static final int MobileData = 2;
    public static final int WifiData = 1;

    private MainInterface.Presenter presenter;

    List<Colectivo> colectivosDisponibles;
    List<Linea> lineasDisponibles;
    List<Recorrido> recorridosDisponibles;

    Boolean listasOkThread;

    Button btnGPS;
    TextView tvUbicacion, tvNetwork;
    private String myLat, myLng;
    private Spinner itemSeleccionLinea, itemSeleccionColectivo, itemSeleccionRecorrido;
    private Button btnIniciarServicio;
    Button finServicio;
    ArrayAdapter<String> adapterSeleccionLinea, adapterSeleccionColectivo, adapterSeleccionRecorrido;
    private TextView tvColectivoSeleccionado, tvLineaSeleccionada;
    Long fechaUbicacionI;
    List<Coordenada> coordenadasSim;
    private ProgressDialog dialog4, dialog3, dialog2, dialog1;
    Boolean listasEstanCargadas;

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
//                    Toast.makeText(getApplicationContext(),"wifi encenidido", Toast.LENGTH_SHORT).show();
                    tvNetwork.setVisibility(View.GONE);
                    break;
                case ConnectivityManager.TYPE_MOBILE:
//                    Toast.makeText(getApplicationContext(),"mobile encenidido", Toast.LENGTH_SHORT).show();
                    tvNetwork.setVisibility(View.GONE);
                    break;
            }
        }else {
            tvNetwork.setVisibility(View.VISIBLE);
//            Toast.makeText(getApplicationContext(),"internet apagado", Toast.LENGTH_SHORT).show();
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
        tvUbicacion = (TextView) findViewById(R.id.tvUbicacion);
        btnGPS = (Button) findViewById(R.id.button);
        btnIniciarServicio = (Button) findViewById(R.id.btnIniciarServicio);
        finServicio = (Button) findViewById(R.id.fin_button);
        finServicio.setEnabled(false);
        btnIniciarServicio.setEnabled(false); // para que no pueda seleccionar una lista vacia
        adapterSeleccionLinea = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapterSeleccionColectivo = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapterSeleccionRecorrido = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        itemSeleccionLinea = (Spinner) findViewById(R.id.spinnerSelLinea);
        itemSeleccionColectivo = (Spinner) findViewById(R.id.spinnerSelColectivo);
        itemSeleccionRecorrido = (Spinner) findViewById(R.id.spinnerSelRecorrido);
        tvLineaSeleccionada = (TextView) findViewById(R.id.tvLineaSeleccionada);
        tvColectivoSeleccionado = (TextView) findViewById(R.id.tvColectivoSeleccionado);
        tvNetwork = (TextView)findViewById(R.id.tv_network);

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
                    .setCancelable(false) // no se puede clickear afuera de el
                    .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.cancel();
//                            recargarListados();
                            new MyVolleyAsyncTask(getApplicationContext(),MainActivity.this).execute();

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

                new MainActivity.MyRecorridoAsyncTask(getApplicationContext(),MainActivity.this, itemSeleccionLinea.getSelectedItem().toString()).execute();

                // cambiarlo, va a tener que ir dentro de otro hilo sino no va a funcionar future get
//                final List<Recorrido> opcionesRecorridos = presenter.consultaRecorridoActivos(itemSeleccionLinea.getSelectedItem().toString());
//
//                final Handler handler3 = new Handler();
//                final Runnable r3 = new Runnable(){
//                    public void run() {
//
//                        adapterSeleccionRecorrido.clear();
//                        for(Recorrido opcionRecorrido: opcionesRecorridos){
//                            adapterSeleccionRecorrido.add(opcionRecorrido.getDenominacion());
//                        }
//                        itemSeleccionRecorrido.setAdapter(adapterSeleccionRecorrido);
//                        adapterSeleccionRecorrido.setDropDownViewResource(R.layout.textview_spinner_selected);
//                    }
//                };
//                handler3.postDelayed(r3,3000);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // escribir codigo..
            }
        });





        //desplaza para abajo para actualizar, reemplaza boton actualizar
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setOnRefreshListener(() -> {

            new MainActivity.MyVolleyAsyncTask(this,this).execute();
//            recargarListados();

        });

    }



    // para recargar listado de lineas colectivos recorridos ante errores
    private void recargarListados() {

        final List<Linea> opcionesLineas;
        final List<Colectivo> opcionesColectivos;
        try {

            // tengo que hacer devuelta lo del async task

            opcionesColectivos = presenter.consultaColectivosActivos();
            opcionesLineas = presenter.consultaLineasActivas();

            dialog2 = new ProgressDialog( this );
            dialog2.setMessage( "Cargando listado de lineas y colectivos" );
            dialog2.show();

            final Handler handler2 = new Handler();
            final Runnable r2 = new Runnable(){
                public void run() {
                    swipe.setRefreshing(false);
                    dialog2.cancel();

                    adapterSeleccionLinea.clear();
                    for(Linea opcion: opcionesLineas){
                        adapterSeleccionLinea.add(opcion.getDenominacion());
                    }
                    adapterSeleccionColectivo.clear();
                    for(Colectivo opcion2: opcionesColectivos){
                        adapterSeleccionColectivo.add(opcion2.getUnidad());
                    }


                    itemSeleccionLinea.setAdapter(adapterSeleccionLinea);
                    adapterSeleccionLinea.setDropDownViewResource(R.layout.textview_spinner_selected);
                    itemSeleccionColectivo.setAdapter(adapterSeleccionColectivo);
                    adapterSeleccionColectivo.setDropDownViewResource(R.layout.textview_spinner_selected);

                    // si los dos estan vacios
                    if(adapterSeleccionLinea.isEmpty() || adapterSeleccionColectivo.isEmpty()){
                        Toast.makeText( getApplicationContext() ,"No se pudo cargar el listado de lineas y colectivos", Toast.LENGTH_SHORT ).show();
                    }else {
                        btnIniciarServicio.setEnabled( true ); // para que pueda guardar la eleccion
                        finServicio.setEnabled( true );
                    }
                }
            };
            handler2.postDelayed(r2,4000);
            // aca deberia llamar a los recorridos de la linea

            final List<Recorrido> opcionesRecorridos = presenter.consultaRecorridoActivos(itemSeleccionLinea.getSelectedItem().toString());

            final Handler handler3 = new Handler();
            final Runnable r3 = new Runnable(){
                public void run() {

                    adapterSeleccionRecorrido.clear();
                    for(Recorrido opcionRecorrido: opcionesRecorridos){
                        adapterSeleccionRecorrido.add(opcionRecorrido.getDenominacion());
                    }
                    itemSeleccionRecorrido.setAdapter(adapterSeleccionRecorrido);
                    adapterSeleccionRecorrido.setDropDownViewResource(R.layout.textview_spinner_selected);
                }
            };
            handler3.postDelayed(r3,3000);


        } catch (ExecutionException | InterruptedException | TimeoutException e) {


    System.out.println("el error al no retornar nada: " + e);

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
                            recargarListados();

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

            return;
//            throw new RuntimeException(e);

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //para menu desplegable// simulacion recorrido
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//        final MainFragment fragment = (MainFragment) getFragmentManager().findFragmentById( R.id.main_fragment );

        // si uno de los dos es vacio
        if(adapterSeleccionLinea.isEmpty() || adapterSeleccionColectivo.isEmpty()){
            Toast.makeText( getApplicationContext() ,"Seleccione linea y colectivo para continuar", Toast.LENGTH_SHORT ).show();
        }else {

        final String seleccionLin2 = itemSeleccionLinea.getSelectedItem().toString();
        final String seleccionCol2 = itemSeleccionColectivo.getSelectedItem().toString();
        final String seleccionRec2 = itemSeleccionRecorrido.getSelectedItem().toString();

        coordenadasSim = new ArrayList<Coordenada>();



        // TODO aca tengo que traer el nombre del recorrido que ya selecciono
            // ahora tambien le pasa el recorrido que selecciona para buscar su id en la bd y linkear
        coordenadasSim = presenter.consultaTrayectoASimular(seleccionLin2, seleccionRec2);

        dialog1 = new ProgressDialog( this );
        dialog1.setMessage( "Cargando recorrido a simular" );
        dialog1.show();

        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                dialog1.cancel();
                if (coordenadasSim.size() != 0) {

                    Toast.makeText( getApplicationContext() ,"Iniciando simulacion..", Toast.LENGTH_SHORT ).show();
                    Coordenada coordInicial = coordenadasSim.get( 0 );

                    presenter.makeRequestPostSimulacion( seleccionLin2, seleccionCol2, seleccionRec2, String.valueOf( coordInicial.getLatitud() ), String.valueOf( coordInicial.getLongitud() ) );
                    //aca tiene que venir la respuesta con "servicio iniciado" llamada desde el model presenter
                    //cambiar de activity a simulacionRecorrido
                } else {
                    Toast.makeText( getApplicationContext(), "No se pudo cargar el recorrido a simular", Toast.LENGTH_LONG ).show();
                }
            }
        };

        handler.postDelayed( r, 5000 );
    }
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }



    //boton iniciar servicio
    public void iniciarServicio(View view) {

        // TODO 5to refactoring
        presenter.obtenerUbicacion();

//        tvUbicacion.setText(location.getLatitude() + "" + location.getLongitude());
//        setLat(String.valueOf(location.getLatitude()));
//        setLng(String.valueOf(location.getLongitude()));

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

//                    MainFragment fragment = (MainFragment) getFragmentManager().findFragmentById(R.id.main_fragment);
//                    fragment.enviarInicioServicioAServidor(seleccionLin, seleccionCol, fechaUbicacionI);
                    //TODO para prueba primer refactoring
                    presenter.enviarInicioServicioAServidor(seleccionLin, seleccionCol, fechaUbicacionI);
                }
            }
        };
        handler.postDelayed(r,4500);

    } // fin boton

    public void finServicio(View view) {
        // TODO 5to refactoring
//        obtenerUbicacion();
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
    }



    @Override
    public void showUbicacion(String strLatitud, String strLongitud) {
        tvUbicacion.setText(strLatitud + "" + strLongitud);
        setLat(strLatitud);
        setLng(strLongitud);
    }

    //TODO estos metodos tambn son iguales, refactoring y que quede uno solo
    @Override
    public void showResponseInicioServicioOk(String response, String seleccionLin, String seleccionCol, Long fechaUbicacionI) {

        Toast.makeText( getApplicationContext() ,response, Toast.LENGTH_LONG ).show();

        if(response.equals("Servicio iniciado")) {
            //pasar devuelta el control al mainActivity para que llame al nuevo activity
            //presenter.showResult() o servicioIniciado() o response()
            //los parametros ya los tenia del activity del que vino
            //
//            MainActivity activity2 = (MainActivity) getActivity();
//            String myLat2 = activity2.getLat();
//            String myLng2 = activity2.getLng();

            //esto deberia hacerlo en el mainActivity, aca solo llama a la api para iniciar el servicio
            Intent ma2 = new Intent(getApplicationContext(), ColectivoEnServicioActivity.class);
            ma2.putExtra("linea", seleccionLin);
            ma2.putExtra("colectivo", seleccionCol);
            ma2.putExtra("latitud", getLat());
            ma2.putExtra("longitud", getLng());
//            ma2.putExtra("fechaUbicacion", String.valueOf(System.currentTimeMillis()));
            ma2.putExtra("fechaUbicacion", String.valueOf(fechaUbicacionI));
//            getActivity().startActivity(ma2);
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
                Intent ma2 = new Intent(MainActivity.this, SimulacionRecorridoActivity.class);
                ma2.putExtra("linea", seleccionLin);
                ma2.putExtra("colectivo", seleccionCol);
                ma2.putExtra("recorrido", seleccionRec);
                ma2.putExtra("latitud", latInicial);
                ma2.putExtra("longitud", lngInicial);
                ma2.putExtra("fechaUbicacion", String.valueOf(System.currentTimeMillis()));
                this.startActivity(ma2);
            }

        } catch (JSONException e) {
            System.out.println("respuesta con error al intentar cargar la simulacion del recorrido" + e.toString());
            throw new RuntimeException(e);
        }


    }



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






    public class MyVolleyAsyncTask extends AsyncTask<String,String, ArrayList<Object>> implements MainInterface.View {

        private Context ctx;
        private MainInterface.Presenter presenter;

        public MyVolleyAsyncTask(Context hostContext, Activity mActivity)
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
                //return del metodo doInBackground
                System.out.println(" error en hilo " + e);
                return null;
//                throw new RuntimeException(e);

                // listasEstanCargadas = false; // enviar esto por intent para leerlo desde el main e inhabilitar botones
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Object> result){

            if(listasOkThread) {
                // para mostrar resultado
                for (Object itemLista : result) {
                    System.out.println(" resultado terminado del hilo " + itemLista.toString());
                }

                List<Colectivo> listaColectivos = (List<Colectivo>) result.get(0);
                List<Linea> listaLineas = (List<Linea>) result.get(1);
                List<Recorrido> listaRecorridos = (List<Recorrido>) result.get(2);
//por ahora los comento xq no me deja usarlos dentro del thread
//                dialog2 = new ProgressDialog( ctx );
//                dialog2.setMessage( "Cargando listado de lineas y colectivos" );
//                dialog2.show();

                swipe.setRefreshing(false);
//                dialog2.cancel();

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
//                                recargarListados();
                                new MyVolleyAsyncTask(ctx,MainActivity.this).execute();

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

                return;



            } // cierra else si las listas no fueron cargadas

        } // cierra onPostExcecute thread



        @Override
        public void showUbicacion(String strLatitud, String strLongitud) {

        }

        @Override
        public void showResponseInicioServicioOk(String response, String seleccionLin, String seleccionCol, Long fechaUbicacionI) {

        }

        @Override
        public void showResponsePostSimulacionOk(String response, String seleccionLin, String seleccionCol, String seleccionRec, String latInicial, String lngInicial) {

        }

        @Override
        public void showResponse(String response) {

        }

        @Override
        public void showResponseError(String error) {

        }
    }






    public class MyRecorridoAsyncTask extends AsyncTask<String, String, List<Recorrido>> implements MainInterface.View {

        private Context ctx;
        private MainInterface.Presenter presenter;
        private String lineaSeleccionadaLocal;

        public MyRecorridoAsyncTask(Context hostContext, Activity mActivity, String lineaSeleccionada)
        {
            ctx = hostContext;
            presenter = new MainPresenter(this, ctx, mActivity);
            lineaSeleccionadaLocal = lineaSeleccionada;
        }

        @Override
        protected List<Recorrido> doInBackground(String... params) {

            // Method runs on a separate thread, make all the network calls you need
            try {

                //lineasDisponibles variable global
                List<Recorrido> recorridos = presenter.consultaRecorridoActivos(lineaSeleccionadaLocal);

//                listasOkThread = true;

                return recorridos;

            } catch (ExecutionException | InterruptedException | TimeoutException e) {
//                listasOkThread = false;
                return null;
//                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(List<Recorrido> result){

//            if(listasOkThread) {
            List<Recorrido> listaRecorridos = result;

            adapterSeleccionRecorrido.clear();
            for(Recorrido opcionRecorrido: listaRecorridos){
                adapterSeleccionRecorrido.add(opcionRecorrido.getDenominacion());
            }
            itemSeleccionRecorrido.setAdapter(adapterSeleccionRecorrido);
            adapterSeleccionRecorrido.setDropDownViewResource(R.layout.textview_spinner_selected);

            System.out.println("los recorridos activos que recupero en onPostExcecute:");
            for (Recorrido recorrido:listaRecorridos) {
                System.out.println("recorrido: " + recorrido.getDenominacion());
            }

        }

        @Override
        public void showUbicacion(String strLatitud, String strLongitud) {

        }

        @Override
        public void showResponseInicioServicioOk(String response, String seleccionLin, String seleccionCol, Long fechaUbicacionI) {

        }

        @Override
        public void showResponsePostSimulacionOk(String response, String seleccionLin, String seleccionCol, String seleccionRec, String latInicial, String lngInicial) {

        }

        @Override
        public void showResponse(String response) {

        }

        @Override
        public void showResponseError(String error) {

        }
    }


}


