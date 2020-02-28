//
// Created by 阳坤 on 2020-01-19.
//



#ifndef NDK_SAMPLE_CONSTANTS_H
#define NDK_SAMPLE_CONSTANTS_H

// TODO 专门定义宏

//引入 log
#include "macro.h"


#define THREAD_MAIN 1   // 此宏代表 主线程的意思
#define THREAD_CHILD 2  // 此宏代表 子线程的意思

//打不开媒体数据源
#define FFMPEG_CAN_NOT_OPEN_URL -1
//找不到媒体流信息
#define FFMPEG_CAN_NOT_FIND_STREAMS -2
//找不到解码器
#define FFMPEG_FIND_DECODER_FAIL -3
//无法根据解码器创建上下文
#define FFMPEG_ALLOC_CODEC_CONTEXT_FAIL -4
//根据流信息 配置上下文参数失败
#define FFMPEG_CODEC_CONTEXT_PARAMETERS_FAIL -5
//打开解码器失败
#define FFMPEG_OPEN_DECODER_FAIL -6
//没有音视频
#define FFMPEG_NOMEDIA -7

//读取媒体数据包失败
#define FFMPEG_READ_PACKETS_FAIL (ERROR_CODE_FFMPEG_PLAY - 8)



//rtmp 初始化失败
#define RTMP_INIT_ERROR  -9
//设置 rtmp url 失败
#define RTMP_SET_URL_ERROR  -10
//连接服务器失败
#define RTMP_CONNECT_ERROR  -11

//语音编码器打开失败
#define FAAC_ENC_OPEN_ERROR  -12


#endif //NDK_SAMPLE_CONSTANTS_H

