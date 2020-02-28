//
// Created by 阳坤 on 2020-02-25.
//

#ifndef NDK_SAMPLE_YKPUSHER_H
#define NDK_SAMPLE_YKPUSHER_H

#include <jni.h>

#include "macro.h"


#include "VideoChannel.h"
#include "AudioChannel.h"
#include "PushCallback.h"
#include "RTMPModel.h"


class YKPusher {
public:

/**
 * 定义视频处理通道
 */
    VideoEncoderChannel *mVideoChannel = 0;
/**
 * 定义音频处理通道
 */
    AudioEncoderChannel *mAudioChannel = 0;


    /**
     * 激活 rtmp
     */
    RTMPModel *mRtmpManager = 0;


    /**
     * 是否正在推流
     * @return
     */
    int isStart();

    int isReadyPushing();


    /**
     * native 初始化
     * @param javVM
     * @param env
     * @param instance
     */
    void init(JavaVM *javVM, JNIEnv *env, jobject instance) {
        PushCallback *pushCallback = new PushCallback(javVM, env, instance);
        VideoEncoderChannel *videoChannel = new VideoEncoderChannel(pushCallback);
        AudioEncoderChannel *audioChannel = new AudioEncoderChannel(pushCallback);
        RTMPModel *rtmpManager = new RTMPModel(pushCallback, audioChannel, videoChannel);
        mVideoChannel = videoChannel;
        mAudioChannel = audioChannel;
        mRtmpManager = rtmpManager;
    }


    /**
     * 释放 native 资源
     */
    void release() {
        if (mVideoChannel) {
            mVideoChannel->release();
            delete mVideoChannel;
            mVideoChannel = 0;
        }

        if (mAudioChannel) {
            mAudioChannel->release();
            delete mAudioChannel;
            mAudioChannel = 0;
        }

        if (mRtmpManager) {
            mRtmpManager->release();
            mRtmpManager = 0;
        }
    }

    /**
     * 停止推流
     */
    void stop() {
        if (mVideoChannel) {
            mVideoChannel->stop();

        }

        if (mAudioChannel) {
            mAudioChannel->stop();

        }

        if (mRtmpManager) {
            mRtmpManager->stop();
        }

    }

    /**
     * 开始链接 rtmp
     * @param path
     */
    void start(const char *path) {
        mRtmpManager->_onConnect(path);
    }


    /**
     * 恢复推流
     */
    void restart() {
        if (mVideoChannel) {
            mVideoChannel->restart();

        }

        if (mAudioChannel) {
            mAudioChannel->restart();

        }

        if (mRtmpManager) {
            mRtmpManager->restart();
        }
    };
};


#endif //NDK_SAMPLE_YKPUSHER_H
