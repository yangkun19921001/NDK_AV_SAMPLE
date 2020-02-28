//
// Created by 阳坤 on 2020-01-18.
//
#include <jni.h>
#include "include/JNICallback.h"
#include "include/YKPlayer.h"
#include <android/native_window_jni.h> // 是为了 渲染到屏幕支持的


extern "C" {
#include <libavutil/avutil.h>
}

JavaVM *javaVM = 0;
YKPlayer *player = 0;
ANativeWindow *nativeWindow = 0;
pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER; // 静态初始化 互斥锁


int JNI_OnLoad(JavaVM *javaVM1, void *pVoid) {
    ::javaVM = javaVM1;
    // 坑，这里记得一定要返回，和异步线程指针函数一样（记得返回）
    return JNI_VERSION_1_6;
}

/**
 *
 * @return 返回 FFMpeg 版本
 */
const char *getFFmpegVersion() {
    return av_version_info();
}


/**
 *
 * 专门渲染的函数
 * @param src_data  解码后的视频 rgba 数据
 * @param width  视频宽
 * @param height 视频高
 * @param src_size 行数 size 相关信息
 *
 */
void renderFrame(uint8_t *src_data, int width, int height, int src_size) {
    pthread_mutex_lock(&mutex);

    if (!nativeWindow) {
        pthread_mutex_unlock(&mutex);
        nativeWindow = 0;
        return;
    }

    //设置窗口属性
    ANativeWindow_setBuffersGeometry(nativeWindow, width, height, WINDOW_FORMAT_RGBA_8888);

    ANativeWindow_Buffer window_buffer;

    if (ANativeWindow_lock(nativeWindow, &window_buffer, 0)) {
        ANativeWindow_release(nativeWindow);
        nativeWindow = 0;
        pthread_mutex_unlock(&mutex);
        return;
    }

    //填数据到 buffer,其实就是修改数据
    uint8_t *dst_data = static_cast<uint8_t *>(window_buffer.bits);
    int lineSize = window_buffer.stride * 4;//RGBA

    //下面就是逐行 copy 了。
    //一行 copy
    for (int i = 0; i < window_buffer.height; ++i) {
        memcpy(dst_data + i * lineSize, src_data + i * src_size, lineSize);
    }
    ANativeWindow_unlockAndPost(nativeWindow);
    pthread_mutex_unlock(&mutex);
}


/**
 * 拿到当前 FFmpeg 版本
 */
extern "C"
JNIEXPORT jstring JNICALL
Java_com_devyk_player_1common_PlayerManager_getFFmpegVersion
        (JNIEnv *env, jobject obj) {

    return env->NewStringUTF(getFFmpegVersion());

};


/**
 * 设置播放 surface
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_devyk_player_1common_PlayerManager_setSurfaceNative(JNIEnv *env, jclass type,
                                                             jobject surface) {
    LOGD("Java_com_devyk_player_1common_PlayerManager_setSurfaceNative");
    pthread_mutex_lock(&mutex);
    if (nativeWindow) {
        ANativeWindow_release(nativeWindow);
        nativeWindow = 0;
    }
    //创建新的窗口用于视频显示窗口
    nativeWindow = ANativeWindow_fromSurface(env, surface);

    pthread_mutex_unlock(&mutex);


}

/**
 * 设置播放源
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_devyk_player_1common_PlayerManager_prepareNative(JNIEnv *env, jclass thiz,
                                                          jstring mDataSource_) {
    // 准备工作的话，首先要来解封装
    JNICallback *jniCallback = new JNICallback(javaVM, env, thiz);
    //转成 C 字符串
    const char *data_source = env->GetStringUTFChars(mDataSource_, NULL);
    player = new YKPlayer(data_source, jniCallback);
    player->setRenderCallback(renderFrame);
    player->prepare();
    env->ReleaseStringUTFChars(mDataSource_, data_source);
}

/**
 * 开始播放
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_devyk_player_1common_PlayerManager_startNative(JNIEnv *env, jclass type) {
    if (player) {
        player->start();
    }
}

/**
 * 停止播放
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_devyk_player_1common_PlayerManager_stopNative(JNIEnv *env, jclass type) {
    // TODO
    if (player)
        player->stop();

}

/**
 * 继续
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_devyk_player_1common_PlayerManager_restartNative(JNIEnv *env, jclass type) {
    // TODO
    if (player) {
        player->restart();
    }

}

/**
 * 释放资源
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_devyk_player_1common_PlayerManager_releaseNative(JNIEnv *env, jclass type) {
    // TODO
    if (player)
        player->release();
}

/**
 * 播放状态
 */
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_devyk_player_1common_PlayerManager_isPlayerNative(JNIEnv *env, jclass type) {
    jboolean isPlay = false;
    if (player) {
        isPlay = player->isPlaying;
    }
    return isPlay;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_devyk_player_1common_PlayerManager_native_1GetDuration(JNIEnv *env, jobject instance) {

    if (player) {
        return player->getDuration();
    }
    return 0;

}
extern "C"
JNIEXPORT void JNICALL
Java_com_devyk_player_1common_PlayerManager_native_1seek(JNIEnv *env, jobject instance,
                                                         jint progress) {

    if (player) {
        player->seek(progress); //不需要转换 jint -> int
    }

}