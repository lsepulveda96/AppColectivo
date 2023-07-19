package com.stcu.appcolectivo.ui;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gpmess.example.volley.app.R;
import com.stcu.appcolectivo.interfaces.MainInterface;
import com.stcu.appcolectivo.model.Colectivo;
import com.stcu.appcolectivo.model.Linea;
import com.stcu.appcolectivo.presenter.MainPresenter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainActivity extends Activity implements MainInterface.View {

    public static final int MobileData = 2;
    public static final int WifiData = 1;

    private MainInterface.Presenter presenter;

    List<Colectivo> colectivosDisponibles;
    List<Linea> lineasDisponibles;


    Button btnGPS;
    TextView tvUbicacion, tvNetwork;
//    TexView tvAccess;
    private String myLat, myLng;
    private Spinner itemSeleccionLinea, itemSeleccionColectivo;
    private Button btnIniciarServicio;

//    private Button btnCargarListas;
    Button finServicio;
    ArrayAdapter<String> adapterSeleccionLinea, adapterSeleccionColectivo;
    private TextView tvColectivoSeleccionado, tvLineaSeleccionada;
    Long fechaUbicacionI;
    List<Coordenada> coordenadasSim;
    private ProgressDialog dialog4, dialog3, dialog2, dialog1;

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
                case ConnectivityManager.TYPE_WIFI:Toast.makeText(getApplicationContext(),"wifi encenidido", Toast.LENGTH_SHORT).show();
                    tvNetwork.setVisibility(View.GONE);
                    break;
                case ConnectivityManager.TYPE_MOBILE:Toast.makeText(getApplicationContext(),"mobile encenidido", Toast.LENGTH_SHORT).show();
                    tvNetwork.setVisibility(View.GONE);
                    break;
            }
        }else {
            tvNetwork.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(),"internet apagado", Toast.LENGTH_SHORT).show();
        }

//        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//        //for airplane mode, networkinfo is null
//        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
//        if (null != activeNetwork) {
//
//            switch (activeNetwork.getType()){
//                case ConnectivityManager.TYPE_WIFI:Toast.makeText(getApplicationContext(),"wifi encenidido", Toast.LENGTH_SHORT).show();
//                    tvNetwork.setVisibility(View.GONE);
//                    break;
//                case ConnectivityManager.TYPE_MOBILE:Toast.makeText(getApplicationContext(),"mobile encenidido", Toast.LENGTH_SHORT).show();
//                    tvNetwork.setVisibility(View.GONE);
//                    break;
//            }
//        }else {
//            tvNetwork.setVisibility(View.VISIBLE);
//            Toast.makeText(getApplicationContext(),"internet apagado", Toast.LENGTH_SHORT).show();
//        }
    //

    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //necesario para comprobar internet en tiempo real
        IntentFilter intentFilter =new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mBroadcastReceiver,intentFilter);
        //


        presenter = new MainPresenter(this, this, this);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        setLat("0");
        tvUbicacion = (TextView) findViewById(R.id.tvUbicacion);
        btnGPS = (Button) findViewById(R.id.button);

//        btnCargarListas = (Button) findViewById(R.id.carga_button);
        btnIniciarServicio = (Button) findViewById(R.id.btnIniciarServicio);
        finServicio = (Button) findViewById(R.id.fin_button);
        finServicio.setEnabled(false);
        btnIniciarServicio.setEnabled(false); // para que no pueda seleccionar una lista vacia
        adapterSeleccionLinea = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapterSeleccionColectivo = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        itemSeleccionLinea = (Spinner) findViewById(R.id.spinnerSelLinea);
        itemSeleccionColectivo = (Spinner) findViewById(R.id.spinnerSelColectivo);
        tvLineaSeleccionada = (TextView) findViewById(R.id.tvLineaSeleccionada);
        tvColectivoSeleccionado = (TextView) findViewById(R.id.tvColectivoSeleccionado);
        tvNetwork = (TextView)findViewById(R.id.tv_network);

//        colectivosDisponibles = new ArrayList<>();
//        lineasDisponibles = new ArrayList<>();


        colectivosDisponibles = getIntent().getParcelableArrayListExtra("listaColectivos");
        lineasDisponibles = getIntent().getParcelableArrayListExtra("listaLineas");

//        ArrayList<String> colectivosDisponibles = getIntent().getExtras().getStringArrayList("listaColectivos");
//
//        ArrayList<String> lineasDisponibles = getIntent().getStringArrayListExtra("listaLineas");

        System.out.println("El tamano de la lista de lineas desde el servidor --------------------- " +lineasDisponibles.size());

        System.out.println("El tamano de la lista de colectivos desde el servidor --------------------- " +colectivosDisponibles.size());





////         esto no anda.
//        Bundle bundle = getIntent().getExtras().getBundle("hsLineas");
//        if (bundle != null) {
//            for (String key : bundle.keySet()) {
//                hashSetLineas.add((String) bundle.get(key));
//            //Log.e(TAG, key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
//            }
//        }
//
//        Bundle bundle2 = getIntent().getExtras().getBundle("hsColectivos");
//        if (bundle2 != null) {
//            for (String key : bundle2.keySet()) {
//                hashSetColectivos.add((String) bundle2.get(key));
//                //Log.e(TAG, key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
//            }
//        }

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


        // si los dos estan vacios
        if(adapterSeleccionLinea.isEmpty() || adapterSeleccionColectivo.isEmpty()){
            Toast.makeText( getApplicationContext() ,"No se pudo cargar el listado de lineas y colectivos", Toast.LENGTH_SHORT ).show();
        }else {

            btnIniciarServicio.setEnabled( true ); // para que pueda guardar la eleccion
            finServicio.setEnabled( true );

        }


        //desplaza para abajo para actualizar, reemplaza boton actualizar
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setOnRefreshListener(() -> {

//            new Task1().execute();
            //esto anda bien!! si no llega a andar lo del asyntask probar esto.
//            final List<String> opcionesLineas = presenter.consultaLineasActivas();
//            final List<String> opcionesColectivos = presenter.consultaColectivosActivos();

            final List<Linea> opcionesLineas = presenter.consultaLineasActivas();
            final List<Colectivo> opcionesColectivos = presenter.consultaColectivosActivos();


            dialog2 = new ProgressDialog( this );
            dialog2.setMessage( "Cargando listado de lineas y colectivos" );
            dialog2.show();

            final Handler handler2 = new Handler();
            final Runnable r2 = new Runnable(){
                public void run() {
                    swipe.setRefreshing(false);
                    dialog2.cancel();
//                    Set<String> hs = new HashSet<String>();
//                    hs.addAll(opcionesLineas);
//                    Set<String> hs2 = new HashSet<String>();
//                    hs2.addAll(opcionesColectivos);
//                    adapterSeleccionLinea.clear();
//                    for(String opcion: hs){
//                        adapterSeleccionLinea.add(opcion);
//                    }
//                    adapterSeleccionColectivo.clear();
//                    for(String opcion2: hs2){
//                        adapterSeleccionColectivo.add(opcion2);
//                    }

//                    Set<String> hs = new HashSet<String>();
//                    hs.addAll(opcionesLineas);
//                    Set<String> hs2 = new HashSet<String>();
//                    hs2.addAll(opcionesColectivos);
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
        });
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

        //TODO 4to refactoring, ojo con lo que devuelve!! donde lo uso? lista de trayecto a simular!
        coordenadasSim = new ArrayList<Coordenada>();
//        coordenadasSim = fragment.consultaTrayectoASimular( seleccionLin2 );
        coordenadasSim = presenter.consultaTrayectoASimular( seleccionLin2);

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
                    // inicia simulacion
                    //TODO mover a model
                    //quede aca, hacer todo este viaje al model
                    //despues el viaje del model a aca, y cambiar de activity al de simulacion
//                    fragment.makeRequestPostSimulacion( seleccionLin2, seleccionCol2, String.valueOf( coordInicial.getLatitud() ), String.valueOf( coordInicial.getLongitud() ) );
                    presenter.makeRequestPostSimulacion( seleccionLin2, seleccionCol2, String.valueOf( coordInicial.getLatitud() ), String.valueOf( coordInicial.getLongitud() ) );
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


    //Boton cargar, para actualizar la lista de colectivos y lineas
    //esto se deberia hacer automaticamente cuando entra a la app! - Echo.
    //reemplazado por deslizar hacia abajo
//    public void Cargar(View view) {
////        MainFragment fragment = (MainFragment) getFragmentManager().findFragmentById(R.id.main_fragment) ;
//
//        //TODO segundo refactoring
////        fragment.consultaLineasActivas();
//
//        //TODO tercer refactoring
//        //fragment.consultaColectivosActivos();
//
//
////        final List<String> opcionesLineas = fragment.getListadoLineas();
////        final List<String> opcionesLineas = getListadoLineas();
////        final List<String> opcionesColectivos = fragment.getListadoColectivos();
////        final List<String> opcionesColectivos = getListadoColectivos();
//
//        final List<String> opcionesLineas = presenter.consultaLineasActivas();
//        final List<String> opcionesColectivos = presenter.consultaColectivosActivos();
//
//
//        dialog2 = new ProgressDialog( this );
//        dialog2.setMessage( "Cargando listado de lineas y colectivos" );
//        dialog2.show();
//
//        final Handler handler2 = new Handler();
//        final Runnable r2 = new Runnable(){
//            public void run() {
//                dialog2.cancel();
//                Set<String> hs = new HashSet<String>();
//                hs.addAll(opcionesLineas);
//                Set<String> hs2 = new HashSet<String>();
//                hs2.addAll(opcionesColectivos);
//                adapter.clear();
//                for(String opcion: hs){
//                    adapter.add(opcion);
//                }
//                adapter2.clear();
//                for(String opcion2: hs2){
//                    adapter2.add(opcion2);
//                }
//                itemSeleccionLinea.setAdapter(adapter);
//                itemSeleccionColectivo.setAdapter(adapter2);
//
//                // si los dos estan vacios
//                if(adapter.isEmpty() || adapter2.isEmpty()){
//                    Toast.makeText( getApplicationContext() ,"No se pudo cargar el listado de lineas y colectivos", Toast.LENGTH_SHORT ).show();
//                }else {
//                    btnIniciarServicio.setEnabled( true ); // para que pueda guardar la eleccion
//                    finServicio.setEnabled( true );
//                }
//            }
//        };
//        handler2.postDelayed(r2,4000);
//
//    } // fin boton


    //boton iniciar servicio
    public void iniciarServicio(View view) {

        // TODO 5to refactoring
//        obtenerUbicacion();
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
        handler.postDelayed(r,2500);

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
//            MainFragment fragment = (MainFragment) getFragmentManager().findFragmentById(R.id.main_fragment);
//            fragment.makeRequestPostFin(seleccionLin, seleccionCol, getLat(), getLng());
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

//        Toast toast1 =
//                Toast.makeText(getActivity(),
//                        response, Toast.LENGTH_LONG);
//        toast1.show();

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

    @Override
    public void showResponsePostSimulacionOk(String response, String seleccionLin, String seleccionCol, String latInicial, String lngInicial) {
//        Toast toast1 =
//                Toast.makeText(getActivity(),
//                        response, Toast.LENGTH_LONG);
//        toast1.show();

        Toast.makeText( getApplicationContext() ,response, Toast.LENGTH_LONG ).show();

        if(response.equals("Servicio iniciado")) {
            //TODO tiene que ir al model a hacer request y volver al main activity para que llame a simulacion
            Intent ma2 = new Intent(getApplicationContext(), SimulacionRecorridoActivity.class);
            ma2.putExtra("linea", seleccionLin);
            ma2.putExtra("colectivo", seleccionCol);
            ma2.putExtra("latitud", latInicial);
            ma2.putExtra("longitud", lngInicial);
            ma2.putExtra("fechaUbicacion", String.valueOf(System.currentTimeMillis()));
            getApplicationContext().startActivity(ma2);
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

//    @Override
//    public void showListadoLineas(List<String> listadoLineas) {
//        this.listadoLineas = listadoLineas;
//
//    }
//
//    @Override
//    public void showListadoColectivos(List<String> listadoColectivos) {
//        this.listadoColectivos = listadoColectivos;
//    }

//    public List<String> getListadoLineas() {
//        return this.listadoLineas;
//    }

//    public List<String> getListadoColectivos() {
//        return this.listadoColectivos;
//    }
    //    //TODO estos metodos son iguales!! refactoring
//    @Override
//    public void showResponseInicioServicioError(String error) {
//        //parametro error por si quiero mostrar la respuesta que devuelve volley
////        Toast toast1 = Toast.makeText(getActivity(),"No se pudo iniciar el servicio", Toast.LENGTH_SHORT);
////        toast1.show();
//        Toast.makeText( getApplicationContext(),"No se pudo iniciar el servicio",Toast.LENGTH_SHORT ).show();
//        Toast.makeText( getApplicationContext(),"Vuelva a intentarlo",Toast.LENGTH_SHORT ).show();
//    }
//    @Override
//    public void showResponsePostSimulacionError(String error) {
//        Toast.makeText( getApplicationContext(),"No se pudo iniciar el servicio",Toast.LENGTH_SHORT ).show();
//        Toast.makeText( getApplicationContext(),"Vuelva a intentarlo",Toast.LENGTH_SHORT ).show();
//    }

    //    // red habilitada
//    public boolean isNetDisponible(){
//        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();
//        return (actNetInfo != null && actNetInfo.isConnected());
//    }

//    // si hay acceso a internet
//    public Boolean isOnlineNet(){
//
//        Process p = null;
//        try {
//            p = Runtime.getRuntime().exec("ping -c 1 www.google.es");
//            int val = p.waitFor();
//            boolean reachable = (val == 0);
//            return reachable;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }


//    public void obtenerUbicacion() {
//        LocationManager locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
//
//        LocationListener locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                tvUbicacion.setText(location.getLatitude() + "" + location.getLongitude());
//                setLat(String.valueOf(location.getLatitude()));
//                setLng(String.valueOf(location.getLongitude()));
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//            }
//        };
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
//    }
        class Task1 extends AsyncTask<Void,Void,Boolean> {

            @Override
            protected void onPreExecute() {
//                super.onPreExecute();
//                dialog2 = new ProgressDialog( MainActivity.this );
//                dialog2.setMessage( "Cargando listado de lineas y colectivos" );
//                dialog2.show();

            }

            @Override
            protected Boolean doInBackground(Void... voids) {

                //aca tiene que haber algo para saber si la resp http esta ok o no
                //que tambien devuelva una booleana con status 200?
                //tendria que devolver un array, en pos 2 que este la respuesta

//                final List<String> opcionesLineas = presenter.consultaLineasActivas();
//                final List<String> opcionesColectivos = presenter.consultaColectivosActivos();

                final List<Linea> opcionesLineas = presenter.consultaLineasActivas();
                final List<Colectivo> opcionesColectivos = presenter.consultaColectivosActivos();



//                final Handler ha
//                ndler2 = new Handler();
//                final Runnable r2 = new Runnable(){
//                    public void run() {

//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }

//                Set<String> hs = new HashSet<String>();
//                        hs.addAll(opcionesLineas);
//                        Set<String> hs2 = new HashSet<String>();
//                        hs2.addAll(opcionesColectivos);


                        MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
//                        adapterSeleccionLinea.clear();
//                        adapterSeleccionColectivo.clear();
//                        for(String opcion: hs){
//                            adapterSeleccionLinea.add(opcion);
//                        }
//
//                        for(String opcion2: hs2){
//                            adapterSeleccionColectivo.add(opcion2);
//                        }

                        adapterSeleccionLinea.clear();
                        adapterSeleccionColectivo.clear();
                        for(Linea opcion: opcionesLineas){
                            adapterSeleccionLinea.add(opcion.getDenominacion());
                        }

                        for(Colectivo opcion2: opcionesColectivos){
                            adapterSeleccionColectivo.add(opcion2.getUnidad());
                        }


                        itemSeleccionLinea.setAdapter(adapterSeleccionLinea);
                        adapterSeleccionLinea.setDropDownViewResource(R.layout.textview_spinner_selected);
                        itemSeleccionColectivo.setAdapter(adapterSeleccionColectivo);
                        adapterSeleccionColectivo.setDropDownViewResource(R.layout.textview_spinner_selected);
                    }
                });


                        // si los dos estan vacios
                        if(adapterSeleccionLinea.isEmpty() || adapterSeleccionColectivo.isEmpty()){
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "No se pudo cargar el listado de lineas y colectivos", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return false;
                        }else {
                            btnIniciarServicio.setEnabled( true ); // para que pueda guardar la eleccion
                            finServicio.setEnabled( true );
                            return true;
                        }
//                    }
//                };
//                handler2.postDelayed(r2,4000);
//                return null;
            }

            @Override
            protected void onPostExecute(Boolean s) {
                swipe.setRefreshing(false);
//                dialog2.cancel();
            }
        }




//hacer algo de esto!
  /*  class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {

                //------------------>>
                HttpGet httppost = new HttpGet("YOU URLS TO JSON");
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);


                    JSONObject jsono = new JSONObject(data);

                    return true;
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {

                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {

        }*/


}




