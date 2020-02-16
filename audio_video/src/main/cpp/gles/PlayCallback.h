//
// Created by 阳坤 on 2020-02-12.
//

#ifndef NDK_SAMPLE_PLAYCALLBACK_H
#define NDK_SAMPLE_PLAYCALLBACK_H

#include <jni.h>

class PlayCallback {
public:
    PlayCallback(JavaVM *javaVM, JNIEnv *env, jobject job);

    ~PlayCallback();

    void onSucceed(const char *);

    void onError(const char *);

    void toJavaMessage(const char *message);


private:
    JavaVM *javaVm = 0;
    JNIEnv *env = 0;
    jobject instance;

    jmethodID jmd_showMessage;

};


#endif //NDK_SAMPLE_PLAYCALLBACK_H
