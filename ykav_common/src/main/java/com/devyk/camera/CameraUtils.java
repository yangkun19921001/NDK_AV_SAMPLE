package com.devyk.camera;

import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;

import com.devyk.Constants;
import com.devyk.blacklist.BlackListHelper;
import com.devyk.camera.exception.CameraDisabledException;
import com.devyk.camera.exception.CameraNotSupportException;
import com.devyk.camera.exception.NoCameraException;
import com.devyk.capture.CameraCapture;
import com.devyk.configuration.CameraConfiguration;
import com.devyk.constant.SopCastConstant;
import com.devyk.utils.SopCastLog;

import java.util.ArrayList;
import java.util.List;


/**
 * @Title: CameraUtils
 * @Package com.youku.crazytogether.app.modules.sopCastV2
 * @Description:
 * @Author Jim
 * @Date 16/3/23
 * @Time 下午12:01
 * @Version
 */
@TargetApi(14)
public class CameraUtils {

    private static Camera.PreviewCallback sPreviewCallback;
    private static CameraCapture.OnChangedSizeListener sScreenListener;
    private static int sRotation = 90;
    private static int sCameraId;
    private static int sWidth;
    private static int sHeight;
    private static Camera sCamera;

    public static List<CameraData> getAllCamerasData(boolean isBackFirst) {
        ArrayList<CameraData> cameraDatas = new ArrayList<>();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                CameraData cameraData = new CameraData(i, CameraData.FACING_FRONT);
                if (isBackFirst) {
                    cameraDatas.add(cameraData);
                } else {
                    cameraDatas.add(0, cameraData);
                }
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                CameraData cameraData = new CameraData(i, CameraData.FACING_BACK);
                if (isBackFirst) {
                    cameraDatas.add(0, cameraData);
                } else {
                    cameraDatas.add(cameraData);
                }
            }
        }
        return cameraDatas;
    }

    public static void initCameraParams(Camera camera, CameraData cameraData, boolean isTouchMode, CameraConfiguration configuration)
            throws CameraNotSupportException {
        boolean isLandscape = (configuration.orientation != CameraConfiguration.Orientation.PORTRAIT);
        int cameraWidth = Math.max(configuration.height, configuration.width);
        int cameraHeight = Math.min(configuration.height, configuration.width);
        sWidth = cameraWidth;
        sHeight = cameraHeight;
        sRotation = configuration.rotation;
        sCameraId = configuration.facing.ordinal();
        sCamera = camera;
        Camera.Parameters parameters = camera.getParameters();
        setPreviewFormat(camera, parameters);
        setPreviewFps(camera, configuration.fps, parameters);
        setPreviewSize(camera, cameraData, cameraWidth, cameraHeight, parameters);

        setPreviewCallback();
        cameraData.hasLight = supportFlash(camera);
        setOrientation(cameraData, isLandscape, camera);
        setFocusMode(camera, cameraData, isTouchMode);
    }


    static Camera.PreviewCallback myCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
//            byte[] yuv = null;
//            switch (sRotation) {
//                case Surface.ROTATION_0:
//                    yuv = rotation90(data, sCameraId, sWidth, sHeight);
//                    break;
//                case Surface.ROTATION_90: // 横屏 左边是头部(home键在右边)
//                    yuv = data;
//                    break;
//                case Surface.ROTATION_270:// 横屏 头部在右边
//                    yuv = data;
//                    break;
//                default:
//                    yuv = rotation90(data, sCameraId, sWidth, sHeight);
//                    break;
//            }
            if (sPreviewCallback != null)
                // data数据依然是倒的
                sPreviewCallback.onPreviewFrame(data, camera);
            camera.addCallbackBuffer(data);
        }
    };


    public static void setPreviewFormat(Camera camera, Camera.Parameters parameters) throws CameraNotSupportException {
        //设置预览回调的图片格式
        try {
            parameters.setPreviewFormat(ImageFormat.NV21);
            camera.setParameters(parameters);
        } catch (Exception e) {
            throw new CameraNotSupportException();
        }
    }

    public static void setPreviewFps(Camera camera, int fps, Camera.Parameters parameters) {
        //设置摄像头预览帧率
        if (BlackListHelper.deviceInFpsBlacklisted()) {
            SopCastLog.d(SopCastConstant.TAG, "Device in fps setting black list, so set the camera fps 15");
            fps = 15;
        }
        try {
            parameters.setPreviewFrameRate(fps);
            camera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int[] range = adaptPreviewFps(fps, parameters.getSupportedPreviewFpsRange());

        try {
            parameters.setPreviewFpsRange(range[0], range[1]);
            camera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int[] adaptPreviewFps(int expectedFps, List<int[]> fpsRanges) {
        expectedFps *= 1000;
        int[] closestRange = fpsRanges.get(0);
        int measure = Math.abs(closestRange[0] - expectedFps) + Math.abs(closestRange[1] - expectedFps);
        for (int[] range : fpsRanges) {
            if (range[0] <= expectedFps && range[1] >= expectedFps) {
                int curMeasure = Math.abs(range[0] - expectedFps) + Math.abs(range[1] - expectedFps);
                if (curMeasure < measure) {
                    closestRange = range;
                    measure = curMeasure;
                }
            }
        }
        return closestRange;
    }

    public static void setPreviewSize(Camera camera, CameraData cameraData, int width, int height,
                                      Camera.Parameters parameters) throws CameraNotSupportException {
        Camera.Size size = getOptimalPreviewSize(camera, width, height);
        if (size == null) {
            throw new CameraNotSupportException();
        } else {
            cameraData.cameraWidth = size.width;
            cameraData.cameraHeight = size.height;
        }
        //设置预览大小
        SopCastLog.d(SopCastConstant.TAG, "Camera Width: " + size.width + "    Height: " + size.height);
        try {
            parameters.setPreviewSize(cameraData.cameraWidth, cameraData.cameraHeight);
            camera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setOrientation(CameraData cameraData, boolean isLandscape, Camera camera) {

//        setPreviewOrientation(rotation);

        int orientation = getDisplayOrientation(cameraData.cameraID);
        if (isLandscape) {
            orientation = orientation - 90;
        }
        camera.setDisplayOrientation(orientation);
    }

    private static void setFocusMode(Camera camera, CameraData cameraData, boolean isTouchMode) {
        cameraData.supportTouchFocus = supportTouchFocus(camera);
        if (!cameraData.supportTouchFocus) {
            setAutoFocusMode(camera);
        } else {
            if (!isTouchMode) {
                cameraData.touchFocusMode = false;
                setAutoFocusMode(camera);
            } else {
                cameraData.touchFocusMode = true;
            }
        }
    }

    public static boolean supportTouchFocus(Camera camera) {
        if (camera != null) {
            return (camera.getParameters().getMaxNumFocusAreas() != 0);
        }
        return false;
    }

    public static void setAutoFocusMode(Camera camera) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.size() > 0 && focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                camera.setParameters(parameters);
            } else if (focusModes.size() > 0) {
                parameters.setFocusMode(focusModes.get(0));
                camera.setParameters(parameters);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setTouchFocusMode(Camera camera) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.size() > 0 && focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                camera.setParameters(parameters);
            } else if (focusModes.size() > 0) {
                parameters.setFocusMode(focusModes.get(0));
                camera.setParameters(parameters);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Camera.Size getOptimalPreviewSize(Camera camera, int width, int height) {
        Camera.Size optimalSize = null;
        double minHeightDiff = Double.MAX_VALUE;
        double minWidthDiff = Double.MAX_VALUE;
        List<Camera.Size> sizes = camera.getParameters().getSupportedPreviewSizes();
        if (sizes == null) return null;
        //找到宽度差距最小的
        for (Camera.Size size : sizes) {
            if (Math.abs(size.width - width) < minWidthDiff) {
                minWidthDiff = Math.abs(size.width - width);
            }
        }
        //在宽度差距最小的里面，找到高度差距最小的
        for (Camera.Size size : sizes) {
            if (Math.abs(size.width - width) == minWidthDiff) {
                if (Math.abs(size.height - height) < minHeightDiff) {
                    optimalSize = size;
                    minHeightDiff = Math.abs(size.height - height);
                }
            }
        }
        return optimalSize;
    }

    public static int getDisplayOrientation(int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation + 360) % 360;
        }
        return result;
    }

    public static void checkCameraService(Context context)
            throws CameraDisabledException, NoCameraException {
        // Check if device policy has disabled the camera.
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        if (dpm.getCameraDisabled(null)) {
            throw new CameraDisabledException();
        }
        List<CameraData> cameraDatas = getAllCamerasData(false);
        if (cameraDatas.size() == 0) {
            throw new NoCameraException();
        }
    }

    public static boolean supportFlash(Camera camera) {
        Camera.Parameters params = camera.getParameters();
        List<String> flashModes = params.getSupportedFlashModes();
        if (flashModes == null) {
            return false;
        }
        for (String flashMode : flashModes) {
            if (Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 将 YUV 数据旋转
     *
     * @param data
     */
    private static byte[] rotation90(byte[] data, int cameraId, int width, int height) {
        byte[] yuv = new byte[width * height * 3 / 2];
        int index = 0;
        int ySize = width * height;
        //u和v
        int uvHeight = height / 2;
        //后置摄像头顺时针旋转90度
        if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            //将y的数据旋转之后 放入新的byte数组
            for (int i = 0; i < height; i++) {
                for (int j = height - 1; j >= 0; j--) {
                    yuv[index++] = data[width * j + i];
                }
            }

            //每次处理两个数据
            for (int i = 0; i < width; i += 2) {
                for (int j = uvHeight - 1; j >= 0; j--) {
                    // v
                    yuv[index++] = data[ySize + width * j + i];
                    // u
                    yuv[index++] = data[ySize + width * j + i + 1];
                }
            }
        } else {
            //逆时针旋转90度
            for (int i = 0; i < width; i++) {
                int nPos = width - 1;
                for (int j = 0; j < width; j++) {
                    yuv[index++] = data[nPos - i];
                    nPos += width;
                }
            }
            //u v
            for (int i = 0; i < width; i += 2) {
                int nPos = ySize + width - 1;
                for (int j = 0; j < uvHeight; j++) {
                    yuv[index++] = data[nPos - i - 1];
                    yuv[index++] = data[nPos - i];
                    nPos += width;
                }
            }
        }
        return yuv;
    }

    /**
     * 屏幕改变的回调接口
     */
    public interface OnChangedSizeListener {
        void onChanged(int w, int h);
    }

    /**
     * 设置当屏幕发生改变需要设置的监听
     *
     * @param listener
     */
    public static void setOnChangedSizeListener(CameraCapture.OnChangedSizeListener listener) {
        sScreenListener = listener;
    }

    /**
     * 设置预览回调，用于软编
     */
    public static void setPreviewCallback() {
        byte[] buffer = new byte[sHeight * sWidth * 3 / 2];
        //数据缓存区
        sCamera.addCallbackBuffer(buffer);
        sCamera.setPreviewCallbackWithBuffer(myCallback);
    }

    /**
     * 设置预览回调，用于软编
     *
     * @param previewCallback
     */
    public static void setPreviewCallback(Camera.PreviewCallback previewCallback) {
        sPreviewCallback = previewCallback;
        byte[] buffer = new byte[sHeight * sWidth * 3 / 2];
        //数据缓存区
        sCamera.addCallbackBuffer(buffer);
        sCamera.setPreviewCallbackWithBuffer(myCallback);
    }

    /**
     * 设置预览方向
     */
    public static void setPreviewOrientation(int rotation) {
        sRotation = rotation;
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(sCameraId, info);
        int degrees = 0;
        switch (sRotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                if (sScreenListener != null)
                    sScreenListener.onChanged(sHeight, sWidth);
                break;
            case Surface.ROTATION_90: // 横屏 左边是头部(home键在右边)
                degrees = 90;
                if (sScreenListener != null)
                    sScreenListener.onChanged(sWidth, sHeight);
                break;
            case Surface.ROTATION_270:// 横屏 头部在右边
                degrees = 270;
                if (sScreenListener != null)
                    sScreenListener.onChanged(sWidth, sHeight);
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        //设置角度
        sCamera.setDisplayOrientation(result);
    }
}
