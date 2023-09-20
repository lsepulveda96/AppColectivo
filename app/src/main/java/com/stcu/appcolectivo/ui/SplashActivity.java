package com.stcu.appcolectivo.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;

import com.gpmess.example.volley.app.R;
import com.stcu.appcolectivo.interfaces.MainInterface;
import com.stcu.appcolectivo.model.Colectivo;
import com.stcu.appcolectivo.model.Linea;
import com.stcu.appcolectivo.presenter.MainPresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class SplashActivity extends Activity implements MainInterface.View  {

    private MainInterface.Presenter presenter;

    // version thread spleeping
//    private List<Colectivo> listaColectivos;
//    private List<Linea> listaLineas;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        presenter = new MainPresenter(this, this, this);

        new MyVolleyAsyncTask(this,this).execute();

//// esto anda - version thread sleeping
/*
//        listaColectivos = presenter.consultaColectivosActivos();
//        listaLineas = presenter.consultaLineasActivas();
//
//        final Handler handler2 = new Handler();
//        final Runnable r2 = new Runnable(){
//            public void run() {
//
//                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//
//                for (Colectivo colectivo: listaColectivos ) {
//                    System.out.println("lo que trae la lista del coelctivo del servidor: " + colectivo.getUnidad());
//                }
//
//                for (Linea linea: listaLineas ) {
//                    System.out.println("lo que trae la lista del linea del servidor: " + linea.getDenominacion());
//                }
//
//                intent.putParcelableArrayListExtra("listaColectivos", (ArrayList<? extends Parcelable>) listaColectivos);
//                intent.putParcelableArrayListExtra("listaLineas", (ArrayList<? extends Parcelable>) listaLineas);
//
//                startActivity(intent);
//                finish();
//
//            }
//        };
//        handler2.postDelayed(r2,4000);
*/

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

    public class MyVolleyAsyncTask extends AsyncTask<String,String, ArrayList<Object>>  implements MainInterface.View {

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
            ArrayList<Object> listaLineasColectivos = new ArrayList();

            listaLineasColectivos.add(presenter.consultaColectivosActivos());
            listaLineasColectivos.add(presenter.consultaLineasActivas());

//                return presenter.consultaLineasActivas();
                return listaLineasColectivos;


            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Object> result){

            // para mostrar resultado
            for (Object itemLista: result ) {
                System.out.println(" resultado terminado del hilo " + itemLista.toString());
            }

            Intent intent = new Intent(SplashActivity.this, MainActivity.class);

            List<Colectivo> listaColectivos = (List<Colectivo>) result.get(0);
            List<Linea> listaLineas = (List<Linea>) result.get(1);


            intent.putParcelableArrayListExtra("listaColectivos", (ArrayList<? extends Parcelable>) listaColectivos);
            intent.putParcelableArrayListExtra("listaLineas", (ArrayList<? extends Parcelable>) listaLineas);
            startActivity(intent);
            finish();
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
