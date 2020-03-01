#include <jni.h>
#include <string>
#include <android/log.h>

// extern int main();  这样写有坑，因为main方法是属于c的，而当前是 CPP

extern "C" {
    int main();

}

extern "C" JNIEXPORT void JNICALL
Java_com_devyk_cmake_1application_MainActivity_testCmake(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";

    __android_log_print(ANDROID_LOG_DEBUG, "devyk", "main--->:%d", main());

}
