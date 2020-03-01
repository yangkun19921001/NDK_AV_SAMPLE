//
// Created by 阳坤 on 2020-02-27.
//

#include "include/RTMPModel.h"
#include "include/PushCallback.h"

RTMPModel *rtmpModel = 0;

void *start(void *pVoid) {
    rtmpModel = static_cast<RTMPModel *>(pVoid);
    rtmpModel->onConnect();
    return 0;
}

void releasePackets(RTMPPacket *&packet) {
    if (packet) {
        RTMPPacket_Free(packet);
        delete packet;
        packet = 0;
    }
}

int32_t getStreamId() {
    return rtmpModel->rtmp->m_stream_id;
}


void callback(RTMPPacket *packet) {
    if (packet) {
        if (rtmpModel) {
            //设置时间戳
            packet->m_nTimeStamp = RTMP_GetTime() - rtmpModel->mStartTime;
            rtmpModel->mPackets.push(packet);
        }
    }
}

/**
 * 设置释放 rtmp 包
 */
void RTMPModel::setPacketReleaseCallback() {
    mPackets.setRtmpReleaseCallback(releasePackets);
}

RTMPModel::RTMPModel(PushCallback *pCallback, AudioEncoderChannel *audioEncoderChannel,
                     VideoEncoderChannel *videoEncoderChannel, int mediacodec) {
    this->pushCallback = pCallback;
    this->mAudioChannel = audioEncoderChannel;
    this->mVideoChannel = videoEncoderChannel;
    //设置语音视频包的监听
    this->mVideoChannel->setVideoCallback(callback);
    this->mAudioChannel->setAudioCallback(callback);
    this->isMediacodec = mediacodec;
    //设置需要释放语音视频 rtmp 包
    setPacketReleaseCallback();
}


RTMPModel::~RTMPModel() {
    if (pushCallback) {
        pushCallback = nullptr;
        pushCallback = 0;
    }

}


/**
 * 真正推流的地方
 */
void RTMPModel::onPush() {
    RTMPPacket *packet = 0;
    while (isStart) {

        if (!isStart) {
            LOGE("释放 native 资源 isStart");
            return;
        }

        mPackets.pop(packet);


        if (!readyPushing) {
            releasePackets(packet);
            return;
        }
        if (!packet) {
            LOGE("释放 native 资源 isStart");
            continue;
        }

        packet->m_nInfoField2 = rtmp->m_stream_id;
        int ret = RTMP_SendPacket(rtmp, packet, 1);
        if (!ret) {
            LOGE("发送失败")
            if (pushCallback) {
                pushCallback->onError(THREAD_CHILD, RTMP_PUSHER_ERROR);
            }
            continue;
        }
    }
    releasePackets(packet);
    release();//释放

}


/**
 * 真正 rtmp 连接的函数
 */
void RTMPModel::onConnect() {
    if (pushCallback)
        pushCallback->onRtmpConnect(THREAD_CHILD);
    if (rtmp && isStart) {
        RTMP_Close(rtmp);
        RTMP_Free(rtmp);
        rtmp = 0;
    }
    this->rtmp = RTMP_Alloc();
    if (!rtmp) {
        if (pushCallback) {
            pushCallback->onError(THREAD_CHILD, RTMP_INIT_ERROR);
        }
        return;
    }

    //初始化
    RTMP_Init(rtmp);
    //设置地址
    int ret = RTMP_SetupURL(rtmp, this->url);
    if (!ret) {
        if (pushCallback) {
            pushCallback->onError(THREAD_CHILD, RTMP_SET_URL_ERROR);
        }
        return;
    }

    //设置超时时间 单位 5
    rtmp->Link.timeout = 5;
    RTMP_EnableWrite(rtmp);

    ret = RTMP_Connect(rtmp, 0);
    if (!ret) {
        if (pushCallback) {
            pushCallback->onError(THREAD_CHILD, RTMP_CONNECT_ERROR);
        }
        return;
    }

    ret = RTMP_ConnectStream(rtmp, 0);
    if (!ret) {
        if (pushCallback) {
            pushCallback->onError(THREAD_CHILD, RTMP_CONNECT_ERROR);
        }
        return;
    }
    //记录一个开始时间
    mStartTime = RTMP_GetTime();
    //表示可以开始推流了
    readyPushing = true;
    isStart = true;
    if (pushCallback)
        pushCallback->onRtmpSucceed(THREAD_CHILD);


    //队列可以开始工作了
    mPackets.setFlag(true);

    //通知音频。视频模块可以开始编码了
    if (!isMediacodec) {
        this->mAudioChannel->startEncoder();
        this->mVideoChannel->startEncoder();
        //保证第一个数据包是音频
        if (mAudioChannel->getAudioTag())
            callback(mAudioChannel->getAudioTag());
    }

    onPush();//死循环阻塞获取推流数据

}

void RTMPModel::_onConnect(const char *url) {
    //开始链接

    //防止 java 传递过来的 jstring 释放。
    char *rtmpUrl = new char[strlen(url) + 1];
    strcpy(rtmpUrl, url);
    this->url = rtmpUrl;
    pthread_create(&mPid, 0, start, this);

}

void RTMPModel::release() {
    isStart = false;
    readyPushing = false;
    if (rtmp) {
        RTMP_Close(rtmp);
        RTMP_Free(rtmp);
        rtmp = 0;
        LOGE("释放 native 资源");
    }
    mPackets.clearQueue();
}

void RTMPModel::restart() {
    mPackets.setFlag(1);
}

void RTMPModel::stop() {
    mPackets.setFlag(0);
}

void RTMPModel::setMediaCodec(int mediacodec) {
    this->isMediacodec = mediacodec;
}



