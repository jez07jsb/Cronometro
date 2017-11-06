package com.evolutil.jez.cronometro;

import android.widget.TextView;

/**
 * <p style="font-family:'Lucida Sans';font-style:'italic';font-weight:'bold';color:green;">
 * Created by Jez on 03/01/2016.
 * </p>
 */
public interface InterfaceCronometro {
    void iniciar();
    void parar();
    boolean status();
    void colocarTempo(double tempo);
    double pegarTempo();
    void setTextView(TextView txv);
}
