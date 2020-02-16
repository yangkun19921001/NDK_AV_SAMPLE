package com.devyk.audiovideo.video;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;


/**
 * <pre>
 *     author  : devyk on 2020-02-12 15:07
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is YUVPlay
 * </pre>
 */
public class YUVPlay extends GLSurfaceView implements SurfaceHolder.Callback {
    private Context context;

    private String TAG = getClass().getSimpleName();
    private String yuv420pPath;
    private Object surface;

    public YUVPlay(Context context) {
        super(context);
    }

    public YUVPlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context.getApplicationContext();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

    }

    public void glesPlay(final String yuv420pPath,final Object surface) {
        this.yuv420pPath = yuv420pPath;
        this.surface = surface;

        Thread thread = new Thread(playRunnable);
        thread.start();

    }

    public native void nativeGlesPlay(String yuv420pPath, Object surface);

    public native void nativeWindowPlay(String yuv420pPath, Object surface);

    /**
     * JNI 调用
     *
     * @param message
     */
    public void showMessage(final String message) {

        Log.d(TAG, message);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public native void onDestory();


    private Runnable playRunnable = new Runnable() {
        @Override
        public void run() {
            nativeGlesPlay(yuv420pPath, surface);
        }
    };
}
