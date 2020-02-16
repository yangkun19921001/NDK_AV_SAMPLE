//
// Created by 阳坤 on 2020-01-19.
//

#include "include/JNICallback.h"


JNICallback::JNICallback(JavaVM *javaVM, JNIEnv *env, jobject instance) {
    this->javaVM = javaVM;
    this->env = env;
    this->instance = env->NewGlobalRef(instance);// 坑，需要是全局（jobject一旦涉及到跨函数，跨线程，必须是全局引用）


    //拿到 Java 对象的 class
    jclass mYKPlayClass = env->GetObjectClass(this->instance);
    //声明 Java onPrepared/onError 回调签名
    const char *sigPre = "()V"; //空参无返回值
    const char *sigErr = "(I)V"; //Int 参数，无返回值

    this->jmd_repared = env->GetMethodID(mYKPlayClass, "onPrepared", sigPre);
    this->jmd_error = env->GetMethodID(mYKPlayClass, "onError", sigErr);


}



void JNICallback::onPrepared(int thread_mode) {
    if (thread_mode == THREAD_MAIN) {
        env->CallVoidMethod(this->instance, jmd_repared);//主线程可以直接调用 Java 方法
    } else {
        //子线程，用附加 native 线程到 JVM 的方式，来获取到权限 env
        JNIEnv *jniEnv = nullptr;
        jint ret = javaVM->AttachCurrentThread(&jniEnv, 0);
        if (ret != JNI_OK) {
            return;
        }
        jniEnv->CallVoidMethod(this->instance, jmd_repared);//开始调用 Java 方法
        javaVM->DetachCurrentThread();//解除附加
    }


}

void JNICallback::onErrorAction(int thread_mode, int error_code) {
    if (thread_mode == THREAD_MAIN) {
        env->CallVoidMethod(this->instance, jmd_error,error_code);//主线程可以直接调用 Java 方法
    } else {
        //子线程，用附加 native 线程到 JVM 的方式，来获取到权限 env
        JNIEnv *jniEnv = nullptr;
        jint ret = javaVM->AttachCurrentThread(&jniEnv, 0);
        if (ret != JNI_OK) {
            return;
        }
        jniEnv->CallVoidMethod(this->instance, jmd_error,error_code);//开始调用 Java 方法
        javaVM->DetachCurrentThread();//解除附加
    }


}

/**
 * 析构函数：专门完成释放的工作
 */
JNICallback::~JNICallback() {
    LOGD("~JNICallback")
    this->javaVM = 0;
    env->DeleteGlobalRef(this->instance);//释放全局
    this->instance = 0;
    env = 0;

}
