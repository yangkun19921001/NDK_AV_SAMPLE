//
// Created by 阳坤 on 2020-02-25.
//


#include "include/YKPusher.h"


/**
 * 对音视频推流变量统一管理
 */

YKPusher *mYKPusher;
JavaVM *javVM = 0;


/**
 * JNI 初始化加载，最先加载
 * @param javaVM
 * @param pVoid
 * @return
 */
int JNI_OnLoad(JavaVM *javaVM, void *pVoid) {
    javVM = javaVM;
    return JNI_VERSION_1_6;;
}


/**
 * native 初始化
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_devyk_pusher_1common_PusherManager_native_1init(JNIEnv *env, jobject instance) {
    LOGD("init");
    mYKPusher = new YKPusher();
    mYKPusher->init(javVM, env, instance);

}


/**
 * 开始推流
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_devyk_pusher_1common_PusherManager_native_1push_1video(JNIEnv *env, jobject instance,
                                                                jbyteArray data_) {
    //查看是否可以开始推流工作
    if (mYKPusher->mVideoChannel && mYKPusher->isReadyPushing()) {
        jbyte *data = env->GetByteArrayElements(data_, NULL);
        LOGD("push");
        mYKPusher->mVideoChannel->encodeData(data);
        env->ReleaseByteArrayElements(data_, data, 0);
    }

}

/**
 * 设置编码信息
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_devyk_pusher_1common_PusherManager_native_1setVideoEncoderInfo(JNIEnv *env,
                                                                        jobject instance, jint w,
                                                                        jint h, jint mFps,
                                                                        jint mBit) {

    LOGD("width: %d , height: %d fps: %d bit: %d", w, h, mFps, mBit);
    if (mYKPusher && mYKPusher->mVideoChannel) {
        mYKPusher->mVideoChannel->setVideoEncoderInfo(w, h, mFps, mBit);
    }


}


/**
 * 开始直播
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_devyk_pusher_1common_PusherManager_native_1start(JNIEnv *env, jobject instance,
                                                          jstring path_) {
    LOGD("start");
    if (mYKPusher && mYKPusher->isReadyPushing()) {
        return;
    }
    const char *path = env->GetStringUTFChars(path_, 0);
    if (mYKPusher) {
        mYKPusher->start(path);

    }
    env->ReleaseStringUTFChars(path_, path);
}


/**
 * 停止
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_devyk_pusher_1common_PusherManager_native_1stop(JNIEnv *env, jobject instance) {
    LOGD("stop");
    if (mYKPusher){
        mYKPusher->stop();
    }

}


/**
 * 释放
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_devyk_pusher_1common_PusherManager_native_1release(JNIEnv *env, jobject instance) {
    LOGD("release");
    if (mYKPusher) {
        mYKPusher->release();
        delete mYKPusher;
        mYKPusher = 0;
    }
}

/**
 * 设置音频编码配置
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_devyk_pusher_1common_PusherManager_native_1setAudioEncInfo(JNIEnv *env, jobject instance,
                                                                    jint sampleRate,
                                                                    jint channels) {

    if (mYKPusher && mYKPusher->mAudioChannel) {
        mYKPusher->mAudioChannel->setAudioEncoderInfo(sampleRate, channels);
    }
}

/**
 * 获取一次音频编码输入的样本数量
 */
extern "C"
JNIEXPORT jint JNICALL
Java_com_devyk_pusher_1common_PusherManager_getInputSamples(JNIEnv *env, jobject instance) {

    if (mYKPusher && mYKPusher->mAudioChannel) {
        return mYKPusher->mAudioChannel->getInputSamples();
    }
    return -1;

}

/**
 * 接收 Java 端的音频 PCM 数据
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_devyk_pusher_1common_PusherManager_native_1pushAudio(JNIEnv *env, jobject instance,
                                                              jbyteArray audioData_) {

    if (mYKPusher && mYKPusher->mAudioChannel && mYKPusher->isReadyPushing()) {
        jbyte *audioData = env->GetByteArrayElements(audioData_, NULL);
        mYKPusher->mAudioChannel->encodeData(audioData);
        env->ReleaseByteArrayElements(audioData_, audioData, 0);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_devyk_pusher_1common_PusherManager_native_1restart(JNIEnv *env, jobject instance) {

    // TODO
    if (mYKPusher) {
        mYKPusher->restart();
    }

}

int YKPusher::isReadyPushing() {
    return mRtmpManager->readyPushing;
}


