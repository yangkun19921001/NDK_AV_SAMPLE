// Created by 阳坤 on 2020-02-27.

#include "include/AudioChannel.h"


AudioEncoderChannel::AudioEncoderChannel() {

}

AudioEncoderChannel::~AudioEncoderChannel() {
    release();
}

void AudioEncoderChannel::release() {
    isStart = false;
    //释放编码器
    if (mAudioCodec) {
        faacEncClose(mAudioCodec);
        DELETE(mBuffer);
        mAudioCodec = 0;
    }

}

void AudioEncoderChannel::setAudioCallback(AudioCallback audioCallback) {
    this->mAudioCallback = audioCallback;
}

AudioEncoderChannel::AudioEncoderChannel(PushCallback *pCallback, int mediacodec) {
    this->mIPushCallback = pCallback;
    this->isMediaCodec = mediacodec;

}

/**
 * rtmp 连接成功 开始编码
 */
void AudioEncoderChannel::startEncoder() {
    if (isMediaCodec)
        return;
    isStart = true;
}


//设置语音软编码参数
void AudioEncoderChannel::setAudioEncoderInfo(int samplesHZ, int channel) {
    release();
    //通道
    mChannels = channel;
    //打开编码器
    //3、一次最大能输入编码器的样本数量 也编码的数据的个数 (一个样本是16位 2字节)
    //4、最大可能的输出数据  编码后的最大字节数
    mAudioCodec = faacEncOpen(samplesHZ, channel, &mInputSamples, &mMaxOutputBytes);
    if (!mAudioCodec) {
        if (mIPushCallback) {
            mIPushCallback->onError(THREAD_MAIN, FAAC_ENC_OPEN_ERROR);
        }
        return;
    }

    //设置编码器参数
    faacEncConfigurationPtr config = faacEncGetCurrentConfiguration(mAudioCodec);
    //指定为 mpeg4 标准
    config->mpegVersion = MPEG4;
    //lc 标准
    config->aacObjectType = LOW;
    //16位
    config->inputFormat = FAAC_INPUT_16BIT;
    // 编码出原始数据 既不是adts也不是adif
    config->outputFormat = 0;
    faacEncSetConfiguration(mAudioCodec, config);

    //输出缓冲区 编码后的数据 用这个缓冲区来保存
    mBuffer = new u_char[mMaxOutputBytes];
    isStart = true;
}

int AudioEncoderChannel::getInputSamples() {
    return mInputSamples;
}

/**
 * 真正软编码的函数
 * @param data
 */
void AudioEncoderChannel::encodeData(int8_t *data) {
    if (!mAudioCodec || !isStart)
        return;
    //返回编码后的数据字节长度
    int bytelen = faacEncEncode(mAudioCodec, reinterpret_cast<int32_t *>(data), mInputSamples,
                                mBuffer, mMaxOutputBytes);
    if (bytelen > 0) {
        //开始打包 rtmp
        int bodySize = 2 + bytelen;
        RTMPPacket *packet = new RTMPPacket;
        RTMPPacket_Alloc(packet, bodySize);
        //双声道
        packet->m_body[0] = 0xAF;
        if (mChannels == 1) {
            packet->m_body[0] = 0xAE;
        }
        //编码出的声音 都是 0x01
        packet->m_body[1] = 0x01;
        //图片数据
        memcpy(&packet->m_body[2], mBuffer, bytelen);

        packet->m_hasAbsTimestamp = FALSE;
        packet->m_nBodySize = bodySize;
        packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
        packet->m_nChannel = 0x11;
        packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
        //发送 rtmp packet
        mAudioCallback(packet);
    }
}

/**
 * 音频头包数据
 * @return
 */
RTMPPacket *AudioEncoderChannel::getAudioTag() {
    if (!mAudioCodec) {
        setAudioEncoderInfo(FAAC_DEFAUTE_SAMPLE_RATE, FAAC_DEFAUTE_SAMPLE_CHANNEL);
        if (!mAudioCodec)return 0;
    }
    u_char *buf;
    u_long len;
    faacEncGetDecoderSpecificInfo(mAudioCodec, &buf, &len);
    int bodySize = 2 + len;
    RTMPPacket *packet = new RTMPPacket;
    RTMPPacket_Alloc(packet, bodySize);
    //双声道
    packet->m_body[0] = 0xAF;
    if (mChannels == 1) {
        packet->m_body[0] = 0xAE;
    }
    packet->m_body[1] = 0x00;
    //图片数据
    memcpy(&packet->m_body[2], buf, len);

    packet->m_hasAbsTimestamp = FALSE;
    packet->m_nBodySize = bodySize;
    packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
    packet->m_nChannel = 0x11;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    return packet;
}

/**
 * 恢复
 */
void AudioEncoderChannel::restart() {
    isStart = true;
}

/**
 * 停止
 */
void AudioEncoderChannel::stop() {
    isStart = false;
}


/**
 * 直接推送 AAC 硬编码
 * @param data
 */
void AudioEncoderChannel::pushAAC(u_char *data, int dataLen, long timestamp) {
    RTMPPacket *packet = (RTMPPacket *) malloc(sizeof(RTMPPacket));
    RTMPPacket_Alloc(packet, dataLen);
    RTMPPacket_Reset(packet);
    packet->m_nChannel = 0x05; //音频
    memcpy(packet->m_body, data, dataLen);
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    packet->m_hasAbsTimestamp = FALSE;
    packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
    packet->m_nBodySize = dataLen;
    if (mAudioCallback)
        mAudioCallback(packet);

}

void AudioEncoderChannel::setMediaCodec(int mediacodec) {
    this->isMediaCodec = mediacodec;

}






