//
// Created by 阳坤 on 2020-02-27.
//

#ifndef NDK_SAMPLE_AUDIOCHANNEL_ENCODER
#define NDK_SAMPLE_AUDIOCHANNEL_ENCODER


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

    AudioEncoderChannel(PushCallback *pCallback);

    void setAudioEncoderInfo(int samplesHZ, int channel);

    int getInputSamples();

    void encodeData(int8_t *data);

    RTMPPacket *getAudioTag();


    void release();

    void setAudioCallback(AudioCallback audioCallback);


    PushCallback *mIPushCallback;

    void startEncoder();


    void _onEncode();

    void restart();

    void stop();

private:
    AudioCallback mAudioCallback;
    int mChannels;
    faacEncHandle mAudioCodec = 0;
    u_long mInputSamples;
    u_long mMaxOutputBytes;
    u_char *mBuffer = 0;

    int isStart = 0;
};


#endif //NDK_SAMPLE_AUDIOCHANNEL_H
