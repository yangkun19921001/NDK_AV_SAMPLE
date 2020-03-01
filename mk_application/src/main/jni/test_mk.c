//
// Created by 阳坤 on 2020-01-11.
//

#include "../../../../../../../../Android/NDK/android-ndk-r15c/sysroot/usr/include/jni.h"
#include "../../../../../../../../Android/NDK/android-ndk-r15c/sysroot/usr/include/android/log.h"

// 声明一个函数
extern int main();

JNIEXPORT void JNICALL
Java_com_devyk_mk_1application_MainActivity_testMK
(JNIEnv * env, jobject inst) {

__android_log_print(ANDROID_LOG_DEBUG, "devyk", "testMK--》：%d", main());

}
