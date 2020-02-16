package com.devyk.audiovideo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.devyk.audioplay.R;
import com.devyk.audiovideo.audio.AudioPlayActivity;
import com.devyk.audiovideo.video.VideoPlayActivity;

/**
 * <pre>
 *     author  : devyk on 2020-02-12 14:44
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is TestActivity
 * </pre>
 */
public class TestActivity extends Activity {

    static {
        System.loadLibrary("audiovideo");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
    }

    public void video(View view) {
        startActivity(new Intent(this, VideoPlayActivity.class));
    }

    public void audio(View view) {
        startActivity(new Intent(this, AudioPlayActivity.class));
    }
}
