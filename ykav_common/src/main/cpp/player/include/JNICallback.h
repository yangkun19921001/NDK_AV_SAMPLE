//
// Created by 阳坤 on 2020-01-19.
//

#ifndef NDK_SAMPLE_JNICALLBACK_H
#define NDK_SAMPLE_JNICALLBACK_H


#include <jni.h>
#include "Constants.h"


class JNICallback {
    //定义公有方法
public:
    JNICallback(JavaVM *javaVM,JNIEnv * env ,jobject instance);

     //回调
     void onPrepared(int thread_mode);
     void onErrorAction(int thread_mode,int error_code);
    void onProgress(int thread, int progress);

    //析构函数声明
    ~JNICallback();

private:
    JavaVM *javaVM = 0;
    JNIEnv *env = 0;
    jobject  instance;

    //相当于反射拿到 Java 函数
    jmethodID jmd_repared;
    jmethodID jmd_error;
    jmethodID jmid_progress;
};


#endif //NDK_SAMPLE_JNICALLBACK_H
