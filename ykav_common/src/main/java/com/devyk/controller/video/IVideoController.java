package com.devyk.controller.video;

import com.devyk.configuration.VideoConfiguration;
import com.devyk.video.OnVideoEncodeListener;

/**
 * @Title: IVideoController
 * @Package com.devyk.controller.video
 * @Description:
 * @Author Jim
 * @Date 2016/11/2
 * @Time 下午2:17
 * @Version
 */

public interface IVideoController {
    void start();
    void stop();
    void pause();
    void resume();
    boolean setVideoBps(int bps);
    void setVideoEncoderListener(OnVideoEncodeListener listener);
    void setVideoConfiguration(VideoConfiguration configuration);
}
