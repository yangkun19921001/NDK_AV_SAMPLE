//
// Created by 阳坤 on 2020-02-27.
//

#ifndef NDK_SAMPLE_AUDIOCHANNEL_ENCODER
#define NDK_SAMPLE_AUDIOCHANNEL_ENCODER

#define FAAC_DEFAUTE_SAMPLE_RATE 44100
#define FAAC_DEFAUTE_SAMPLE_CHANNEL 1

#include <rtmp.h>
#include "PushCallback.h"
#include <faac.h>
#include <sys/types.h>
#include <cstring>
#include "macro.h"
#include "safe_queue.h"

class AudioEncoderChannel {
    typedef void (*AudioCallback)(RTMPPacket *packet);

public:
    AudioEncoderChannel();

    ~AudioEncoderChannel();

    AudioEncoderChannel(PushCallback *pCallback,int mediacodec);

    void setAudioEncoderInfo(int samplesHZ, int channel);

    int getInputSamples();

    void encodeData(int8_t *data);

    void pushAAC(u_char *data,int len, long timestamp);

    RTMPPacket *getAudioTag();


    void release();

    void setAudioCallback(AudioCallback audioCallback);


    PushCallback *mIPushCallback;

    void startEncoder();



    void restart();

    void stop();

    void setMediaCodec(int i);

private:
    AudioCallback mAudioCallback;
    int mChannels;
    faacEncHandle mAudioCodec = 0;
    u_long mInputSamples;
    u_long mMaxOutputBytes;
    u_char *mBuffer = 0;

    int isStart = 0;

    int isMediaCodec = 0;
};


#endif //NDK_SAMPLE_AUDIOCHANNEL_H
