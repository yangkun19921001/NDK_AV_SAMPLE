//
// Created by 阳坤 on 2020-02-09.
//

#include <jni.h>
#include <pthread.h>
#include "OpenSLAudioPlay.h"


/**
 * 播放 pcmFile
 */
FILE *pcmFile = 0;
/**
 *
 */
OpenSLAudioPlay *slAudioPlayer = nullptr;
/**
 * 是否正在播放
 */
bool isPlaying = false;

void *playThreadFunc(void *arg);

void *playThreadFunc(void *arg) {
    const int bufferSize = 2048;
    short buffer[bufferSize];
    while (isPlaying && !feof(pcmFile)) {
        fread(buffer, 1, bufferSize, pcmFile);
        slAudioPlayer->enqueueSample(buffer, bufferSize);
    }

    return 0;
}


extern "C"
JNIEXPORT void JNICALL
Java_com_devyk_audiovideo_audio_AudioPlayActivity_nativePlayPcm(JNIEnv *env, jclass type,jstring pcmPath_) {
    //将 Java 传递过来的 String 转为 C 中的 char *
    const char * _pcmPath =  env->GetStringUTFChars(pcmPath_,NULL);

    //如果已经实例化了，就释放资源
    if (slAudioPlayer) {
        slAudioPlayer->release();
        delete slAudioPlayer;
        slAudioPlayer = nullptr;
    }
    //实例化 OpenSLAudioPlay
    slAudioPlayer = new OpenSLAudioPlay(44100, SAMPLE_FORMAT_16, 1);
    slAudioPlayer->init();
    pcmFile = fopen(_pcmPath, "r");
    isPlaying = true;
    pthread_t playThread;
    pthread_create(&playThread, nullptr, playThreadFunc, 0);

    env->ReleaseStringUTFChars(pcmPath_,_pcmPath);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_devyk_audiovideo_audio_AudioPlayActivity_nativeStopPcm(JNIEnv *env, jclass type) {
    isPlaying = false;
    if (slAudioPlayer) {
        slAudioPlayer->release();
        delete slAudioPlayer;
        slAudioPlayer = nullptr;
    }
    if (pcmFile) {
        fclose(pcmFile);
        pcmFile = nullptr;
    }

}

