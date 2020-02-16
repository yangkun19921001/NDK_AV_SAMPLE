package com.devyk.ndk_sample;

import android.util.Log;

/**
 * <pre>
 *     author  : devyk on 2020-01-07 23:13
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is Person
 * </pre>
 */
public class Person {
  private String name;
  private int age;



  public Person(){
      Log.d("DevYk","JNI 调用成功！");
  }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
