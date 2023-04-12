package com.example.visualizertest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.Manifest;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Visualizer visualizer;
    VisualizerView visualizerView;
    private LinearLayout visualizerViewContainer;
    private Button playButton;
    private Button stopButton;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        visualizerViewContainer = findViewById(R.id.visualizer_view_container);
        playButton = findViewById(R.id.play_button);
        stopButton = findViewById(R.id.stop_button);

        requestPermissions();

        playButton.setOnClickListener(new View.OnClickListener() { //Hier wird ein Objekt einer anonymen Klasse erstellt, die View.OnClickListener implementiert.
            @Override
            public void onClick(View v) { //View v ist eine Referenz auf die View, die angeklickt wurde – in diesem Fall der playButton.
                if(!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    visualizer.setEnabled(true);
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    visualizer.setEnabled(false);
                }
            }
        });

    }
    private void requestPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //Falls SDK Android 6 oder neuer
            if( //Falls eine der beiden Permissions noch nicht vorliegt
                    ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED
                            ||
                            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
            } else {
                setupMediaPlayer();
                setupVisualizer();
            }

        } else {
            setupMediaPlayer();
            setupVisualizer();
        }
    }
    @Override
    public void onRequestPermissionsResult( //Wird aufgerufen, wenn der Benutzer auf die Permission Requests reagiert hat.
            int requestCode,
            @NonNull String[] permissions, //Angeforderte Berechtigungen
            @NonNull int[] grantResults) { //Erteilte Berechtigungen. Jeder Wert im Array entspricht dem Ergebnis für die entsprechende Berechtigung im permissions-Array.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                setupMediaPlayer();
                setupVisualizer();
            } else {
                // TODO: User ist nicht einverstanden.
            }
        }
    }
    private void setupMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.strike);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    private void setupVisualizer() {
        visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
        System.out.println("Laenge des byte-Arrays mit den zu visualisierenden Audiodaten, von...bis" + Arrays.toString(Visualizer.getCaptureSizeRange()));
        System.out.println("Maximale capture-Rate in Hertz (Hz): " + Visualizer.getMaxCaptureRate());
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

        visualizerView = new VisualizerView(this);
        visualizerView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        visualizerView.setColor(Color.WHITE);

        visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveformDaten, int samplingRate) { //wird aufgerufen, wenn neue Wellenform-Daten verfügbar sind.
                //Waveform: Amplitude der Audiosignale im Zeitbereich
                //visualizerView.updateVisualizerWaveform(waveformDaten);
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fftDaten, int samplingRate) {
                //Fast Fourier Transform: Frequenzkomponenten des Audiosignals im Frequenzbereich
                visualizerView.updateVisualizerFFT(fftDaten, samplingRate);
            }
        }, Visualizer.getMaxCaptureRate(), true, true); //Abtastrate in Hz

        visualizer.setEnabled(false);
        visualizerViewContainer.addView(visualizerView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            visualizer.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            visualizer.setEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (visualizer != null) {
            visualizer.release();
            visualizer = null;
        }
    }

}