package com.evolutil.jez.cronometro;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ActMain extends AppCompatActivity {

    private static final String LOG_TAG = "ACT_CRONOMETRO";

    private InterfaceCronometro interfaceCronometro;

    private ServiceConnection conexaoCronometro = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ServiceCronometro.ServiceCronometroBinder serviceCronometroBinder = (ServiceCronometro.ServiceCronometroBinder) service;
            interfaceCronometro = serviceCronometroBinder.getInterfaceCronometro();
            interfaceCronometro.setTextView(txv);
            String txt;
            if (interfaceCronometro.status()) {
                txt = "Parar";
            } else {
                txt = "Iniciar";
                if (interfaceCronometro.pegarTempo() > 0) {
                    button_zero.setVisibility(View.VISIBLE);
                }
            }
            button.setText(txt);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            interfaceCronometro = null;
        }
    };

    private Intent intentServiceCronometro;

    private AppCompatButton button, button_zero;

    private TextView txv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        txv = (TextView) findViewById(R.id.textView);

        button = (AppCompatButton) findViewById(R.id.button);

        button_zero = (AppCompatButton) findViewById(R.id.button_zero);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interfaceCronometro != null) {
                    if (interfaceCronometro.status()) {
                        interfaceCronometro.parar();
                        button.setText(R.string.iniciar);
                        button_zero.setVisibility(View.VISIBLE);
                    } else {
                        interfaceCronometro.iniciar();
                        button.setText(R.string.parar);
                        button_zero.setVisibility(View.GONE);
                    }
                }
            }
        });

        button_zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp = "00:00:00.000";
                if (interfaceCronometro != null) {
                    interfaceCronometro.colocarTempo(0);
                    txv.setText(tmp);
                    v.setVisibility(View.GONE);
                }
            }
        });

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeAsUpIndicator(R.drawable.ic_cronometro);
        }

        intentServiceCronometro = new Intent(ActMain.this, ServiceCronometro.class);

        startService(intentServiceCronometro);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean bindStatus = bindService(intentServiceCronometro, conexaoCronometro, Service.BIND_AUTO_CREATE);
        if (bindStatus) {
            Log.i(LOG_TAG, "bindService() - Sucesso ao conectar a Activity ao Service");
        }
    }

    @Override
    protected void onPause() {
        if (interfaceCronometro != null) {
            double tempo = interfaceCronometro.pegarTempo();
            Log.i(LOG_TAG, "onPause() - Executado - Tempo em ServiceCronometro:" + tempo);
            unbindService(conexaoCronometro);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        stopService(intentServiceCronometro);
        super.onDestroy();
    }
}
