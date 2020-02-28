//
// Created by 阳坤 on 2020-02-27.
//

#ifndef NDK_SAMPLE_RTMPMODEL_H
#define NDK_SAMPLE_RTMPMODEL_H

#include <jni.h>
#include <string.h>
#include <pthread.h>
#include "PushCallback.h"
#include <rtmp.h>
#include "safe_queue.h"
#include "AudioChannel.h"
#include "VideoChannel.h"

class RTMPModel {

public:
    /**
    * 定义 rtmp pact 队列
    */
    SafeQueue<RTMPPacket *> mPackets;
    /**
      * 定义推流标志位
    */
    int isStart = false;

    RTMPModel(PushCallback *pCallback, AudioEncoderChannel *audioEncoderChannel,
              VideoEncoderChannel *videoEncoderChannel);

    ~RTMPModel();

    void _onConnect(const char *url);

    void onConnect();


    void onPush();


    void release();




    /**
 * 定义一个 RTMP 开始链接的时间
 */
    uint32_t mStartTime;
    /**
    * 定义准备推流的标志
    */
    int readyPushing = false;

    void restart();

    void stop();

private:
    char *url;
    PushCallback *pushCallback;
    pthread_mutex_t *mMutex;
    pthread_t mPid;



    RTMP *rtmp;
    /**
      * 头包 Audio 信息
    */
    RTMPPacket *mAudioPacketTag = 0;

    /**
 * 定义视频处理通道
 */
    VideoEncoderChannel *mVideoChannel = 0;
/**
 * 定义音频处理通道
 */
    AudioEncoderChannel *mAudioChannel = 0;

    void setPacketReleaseCallback();


};


#endif //NDK_SAMPLE_RTMPMODEL_H
