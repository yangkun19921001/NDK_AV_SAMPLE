//
// Created by 阳坤 on 2020-02-11.
//

#include "video_play.h"
#include <jni.h>
#include "gles_play.h"

const JavaVM *javaVm = 0;
Gles_play *gles_play = 0;

int JNI_OnLoad(JavaVM *javaVm, void *pVoid) {
    ::javaVm = javaVm;
    return JNI_VERSION_1_6; // 坑，这里记得一定要返回，和异步线程指针函数一样（记得返回）
}


extern "C"
JNIEXPORT void JNICALL
Java_com_devyk_audiovideo_video_YUVPlay_nativeGlesPlay__Ljava_lang_String_2Ljava_lang_Object_2(
        JNIEnv *env, jobject instance, jstring yuv420pPath_, jobject surface) {
    const char *yuv420pPath = env->GetStringUTFChars(yuv420pPath_, 0);

    PlayCallback *callback = new PlayCallback(const_cast<JavaVM *>(javaVm), env, instance);
    gles_play = new Gles_play(env, instance, callback, yuv420pPath, surface);
    //这里prepare 内部会开启一个子线程，由于开启会造成 堆栈溢出 固取消了  JNI 中开启
//    gles_play->prepare();
    gles_play->start();
    env->ReleaseStringUTFChars(yuv420pPath_, yuv420pPath);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_devyk_audiovideo_video_YUVPlay_nativeWindowPlay__Ljava_lang_String_2Ljava_lang_Object_2(
        JNIEnv *env, jobject instance, jstring yuv420pPath_, jobject surface) {
    const char *yuv420pPath = env->GetStringUTFChars(yuv420pPath_, 0);
    // TODO

    env->ReleaseStringUTFChars(yuv420pPath_, yuv420pPath);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_devyk_audiovideo_video_YUVPlay_onDestory(JNIEnv *env, jobject instance) {
    if (gles_play)
        gles_play->release();
}


