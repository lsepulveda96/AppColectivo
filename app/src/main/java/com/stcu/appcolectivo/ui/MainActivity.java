package com.stcu.appcolectivo.ui;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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


public class MainActivity extends Activity implements MainInterface.View {

    public static final int MobileData = 2;
    public static final int WifiData = 1;

    private MainInterface.Presenter presenter;

    List<Colectivo> colectivosDisponibles;
    List<Linea> lineasDisponibles;



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

        colectivosDisponibles = getIntent().getParcelableArrayListExtra("listaColectivos");
        lineasDisponibles = getIntent().getParcelableArrayListExtra("listaLineas");


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

        // si los dos estan vacios
        if(adapterSeleccionLinea.isEmpty() || adapterSeleccionColectivo.isEmpty()){
            Toast.makeText( getApplicationContext() ,"No se pudo cargar el listado de lineas y colectivos", Toast.LENGTH_SHORT ).show();
        }else {

            btnIniciarServicio.setEnabled( true ); // para que pueda guardar la eleccion
            finServicio.setEnabled( true );

        }


        // ante un cambio en la linea, llama nuevamente a sus recorrido correspondientes
        itemSeleccionLinea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                System.out.println("el item cambio");
//                Toast.makeText(MainActivity.this, "el item linea cambio", Toast.LENGTH_SHORT).show();

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
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // escribir codigo..
            }
        });





        //desplaza para abajo para actualizar, reemplaza boton actualizar
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setOnRefreshListener(() -> {

//            new Task1().execute();

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

        });

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


    // todo esto trabajando en este para simulacion recorrido
    @Override
    public void showResponsePostSimulacionOk(String response, String seleccionLin, String seleccionCol, String seleccionRec, String latInicial, String lngInicial) {
       //antiguo
        /* Toast.makeText( getApplicationContext() ,response, Toast.LENGTH_LONG ).show();
        if(response.equals("Servicio iniciado")) {
            //TODO tiene que ir al model a hacer request y volver al main activity para que llame a simulacion
            Intent ma2 = new Intent(getApplicationContext(), SimulacionRecorridoActivity.class);
            ma2.putExtra("linea", seleccionLin);
            ma2.putExtra("colectivo", seleccionCol);
            ma2.putExtra("recorrido", seleccionRec);
            ma2.putExtra("latitud", latInicial);
            ma2.putExtra("longitud", lngInicial);
            ma2.putExtra("fechaUbicacion", String.valueOf(System.currentTimeMillis()));
            getApplicationContext().startActivity(ma2);
        }*/
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

    //probarlo, pero todavia no lo uso
//        class Task1 extends AsyncTask<Void,Void,Boolean> {
//
//            @Override
//            protected void onPreExecute() {
////                super.onPreExecute();
////                dialog2 = new ProgressDialog( MainActivity.this );
////                dialog2.setMessage( "Cargando listado de lineas y colectivos" );
////                dialog2.show();
//
//            }
//
//            @Override
//            protected Boolean doInBackground(Void... voids) {
//
//                //aca tiene que haber algo para saber si la resp http esta ok o no
//                //que tambien devuelva una booleana con status 200?
//                //tendria que devolver un array, en pos 2 que este la respuesta
//
//                final List<Linea> opcionesLineas = presenter.consultaLineasActivas();
//                final List<Colectivo> opcionesColectivos = presenter.consultaColectivosActivos();
//
//                        MainActivity.this.runOnUiThread(new Runnable() {
//                    public void run() {
//
//                        adapterSeleccionLinea.clear();
//                        adapterSeleccionColectivo.clear();
//                        for(Linea opcion: opcionesLineas){
//                            adapterSeleccionLinea.add(opcion.getDenominacion());
//                        }
//
//                        for(Colectivo opcion2: opcionesColectivos){
//                            adapterSeleccionColectivo.add(opcion2.getUnidad());
//                        }
//
//                        itemSeleccionLinea.setAdapter(adapterSeleccionLinea);
//                        adapterSeleccionLinea.setDropDownViewResource(R.layout.textview_spinner_selected);
//                        itemSeleccionColectivo.setAdapter(adapterSeleccionColectivo);
//                        adapterSeleccionColectivo.setDropDownViewResource(R.layout.textview_spinner_selected);
//                    }
//                });
//
//
//                        // si los dos estan vacios
//                        if(adapterSeleccionLinea.isEmpty() || adapterSeleccionColectivo.isEmpty()){
//                            MainActivity.this.runOnUiThread(new Runnable() {
//                                public void run() {
//                                    Toast.makeText(getApplicationContext(), "No se pudo cargar el listado de lineas y colectivos", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                            return false;
//                        }else {
//                            btnIniciarServicio.setEnabled( true ); // para que pueda guardar la eleccion
//                            finServicio.setEnabled( true );
//                            return true;
//                        }
////                    }
////                };
////                handler2.postDelayed(r2,4000);
////                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Boolean s) {
//                swipe.setRefreshing(false);
////                dialog2.cancel();
//            }
//        }




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




