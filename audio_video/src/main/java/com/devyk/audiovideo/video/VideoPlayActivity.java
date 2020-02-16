package com.devyk.audiovideo.video;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.devyk.audioplay.R;
import com.devyk.audiovideo.SuUtil;

import java.util.List;

/**
 * <pre>
 *     author  : devyk on 2020-02-12 13:40
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is VideoPlayActivity
 * </pre>
 */
public class VideoPlayActivity extends Activity {
    private YUVPlay mYUVPlay;
    /**
     * 播放 path
     */
    private final String PATH = Environment.getExternalStorageDirectory() + "/yuvtest.yuv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        mYUVPlay = findViewById(R.id.surface);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mYUVPlay.onDestory();
        mYUVPlay = null;
    }

    /**
     * OpenGL ES 播放 YUV
     *
     * @param view
     */
    public void gles_play(View view) {
        mYUVPlay.glesPlay(PATH, mYUVPlay.getHolder().getSurface());
    }

    /**
     * nativeWindow 播放 YUV
     *
     * @param view
     */
    public void native_window_play(View view) {
        mYUVPlay.nativeWindowPlay(PATH, mYUVPlay.getHolder().getSurface());
    }

}
