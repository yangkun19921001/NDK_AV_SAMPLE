package com.devyk.ykav_sample;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import com.devyk.Constants;
import com.devyk.pusher_common.AudioVideoParameter;
import com.devyk.pusher_common.PusherManager;
import com.devyk.ykav_sample.view.MultiToggleImageButton;

/**
 * <pre>
 *     author  : devyk on 2020-02-25 14:05
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is PusherActivity
 * </pre>
 */
public class PusherActivity extends Activity {

    private ProgressBar mProgressConnecting;
    private MultiToggleImageButton mMicBtn, mFlashBtn, mFaceBtn, mBeautyBtn, mFocusBtn;
    private ImageButton mRecordBtn;


    private PusherManager mPusherManager;
    private SurfaceView surface;

    private boolean isLiveing;

    private int bitrate = 500;
    private int fps = 20;
    private int height = 720;
    private int width = 1280;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pusher);
        surface = findViewById(R.id.surface);
        initPush();
        initViews();
        initListeners();
    }


    private void initViews() {
        mMicBtn = (MultiToggleImageButton) findViewById(R.id.record_mic_button);
        mFlashBtn = (MultiToggleImageButton) findViewById(R.id.camera_flash_button);
        mFaceBtn = (MultiToggleImageButton) findViewById(R.id.camera_switch_button);
        mBeautyBtn = (MultiToggleImageButton) findViewById(R.id.camera_render_button);
        mFocusBtn = (MultiToggleImageButton) findViewById(R.id.camera_focus_button);
        mRecordBtn = (ImageButton) findViewById(R.id.btnRecord);
        mProgressConnecting = (ProgressBar) findViewById(R.id.progressConnecting);
    }


    private void initListeners() {
        mMicBtn.setOnStateChangeListener(new MultiToggleImageButton.OnStateChangeListener() {
            @Override
            public void stateChanged(View view, int state) {
            }
        });
        mFlashBtn.setOnStateChangeListener(new MultiToggleImageButton.OnStateChangeListener() {
            @Override
            public void stateChanged(View view, int state) {
            }
        });
        mFaceBtn.setOnStateChangeListener(new MultiToggleImageButton.OnStateChangeListener() {
            @Override
            public void stateChanged(View view, int state) {
                switchCamera();
            }
        });
        mBeautyBtn.setOnStateChangeListener(new MultiToggleImageButton.OnStateChangeListener() {
            @Override
            public void stateChanged(View view, int state) {

            }
        });
        mFocusBtn.setOnStateChangeListener(new MultiToggleImageButton.OnStateChangeListener() {
            @Override
            public void stateChanged(View view, int state) {
            }
        });
        mRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLiveing) {
                    mProgressConnecting.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "stop living", Toast.LENGTH_SHORT).show();
                    mRecordBtn.setBackgroundResource(R.mipmap.ic_record_start);
                    stopLive();
                    isLiveing = false;
                } else {
                    startLive();
                }
            }
        });


        mPusherManager.addPushListener(new PusherManager.IPushListener() {
            @Override
            public void onConnect() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressConnecting.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "开始连接服务器...", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onConnectSucceed() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressConnecting.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "服务器连接成功，开始推流!", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onError(final String eror) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressConnecting.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), eror, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    private void initPush() {
        mPusherManager = new PusherManager(getParameter());
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Log.d("onConfigurationChanged", "rotation:" + rotation);

        mPusherManager.setVideoPreviewRotation(rotation);

    }




    @Override
    protected void onRestart() {
        super.onRestart();
        mPusherManager.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPusherManager.stopLive();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPusherManager.stopLive();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPusherManager.release();
    }

    public void stopLive() {
        mPusherManager.stopLive();
    }

    public void switchCamera() {
        mPusherManager.switchCamera();
    }

    private void startLive() {
        final EditText editText = new EditText(this);
        editText.setText(Constants.RTMP_PUSH);
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setView(editText)
                .setPositiveButton("开始直播", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!editText.getText().toString().isEmpty()) {

                            Constants.RTMP_PUSH = editText.getText().toString().trim();
                            mPusherManager.startLive(Constants.RTMP_PUSH);
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "不能为空!", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create().show();
    }

    private AudioVideoParameter getParameter() {
        return AudioVideoParameter.Builder.asBuilder().
                withBitrate(bitrate).
                withCameraId(Camera.CameraInfo.CAMERA_FACING_BACK).
                withSurfaceHolder(surface.getHolder()).
                withFps(fps).
                withHeight(height).
                withWidth(width).
                withRotation(getWindowManager().getDefaultDisplay().getRotation()).
                build();
    }

}
