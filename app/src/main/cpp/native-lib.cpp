#include <jni.h>
#include <string>
#include <android/log.h>

#include <iostream>

#define TAG "native-lib"
// __VA_ARGS__ 代表 ...的可变参数
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG,  __VA_ARGS__);
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG,  __VA_ARGS__);
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG,  __VA_ARGS__);


extern "C"
JNIEXPORT void JNICALL
Java_com_devyk_ndk_1sample_MainActivity_test1(JNIEnv *env, jobject instance,
                                              jboolean jboolean1,
                                              jbyte jbyte1,
                                              jchar jchar1,
                                              jshort jshort1,
                                              jlong jlong1,
                                              jfloat jfloat1,
                                              jdouble jdouble1,
                                              jstring name_,
                                              jint age,
                                              jintArray i_,
                                              jobjectArray strs,
                                              jobject person,
                                              jbooleanArray bArray_
) {


    //1. 接收 Java 传递过来的 boolean 值
    unsigned char b_boolean = jboolean1;
    LOGD("boolean-> %d", b_boolean);

    //2. 接收 Java 传递过来的 boolean 值
    char c_byte = jbyte1;
    LOGD("jbyte-> %d", c_byte);


    //3. 接收 Java 传递过来的 char 值
    unsigned short c_char = jchar1;
    LOGD("char-> %d", c_char);


    //4. 接收 Java 传递过来的 short 值
    short s_short = jshort1;
    LOGD("short-> %d", s_short);

    //5. 接收 Java 传递过来的 long 值
    long l_long = jlong1;
    LOGD("long-> %d", l_long);

    //6. 接收 Java 传递过来的 float 值
    float f_float = jfloat1;
    LOGD("float-> %f", f_float);

    //7. 接收 Java 传递过来的 double 值
    double d_double = jdouble1;
    LOGD("double-> %f", d_double);

    //8. 接收 Java 传递过来的 String 值
    const char *name_string = env->GetStringUTFChars(name_, 0);
    LOGD("string-> %s", name_string);

    //9. 接收 Java 传递过来的 int 值
    int age_java = age;
    LOGD("int:%d", age_java);

    //10. 打印 Java 传递过来的 int []
    jint *intArray = env->GetIntArrayElements(i_, NULL);
    //拿到数组长度
    jsize intArraySize = env->GetArrayLength(i_);
    for (int i = 0; i < intArraySize; ++i) {
        LOGD("intArray->%d：", intArray[i]);
    }
    //释放数组
    env->ReleaseIntArrayElements(i_, intArray, 0);

    //11. 打印 Java 传递过来的 String[]
    jsize stringArrayLength = env->GetArrayLength(strs);
    for (int i = 0; i < stringArrayLength; ++i) {
        jobject jobject1 = env->GetObjectArrayElement(strs, i);
        //强转 JNI String
        jstring stringArrayData = static_cast<jstring >(jobject1);

        //转 C  String
        const char *itemStr = env->GetStringUTFChars(stringArrayData, NULL);
        LOGD("String[%d]: %s", i, itemStr);
        //回收 String[]
        env->ReleaseStringUTFChars(stringArrayData, itemStr);
    }



    //12. 打印 Java 传递过来的 Object 对象
    //12.1 获取字节码
    const char *person_class_str = "com/devyk/ndk_sample/Person";
    //12.2 转 jni jclass
    jclass person_class = env->FindClass(person_class_str);
    //12.3 拿到方法签名 javap -s
    const char *sig = "()Ljava/lang/String;";
    jmethodID jmethodID1 = env->GetMethodID(person_class, "getName", sig);

    jobject obj_string = env->CallObjectMethod(person, jmethodID1);
    jstring perStr = static_cast<jstring >(obj_string);
    const char *itemStr2 = env->GetStringUTFChars(perStr, NULL);
    LOGD("Person: %s", itemStr2);
    env->DeleteLocalRef(person_class); // 回收
    env->DeleteLocalRef(person); // 回收


    //13. 打印 Java 传递过来的 booleanArray
    jsize booArrayLength = env->GetArrayLength(bArray_);
    jboolean *bArray = env->GetBooleanArrayElements(bArray_, NULL);
    for (int i = 0; i < booArrayLength; ++i) {
        bool b = bArray[i];
        jboolean b2 = bArray[i];
        LOGD("boolean:%d", b)
        LOGD("jboolean:%d", b2)
    }
    //回收
    env->ReleaseBooleanArrayElements(bArray_, bArray, 0);

}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_devyk_ndk_1sample_MainActivity_getPerson(JNIEnv *env, jobject instance) {

    //1. 拿到 Java 类的全路径
    const char *person_java = "com.devyk.ndk_sample.Person";
    const char *method = "<init>"; // Java构造方法的标识

    //2. 找到需要处理的 Java 对象 class
    jclass j_person_class = env->FindClass(person_java);

    //3. 拿到空参构造方法
    jmethodID person_constructor = env->GetMethodID(j_person_class, method, "()V");

    //4. 创建对象
    jobject person_obj = env->NewObject(j_person_class, person_constructor);

    //5. 拿到 setName 方法的签名，并拿到对应的 setName 方法
    const char *nameSig = "(Ljava/lang/String;)V";
    jmethodID nameMethodId = env->GetMethodID(j_person_class, "setName", nameSig);

    //6. 拿到 setAge 方法的签名，并拿到 setAge 方法
    const char *ageSig = "(I)V";
    jmethodID ageMethodId = env->GetMethodID(j_person_class, "setAge", ageSig);

    //7. 正在调用 Java 对象函数
    const char *name = "devyk";
    jstring newStringName = env->NewStringUTF(name);
    env->CallVoidMethod(person_obj, nameMethodId, newStringName);
    env->CallVoidMethod(person_obj, ageMethodId, 28);

    const char *sig = "()Ljava/lang/String;";
    jmethodID jtoString = env->GetMethodID(j_person_class, "toString", sig);
    jobject obj_string = env->CallObjectMethod(person_obj, jtoString);
    jstring perStr = static_cast<jstring >(obj_string);
    const char *itemStr2 = env->GetStringUTFChars(perStr, NULL);
    LOGD("Person: %s", itemStr2);
    return person_obj;
}

/**
 * TODO 动态注册
*/

/**
 * 对应java类的全路径名，.用/代替
 */
const char *classPathName = "com/devyk/ndk_sample/MainActivity";

extern "C"  //支持 C 语言
JNIEXPORT void JNICALL //告诉虚拟机，这是jni函数
native_dynamicRegister(JNIEnv *env, jobject instance, jstring name) {
    const char *j_name = env->GetStringUTFChars(name, NULL);
    LOGD("动态注册: %s", j_name)
    //释放
    env->ReleaseStringUTFChars(name, j_name);
}

extern "C"  //支持 C 语言
JNIEXPORT void JNICALL //告诉虚拟机，这是jni函数
native_dynamicRegister2(JNIEnv *env, jobject instance, jstring name) {
    const char *j_name = env->GetStringUTFChars(name, NULL);
    LOGD("动态注册: %s", j_name)

    jclass clazz = env->GetObjectClass(instance);//拿到当前类的class
    jmethodID mid = env->GetMethodID(clazz, "testException", "()V");//执行 Java 测试抛出异常的代码
    env->CallVoidMethod(instance, mid); // 执行会抛出一个异常
    jthrowable exc = env->ExceptionOccurred(); // 检测是否发生异常
    if (exc) {//如果发生异常
        env->ExceptionDescribe(); // 打印异常信息
        env->ExceptionClear(); // 清除掉发生的异常
        jclass newExcCls = env->FindClass("java/lang/IllegalArgumentException");
        env->ThrowNew(newExcCls, "JNI 中发生了一个异常信息"); // 返回一个新的异常到 Java
    }

    //释放
    env->ReleaseStringUTFChars(name, j_name);
}

jclass personClass;
extern "C"  //支持 C 语言
JNIEXPORT void JNICALL //告诉虚拟机，这是jni函数
native_test4(JNIEnv *env, jobject instance) {
    LOGD("测试局部引用")


    if (personClass == NULL) {
//        const char *person_class = "com/devyk/ndk_sample/Person";
//        personClass = env->FindClass(person_class);

        //1. 提升全局解决不能重复使用问题
        const char *person_class = "com/devyk/ndk_sample/Person";
        jclass jclass1 = env->FindClass(person_class);
//        personClass = static_cast<jclass>(env->NewGlobalRef(jclass1));
        personClass = static_cast<jclass>(env->NewWeakGlobalRef(jclass1));
        LOGD("personClass == null 执行了。")
    }

    //Java Person 构造方法实例化
    const char *sig = "()V";
    const char *method = "<init>";//Java 构造方法标识
    jmethodID init = env->GetMethodID(personClass, method, sig);
    //创建出来
    env->NewObject(personClass, init);

    //2. 显式释放主动删除局部引用
//    env->DeleteLocalRef(personClass);
    env->DeleteWeakGlobalRef(personClass);
    personClass = NULL;


}

extern "C"  //支持 C 语言
JNIEXPORT void JNICALL //告诉虚拟机，这是jni函数
native_count(JNIEnv *env, jobject instance) {

    jclass cls = env->GetObjectClass(instance);
    jfieldID fieldID = env->GetFieldID(cls, "count", "I");

    if (env->MonitorEnter(instance) != JNI_OK) {
        LOGE("%s: MonitorEnter() failed", __FUNCTION__);
    }

    /* synchronized block */
    int val = env->GetIntField(instance, fieldID);
    val++;
    LOGI("count=%d", val);
    env->SetIntField(instance, fieldID, val);

    if (env->ExceptionOccurred()) {
        LOGE("ExceptionOccurred()...");
        if (env->MonitorExit(instance) != JNI_OK) {
            LOGE("%s: MonitorExit() failed", __FUNCTION__);
        };
    }

    if (env->MonitorExit(instance) != JNI_OK) {
        LOGE("%s: MonitorExit() failed", __FUNCTION__);
    };

}


JavaVM * jvm;
jobject instance;
void * customThread(void * pVoid) {


    // 调用的话，一定需要JNIEnv *env
    // JNIEnv *env 无法跨越线程，只有JavaVM才能跨越线程

    JNIEnv * env = NULL; // 全新的env
    int result = jvm->AttachCurrentThread(&env, 0); // 把native的线程，附加到JVM
    if (result != 0) {
        return 0;
    }

    jclass mainActivityClass = env->GetObjectClass(instance);

    // 拿到MainActivity的updateUI
    const char * sig = "()V";
    jmethodID updateUI = env->GetMethodID(mainActivityClass, "updateUI", sig);

    env->CallVoidMethod(instance, updateUI);

    // 解除 附加 到 JVM 的native线程
    jvm->DetachCurrentThread();

    return 0;
}

extern "C"  //支持 C 语言
JNIEXPORT void JNICALL //告诉虚拟机，这是jni函数
native_testThread(JNIEnv *env, jobject thiz) {
    instance = env->NewGlobalRef(thiz); // 全局的，就不会被释放，所以可以在线程里面用
    // 如果是非全局的，函数一结束，就被释放了
    pthread_t pthreadID;
    pthread_create(&pthreadID, 0, customThread, instance);
    pthread_join(pthreadID, 0);

}

extern "C"  //支持 C 语言
JNIEXPORT void JNICALL //告诉虚拟机，这是jni函数
native_unThread(JNIEnv *env, jobject thiz) {

    if (NULL != instance) {
        env->DeleteGlobalRef(instance);
        instance = NULL;
    }

}


/* 源码结构体
 * typedef struct {
    const char* name;
    const char* signature;
    void*       fnPtr;
    } JNINativeMethod;
 */
/**
 * JNINativeMethod 结构体的数组
 * 结构体参数1：对应java类总的native方法
 * 结构体参数2：对应java类总的native方法的描述信息，用javap -s xxxx.class 查看
 * 结构体参数3：c/c++ 种对应的方法名
 */

static const JNINativeMethod jniNativeMethod[] = {
        {"dynamicRegister",  "(Ljava/lang/String;)V", (void *) (native_dynamicRegister)},
        {"dynamicRegister2", "(Ljava/lang/String;)V", (void *) (native_dynamicRegister2)},
        {"test4",            "()V",                   (void *) (native_test4)},
        {"nativeCount",      "()V",                   (void *) (native_count)},
        {"testThread",       "()V",                   (void *) (native_testThread)},
        {"unThread",         "()V",                   (void *) (native_unThread)}
};


/**
 * 该函数定义在jni.h头文件中，System.loadLibrary()时会调用JNI_OnLoad()函数
 */
JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *javaVm, void *pVoid) {
    jvm = javaVm;
    //通过虚拟机 创建全新的 evn
    JNIEnv *jniEnv = NULL;
    jint result = javaVm->GetEnv(reinterpret_cast<void **>(&jniEnv), JNI_VERSION_1_6);
    if (result != JNI_OK) {
        return JNI_ERR; // 主动报错
    }
    jclass mainActivityClass = jniEnv->FindClass(classPathName);
    jniEnv->RegisterNatives(mainActivityClass, jniNativeMethod,
                            sizeof(jniNativeMethod) / sizeof(JNINativeMethod));//动态注册的数量

    return JNI_VERSION_1_6;
}

