//
// Created by 阳坤 on 2020-02-27.
//

#ifndef NDK_SAMPLE_VideoEncoderChannel
#define NDK_SAMPLE_VideoEncoderChannel

#include <rtmp.h>
#include <pthread.h>
#include <x264.h>
#include "PushCallback.h"
#include "safe_queue.h"

class VideoEncoderChannel {
    typedef void (*VideoEncoderCallback)(RTMPPacket *packet);

public:
    VideoEncoderChannel();

    ~VideoEncoderChannel();

    void release();

    //设置编码完成的回调
    void setVideoCallback(VideoEncoderCallback);

    //创建编码器
    void setVideoEncoderInfo(int width, int height, int fps, int bit);

    //编码的原始 YUV 数据
    void encodeData(int8_t *data);


    VideoEncoderChannel(PushCallback *pCallback,int isMediaCodec);

    void startEncoder();

    void onEncoder();

    int isStart = 0;
    /**
        * 定义 编码 队列
    */
    SafeQueue<int8_t *> mVideoPackets;

    void restart();

    void stop();
    void sendSpsPps(uint8_t *sps, uint8_t *pps, int sps_len, int pps_len);

    void sendFrame(int type, uint8_t *payload, int i_playload, long i);
    void sendH264(int isKey, uint8_t *buf, int len, int);

    void setMediaCodec(int mediacodec);

private:
    pthread_mutex_t mMutex;
    int mWidth, mHeight, mFps, mBit, mY_Size, mUV_Size;
    x264_t *mVideoCodec = 0;
    x264_picture_t *pic_in = 0;
    VideoEncoderCallback mVideoCallback;
    PushCallback *mIPushCallback;

    pthread_t mPid;

    int isMediaCodec = 0;

};
#endif //NDK_SAMPLE_VideoEncoderChannel
