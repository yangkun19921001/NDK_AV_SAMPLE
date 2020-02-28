package com.devyk.pusher_common;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.devyk.capture.AudioCapture;

/**
 * <pre>
 *     author  : devyk on 2020-02-25 14:40
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AudioChannel
 * </pre>
 */
public class AudioChannel extends BaseChannel{
    private PusherManager mPusherManager;
    private AudioCapture mAudioCapture;

    private int channels = 1;

    /**
     * 采样率
     */
    private int sampleRate = 44100;
    /**
     * 采样大小
     */
    private int inputSamples;

    //准备录音机 采集pcm 数据
    int channelConfig;

    /**
     * 是否开始
     */
    boolean isLive = false;

    public AudioChannel(PusherManager pusherManager) {
        mPusherManager = pusherManager;

        initAudioCapture();
    }

    private void initAudioCapture() {
        mAudioCapture = new AudioCapture();

        if (channels == 2) {
            channelConfig = AudioFormat.CHANNEL_IN_STEREO;
        } else {
            channelConfig = AudioFormat.CHANNEL_IN_MONO;
        }


        mPusherManager.native_setAudioEncInfo(sampleRate, channels);
        //16 位 2个字节
        inputSamples = mPusherManager.getInputSamples() * 2;


        mAudioCapture.setOnAudioFrameCapturedListener(new AudioCapture.OnAudioFrameCapturedListener() {
            @Override
            public void onAudioFrameCaptured(byte[] audioData) {
                if (isLive){
                    mPusherManager.native_pushAudio(audioData);
                }
            }
        });

        //最小需要的缓冲区
        int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT) * 2;
        mAudioCapture.startCapture(MediaRecorder.AudioSource.MIC,sampleRate,channelConfig,AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize > inputSamples ? minBufferSize : inputSamples,inputSamples);
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
        isLive = false;
        mAudioCapture.stopCapture();
    }

    @Override
    public void onRestart() {
        isLive = true;
    }
}
