package com.devyk;

import android.os.Environment;

/**
 * <pre>
 *     author  : devyk on 2020-01-19 09:49
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is Constants
 * </pre>
 */
public class Constants {

    /**
     * 是否硬编码
     */
    public static boolean isMediaCodec = true;


    //湖南卫视
//    public static final String HUNAN_PATH = "rtmp://58.200.131.2:1935/livetv/hunantv";
    public static final String HUNAN_PATH = "rtmp://49.235.159.44:1992/devykLive/live1";
    //HTTP 拉流
    public static final String HTTP_PATH = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8";
    //rtsp 拉流--》测试暂时播放不成功
    public static final String RTSP_PATH = "rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov";
    //Http MP4 文件
    public static final String MP4_PLAY = "http://vfx.mtime.cn/Video/2019/02/04/mp4/190204084208765161.mp4";
    //本地视频文件
    public static final String LOCAL_FILE = Environment.getExternalStorageDirectory() + "/news.mov";
//    public static final String LOCAL_FILE = Environment.getExternalStorageDirectory() + "/zhangjie_mo.mov";

    public static final String nativePath = Environment.getExternalStorageDirectory() + "/NDKCrash";
    public static final String JavaPath = Environment.getExternalStorageDirectory() + "/JavaCrash";
    public static String RTMP_PUSH = "rtmp://www.devyk.cn:1992/devykLive/live1";

    /**
     * 播放错误码
     */
    public interface IMessageType {

        //打不开媒体数据源
        // #define FFMPEG_CAN_NOT_OPEN_URL (ERROR_CODE_FFMPEG_PREPARE - 1)
        int FFMPEG_CAN_NOT_OPEN_URL = -1;

        //找不到媒体流信息
        // #define FFMPEG_CAN_NOT_FIND_STREAMS (ERROR_CODE_FFMPEG_PREPARE - 2)
        int FFMPEG_CAN_NOT_FIND_STREAMS = -2;

        //找不到解码器
        // #define FFMPEG_FIND_DECODER_FAIL (ERROR_CODE_FFMPEG_PREPARE - 3)
        int FFMPEG_FIND_DECODER_FAIL = -3;

        //无法根据解码器创建上下文
        // #define FFMPEG_ALLOC_CODEC_CONTEXT_FAIL (ERROR_CODE_FFMPEG_PREPARE - 4)
        int FFMPEG_ALLOC_CODEC_CONTEXT_FAIL = -4;

        //根据流信息 配置上下文参数失败
        // #define FFMPEG_CODEC_CONTEXT_PARAMETERS_FAIL (ERROR_CODE_FFMPEG_PREPARE - 5)
        int FFMPEG_CODEC_CONTEXT_PARAMETERS_FAIL = -5;

        //打开解码器失败
        // #define FFMPEG_OPEN_DECODER_FAIL (ERROR_CODE_FFMPEG_PREPARE - 6)
        int FFMPEG_OPEN_DECODER_FAIL = -6;

        //没有音视频
        // #define FFMPEG_NOMEDIA (ERROR_CODE_FFMPEG_PREPARE - 7)
        int FFMPEG_NOMEDIA = -7;

        //读取媒体数据包失败
        // #define FFMPEG_READ_PACKETS_FAIL (ERROR_CODE_FFMPEG_PLAY - 8)
        int FFMPEG_READ_PACKETS_FAIL = -8;


        //rtmp 初始化失败
        int RTMP_INIT_ERROR = -9;
        //设置 rtmp url 失败
        int RTMP_SET_URL_ERROR = -10;
        //连接服务器失败
        int RTMP_CONNECT_ERROR = -11;
        int FAAC_ENC_OPEN_ERROR = -12;
        int RTMP_PUSHER_ERROR  = -13;

    }


    public static boolean supportTouchFocus = false;
    public static boolean touchFocusMode = false;
}
