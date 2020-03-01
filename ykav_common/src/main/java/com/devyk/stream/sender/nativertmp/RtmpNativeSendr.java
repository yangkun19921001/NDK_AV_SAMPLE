package com.devyk.stream.sender.nativertmp;

import android.util.Log;

import com.devyk.pusher_common.PusherManager;
import com.devyk.stream.packer.rtmp.RtmpPacker;
import com.devyk.stream.sender.Sender;
import com.devyk.utils.WeakHandler;

/**
 * <pre>
 *     author  : devyk on 2020-03-01 02:03
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is RtmpNativeSendr
 * </pre>
 */
public class RtmpNativeSendr implements Sender, PusherManager.IPushListener {

    private WeakHandler mHandler = new WeakHandler();

    /**
     * 推流管理
     */
    private final PusherManager mPusherManager;

    /**
     * 推流地址
     */
    private String mRtmpUrl;
    private OnSenderListener mListener;


    public RtmpNativeSendr() {
        mPusherManager = new PusherManager(null);
        mPusherManager.addPushListener(this);
    }



    /**
     * 设置 rtmp 地址
     *
     * @param url
     */
    public void setAddress(String url) {
        mRtmpUrl = url;
    }

    /**
     * 设置发送监听
     *
     * @param listener
     */
    public void setSenderListener(OnSenderListener listener) {
        mListener = listener;
    }

    public void connect() {
        mPusherManager.startLive(mRtmpUrl);
    }

    /**
     * 开始
     */
    @Override
    public void start() {

    }

    /**
     * 打包之后的数据
     *
     * @param data
     * @param type
     */
    @Override
    public void onData(byte[] data, int type) {
        if (type == RtmpPacker.FIRST_AUDIO || type == RtmpPacker.AUDIO) {//音频数据
            mPusherManager.pushAACData(data, data.length, type);
        } else if (type == RtmpPacker.FIRST_VIDEO ||
                type == RtmpPacker.INTER_FRAME || type == RtmpPacker.KEY_FRAME) {//视频数据
            mPusherManager.pushH264(data, type, 0);
        } else if (type == RtmpPacker.PCM) {
            mPusherManager.pushPCM(data);
        } else if (type == RtmpPacker.YUV) {
            mPusherManager.pushYUV(data);
        }
    }

    /**
     * 停止
     */
    @Override
    public void stop() {
        mPusherManager.release();
    }

    @Override
    public void onConnect() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null)
                    mListener.onConnecting();
            }
        });


    }

    @Override
    public void onConnectSucceed() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null)
                    mListener.onConnected();
            }
        });

    }

    @Override
    public void onError(final String eror) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null)
                    mListener.otherFail(eror);
            }
        });

    }

    public void setAudioEncInfo(int sampleRate, int channels) {
        mPusherManager.native_setAudioEncInfo(sampleRate, channels);
    }

    public void setVideoEncInfo(int w, int h, int fps, int bit) {
        mPusherManager.native_setVideoEncoderInfo(w, h, fps, bit);
    }

    public int getInputSapmples() {
        return mPusherManager.getInputSamples();
    }

    public void setMediaCodec(boolean b) {
        mPusherManager.setMediaCodec(b);
    }

    public interface OnSenderListener {
        void onConnecting();

        void onConnected();

        void onDisConnected();

        void onPublishFail();

        void otherFail(String error);
    }
}
