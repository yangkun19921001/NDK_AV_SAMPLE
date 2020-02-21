//
// Created by 阳坤 on 2020-01-19.
//

#ifndef NDK_SAMPLE_YKPLAYER_H
#define NDK_SAMPLE_YKPLAYER_H


#include "JNICallback.h"
#include "VideoChannel.h"
#include "AudioChannel.h"

extern "C" {
#include <libavformat/avformat.h>
#include <libavutil/time.h>
}

class YKPlayer {
public:
    YKPlayer();

    YKPlayer(const char *data_source, JNICallback *pCallback);

    ~YKPlayer();

    void prepare();

    void prepare_();

    void start();

    void start_();
    void restart();

    void stop();

    void release();

    void setRenderCallback(RenderCallback renderCallback);

    bool isPlaying;


    int getDuration() {
        return duration;
    }

    void seek(int i);

private:
    char *data_source = 0;
    pthread_t pid_prepare;

    AVFormatContext *formatContext = 0;

    AudioChannel *audioChannel = 0;

    VideoChannel *videoChannel = 0;

    JNICallback *pCallback = 0;

    pthread_t pid_start;

    bool isStop = false;

    RenderCallback renderCallback;

    AVCodecContext *codecContext = 0;

    pthread_mutex_t seekMutex;

    int duration = 0;

    bool isSeek = 0;

};


#endif //NDK_SAMPLE_YKPLAYER_H
