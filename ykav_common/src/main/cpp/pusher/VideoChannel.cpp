//
// Created by 阳坤 on 2020-02-27.
//

#include <macro.h>
#include "include/VideoChannel.h"


void *_startEncoder(void *pVoid) {
    VideoEncoderChannel *videoEncoderChannel = static_cast<VideoEncoderChannel *>(pVoid);
    videoEncoderChannel->onEncoder();
    return 0;
}

VideoEncoderChannel::VideoEncoderChannel() {
    //初始化互斥锁
    pthread_mutex_init(&mMutex, 0);

}

VideoEncoderChannel::VideoEncoderChannel(PushCallback *pCallback, int mediacodec) {
    //初始化互斥锁
    pthread_mutex_init(&mMutex, 0);
    this->mIPushCallback = pCallback;
    this->isMediaCodec = mediacodec;
}

VideoEncoderChannel::~VideoEncoderChannel() {
    release();
}

void VideoEncoderChannel::release() {
    isStart = false;
    pthread_mutex_destroy(&mMutex);
    if (mVideoCodec) {
        x264_encoder_close(mVideoCodec);
        mVideoCodec = 0;
    }

    if (pic_in) {
        x264_picture_clean(pic_in);
        DELETE (pic_in)
    }
    mVideoPackets.clearQueue();

}

void VideoEncoderChannel::setVideoCallback(VideoEncoderCallback videoCallback) {
    this->mVideoCallback = videoCallback;
}

void VideoEncoderChannel::setVideoEncoderInfo(int width, int height, int fps, int bit) {
    pthread_mutex_lock(&mMutex);
    this->mWidth = width;
    this->mHeight = height;
    this->mFps = fps;
    this->mBit = bit;
    this->mY_Size = width * height;
    this->mUV_Size = mY_Size / 4;

    //如果编码器已经存在，需要释放
    if (mVideoCodec || pic_in) {
        release();
    }


    //打开x264编码器
    //x264编码器的属性
    x264_param_t param;
    //2： 最快
    //3:  无延迟编码
    x264_param_default_preset(&param, x264_preset_names[0], x264_tune_names[7]);
    //base_line 3.2 编码规格
    param.i_level_idc = 32;
    //输入数据格式
    param.i_csp = X264_CSP_I420;
    param.i_width = width;
    param.i_height = height;
    //无b帧
    param.i_bframe = 0;
    //参数i_rc_method表示码率控制，CQP(恒定质量)，CRF(恒定码率)，ABR(平均码率)
    param.rc.i_rc_method = X264_RC_ABR;
    //码率(比特率,单位Kbps)
    param.rc.i_bitrate = mBit;
    //瞬时最大码率
    param.rc.i_vbv_max_bitrate = mBit * 1.2;
    //设置了i_vbv_max_bitrate必须设置此参数，码率控制区大小,单位kbps
    param.rc.i_vbv_buffer_size = mBit;

    //帧率
    param.i_fps_num = fps;
    param.i_fps_den = 1;
    param.i_timebase_den = param.i_fps_num;
    param.i_timebase_num = param.i_fps_den;
//    param.pf_log = x264_log_default2;
    //用fps而不是时间戳来计算帧间距离
    param.b_vfr_input = 0;
    //帧距离(关键帧)  2s一个关键帧
    param.i_keyint_max = fps * 2;
    // 是否复制sps和pps放在每个关键帧的前面 该参数设置是让每个关键帧(I帧)都附带sps/pps。
    param.b_repeat_headers = 1;
    //多线程
    param.i_threads = 1;

    x264_param_apply_profile(&param, "baseline");
    //打开编码器
    mVideoCodec = x264_encoder_open(&param);
    pic_in = new x264_picture_t;
    x264_picture_alloc(pic_in, X264_CSP_I420, width, height);
    //相当于重启编码器
    isStart = true;
    pthread_mutex_unlock(&mMutex);


}

/**
 * 放入编码队列
 * @param data  I420 格式的数据
 */
void VideoEncoderChannel::encodeData(int8_t *data) {
    if (isStart)
        mVideoPackets.push(data);
}


/**
 * 开启编码线程
 */
void VideoEncoderChannel::startEncoder() {
    if (isMediaCodec) {
        LOGD("不需要开启软件编码");
        return;
    }
    mVideoPackets.setFlag(1);
    isStart = true;
    pthread_create(&mPid, 0, _startEncoder, this);
}

/**
 * 真正软编码的函数
 */
void VideoEncoderChannel::onEncoder() {

    while (isStart) {

        if (!mVideoCodec) {
            continue;
        }

        int8_t *data = 0;
        mVideoPackets.pop(data);
        if (!data) {
            LOGE("获取 YUV 数据错误");
            continue;
        }

        //copy Y 数据
        memcpy(this->pic_in->img.plane[0], data, mY_Size);
        //拿到 UV 数据
        for (int i = 0; i < mUV_Size; ++i) {
            //拿到 u 数据
            *(pic_in->img.plane[1] + i) = *(data + mY_Size + i * 2 + 1);
            //拿到 v 数据
            *(pic_in->img.plane[2] + i) = *(data + mY_Size + i * 2);
        }

        //编码出来的数据
        x264_nal_t *pp_nal;
        //编码出来的帧数量
        int pi_nal = 0;
        x264_picture_t pic_out;
        //开始编码
        x264_encoder_encode(mVideoCodec, &pp_nal, &pi_nal, pic_in, &pic_out);

        //如果是关键帧
        int sps_len = 0;
        int pps_len = 0;
        uint8_t sps[100];
        uint8_t pps[100];

        for (int i = 0; i < pi_nal; ++i) {
            if (pp_nal[i].i_type == NAL_SPS) {
                //排除掉 h264的间隔 00 00 00 01
                sps_len = pp_nal[i].i_payload - 4;
                memcpy(sps, pp_nal[i].p_payload + 4, sps_len);
            } else if (pp_nal[i].i_type == NAL_PPS) {
                pps_len = pp_nal[i].i_payload - 4;
                memcpy(pps, pp_nal[i].p_payload + 4, pps_len);
                //pps肯定是跟着sps的
                sendSpsPps(sps, pps, sps_len, pps_len);
            } else {
                sendFrame(pp_nal[i].i_type, pp_nal[i].p_payload, pp_nal[i].i_payload, 0);
            }
        }
    }

}


/**
 * 发送 sps pps
 * @param sps  编码第一帧数据
 * @param pps  编码第二帧数据
 * @param sps_len  编码第一帧数据的长度
 * @param pps_len  编码第二帧数据的长度
 */
void VideoEncoderChannel::sendSpsPps(uint8_t *sps, uint8_t *pps, int sps_len, int pps_len) {
    int bodySize = 13 + sps_len + 3 + pps_len;
    RTMPPacket *packet = new RTMPPacket;
    //
    RTMPPacket_Alloc(packet, bodySize);
    int i = 0;
    //固定头
    packet->m_body[i++] = 0x17;
    //类型
    packet->m_body[i++] = 0x00;
    //composition time 0x000000
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;

    //版本
    packet->m_body[i++] = 0x01;
    //编码规格
    packet->m_body[i++] = sps[1];
    packet->m_body[i++] = sps[2];
    packet->m_body[i++] = sps[3];
    packet->m_body[i++] = 0xFF;

    //整个sps
    packet->m_body[i++] = 0xE1;
    //sps长度
    packet->m_body[i++] = (sps_len >> 8) & 0xff;
    packet->m_body[i++] = sps_len & 0xff;
    memcpy(&packet->m_body[i], sps, sps_len);
    i += sps_len;

    //pps
    packet->m_body[i++] = 0x01;
    packet->m_body[i++] = (pps_len >> 8) & 0xff;
    packet->m_body[i++] = (pps_len) & 0xff;
    memcpy(&packet->m_body[i], pps, pps_len);

    //视频
    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nBodySize = bodySize;
    //随意分配一个管道（尽量避开rtmp.c中使用的）
    packet->m_nChannel = 0x10;
    //sps pps没有时间戳
    packet->m_nTimeStamp = 0;
    //不使用绝对时间
    packet->m_hasAbsTimestamp = 0;
    packet->m_headerType = RTMP_PACKET_SIZE_MEDIUM;

    mVideoCallback(packet);
}

/**
 * 发送视频帧 -- 关键帧
 * @param type
 * @param payload
 * @param i_playload
 */
void VideoEncoderChannel::sendFrame(int type, uint8_t *payload, int i_payload, long timestamp) {
    if (payload[2] == 0x00) {
        i_payload -= 4;
        payload += 4;
    } else {
        i_payload -= 3;
        payload += 3;
    }
    //看表
    int bodySize = 9 + i_payload;
    RTMPPacket *packet = new RTMPPacket;
    //
    RTMPPacket_Alloc(packet, bodySize);

    packet->m_body[0] = 0x27;
    if (type == NAL_SLICE_IDR) {
        packet->m_body[0] = 0x17;
        LOGE("关键帧");
    }
    //类型
    packet->m_body[1] = 0x01;
    //时间戳
    packet->m_body[2] = 0x00;
    packet->m_body[3] = 0x00;
    packet->m_body[4] = 0x00;
    //数据长度 int 4个字节
    packet->m_body[5] = (i_payload >> 24) & 0xff;
    packet->m_body[6] = (i_payload >> 16) & 0xff;
    packet->m_body[7] = (i_payload >> 8) & 0xff;
    packet->m_body[8] = (i_payload) & 0xff;

    //图片数据
    memcpy(&packet->m_body[9], payload, i_payload);

    packet->m_hasAbsTimestamp = 0;
    packet->m_nBodySize = bodySize;
    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nChannel = 0x10;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    if (mVideoCallback)
        mVideoCallback(packet);
}


/**
 * 重新开始
 */
void VideoEncoderChannel::restart() {
//    mVideoPackets.setFlag(1);
}

/**
 * 停止
 */
void VideoEncoderChannel::stop() {
//    mVideoPackets.setFlag(0);
}


/**
 *
 * @param type  视频帧类型
 * @param buf  H264
 * @param len H264 长度
 */
void VideoEncoderChannel::sendH264(int type, uint8_t *data, int dataLen, int timeStamp) {


    RTMPPacket *packet = (RTMPPacket *) malloc(sizeof(RTMPPacket));
    RTMPPacket_Alloc(packet, dataLen);
    RTMPPacket_Reset(packet);

    packet->m_nChannel = 0x04; //视频

    if (type == RTMP_PACKET_KEY_FRAME) {
        LOGE("视频关键帧");
    }

    memcpy(packet->m_body, data, dataLen);
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    packet->m_hasAbsTimestamp = FALSE;
    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nBodySize = dataLen;
    if (mVideoCallback)
        mVideoCallback(packet);

}

void VideoEncoderChannel::setMediaCodec(int mediacodec) {
    this->isMediaCodec = mediacodec;
}



