package com.devyk.cmake_application;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    static {

        System.loadLibrary("Test");
        System.loadLibrary("native-lib");// 必须必总库，先加载，让系统内存生成一个副部，此副部给总库用
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testCmake();
    }

    /**
     * 测试 mk 构建程序
     */
    public native static void testCmake();
}
