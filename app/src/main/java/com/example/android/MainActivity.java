package com.example.android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private static String fileName = null;
    private RecordAudio recorder = null;
    public static boolean permissionToHttpRequestAccepted;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();

        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/android.wav";
        System.out.println(fileName + " - RUTA");
        recorder = new RecordAudio(fileName);

        Button principalButton = (Button) findViewById(R.id.button);
        principalButton.setOnClickListener(new View.OnClickListener() {
            boolean recording = true;

            @Override
            public void onClick(View v) {
                permissionToHttpRequestAccepted = false;
                recorder.record(recording, principalButton);
                recording = !recording;
                if (permissionToHttpRequestAccepted){
                    new HttpConnection(context, fileName).start();
                }
            }
        });

        Button buttonPlay = (Button) findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            boolean playingAudio = true;
            @Override
            public void onClick(View v) {
                recorder.play(playingAudio, buttonPlay);
                playingAudio = !playingAudio;
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder.getRecorder() != null) {
            recorder.getRecorder().release();
            recorder = null;
        }

        if (recorder.getPlayer() != null) {
            recorder.getPlayer().release();
            recorder.setPlayer(null);
        }
    }
}

