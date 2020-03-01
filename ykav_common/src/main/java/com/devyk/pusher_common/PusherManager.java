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

    private IPushListener iPushListener;

    public PusherManager(AudioVideoParameter parameter) {
        if (parameter == null) return;
        setMediaCodec(parameter.isMediaCodec);
        if (Constants.isMediaCodec) return;
    }

    public void switchCamera() {
    }

    public void startLive(String path) {
        native_init(Constants.isMediaCodec);
        native_start(path);
    }

    public void onRestart() {
        if (Constants.isMediaCodec) return;
    }


    public void stopLive() {
        if (Constants.isMediaCodec) return;
    }


    public void release() {
        native_release();
        if (Constants.isMediaCodec) return;
    }

    public void setVideoPreviewRotation(int rotaion) {
        if (Constants.isMediaCodec) return;
//        mVideoChannel.setVideoPreviewRotation(rotaion);
    }


    /**
     * 初始化  native
     */
    public native void native_init(boolean isMediaCodec);

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
     * 释放 native 资源
     */
    public native void native_release();

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
     * 发送 PCM 原始数据
     *
     * @param h264
     */
    public native void pushH264(byte[] h264, int type, long timeStamp);


    /**
     * 获取一次输入音频的样本数量
     *
     * @return
     */
    public native int getInputSamples();


    /**
     * @param audio     直接推编码完成之后的音频流
     * @param length
     * @param timestamp
     */
    public native void pushAACData(byte[] audio, int length, int timestamp);


    /**
     * 设置是否硬编码
     *
     * @param b
     */
    private native void native_mediacodec(boolean b);

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

    public boolean isMediaCodec() {
        return Constants.isMediaCodec;
    }

    public void setMediaCodec(boolean b) {
        Constants.isMediaCodec = b;
        native_mediacodec(b);
    }

    public void pushPCM(byte[] data) {
        native_pushAudio(data);
    }

    public void pushYUV(byte[] data) {
        native_push_video(data);
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
