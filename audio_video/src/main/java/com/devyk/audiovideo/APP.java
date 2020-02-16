package com.devyk.audiovideo;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.devyk.crash_module.Crash;
import com.devyk.crash_module.inter.JavaCrashUtils;

import java.io.File;

/**
 * <pre>
 *     author  : devyk on 2020-02-12 18:37
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is APP
 * </pre>
 */
public class APP extends Application {

    private String nativePath = Environment.getExternalStorageDirectory() + "/NDKCrash";
    private String JavaPath = Environment.getExternalStorageDirectory() + "/JavaCrash";

    private String TAG = getClass().getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        //3. application 中初始化
//nativePath: 保存的 dmp 日志
//javaPath: 保存的 java 崩溃日志
//onCrashListener:  java 崩溃监听回调
//框架初始化

        if (!new File(nativePath).exists()) {
            new File(nativePath).mkdirs();
        }
        if (!new File(JavaPath).exists()) {
            new File(JavaPath).mkdirs();
        }


        new Crash.CrashBuild(getApplicationContext())
                .nativeCrashPath(nativePath)
                .javaCrashPath(JavaPath, new JavaCrashUtils.OnCrashListener() {
                    @Override
                    public void onCrash(String crashInfo, Throwable e) {
                        Log.d(TAG, crashInfo);
                    }
                })
                .build();
    }




}
