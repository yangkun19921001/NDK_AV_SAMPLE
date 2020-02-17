package com.devyk.player_common;

import android.content.Context;
import android.util.Log;
import android.view.Surface;

import com.devyk.player_common.callback.OnPreparedListener;
import com.devyk.player_common.callback.OnProgressListener;

import java.io.File;

import static android.content.ContentValues.TAG;
import static com.devyk.player_common.Constants.JavaPath;
import static com.devyk.player_common.Constants.nativePath;

/**
 * <pre>
 *     author  : devyk on 2020-01-18 22:39
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is PlayerManager
 * </pre>
 */
public class PlayerManager {
    static {
        System.loadLibrary("YK_PLAYER");
    }

    private static PlayerManager instance;

    public static PlayerManager getInstance() {
        if (instance == null)
            instance = new PlayerManager();
        return instance;
    }


    public PlayerManager(){


    }

    private OnPreparedListener mOnPreparedListener;

    private OnProgressListener onProgressListener;

    /**
     * 当前 ffmpeg 版本
     */
    public native String getFFmpegVersion();

    /**
     * 设置 surface
     * @param surface
     */
    public native void setSurfaceNative(Surface surface);

    /**
     * 做一些准备工作
     * @param mDataSource 播放气质
     */
    public native void prepareNative(String mDataSource);

    /**
     * 准备工作完成，开始播放
     */
    public native void startNative();

    /**
     * 如果点击停止播放，那么就调用该函数进行恢复播放
     */
    public native void restartNative();

    /**
     * 停止播放
     */
    public native void stopNative();

    /**
     * 释放资源
     */
    public native void releaseNative();

    /**
     * 是否正在播放
     * @return
     */
    public native boolean isPlayerNative();

    /**
     * 获取播放的 时间
     * @return
     */
    public native int native_GetDuration();

    /**
     * 拖动播放
     * @param progress
     */
    public native void native_seek(int progress);


    /**
     * 给 JNI 方法调用
     */
    public void onPrepared() {
        if (null != mOnPreparedListener)
            mOnPreparedListener.onPrepared();
    }

    /**
     * native 回调给java 播放进去的
     * @param progress
     */
    public void onProgress(int progress) {
        if (null != onProgressListener) {
            onProgressListener.onProgress(progress);
        }
    }

    /**
     * 播放错误回调
     */
    public void onError(int errorCode) {
        if (null == mOnPreparedListener) return;
        String errorText = null;
        switch (errorCode) {
            case Constants.PlayFlags.FFMPEG_ALLOC_CODEC_CONTEXT_FAIL:
                errorText = "无法根据解码器创建上下文";
                break;
            case Constants.PlayFlags.FFMPEG_CAN_NOT_FIND_STREAMS:
                errorText = "找不到媒体流信息";
                break;
            case Constants.PlayFlags.FFMPEG_CAN_NOT_OPEN_URL:
                errorText = "打不开媒体数据源";
                break;
            case Constants.PlayFlags.FFMPEG_CODEC_CONTEXT_PARAMETERS_FAIL:
                errorText = "根据流信息 配置上下文参数失败";
                break;
            case Constants.PlayFlags.FFMPEG_FIND_DECODER_FAIL:
                errorText = "找不到解码器";
                break;
            case Constants.PlayFlags.FFMPEG_NOMEDIA:
                errorText = "没有音视频";
                break;
            case Constants.PlayFlags.FFMPEG_READ_PACKETS_FAIL:
                errorText = "读取媒体数据包失败";
                break;
            default:
                errorText = "未知错误，自己去检测你的垃圾代码...";
                break;
        }
        this.mOnPreparedListener.onError(errorText);

    }

    /**
     * 设置播放回调
     */
    public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        mOnPreparedListener = onPreparedListener;
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }


    public void onRelease() {
        if (mOnPreparedListener != null)
            mOnPreparedListener = null;
    }


}

