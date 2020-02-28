package com.devyk.ykav_sample;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author  : devyk on 2020-02-25 14:06
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is SampleActivity
 * </pre>
 */
public class SampleActivity extends AppCompatActivity {


    private String[] mPermissions = new String[]{Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private List<String> mPermissionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0才用动态权限
            requestPermission();
        }


    }

    private void requestPermission() {
        //申请音频视频的动态权限
        //逐个判断你要的权限是否已经通过
        for (int i = 0; i < mPermissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, mPermissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(mPermissions[i]);//添加还未授予的权限
            }
        }

        //申请权限
        if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
            ActivityCompat.requestPermissions(this, mPermissions, 0x01);
        }
    }


    /**
     * ⑨重写onRequestPermissionsResult方法
     * 获取动态权限请求的结果,再开启录制音频
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0x01 && grantResults.length > 0) {
            // 因为是多个权限，所以需要一个循环获取每个权限的获取情况
            for (int i = 0; i < grantResults.length; i++) {
                // PERMISSION_DENIED 这个值代表是没有授权，我们可以把被拒绝授权的权限显示出来
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getApplicationContext(), permissions[i] + "权限被拒绝了", Toast.LENGTH_SHORT).show();
                }
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /**
     * 播放器
     *
     * @param view
     */
    public void player(View view) {
        startActivity(new Intent(this, PlayerActivity.class));
    }

    /**
     * 推流器
     *
     * @param view
     */
    public void pusher(View view) {
        startActivity(new Intent(this, PusherActivity.class));
    }
}
