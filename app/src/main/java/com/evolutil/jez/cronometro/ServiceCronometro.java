package com.evolutil.jez.cronometro;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <p style="font-family:'Lucida Sans';font-style:'italic';font-weight:'bold';color:green;">
 * Created by Jez on 03/01/2016.
 * </p>
 */
public class ServiceCronometro extends Service implements InterfaceCronometro {

    private static final String TAG = "SVC_CRONOMETRO";
    private SimpleDateFormat sdf;
    private boolean status;
    private double tempo;
    private TextView txv;
    private Runnable r;
    private Handler h;

    public class ServiceCronometroBinder extends Binder {
        public InterfaceCronometro getInterfaceCronometro() {
            return ServiceCronometro.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceCronometroBinder();
    }

    @Override
    public void onCreate() {
        sdf = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        h = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                if (status) {
                    tempo+=31;
                    String contagem_legivel = sdf.format((long) tempo);
                    txv.setText(contagem_legivel);

                    h.postDelayed(r, 31);
                }
            }
        };
        Log.i(TAG, "onCreate() - Executado");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        parar();
        Log.i(TAG, "onDestroy() - Executado");
        super.onDestroy();
    }

    /* Metodos da interface de Cronometro */

    @Override
    public void iniciar() {
        if (txv == null) return;
        h.post(r);
        status = true;
        Log.i(TAG, "iniciar() - Executado");
    }

    @Override
    public void parar() {
        status = false;
        h.removeCallbacks(r);
        Log.i(TAG, "parar() - Executado");
    }

    @Override
    public boolean status() {
        return status;
    }

    @Override
    public void colocarTempo(double tempo) {
        this.tempo = tempo;
    }

    @Override
    public double pegarTempo() {
        return tempo;
    }

    @Override
    public void setTextView(TextView txv) {
        this.txv = txv;
    }
}
