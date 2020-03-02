package com.devyk.ykav_sample;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.devyk.camera.CameraHolder;
import com.devyk.camera.CameraListener;
import com.devyk.configuration.AudioConfiguration;
import com.devyk.configuration.CameraConfiguration;
import com.devyk.configuration.VideoConfiguration;
import com.devyk.entity.Watermark;
import com.devyk.entity.WatermarkPosition;
import com.devyk.pusher_common.PusherManager;
import com.devyk.stream.sender.nativertmp.RtmpNativeSendr;
import com.devyk.ui.CameraLivingView;
import com.devyk.ui.SoftLivingView;
import com.devyk.utils.SopCastLog;
import com.devyk.video.effect.GrayEffect;
import com.devyk.video.effect.NullEffect;
import com.devyk.ykav_sample.view.MultiToggleImageButton;

/**
 * <pre>
 *     author  : devyk on 2020-03-01 17:28
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AVSoftPushActivity
 * </pre>
 */
public class AVSoftPushActivity extends Activity {

    private ProgressBar mProgressConnecting;
    private MultiToggleImageButton mMicBtn, mFlashBtn, mFaceBtn, mBeautyBtn, mFocusBtn;
    private ImageButton mRecordBtn;
    private GestureDetector mGestureDetector;
    private SoftLivingView mLFLiveView;

    private RtmpNativeSendr mRtmpSender;
    private VideoConfiguration mVideoConfiguration;

    private GrayEffect mGrayEffect;
    private NullEffect mNullEffect;

    private Dialog mUploadDialog;
    private EditText mAddressET;

    private boolean isGray;
    private boolean isRecording;

    private boolean isLiveing;

    private int bitrate = 500;
    private int fps = 20;
    private int height = 720;
    private int width = 1280;
    private SurfaceView sur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soft_pusher);
        initEffects();
        initViews();
        initListeners();
        initLiveView();
        initRtmpAddressDialog();
    }


    private void initEffects() {
        mGrayEffect = new GrayEffect(this);
        mNullEffect = new NullEffect(this);
    }

    private void initViews() {
        mLFLiveView = (SoftLivingView) findViewById(R.id.liveView);
        mMicBtn = (MultiToggleImageButton) findViewById(R.id.record_mic_button);
        mFlashBtn = (MultiToggleImageButton) findViewById(R.id.camera_flash_button);
        mFaceBtn = (MultiToggleImageButton) findViewById(R.id.camera_switch_button);
        mBeautyBtn = (MultiToggleImageButton) findViewById(R.id.camera_render_button);
        mFocusBtn = (MultiToggleImageButton) findViewById(R.id.camera_focus_button);
        mRecordBtn = (ImageButton) findViewById(R.id.btnRecord);
        mProgressConnecting = (ProgressBar) findViewById(R.id.progressConnecting);
        mLFLiveView.setVisibility(View.VISIBLE);
    }


    private void initListeners() {
        mMicBtn.setOnStateChangeListener(new MultiToggleImageButton.OnStateChangeListener() {
            @Override
            public void stateChanged(View view, int state) {
//                mLFLiveView.mute(true);
            }
        });
        mFlashBtn.setOnStateChangeListener(new MultiToggleImageButton.OnStateChangeListener() {
            @Override
            public void stateChanged(View view, int state) {
                mLFLiveView.switchTorch();
            }
        });
        mFaceBtn.setOnStateChangeListener(new MultiToggleImageButton.OnStateChangeListener() {
            @Override
            public void stateChanged(View view, int state) {
                mLFLiveView.switchCamera();
            }
        });
        mBeautyBtn.setOnStateChangeListener(new MultiToggleImageButton.OnStateChangeListener() {
            @Override
            public void stateChanged(View view, int state) {
                if (isGray) {
                    mLFLiveView.setEffect(mNullEffect);
                    isGray = false;
                } else {
                    mLFLiveView.setEffect(mGrayEffect);
                    isGray = true;
                }
            }
        });
        mFocusBtn.setOnStateChangeListener(new MultiToggleImageButton.OnStateChangeListener() {
            @Override
            public void stateChanged(View view, int state) {
                mLFLiveView.switchFocusMode();
            }
        });
        mRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    mProgressConnecting.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "stop living", Toast.LENGTH_SHORT).show();
                    mRecordBtn.setBackgroundResource(R.mipmap.ic_record_start);
                    mLFLiveView.stop();
                    isRecording = false;
                } else {
                    mUploadDialog.show();
                }
            }
        });

        //设置预览监听
        mLFLiveView.setCameraOpenListener(new CameraListener() {
            @Override
            public void onOpenSuccess() {
                Toast.makeText(getApplicationContext(), "camera open success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onOpenFail(int error) {
                Toast.makeText(getApplicationContext(), "camera open fail", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCameraChange() {
                Toast.makeText(getApplicationContext(), "camera switch", Toast.LENGTH_LONG).show();
            }
        });


        mLFLiveView.setLivingStartListener(new SoftLivingView.LivingStartListener() {
            @Override
            public void startError(int error) {
                //直播失败
                Toast.makeText(getApplicationContext(), "start living fail", Toast.LENGTH_SHORT).show();
                mLFLiveView.stop();
            }

            @Override
            public void startSuccess() {
                //直播成功
                Toast.makeText(getApplicationContext(), "start living", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initLiveView() {
        SopCastLog.isOpen(true);
        mLFLiveView.init();
        CameraConfiguration.Builder cameraBuilder = new CameraConfiguration.Builder();
        cameraBuilder.setOrientation(CameraConfiguration.Orientation.LANDSCAPE)
                .setFacing(CameraConfiguration.Facing.BACK);
        CameraConfiguration cameraConfiguration = cameraBuilder.build();
        mLFLiveView.setCameraConfiguration(cameraConfiguration);
        //语音参数配置
        setAudioInfo();
        //视频参数配置
        setVideoInfo();
        //设置水印
        setWatermark();
        //设置手势识别
        setGestureDetector();
        //设置发送器
        setNativeRtmpPush();


    }

    private void initRtmpAddressDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View playView = inflater.inflate(R.layout.address_dialog, (ViewGroup) findViewById(R.id.dialog));
        mAddressET = (EditText) playView.findViewById(R.id.address);
        mAddressET.setText("rtmp://49.235.159.44:1992/devykLive/live1");
        Button okBtn = (Button) playView.findViewById(R.id.ok);
        Button cancelBtn = (Button) playView.findViewById(R.id.cancel);
        AlertDialog.Builder uploadBuilder = new AlertDialog.Builder(this);
        uploadBuilder.setTitle("输入推流地址");
        uploadBuilder.setView(playView);

        mUploadDialog = uploadBuilder.create();
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uploadUrl = mAddressET.getText().toString();
                if (TextUtils.isEmpty(uploadUrl)) {
                    Toast.makeText(getApplicationContext(), "Upload address is empty!", Toast.LENGTH_SHORT).show();
                } else {
                    //设置 rtmp 地址
                    mRtmpSender.setAddress(uploadUrl);
                    //开始连接
                    mRtmpSender.connect();
                }
                mUploadDialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUploadDialog.dismiss();
            }
        });
    }

    /**
     * 设置视频编码信息
     */
    private void setVideoInfo() {
        mVideoConfiguration = new VideoConfiguration.Builder()
                .setSize(1280, 720)
                .setBps(300, 500)
                .setFps(20)
                .setMediaCodec(false)
                .build();
        mLFLiveView.setVideoConfiguration(mVideoConfiguration);
    }

    /**
     * 设置语音编码信息
     */
    private void setAudioInfo() {

        AudioConfiguration mAudioConfig = new AudioConfiguration.Builder().setMediaCodec(false).build();

        mLFLiveView.setAudioConfiguration(mAudioConfig);
    }


    private void setWatermark() {
        Bitmap watermarkImg = BitmapFactory.decodeResource(getResources(), R.mipmap.devyk);
        Watermark watermark = new Watermark(watermarkImg, 50, 25, WatermarkPosition.WATERMARK_ORIENTATION_BOTTOM_RIGHT, 8, 8);
        mLFLiveView.setWatermark(watermark);
    }

    private void setGestureDetector() {
        mGestureDetector = new GestureDetector(this, new GestureListener());
        mLFLiveView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return false;
            }
        });
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > 100
                    && Math.abs(velocityX) > 200) {
                // Fling left
                Toast.makeText(getApplicationContext(), "Fling Left", Toast.LENGTH_SHORT).show();
            } else if (e2.getX() - e1.getX() > 100
                    && Math.abs(velocityX) > 200) {
                // Fling right
                Toast.makeText(getApplicationContext(), "Fling Right", Toast.LENGTH_SHORT).show();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    private RtmpNativeSendr.OnSenderListener mSenderListener = new RtmpNativeSendr.OnSenderListener() {

        /**
         * 开始链接
         */
        @Override
        public void onConnecting() {
            mProgressConnecting.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "start connecting", Toast.LENGTH_SHORT).show();
            mRecordBtn.setBackgroundResource(R.mipmap.ic_record_stop);
            isRecording = true;
        }

        /**
         * 链接成功
         */
        @Override
        public void onConnected() {
            mProgressConnecting.setVisibility(View.GONE);
            mLFLiveView.start();


        }

        /**
         * 取消链接
         */
        @Override
        public void onDisConnected() {
            mProgressConnecting.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "fail to live", Toast.LENGTH_SHORT).show();
            mRecordBtn.setBackgroundResource(R.mipmap.ic_record_start);
            mLFLiveView.stop();
            isRecording = false;
        }

        /**
         * 推送失败
         */
        @Override
        public void onPublishFail() {
            mProgressConnecting.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "fail to publish stream", Toast.LENGTH_SHORT).show();
            mRecordBtn.setBackgroundResource(R.mipmap.ic_record_start);
            isRecording = false;
        }

        /**
         * rtmp 其它处理失败
         * @param error
         */
        @Override
        public void otherFail(String error) {
            mProgressConnecting.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "fail to publish stream：" + error, Toast.LENGTH_SHORT).show();
            mRecordBtn.setBackgroundResource(R.mipmap.ic_record_start);
            isRecording = false;
        }
    };

    /**
     * 此为扩展函数
     */
    private void setNativeRtmpPush() {
        mRtmpSender = new RtmpNativeSendr();
        mRtmpSender.setSenderListener(mSenderListener);
        mLFLiveView.setSender(mRtmpSender);
    }


    @Override
    protected void onStop() {
        super.onStop();
        mLFLiveView.pause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLFLiveView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLFLiveView.stop();
        mLFLiveView.release();
    }
}
