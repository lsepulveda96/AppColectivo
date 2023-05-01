package com.stcu.appcolectivo.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gpmess.example.volley.app.R;
import com.stcu.appcolectivo.MainActivity;
import com.stcu.appcolectivo.interfaces.MainInterface;
import com.stcu.appcolectivo.presenter.MainPresenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        presenter = new MainPresenter(this, this, this);


        TimerTask tarea = new TimerTask() {
            @Override
            public void run() {

                final List<String> opcionesLineas = presenter.consultaLineasActivas();
                final List<String> opcionesColectivos = presenter.consultaColectivosActivos();

                HashSet<String> hashSetLineas = new HashSet<String>();
                hashSetLineas.addAll(opcionesLineas);

                HashSet<String> hashSetColectivos = new HashSet<String>();
                hashSetColectivos.addAll(opcionesColectivos);

//                ArrayList<String> strList = new ArrayList<String>(opcionesColectivos);

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                intent.putParcelableArrayListExtra("listaColectivos", (ArrayList<? extends Parcelable>) strList);
//                intent.putParcelableArrayListExtra("listaLineas", (ArrayList<? extends Parcelable>) Arrays.asList(opcionesLineas));

                intent.putExtra("hsLineas", hashSetLineas);
                intent.putExtra("hsColectivos", hashSetColectivos);
                startActivity(intent);
                finish();
            }
        };

        Timer tiempo = new Timer();
        //espera 5 segundos y pasa a la ventana principal
        tiempo.schedule(tarea, 6000);
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
