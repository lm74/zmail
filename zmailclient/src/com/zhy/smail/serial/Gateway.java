package com.zhy.smail.serial;

import java.io.IOException;

/**
 * Created by wenliz on 2017/2/24.
 */
public class Gateway {
    public boolean sendMessage(byte[] data) throws IOException, InterruptedException{
        return true;
    }

    public boolean isOpened() {
        return true;
    }
    public void close() {

    }
}
