//
// Created by 阳坤 on 2020-01-19.
//

#ifndef NDK_SAMPLE_AUDIOCHANNEL_H
#define NDK_SAMPLE_AUDIOCHANNEL_H


#include "BaseChannel.h"
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

extern "C" {
#include <libswscale/swscale.h>
#include <libavutil/channel_layout.h>
#include <libswresample/swresample.h> // 重采样支持
};

#define AUDIO_SAMPLE_RATE 44100

class AudioChannel : public BaseChannel {
public:
    AudioChannel(int stream_index,
                 AVCodecContext *pContext,AVRational);

    ~AudioChannel();

    void stop();

    void start();

    void audio_decode();

    void audio_player();

    int getPCM();

    // 定义一个缓冲区
    uint8_t *out_buffers = 0;

    int out_channels;
    int out_sample_size;
    int out_sample_rate;
    int out_buffers_size;

    void release();

    void restart();



private:
    // 线程ID
    pthread_t pid_audio_decode;
    pthread_t pid_audio_player;

    // 引擎
    SLObjectItf engineObject;
    // 引擎接口
    SLEngineItf engineInterface;
    // 混音器
    SLObjectItf outputMixObject;
    // 播放器的
    SLObjectItf bqPlayerObject;
    // 播放器接口
    SLPlayItf bqPlayerPlay;
    // 获取播放器队列接口
    SLAndroidSimpleBufferQueueItf bqPlayerBufferQueue;

    SwrContext *swr_ctx;




};


#endif //NDK_SAMPLE_AUDIOCHANNEL_H
