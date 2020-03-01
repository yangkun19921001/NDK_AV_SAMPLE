package com.devyk.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.os.Build;
import android.os.PowerManager;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.devyk.audio.AudioUtils;
import com.devyk.camera.CameraData;
import com.devyk.camera.CameraHolder;
import com.devyk.camera.CameraListener;
import com.devyk.capture.CameraCapture;
import com.devyk.configuration.AudioConfiguration;
import com.devyk.configuration.CameraConfiguration;
import com.devyk.configuration.VideoConfiguration;
import com.devyk.constant.SopCastConstant;
import com.devyk.controller.StreamController;
import com.devyk.controller.audio.NormalAudioController;
import com.devyk.controller.video.CameraVideoController;
import com.devyk.entity.Watermark;
import com.devyk.mediacodec.AudioMediaCodec;
import com.devyk.mediacodec.MediaCodecHelper;
import com.devyk.mediacodec.VideoMediaCodec;
import com.devyk.pusher_common.AudioChannel;
import com.devyk.pusher_common.VideoChannel;
import com.devyk.stream.packer.Packer;
import com.devyk.stream.packer.rtmp.RtmpPacker;
import com.devyk.stream.sender.Sender;
import com.devyk.stream.sender.nativertmp.RtmpNativeSendr;
import com.devyk.utils.SopCastLog;
import com.devyk.utils.SopCastUtils;
import com.devyk.utils.WeakHandler;
import com.devyk.video.effect.Effect;

/**
 * @Title: CameraLivingView
 * @Package com.devyk.ui
 * @Description:
 * @Author Jim
 * @Date 16/9/18
 * @Time 下午5:41
 * @Version
 */
public class SoftLivingView extends CameraView implements Packer.OnVideoPacketListener, Packer.OnAudioPacketListener {


    private static final String TAG = SopCastConstant.TAG;
    private Context mContext;
    private PowerManager.WakeLock mWakeLock;
    private VideoConfiguration mVideoConfiguration = VideoConfiguration.createDefault();
    private AudioConfiguration mAudioConfiguration = AudioConfiguration.createDefault();
    private CameraListener mOutCameraOpenListener;
    private LivingStartListener mLivingStartListener;
    private WeakHandler mHandler = new WeakHandler();
    private RtmpNativeSendr mSender;
    private VideoChannel mVideoChannel;
    private AudioChannel mAudioChannel;

    public void start() {


        mVideoChannel = new VideoChannel(this);
        mAudioChannel = new AudioChannel(mAudioConfiguration, this);
        mVideoChannel.startLive();
        mAudioChannel.startLive();
        mLivingStartListener.startSuccess();
    }

    public void setSender(RtmpNativeSendr mRtmpSender) {
        mSender = mRtmpSender;
        mSender.setMediaCodec(false);

    }


    @Override
    public int getInputSamples() {
        return mSender.getInputSapmples();
    }

    @Override
    public void sendAudioInfo(int frequency, int channels) {
        mSender.setAudioEncInfo(frequency, channels);
        //链接成功，推送音视频配置信息
        mSender.setVideoEncInfo(mVideoConfiguration.width, mVideoConfiguration.height, mVideoConfiguration.fps
                , (mVideoConfiguration.minBps + mVideoConfiguration.maxBps) / 2);
    }


    public interface LivingStartListener {
        void startError(int error);

        void startSuccess();
    }

    public SoftLivingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initViews();
    }


    public SoftLivingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        initViews();
    }

    private void initViews() {
        mRenderer.setCameraOpenListener(mCameraOpenListener);
    }

    public SoftLivingView(Context context) {
        super(context);
        mContext = context;
    }


    @SuppressLint("InvalidWakeLockTag")
    public void init() {
        SopCastLog.d(TAG, "Version : " + SopCastConstant.VERSION);
        SopCastLog.d(TAG, "Branch : " + SopCastConstant.BRANCH);

        PowerManager mPowerManager = ((PowerManager) mContext.getSystemService(getContext().POWER_SERVICE));
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                PowerManager.ON_AFTER_RELEASE, TAG);


    }


    public void setLivingStartListener(LivingStartListener listener) {
        mLivingStartListener = listener;
    }

    public void setVideoConfiguration(VideoConfiguration videoConfiguration) {
        mVideoConfiguration = videoConfiguration;

    }

    public void setCameraConfiguration(CameraConfiguration cameraConfiguration) {
        CameraHolder.instance().setConfiguration(cameraConfiguration);
    }

    public void setAudioConfiguration(AudioConfiguration audioConfiguration) {
        mAudioConfiguration = audioConfiguration;

    }


    /**
     * 采集到的原始音视频数据
     *
     * @param data
     * @param packetType
     */
    @Override
    public void onPacket(byte[] data, int packetType) {
        if (mSender != null)
            mSender.onData(data, packetType);
    }

    public void stop() {
        if (mVideoChannel != null && mAudioChannel != null) {
            mVideoChannel.stopLive();
            mAudioChannel.stopLive();
        }
        screenOff();
        setAudioNormal();

        mSender.stop();
    }

    private void screenOn() {
        if (mWakeLock != null) {
            if (!mWakeLock.isHeld()) {
                mWakeLock.acquire();
            }
        }
    }

    private void screenOff() {
        if (mWakeLock != null) {
            if (mWakeLock.isHeld()) {
                mWakeLock.release();
            }
        }
    }

    public void pause() {
    }

    public void resume() {

    }


    public void setEffect(Effect effect) {
        mRenderSurfaceView.setEffect(effect);
    }

    public void setWatermark(Watermark watermark) {
        mRenderer.setWatermark(watermark);
    }


    private boolean isCameraOpen() {
        return mRenderer.isCameraOpen();
    }

    public void setCameraOpenListener(CameraListener cameraOpenListener) {
        mOutCameraOpenListener = cameraOpenListener;
    }

    public void switchCamera() {
        boolean change = CameraHolder.instance().switchCamera();
        if (change) {
            changeFocusModeUI();
            if (mOutCameraOpenListener != null) {
                mOutCameraOpenListener.onCameraChange();
            }
        }
    }

    public CameraData getCameraData() {
        return CameraHolder.instance().getCameraData();
    }

    public void switchFocusMode() {
        CameraHolder.instance().switchFocusMode();
        changeFocusModeUI();
    }

    public void switchTorch() {
        CameraHolder.instance().switchLight();
    }

    public void release() {
        screenOff();
        mWakeLock = null;
        CameraHolder.instance().releaseCamera();
        CameraHolder.instance().release();
        setAudioNormal();
        mSender.stop();
        if (mVideoChannel != null && mAudioChannel != null) {
            mVideoChannel.release();
            mAudioChannel.release();
        }
    }

    private CameraListener mCameraOpenListener = new CameraListener() {
        @Override
        public void onOpenSuccess() {
            changeFocusModeUI();
            if (mOutCameraOpenListener != null) {
                mOutCameraOpenListener.onOpenSuccess();
            }
        }

        @Override
        public void onOpenFail(int error) {
            if (mOutCameraOpenListener != null) {
                mOutCameraOpenListener.onOpenFail(error);
            }
        }

        @Override
        public void onCameraChange() {
            // Won't Happen
        }
    };

    private void setAudioNormal() {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(false);
    }


    /**
     * 设置预览方向
     */
    public void setPreviewOrientation(int rotation) {
        CameraHolder.instance().setPreviewOrientation(rotation);
    }
}
