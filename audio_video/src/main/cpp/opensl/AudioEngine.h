//
// Created by 阳坤 on 2020-02-09.
//

#ifndef NDK_SAMPLE_AUDIOENGINE_H
#define NDK_SAMPLE_AUDIOENGINE_H

#include <SLES/OpenSLES.h>
#include <stdio.h>
#include <SLES/OpenSLES_Android.h>
#include <assert.h>
#include <android/log.h>

class AudioEngine {
public:
    SLObjectItf engineObj;
    SLEngineItf engine;

    SLObjectItf outputMixObj;

private:
    void createEngine() {
        // 音频的播放，就涉及到了，OpenLSES
        // TODO 第一大步：创建引擎并获取引擎接口
        // 1.1创建引擎对象：SLObjectItf engineObject
        SLresult result = slCreateEngine(&engineObj, 0, NULL, 0, NULL, NULL);
        if (SL_RESULT_SUCCESS != result) {
            return;
        }

        // 1.2 初始化引擎
        result = (*engineObj) ->Realize(engineObj, SL_BOOLEAN_FALSE);
        if (SL_BOOLEAN_FALSE != result) {
            return;
        }

        // 1.3 获取引擎接口 SLEngineItf engineInterface
        result = (*engineObj) ->GetInterface(engineObj, SL_IID_ENGINE, &engine);
        if (SL_RESULT_SUCCESS != result) {
            return;
        }

        // TODO 第二大步 设置混音器
        // 2.1 创建混音器：SLObjectItf outputMixObject
        result = (*engine)->CreateOutputMix(engine, &outputMixObj, 0, 0, 0);

        if (SL_RESULT_SUCCESS != result) {
            return;
        }

        // 2.2 初始化 混音器
        result = (*outputMixObj)->Realize(outputMixObj, SL_BOOLEAN_FALSE);
        if (SL_BOOLEAN_FALSE != result) {
            return;
        }
    }

    virtual void release() {
        if (outputMixObj) {
            (*outputMixObj)->Destroy(outputMixObj);
            outputMixObj = nullptr;
        }

        if (engineObj) {
            (*engineObj)->Destroy(engineObj);
            engineObj = nullptr;
            engine = nullptr;
        }
    }

public:
    AudioEngine() : engineObj(nullptr), engine(nullptr), outputMixObj(nullptr) {
        createEngine();
    }

    virtual ~AudioEngine() {
        release();
    }
};


#endif //NDK_SAMPLE_AUDIOENGINE_H
