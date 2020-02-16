package com.devyk.audiovideo.audio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.devyk.audioplay.R;
import com.devyk.audiovideo.Utils;

public class AudioPlayActivity extends AppCompatActivity {



    /**
     * PCM 播放实例
     */
    private AudioTracker mAudioTracker;

    /**
     * 播放 path
     */
    private final String PATH = Environment.getExternalStorageDirectory() + "/_test.pcm";
    /**
     * 采集
     */
    private AudioRecordDemo mAudioRecordDemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_play);

        mAudioTracker = new AudioTracker(this);

        mAudioRecordDemo = new AudioRecordDemo();

        mAudioRecordDemo.setOnAudioFrameCapturedListener(new AudioRecordDemo.OnAudioFrameCapturedListener() {
            @Override
            public void onAudioFrameCaptured(byte[] audioData) {
                Utils.writePCM(audioData);
            }
        });
    }

    public void playPCM(View view) {

        mAudioTracker.createAudioTrack(PATH);

        mAudioTracker.start();


    }

    @Override
    protected void onStop() {
        super.onStop();
        mAudioTracker.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAudioTracker.release();
        nativeStopPcm();

    }

    public void stopRecord(View view) {
        mAudioRecordDemo.stopCapture();
    }

    public void playRecord(View view) {
        mAudioRecordDemo.startCapture();
    }

    public void OpenSL_Play_PCM(View view) {
        nativePlayPcm(PATH);
    }

    public void OpenSL_Stop_PCM(View view) {
        nativeStopPcm();
    }



    private native static void nativePlayPcm(String pcmPath);
    private native static void nativeStopPcm();
}
