//
// Created by 阳坤 on 2020-02-27.
//

#include "include/PushCallback.h"

PushCallback::PushCallback(JavaVM *javaVM, JNIEnv *env, jobject object) {
    this->mJavaVM = javaVM;
    this->mJNIEnv = env;
    //必须声明全局 不然会报 error JNI DETECTED ERROR IN APPLICATION: use of invalid jobject 0xff868d8c
    this->mJobject = env->NewGlobalRef(object);// 坑，需要是全局（jobject一旦涉及到跨函数，跨线程，必须是全局引用）

    //拿到 java 声明 native 当前类的 class
    jclass javaClass = env->GetObjectClass(object);
    //拿到对应的接收的回调函数
    this->mConnectId = env->GetMethodID(javaClass, "onRtmpConnect", "()V");
    this->mSucceedId = env->GetMethodID(javaClass, "onRtmpSucceed", "()V");
    this->mErrorId = env->GetMethodID(javaClass, "onError", "(I)V");
}

PushCallback::~PushCallback() {
    this->mJavaVM = 0;
    mJNIEnv->DeleteGlobalRef(this->mJobject);//释放全局
    this->mJobject = 0;
    mJNIEnv = 0;

}

void PushCallback::onRtmpConnect(int thread_mode) {
    if (thread_mode == THREAD_MAIN) {
        this->mJNIEnv->CallVoidMethod(this->mJobject, mConnectId);//主线程可以直接调用 Java 方法
    } else {
        //子线程，用附加 native 线程到 JVM 的方式，来获取到权限 env
        JNIEnv *jniEnv = nullptr;
        jint ret = mJavaVM->AttachCurrentThread(&jniEnv, 0);
        if (ret != JNI_OK) {
            return;
        }
        jniEnv->CallVoidMethod(this->mJobject, mConnectId);//开始调用 Java 方法
        mJavaVM->DetachCurrentThread();//解除附加
    }

}

void PushCallback::onRtmpSucceed(int thread_mode) {
    if (thread_mode == THREAD_MAIN) {
        this->mJNIEnv->CallVoidMethod(this->mJobject, mSucceedId);//主线程可以直接调用 Java 方法
    } else {
        //子线程，用附加 native 线程到 JVM 的方式，来获取到权限 env
        JNIEnv *jniEnv = nullptr;
        jint ret = mJavaVM->AttachCurrentThread(&jniEnv, 0);
        if (ret != JNI_OK) {
            return;
        }
        jniEnv->CallVoidMethod(this->mJobject, mSucceedId);//开始调用 Java 方法
        mJavaVM->DetachCurrentThread();//解除附加
    }

}

void PushCallback::onError(int thread_mode, int errCode) {
    if (thread_mode == THREAD_MAIN) {
        this->mJNIEnv->CallVoidMethod(this->mJobject, mErrorId, errCode);//主线程可以直接调用 Java 方法
    } else {
        //子线程，用附加 native 线程到 JVM 的方式，来获取到权限 env
        JNIEnv *jniEnv = nullptr;
        jint ret = mJavaVM->AttachCurrentThread(&jniEnv, 0);
        if (ret != JNI_OK) {
            return;
        }
        jniEnv->CallVoidMethod(this->mJobject, mErrorId, errCode);//开始调用 Java 方法
        mJavaVM->DetachCurrentThread();//解除附加
    }

}
