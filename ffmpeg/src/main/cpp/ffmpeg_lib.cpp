//
// Created by 阳坤 on 2020-01-15.
//


#include <jni.h>

// 有坑，会报错，必须混合编译
//#include <libavutil/avutil.h>

extern "C" {
#include <libavutil/avutil.h>
}


/**
 * 拿到 ffmpeg 当前版本
 * @return
 */
const char *getFFmpegVer() {
    return av_version_info();
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_devyk_ffmpeg_MainActivity_getFFmpegVersion(JNIEnv *env, jclass type) {
    return env->NewStringUTF(getFFmpegVer());
}
