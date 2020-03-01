package com.devyk.pusher_common;

import android.app.Activity;
import android.hardware.Camera;
import android.util.Log;

import com.devyk.Constants;
import com.devyk.camera.CameraHolder;
import com.devyk.capture.CameraCapture;
import com.devyk.stream.packer.Packer;
import com.devyk.stream.packer.rtmp.RtmpPacker;

import java.lang.ref.WeakReference;

/**
 * <pre>
 *     author  : devyk on 2020-02-25 14:41
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is VideoChannel
 * </pre>
 */
public class VideoChannel extends BaseChannel implements IPush, Camera.PreviewCallback {

    private String TAG = this.getClass().getSimpleName();
    /**
     * 视频  fps
     */
    private boolean isLive;
    private Packer.OnPacketListener onPacketListener;


    public VideoChannel(Packer.OnPacketListener onPacketListener) {
        this.onPacketListener = onPacketListener;
        setPreviewCallback(this);
    }




    @Override
    public void startLive() {
        isLive = true;

    }

    @Override
    public void stopLive() {
        isLive = false;
    }

    @Override
    public void release() {
    }

    @Override
    public void onRestart() {
        isLive = true;

    }

    /**
     * 推视频流
     *
     * @param data
     */
    @Override
    public void push(byte[] data) {
        if (isLive && onPacketListener != null) {
            onPacketListener.onPacket(data, RtmpPacker.YUV);
        }
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        push(data);

    }


    public void switchCamera() {
    }

    /**
     * 设置当屏幕发生改变需要设置的监听
     *
     * @param listener
     */
    public void setOnChangedSizeListener(CameraCapture.OnChangedSizeListener listener) {
        CameraHolder.instance().setOnChangedSizeListener(listener);
    }

    /**
     * 设置预览回调，用于软编
     *
     * @param previewCallback
     */
    public void setPreviewCallback(Camera.PreviewCallback previewCallback) {
        CameraHolder.instance().setPreviewCallback(previewCallback);

    }

    /**
     * 设置预览方向
     */
    public void setPreviewOrientation(int rotation) {
        CameraHolder.instance().setPreviewOrientation(rotation);
    }
}
