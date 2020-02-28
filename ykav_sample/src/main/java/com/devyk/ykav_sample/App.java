package com.devyk.ykav_sample;

import android.app.Application;
import android.util.Log;

import com.devyk.crash_module.Crash;
import com.devyk.crash_module.inter.JavaCrashUtils;

import java.io.File;

import static android.content.ContentValues.TAG;
import static com.devyk.Constants.JavaPath;
import static com.devyk.Constants.nativePath;

/**
 * <pre>
 *     author  : devyk on 2020-02-25 14:12
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is App
 * </pre>
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


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
