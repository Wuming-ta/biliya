package com.jfeat.plugintest.zbus;

import com.jfeat.ext.plugin.zbus.annotation.MqHandler;
import com.jfeat.ext.plugin.zbus.handler.TMsgHandler;

/**
 * Created by jacky on 3/19/16.
 */
@MqHandler("jfeat-mq")
public class MsgHandlerA extends TMsgHandler<String> {
    @Override
    public void handle(String msg) {
        System.out.println("receive msg: " + msg);
    }
}
