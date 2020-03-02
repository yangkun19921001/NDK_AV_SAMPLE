package com.devyk.pusher_common;

import android.app.Activity;
import android.view.SurfaceHolder;

/**
 * <pre>
 *     author  : devyk on 2020-02-25 14:48
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is VideoParameter
 * </pre>
 */
public class AudioVideoParameter {

    public int width;

    public int height;

    public int bitrate;

    public int fps;

    public int cameraId;

    public int rotation;

    public boolean isMediaCodec;

    public SurfaceHolder surfaceHolder;

    public static final class Builder {
        int cameraId;
        private int width;
        private int height;
        private int bitrate;
        private int fps;
        private int rotation;

        private boolean isMediaCodec;




        private SurfaceHolder surfaceHolder;


        public static Builder asBuilder() {
            return new Builder();
        }

        private Builder() {
        }
        public Builder withWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder withHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder withBitrate(int bitrate) {
            this.bitrate = bitrate;
            return this;
        }

        public Builder withFps(int fps) {
            this.fps = fps;
            return this;
        }

        public Builder withCameraId(int cameraId) {
            this.cameraId = cameraId;
            return this;
        }



        public Builder withRotation(int  rotation) {
            this.rotation = rotation;
            return this;
        }

        public Builder withMediaCodec(boolean  mediacodec) {
            this.isMediaCodec = mediacodec;
            return this;
        }

        public Builder withSurfaceHolder(SurfaceHolder  surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
            return this;
        }

        public AudioVideoParameter build() {
            AudioVideoParameter avParameter = new AudioVideoParameter();
            avParameter.height = this.height;
            avParameter.bitrate = this.bitrate;
            avParameter.cameraId = this.cameraId;
            avParameter.width = this.width;
            avParameter.fps = this.fps;
            avParameter.rotation = this.rotation;
            avParameter.surfaceHolder = this.surfaceHolder;
            avParameter.isMediaCodec = this.isMediaCodec;
            return avParameter;
        }
    }
}
