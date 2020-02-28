package com.devyk.player_common.play;

import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.devyk.player_common.PlayerManager;
import com.devyk.player_common.callback.OnPreparedListener;
import com.devyk.player_common.callback.OnProgressListener;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 *     author  : devyk on 2020-01-19 09:35
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is YKPlayer
 * </pre>
 */
public class YKPlayer implements SurfaceHolder.Callback {
    private String TAG = this.getClass().getSimpleName();

    Executor executor;
    ExecutorService executorService;

    /**
     * 播放源（文件路径、直播源）
     */
    private String mDataSource;
    private SurfaceHolder mSurfaceHolder;
    private  PlayerManager mPlayerManager;


    public YKPlayer() {
        mPlayerManager = new PlayerManager();
        Log.d(TAG, "Player 空参");

    }


    /**
     * 获取 ffmpeg 版本
     */
    public String ffmpegVersion(){
        return mPlayerManager.getFFmpegVersion();
    }

    public ExecutorService newFixThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }


    /**
     * 设置播放源
     */
    public void setDataSource(String dataSource) {
        this.mDataSource = dataSource;
    }

    /**
     * 设置回调
     */
    public void setSurfaceView(SurfaceView surfaceView) {
        if (null != this.mSurfaceHolder) {
            this.mSurfaceHolder.removeCallback(this);
        }
        this.mSurfaceHolder = surfaceView.getHolder();
        this.mSurfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Surface surface = mSurfaceHolder.getSurface();
        mPlayerManager.setSurfaceNative(surface);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    /**
     * 准备工作(解封装 把压缩解压)
     */
    public void prepare() {
        mPlayerManager.prepareNative(mDataSource);
    }

    /**
     * 开始播放
     */
    public void start() {
        mPlayerManager.startNative();
    }


    /**
     * 恢复
     */
    public void onRestart() {
        mPlayerManager.restartNative();
    }

    /**
     * 停止播放
     */
    public void stop() {
        mPlayerManager.stopNative();
    }

    /**
     * 资源释放
     */
    public void release() {
        mPlayerManager.releaseNative();
    }

    /**
     * 是否正在播放
     */
    public boolean isPlayer() {
        return mPlayerManager.isPlayerNative();
    }


    /**
     * 设置播放回调
     */
    public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        mPlayerManager.setOnPreparedListener(onPreparedListener);
    }


    /**
     * 设置播放进度回调
     */
    public void setOnProgressListener(OnProgressListener onProgressListener) {
        mPlayerManager.setOnProgressListener(onProgressListener);
    }


    public int getDuration() {
        return mPlayerManager.native_GetDuration();
    }

    public void seek(final int progress) {

        newFixThreadPool(5).submit(new Runnable() {
            @Override
            public void run() {
                mPlayerManager.native_seek(progress);
            }
        });
    }
}
