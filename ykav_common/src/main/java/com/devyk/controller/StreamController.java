package com.devyk.controller;

import android.media.MediaCodec;

import com.devyk.audio.OnAudioEncodeListener;
import com.devyk.configuration.AudioConfiguration;
import com.devyk.configuration.VideoConfiguration;
import com.devyk.controller.audio.IAudioController;
import com.devyk.controller.video.IVideoController;
import com.devyk.stream.packer.Packer;
import com.devyk.stream.sender.Sender;
import com.devyk.utils.SopCastUtils;
import com.devyk.video.OnVideoEncodeListener;

import java.nio.ByteBuffer;

/**
 * @Title: StreamController
 * @Package com.devyk.controller
 * @Description:
 * @Author Jim
 * @Date 16/9/14
 * @Time 上午11:44
 * @Version
 */
public class StreamController implements OnAudioEncodeListener, OnVideoEncodeListener, Packer.OnPacketListener{
    private Packer mPacker;
    private Sender mSender;
    private IVideoController mVideoController;
    private IAudioController mAudioController;

    public StreamController(IVideoController videoProcessor, IAudioController audioProcessor) {
        mAudioController = audioProcessor;
        mVideoController = videoProcessor;
    }

    public void setVideoConfiguration(VideoConfiguration videoConfiguration) {
        mVideoController.setVideoConfiguration(videoConfiguration);
    }

    public void setAudioConfiguration(AudioConfiguration audioConfiguration) {
        mAudioController.setAudioConfiguration(audioConfiguration);
    }

    public void setPacker(Packer packer) {
        mPacker = packer;
        mPacker.setPacketListener(this);
    }

    public void setSender(Sender sender) {
        mSender = sender;
    }

    public synchronized void start() {
        SopCastUtils.processNotUI(new SopCastUtils.INotUIProcessor() {
            @Override
            public void process() {
                if(mPacker == null) {
                    return;
                }
                if(mSender == null) {
                    return;
                }
                mPacker.start();
                mSender.start();
                mVideoController.setVideoEncoderListener(StreamController.this);
                mAudioController.setAudioEncodeListener(StreamController.this);
                mAudioController.start();
                mVideoController.start();
            }
        });
    }

    public synchronized void stop() {
        SopCastUtils.processNotUI(new SopCastUtils.INotUIProcessor() {
            @Override
            public void process() {
                mVideoController.setVideoEncoderListener(null);
                mAudioController.setAudioEncodeListener(null);
                mAudioController.stop();
                mVideoController.stop();
                if(mSender != null) {
                    mSender.stop();
                }
                if(mPacker != null) {
                    mPacker.stop();
                }
            }
        });
    }

    public synchronized void pause() {
        SopCastUtils.processNotUI(new SopCastUtils.INotUIProcessor() {
            @Override
            public void process() {
                mAudioController.pause();
                mVideoController.pause();
            }
        });
    }

    public synchronized void resume() {
        SopCastUtils.processNotUI(new SopCastUtils.INotUIProcessor() {
            @Override
            public void process() {
                mAudioController.resume();
                mVideoController.resume();
            }
        });
    }

    public void mute(boolean mute) {
        mAudioController.mute(mute);
    }

    public int getSessionId() {
        return mAudioController.getSessionId();
    }

    public boolean setVideoBps(int bps) {
        return mVideoController.setVideoBps(bps);
    }

    @Override
    public void onAudioEncode(ByteBuffer bb, MediaCodec.BufferInfo bi) {
        if(mPacker != null) {
            mPacker.onAudioData(bb, bi);
        }
    }

    @Override
    public void onVideoEncode(ByteBuffer bb, MediaCodec.BufferInfo bi) {
        if(mPacker != null) {
            mPacker.onVideoData(bb, bi);
        }
    }

    @Override
    public void onPacket(byte[] data, int packetType) {
        if(mSender != null) {
            mSender.onData(data, packetType);
        }
    }
}
