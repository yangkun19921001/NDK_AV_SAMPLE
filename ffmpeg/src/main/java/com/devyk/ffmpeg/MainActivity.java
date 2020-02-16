package com.devyk.ffmpeg;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("ffmpeg_lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView ffmpegVer = findViewById(R.id.ffmpeg_ver);
        ffmpegVer.setText("当前 FFmpeg 版本为:" + getFFmpegVersion());
    }


    /**
     * @return 返回当前
     */
    public native static String getFFmpegVersion();
}
