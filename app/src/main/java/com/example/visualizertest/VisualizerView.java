package com.example.visualizertest;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.Arrays;

public class VisualizerView extends View {
    private byte[] fftDaten;
    int samplingRate;
    private float[] punkteZumZeichnen;
    private Paint paint = new Paint();
    private Rect viewRect = new Rect();
    int[] visuAlteAmplituden = new int[4];


    private long aufrufZaehler = 0;
    private long lastUpdateTime = 0;






    //Wenn die View direkt im Java-Code erstellt wird
    public VisualizerView(Context context) {

        this(context, null); //leitet den Aufruf an den 2. Konstruktor weiter
    }

    //Wenn die View aus einer XML-Layout-Datei inflated wird; mit Attributen;
    public VisualizerView(Context context, AttributeSet attrs) {

        this(context, attrs, 0); //leitet den Aufruf an den 3. Konstruktor weiter
    }

    //Wenn die View aus einer XML-Layout-Datei inflated wird; mit Attributen; und Standard-Style-Attribut der View;
    public VisualizerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        fftDaten = null;

        paint.setStrokeWidth(3f);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
    }



    public void updateVisualizerFFT(byte[] fftDaten, int samplingRate) {
        this.samplingRate = samplingRate / 1000;
        this.fftDaten = fftDaten;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) { //Wird automatisch aufgerufen, wenn die View neu gezeichnet werden soll
        super.onDraw(canvas);


        if (fftDaten != null) {
            updateRateAusgeben();
            viewRect.set(0, 0, getWidth(), getHeight());
            int visuAnzahlBaender = 4;
            int visuBreiteProBand = viewRect.width() / visuAnzahlBaender;
            int[] visuAmplituden = new int[visuAnzahlBaender];
            int[] audioBandGrenzen = new int[] {0, 200, 800, 2000, 8000}; //Hz
            int visuDecay = 30;

            //Amplituden berechnen
            for (int i = 0; i < fftDaten.length / 2; i++) { //iteriere durchs fft-Array
                int re = i * 2;
                int im = i * 2 + 1;
                int frequenzAmIndex = (int) ((i + 1) * samplingRate / 2 / fftDaten.length);

                float amplitude = (float) Math.hypot(fftDaten[re], fftDaten[im]);
                int amplitudeNormalisiert = Math.min(viewRect.height(), (int) (amplitude * viewRect.height() / 255f));

                //Amplitude ins passende visuAmplituden-Array schreiben, wenn es die hoechste im Frequenzband ist
                for (int j = 0; j < visuAnzahlBaender; j++) { //iteriere durch die Visu-Bänder
                    if (frequenzAmIndex <= audioBandGrenzen[j + 1] && frequenzAmIndex >= audioBandGrenzen[j]) {
                        visuAmplituden[j] = Math.max(Math.max(visuAmplituden[j], amplitudeNormalisiert), visuAlteAmplituden[j]);
                    }
                }
            }

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);

            for (int i = 0; i < visuAnzahlBaender; i++) {
                int left = i * visuBreiteProBand;
                int top = viewRect.height() - visuAmplituden[i] ;
                int right = left + visuBreiteProBand;
                int bottom = viewRect.height();
                canvas.drawRect(left, top, right, bottom, paint);
            }

            for (int j = 0; j < visuAnzahlBaender; j++) { //iteriere durch die Visu-Bänder
                visuAlteAmplituden[j] = visuAmplituden[j] - visuDecay;
            }

        }
    }





    private void updateRateAusgeben() {

        aufrufZaehler++;
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUpdateTime >= 1000) { //nur 1x pro Sekunde berechnen und ausgeben
            // berechnen
            float aufrufeProSekunde = (float) aufrufZaehler / ((currentTime - lastUpdateTime) / 1000.0f);

            // Call counter zurücksetzen
            aufrufZaehler = 0;
            //lastUpdateTime aktualisieren
            lastUpdateTime = currentTime;

            System.out.println("Update rate: " + aufrufeProSekunde + " Aufrufe pro Sekunde");
        }
    }

    public void setColor(int color) {
        paint.setColor(color);
    }
}
