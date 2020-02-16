//
// Created by 阳坤 on 2020-02-12.
//

#include "PlayCallback.h"

PlayCallback::~PlayCallback() {

}


void PlayCallback::toJavaMessage(const char *message) {
//拿到 jclass
    jclass videoPlayClass = this->env->GetObjectClass(instance);
//拿到 拿到 methodID
    this->jmd_showMessage = this->env->GetMethodID(videoPlayClass, "showMessage",
                                                   "(Ljava/lang/String;)V");

    jstring string = env->NewStringUTF(message);
//通过反射执行 Java 方法
    this->env->CallVoidMethod(instance, jmd_showMessage, string);
}


void PlayCallback::onError(const char *message) {
    toJavaMessage(message);

}

void PlayCallback::onSucceed(const char *message) {
    toJavaMessage(message);
}


PlayCallback::PlayCallback(JavaVM *javaVM, JNIEnv *env, jobject jobject) {
    this->javaVm = javaVM;
    this->env = env;
    this->instance = env->NewGlobalRef(jobject);//提升全局

}








