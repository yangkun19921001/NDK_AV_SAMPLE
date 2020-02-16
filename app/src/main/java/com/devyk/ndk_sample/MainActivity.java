package com.devyk.ndk_sample;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private String TAG = this.getClass().getSimpleName();

    /**
     * 1. 加载 native 库
     */
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * 计数器
     */
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView text = findViewById(R.id.sample_text);

        /** 1. Java 数据传递给 native */
        test1(true,
                (byte) 1,
                ',',
                (short) 3,
                4,
                3.3f,
                2.2d,
                "DevYK",
                28,
                new int[]{1, 2, 3, 4, 5, 6, 7},
                new String[]{"1", "2", "4"},
                new Person("阳坤", 27),
                new boolean[]{false, true}
        );


        /**2. 处理 Java 对象*/
        String str = getPerson().toString();
        text.setText(str);

        /**3.动态注册的 native */
//        dynamicRegister("我是动态注册的");
        /**4.异常处理*/
//        dynamicRegister2("测试异常处理");

    }

    /**
     * Java 将数据传递到 native 中
     */
    public native void test1(
            boolean b,
            byte b1,
            char c,
            short s,
            long l,
            float f,
            double d,
            String name,
            int age,
            int[] i,
            String[] strs,
            Person person,
            boolean[] bArray
    );


    public native Person getPerson();


    /**
     * 动态注册
     */
    public native void dynamicRegister(String name);

    public native void dynamicRegister2(String name);


    /**
     * 测试抛出异常
     *
     * @throws NullPointerException
     */
    private void testException() throws NullPointerException {
        throw new NullPointerException("MainActivity testException NullPointerException");
    }

    public void test3(View view) {
        test4();
    }

    public native void test4();




    public void test4(View view) {
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    count();
                    nativeCount();
                }
            }).start();
        }
    }

    private void count() {
        synchronized (this) {
            count++;
            Log.d("Java", "count=" + count);
        }
    }

    public native void nativeCount();



    public void test5(View view) {
        testThread();
    }

    // AndroidUI操作，让C++线程里面来调用
    public void updateUI() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("UI")
                    .setMessage("native 运行在主线程，直接更新 UI ...")
                    .setPositiveButton("确认", null)
                    .show();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("UI")
                            .setMessage("native运行在子线程切换为主线程更新 UI ...")
                            .setPositiveButton("确认", null)
                            .show();
                }
            });
        }
    }
    public native void testThread();
    public native void unThread();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unThread();
    }
}