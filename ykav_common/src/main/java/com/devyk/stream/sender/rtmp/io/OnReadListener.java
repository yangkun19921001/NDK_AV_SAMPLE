package com.devyk.stream.sender.rtmp.io;

import com.devyk.stream.sender.rtmp.packets.Chunk;

/**
 * @Title: OnReadListener
 * @Package com.jimfengfly.rtmppublisher.io
 * @Description:
 * @Author Jim
 * @Date 2016/11/30
 * @Time 上午9:50
 * @Version
 */

public interface OnReadListener {
    void onChunkRead(Chunk chunk);
    void onDisconnect();
    void onStreamEnd();
}
