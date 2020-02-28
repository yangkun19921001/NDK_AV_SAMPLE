package com.devyk.pusher_common;

import android.app.Activity;
import android.hardware.Camera;
import android.util.Log;

import com.devyk.capture.CameraCapture;

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
public class VideoChannel extends BaseChannel implements IPush, Camera.PreviewCallback, CameraCapture.OnChangedSizeListener {

    private String TAG = this.getClass().getSimpleName();

    /**
     * 管理 native 方法的
     */
    private PusherManager mPushManager;

    /**
     * 管理视频预览采集的
     */
    private CameraCapture mCameraCapture;
    /**
     * 视频码率
     */
    private int mBit;

    /**
     * 视频  fps
     */
    private int mFps;

    /**
     * 视频  fps
     */
    private boolean isLive;


    /**
     * @param parameter 视频参数和关键类
     */
    public VideoChannel(AudioVideoParameter parameter, PusherManager pusherManager) {
        if (parameter == null || pusherManager == null || parameter.surfaceHolder == null) {
            Log.e(TAG, "check parameter is init ?");
            return;
        }
        this.mBit = parameter.bitrate;
        this.mCameraCapture = new CameraCapture(parameter.rotation, parameter.cameraId, parameter.width, parameter.height);
        this.mPushManager = pusherManager;
        this.mFps = parameter.fps;

        //设置预览回调
        mCameraCapture.setPreviewCallback(this);
        //设置预览窗口改变回调
        mCameraCapture.setOnChangedSizeListener(this);
        //设置预览显示 Holder
        mCameraCapture.setPreviewDisplay(parameter.surfaceHolder);


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
        if (mCameraCapture != null) mCameraCapture.release();
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
        if (isLive && mPushManager != null)
            mPushManager.native_push_video(data);
    }

    @Override
    public void onChanged(int w, int h) {
        if (mPushManager != null)
            mPushManager.native_setVideoEncoderInfo(w, h, mFps, mBit);

    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        push(data);

    }


    public void switchCamera() {
        if (mCameraCapture != null)
            mCameraCapture.switchCamera();
    }


    public void setVideoPreviewRotation(int rotaion) {
        if (mCameraCapture != null)
            mCameraCapture.setPreviewOrientation(rotaion);

    }
}
