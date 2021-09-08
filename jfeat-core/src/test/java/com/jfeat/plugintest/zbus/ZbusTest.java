package com.jfeat.plugintest.zbus;

import com.jfeat.ext.plugin.zbus.ZbusPlugin;
import com.jfeat.ext.plugin.zbus.sender.MqSender;
import com.jfeat.ext.plugin.zbus.sender.Sender;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zbus.mq.server.MqServer;
import org.zbus.mq.server.MqServerConfig;

import java.io.IOException;

/**
 * Created by jacky on 3/19/16.
 */
public class ZbusTest {
    public static MqServer server;
    public static ZbusPlugin zp;

    @BeforeClass
    public static void beforeClass() throws IOException {
        //MqServerConfig config = new MqServerConfig();
        //config.serverPort = 15555;
        //config.storePath = "./store";
        //server = new MqServer(config);
        //server.start();

        //初始化zbus插件
        zp = new ZbusPlugin(true, "com.jfeat.plugintest");
        //创建一个MQ
        //zp.registerMqMsgHandler("jfeat-mq", new MsgHandlerA());
        zp.start();
    }

    @AfterClass
    public static void afterClass() throws IOException {
        zp.stop();
        //server.close();
    }

    //@Test
    public void test() throws IOException, InterruptedException {
        Sender<String> sender = new MqSender<>("jfeat-mq");
        sender.sendSync("hello");
        Thread.sleep(5000);
    }
}
