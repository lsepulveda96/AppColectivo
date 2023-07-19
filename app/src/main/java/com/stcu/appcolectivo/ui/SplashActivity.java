package com.stcu.appcolectivo.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.toolbox.BasicNetwork;
import com.gpmess.example.volley.app.R;
import com.stcu.appcolectivo.interfaces.MainInterface;
import com.stcu.appcolectivo.model.Colectivo;
import com.stcu.appcolectivo.model.Linea;
import com.stcu.appcolectivo.presenter.MainPresenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity implements MainInterface.View  {

    private MainInterface.Presenter presenter;
    private ProgressDialog dialog2;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter2;
    private Spinner itemSeleccionLinea;
    private Spinner itemSeleccionColectivo;

    private List<Colectivo> listaColectivos;
    private List<Linea> listaLineas;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        presenter = new MainPresenter(this, this, this);

        listaColectivos = presenter.consultaColectivosActivos();
        listaLineas = presenter.consultaLineasActivas();

        final Handler handler2 = new Handler();
        final Runnable r2 = new Runnable(){
            public void run() {

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);

//                final List<String> opcionesLineas = presenter.consultaLineasActivas();
//                final List<String> opcionesColectivos = presenter.consultaColectivosActivos();




                System.out.println("lo que trae la lista del coelctivo del servidor tamanioooooo: " + listaColectivos.size());
                System.out.println("lo que trae la lista del linea del servidor tamanioooooo: " + listaLineas.size());

                for (Colectivo colectivo: listaColectivos ) {
                    System.out.println("lo que trae la lista del coelctivo del servidor: " + colectivo.getUnidad());
                }

                for (Linea linea: listaLineas ) {
                    System.out.println("lo que trae la lista del linea del servidor: " + linea.getDenominacion());
                }

                intent.putParcelableArrayListExtra("listaColectivos", (ArrayList<? extends Parcelable>) listaColectivos);
                intent.putParcelableArrayListExtra("listaLineas", (ArrayList<? extends Parcelable>) listaLineas);

//                HashSet<String> hs = new HashSet<String>();
//                        hs.addAll(opcionesLineas);
//                HashSet<String> hs2 = new HashSet<String>();
//                        hs2.addAll(opcionesColectivos);

//                Iterator<String> it = hs.iterator();
//                while (it.hasNext()) {
//                    System.out.println(it.next());
//                }
//                intent.putExtra("listaLineas", hs);
//                intent.putExtra("listaColectivos", hs2);


                startActivity(intent);
                finish();

            }
        };
        handler2.postDelayed(r2,4000);

    }


    @Override
    public void showUbicacion(String strLatitud, String strLongitud) {

    }

    @Override
    public void showResponseInicioServicioOk(String response, String seleccionLin, String seleccionCol, Long fechaUbicacionI) {

    }

    @Override
    public void showResponsePostSimulacionOk(String response, String seleccionLin, String seleccionCol, String latInicial, String lngInicial) {

    }

    @Override
    public void showResponse(String response) {

    }

    @Override
    public void showResponseError(String error) {

    }
}
