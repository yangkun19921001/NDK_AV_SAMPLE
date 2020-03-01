package com.devyk.pusher_common;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaRecorder;

import com.devyk.audio.AudioProcessor;
import com.devyk.audio.AudioUtils;
import com.devyk.audio.OnAudioEncodeListener;
import com.devyk.capture.AudioCapture;
import com.devyk.configuration.AudioConfiguration;
import com.devyk.constant.SopCastConstant;
import com.devyk.stream.packer.Packer;
import com.devyk.stream.packer.rtmp.RtmpPacker;
import com.devyk.utils.SopCastLog;

import java.nio.ByteBuffer;

/**
 * <pre>
 *     author  : devyk on 2020-02-25 14:40
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is AudioChannel
 * </pre>
 */
public class AudioChannel extends BaseChannel {
    private AudioCapture mAudioCapture;


    //准备录音机 采集pcm 数据
    int channelConfig;


    int channels = 1;

    /**
     * 是否开始
     */
    boolean isLive = false;
    private AudioConfiguration mAudioConfiguration;
    private Packer.OnAudioPacketListener mOnPacketListener;
    private AudioRecord mAudioRecord;
    private AudioProcessor mAudioProcessor;

    public AudioChannel(AudioConfiguration audioConfiguration, Packer.OnAudioPacketListener onPacketListener) {
        mAudioConfiguration = audioConfiguration;
        mOnPacketListener = onPacketListener;
        initAudioCapture();
    }

    private void initAudioCapture() {

        //最小需要的缓冲区

        mAudioCapture = new AudioCapture();

        if (channels == 2) {
            channelConfig = AudioFormat.CHANNEL_IN_STEREO;
        } else {
            channelConfig = AudioFormat.CHANNEL_IN_MONO;
        }

        mOnPacketListener.sendAudioInfo(mAudioConfiguration.frequency,channels);

        //16 位 2个字节
       int inputSamples = mOnPacketListener.getInputSamples() * 2;


        mAudioCapture.setOnAudioFrameCapturedListener(new AudioCapture.OnAudioFrameCapturedListener() {
            @Override
            public void onAudioFrameCaptured(byte[] audioData) {
                if (isLive){
                    mOnPacketListener.onPacket(audioData, RtmpPacker.PCM);
                }
            }
        });

        //最小需要的缓冲区
        int minBufferSize = AudioRecord.getMinBufferSize(mAudioConfiguration.frequency, channelConfig, AudioFormat.ENCODING_PCM_16BIT) * 2;
        mAudioCapture.startCapture(MediaRecorder.AudioSource.MIC,mAudioConfiguration.frequency,channelConfig,AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize > inputSamples ? minBufferSize : inputSamples,inputSamples);
    }


    public void start() {
        SopCastLog.d(SopCastConstant.TAG, "Audio Recording start");
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
