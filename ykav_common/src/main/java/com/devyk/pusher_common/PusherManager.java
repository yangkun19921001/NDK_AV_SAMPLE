package com.devyk.pusher_common;

import android.app.Activity;
import android.os.Handler;
import android.widget.Toast;

import com.devyk.Constants;
import com.devyk.player_common.PlayerManager;

import java.lang.reflect.Parameter;

/**
 * <pre>
 *     author  : devyk on 2020-02-25 14:01
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is PusherManager 负责推流管理类
 * </pre>
 */
public class PusherManager {

    static {
        System.loadLibrary("ykpusher"); //加载推流 so
    }

    private AudioChannel mAudioChannel;
    private VideoChannel mVideoChannel;
    private IPushListener iPushListener;

    public PusherManager(AudioVideoParameter parameter) {
        native_init();
        this.mAudioChannel = new AudioChannel(this);
        this.mVideoChannel = new VideoChannel(parameter, this);
    }

    public void switchCamera() {
        mVideoChannel.switchCamera();
    }

    public void startLive(String path) {
        native_start(path);
        mVideoChannel.startLive();
        mAudioChannel.startLive();
    }

    public void onRestart() {
        native_restart();
        mVideoChannel.onRestart();
        mAudioChannel.onRestart();
    }




    public void stopLive() {
        mVideoChannel.stopLive();
        mAudioChannel.stopLive();
        native_stop();
    }


    public void release() {
        mVideoChannel.release();
        mAudioChannel.release();
        native_release();
    }

    public void setVideoPreviewRotation(int rotaion) {
        mVideoChannel.setVideoPreviewRotation(rotaion);
    }

    /**
     * 重新开始播放
     */
    public native void native_restart();

    /**
     * 初始化  native
     */
    public native void native_init();

    /**
     * push 视频原始 nv21
     *
     * @param data
     */
    public native void native_push_video(byte[] data);

    /**
     * 设置编码信息
     *
     * @param w
     * @param h
     * @param mFps
     * @param mBit
     */
    public native void native_setVideoEncoderInfo(int w, int h, int mFps, int mBit);

    /**
     * 推流地址
     *
     * @param path
     */
    public native void native_start(String path);

    /**
     * 恢复播放
     */
    public native void onResume();


    /**
     * 停止推流
     */
    private native void native_stop();

    /**
     * 释放 native 资源
     */
    private native void native_release();

    /**
     * 设置语音参数
     *
     * @param sampleRate
     * @param channels
     */
    public native void native_setAudioEncInfo(int sampleRate, int channels);

    /**
     * 发送 PCM 原始数据
     *
     * @param audioData
     */
    public native void native_pushAudio(byte[] audioData);


    /**
     * 获取一次输入音频的样本数量
     *
     * @return
     */
    public native int getInputSamples();


    /**
     * 当 rtmp 在 native 开始连接的时候会回调
     */
    public void onRtmpConnect() {
        if (iPushListener != null) {
            iPushListener.onConnect();
        }
    }

    /**
     * 当 rtmp 在 native 连接成功的时候会回调
     */
    public void onRtmpSucceed() {
        if (iPushListener != null) {
            iPushListener.onConnectSucceed();
        }

    }

    /**
     * 当 rtmp 在 native 连接失败或其它异常的时候会回调
     */
    public void onError(int errCode) {
        if (iPushListener != null) {
            if (Constants.IMessageType.RTMP_INIT_ERROR == errCode) {
                iPushListener.onError("RTMP 模块初始化失败了，请联系管理员!");

            } else if (Constants.IMessageType.RTMP_CONNECT_ERROR == errCode) {
                iPushListener.onError("连接服务器失败，请联系管理员!");
            } else if (Constants.IMessageType.RTMP_SET_URL_ERROR == errCode) {
                iPushListener.onError("请检查 url 地址.");
            } else {
                iPushListener.onError("未知错误!");
            }

        }
    }




    public interface IPushListener {
        void onConnect();

        void onConnectSucceed();

        void onError(String eror);
    }

    public void addPushListener(IPushListener iPushListener) {
        this.iPushListener = iPushListener;
    }

}
