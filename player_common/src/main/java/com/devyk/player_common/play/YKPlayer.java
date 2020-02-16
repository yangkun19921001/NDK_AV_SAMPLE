package com.devyk.player_common.play;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.devyk.player_common.Constants;
import com.devyk.player_common.PlayerManager;
import com.devyk.player_common.callback.OnPreparedListener;

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

    /**
     * 播放源（文件路径、直播源）
     */
    private String mDataSource;
    private SurfaceHolder mSurfaceHolder;


    public YKPlayer() {
        Log.d(TAG, "Player 空参");
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
        PlayerManager.getInstance().setSurfaceNative(surface);
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
        PlayerManager.getInstance().prepareNative(mDataSource);
    }

    /**
     * 开始播放
     */
    public void start() {
        PlayerManager.getInstance().startNative();
    }


    /**
     * 恢复
     */
    public void onRestart() {
        PlayerManager.getInstance().restartNative();
    }

    /**
     * 停止播放
     */
    public void stop() {
        PlayerManager.getInstance().stopNative();
    }

    /**
     * 资源释放
     */
    public void release() {
        PlayerManager.getInstance().releaseNative();
    }

    /**
     * 是否正在播放
     */
    public boolean isPlayer() {
        return PlayerManager.getInstance().isPlayerNative();
    }


}
