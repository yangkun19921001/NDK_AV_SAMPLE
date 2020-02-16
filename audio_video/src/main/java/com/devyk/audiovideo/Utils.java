package com.devyk.audiovideo;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <pre>
 *     author  : devyk on 2020-02-09 00:36
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is Utils
 * </pre>
 */
public class Utils {


    public static void writePCM(byte[] frme) {
        try {
            String filePath = Environment.getExternalStorageDirectory() + "/_test.pcm";
            File file = new File(filePath);
            FileOutputStream fos = null;
            if (!file.exists()) {
                file.createNewFile();//如果文件不存在，就创建该文件
                fos = new FileOutputStream(file);//首次写入获取
            } else {
                //如果文件已存在，那么就在文件末尾追加写入
                fos = new FileOutputStream(file, true);//这里构造方法多了一个参数true,表示在文件末尾追加写入
            }
            fos.write(frme);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
