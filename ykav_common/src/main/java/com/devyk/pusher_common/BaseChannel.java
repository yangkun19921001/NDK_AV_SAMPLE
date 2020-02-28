package com.devyk.pusher_common;

/**
 * <pre>
 *     author  : devyk on 2020-02-25 14:42
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is BaseChannel
 * </pre>
 */
public abstract class BaseChannel {



    public abstract void startLive();

    public  abstract void stopLive();

    public  abstract void release();

    public abstract void onRestart();
}
