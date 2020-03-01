package com.devyk.screen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;

import com.devyk.configuration.AudioConfiguration;
import com.devyk.configuration.VideoConfiguration;
import com.devyk.constant.SopCastConstant;
import com.devyk.controller.StreamController;
import com.devyk.controller.audio.NormalAudioController;
import com.devyk.controller.video.ScreenVideoController;
import com.devyk.stream.packer.Packer;
import com.devyk.stream.sender.Sender;
import com.devyk.utils.SopCastLog;
import com.devyk.utils.SopCastUtils;

/**
 * @Title: ScreenRecordActivity
 * @Package com.devyk.screen
 * @Description:
 * @Author Jim
 * @Date 2016/11/2
 * @Time 下午2:45
 * @Version
 */

public class ScreenRecordActivity extends Activity {
    private static final String TAG = SopCastConstant.TAG;
    private static final int RECORD_REQUEST_CODE = 101;
    private StreamController mStreamController;
    private MediaProjectionManager mMediaProjectionManage;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void requestRecording() {
        if(!SopCastUtils.isOverLOLLIPOP()) {
            SopCastLog.d(TAG, "Device don't support screen recording.");
            return;
        }
        mMediaProjectionManage = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent captureIntent = mMediaProjectionManage.createScreenCaptureIntent();
        startActivityForResult(captureIntent, RECORD_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECORD_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                NormalAudioController audioController = new NormalAudioController();
                ScreenVideoController videoController = new ScreenVideoController(mMediaProjectionManage, resultCode, data);
                mStreamController = new StreamController(videoController, audioController);
                requestRecordSuccess();
            } else {
                requestRecordFail();
            }
        }
    }

    protected void requestRecordSuccess() {

    }

    protected void requestRecordFail() {

    }

    public void setVideoConfiguration(VideoConfiguration videoConfiguration) {
        if(mStreamController != null) {
            mStreamController.setVideoConfiguration(videoConfiguration);
        }
    }

    public void setAudioConfiguration(AudioConfiguration audioConfiguration) {
        if(mStreamController != null) {
            mStreamController.setAudioConfiguration(audioConfiguration);
        }
    }

    protected void startRecording() {
        if(mStreamController != null) {
            mStreamController.start();
        }
    }

    protected void stopRecording() {
        if(mStreamController != null) {
            mStreamController.stop();
        }
    }

    protected void pauseRecording() {
        if(mStreamController != null) {
            mStreamController.pause();
        }
    }


    protected void resumeRecording() {
        if(mStreamController != null) {
            mStreamController.resume();
        }
    }

    protected void muteRecording(boolean mute) {
        if(mStreamController != null) {
            mStreamController.mute(mute);
        }
    }

    protected boolean setRecordBps(int bps) {
        if(mStreamController != null) {
            return mStreamController.setVideoBps(bps);
        } else {
            return false;
        }
    }

    protected void setRecordPacker(Packer packer) {
        if(mStreamController != null) {
            mStreamController.setPacker(packer);
        }
    }

    protected void setRecordSender(Sender sender) {
        if(mStreamController != null) {
            mStreamController.setSender(sender);
        }
    }

    @Override
    protected void onDestroy() {
        stopRecording();
        super.onDestroy();
    }
}
