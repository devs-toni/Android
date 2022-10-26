package com.example.android;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Button;

import java.io.IOException;

public class RecordAudio {

    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    private static final String LOG_TAG = "AudioRecordTest";
    private String filename = "";

    public RecordAudio (String filename){
        this.filename = filename;
    }

    void play(boolean mStartPlaying, Button buttonPlay) {
        onPlay(mStartPlaying);
        if (mStartPlaying) {
            buttonPlay.setText("Stop");
        } else {
            buttonPlay.setText("Play");
        }
    }

    void record(boolean mStartRecording, Button buttonRecord){
        onRecord(mStartRecording);
        if (mStartRecording) {
            buttonRecord.setText("Stop");
        } else {
            buttonRecord.setText("RECORD");
        }
    }

    void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(filename);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    void stopPlaying() {
        player.release();
        player = null;
    }

    void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(filename);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        MainActivity.permissionToHttpRequestAccepted = true;
    }

    public MediaRecorder getRecorder() {
        return recorder;
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public void setPlayer(MediaPlayer player) {
        this.player = player;
    }
}
