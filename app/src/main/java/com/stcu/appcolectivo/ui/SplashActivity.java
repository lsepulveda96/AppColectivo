package com.stcu.appcolectivo.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.widget.Toast;

import com.gpmess.example.volley.app.R;
import com.stcu.appcolectivo.interfaces.MainInterface;
import com.stcu.appcolectivo.model.Colectivo;
import com.stcu.appcolectivo.model.Linea;
import com.stcu.appcolectivo.model.Recorrido;
import com.stcu.appcolectivo.presenter.MainPresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class SplashActivity extends Activity implements MainInterface.View  {

    private MainInterface.Presenter presenter;
    boolean listasOkThread;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        presenter = new MainPresenter(this, this, this);

        new MyVolleyAsyncTask(this).execute();
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

    public class MyVolleyAsyncTask extends AsyncTask<String,String, ArrayList<Object>> {

        private Context ctx;

        public MyVolleyAsyncTask(Context hostContext)
        {
            ctx = hostContext;
        }

        @Override
        protected ArrayList<Object> doInBackground(String... params) {

            // Method runs on a separate thread, make all the network calls you need
            try {
                ArrayList<Object> listaLineasColectivosRecorridos = new ArrayList();

                List<Linea> lineasActivas = presenter.consultaLineasActivas();
                listaLineasColectivosRecorridos.add(presenter.consultaColectivosActivos());
                listaLineasColectivosRecorridos.add(lineasActivas);
                List<Recorrido> recorridos = presenter.consultaRecorridoActivos(lineasActivas.get(0).getDenominacion());
                listaLineasColectivosRecorridos.add(recorridos);

                System.out.println("los recorridos activos que recupero:");
                for (Recorrido recorrido:recorridos) {
                    System.out.println("recorrido: " + recorrido.getDenominacion());
                }

                listasOkThread = true;

                return listaLineasColectivosRecorridos;

            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                System.out.println("Error en splash activity al recuperar listas: " + e);
                listasOkThread = false;
                return null;
//                throw new RuntimeException(e);
            }
        } // fin doInBackground

        @Override
        protected void onPostExecute(ArrayList<Object> result){

            Intent intent = new Intent(SplashActivity.this, MainActivity.class);

            if(listasOkThread) {
                // para mostrar resultado
                for (Object itemLista : result) {
                    System.out.println(" +++ resultado Thread onPostExecute SplashActivity " + itemLista.toString());
                }

                List<Colectivo> listaColectivos = (List<Colectivo>) result.get(0);
                List<Linea> listaLineas = (List<Linea>) result.get(1);
                List<Recorrido> listaRecorridos = (List<Recorrido>) result.get(2);

                intent.putParcelableArrayListExtra("listaColectivos", (ArrayList<? extends Parcelable>) listaColectivos);
                intent.putParcelableArrayListExtra("listaLineas", (ArrayList<? extends Parcelable>) listaLineas);
                intent.putParcelableArrayListExtra("listaRecorridos", (ArrayList<? extends Parcelable>) listaRecorridos);
                intent.putExtra("listasEstanCargadas", true);
                startActivity(intent);
                finish();
            }else{
                // forma de usar un Toast dentro del Thread
                /*              new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ctx, "No fue posible cargar el listado de colectivos - lineas", Toast.LENGTH_SHORT).show();
                                    }
                                });*/
                intent.putExtra("listasEstanCargadas", false);
                startActivity(intent);
                finish();
            } // fin else listas cargadas
        } // fin onPostExcecute
    }

}
