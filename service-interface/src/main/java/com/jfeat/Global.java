package com.jfeat;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author jackyhuang
 * @date 2020/1/1
 */
public class Global {

    /**
     * 是否在微信公众号后台设置了 服务器托管。
     * 默认是false
     * 如果设置了托管，消息处理接口里面会对该变量设置为true
     */
    private static AtomicBoolean weixinHosted = new AtomicBoolean(false);

    public static boolean isWeixinHosted() {
        return weixinHosted.get();
    }

    public static void setWeixinHosted(boolean weixinHosted) {
        Global.weixinHosted.compareAndSet(false, true);
    }
}
