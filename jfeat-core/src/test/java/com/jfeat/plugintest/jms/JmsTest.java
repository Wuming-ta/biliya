package com.jfeat.plugintest.jms;

import com.jfeat.ext.plugin.jms.JmsKit;
import com.jfeat.ext.plugin.jms.JmsPlugin;

import java.io.*;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 * Created by jacky on 1/6/15.
 */
public class JmsTest {


    @Test
    public void t() {
        IMessage m = new Message();
        m.setName("ABC");
        ObjectAndByte ob = new ObjectAndByte();
        byte[] bytes = ob.toByteArray(m);
        System.out.println(new String(bytes));
        System.out.println("===========");
        IMessage m2 = (IMessage) ob.toObject(bytes);
        System.out.println(m2.getName());
    }

   //@Test
    public void send() throws InterruptedException {
        //System.setProperty("jms.properties.file", "C:\\Data\\Work\\workspace\\osp\\trunk\\jfeat\\jfeat-core\\target\\test-classes\\jms.properties");
        System.setProperty("jms.properties.file", "jms.properties");
        JmsPlugin jmsPlugin = new JmsPlugin();

        //JmsPlugin jmsPlugin = new JmsPlugin("failover:tcp://127.0.0.1:61616?connectionTimeout=0", "system", "manager");
        jmsPlugin.addReceiveTopic("t1", 3000, "com.jfeat.plugintest.jms.CReceiveResolver");
        jmsPlugin.addSendTopic("t1", 3000);
        jmsPlugin.start();
        TimeUnit.SECONDS.sleep(10);
        JmsKit.sendQueue("q1", new M(), "a");
        JmsKit.sendTopic("t1", new M(), "3000");
        JmsKit.sendTopic("t1", new M(), 39000);
        TimeUnit.SECONDS.sleep(30);
    }

    //@Test
    public void topic() throws InterruptedException {
        System.setProperty("jms.properties.file", "jms.properties");
        JmsPlugin jmsPlugin = new JmsPlugin();
        jmsPlugin.addSendTopic("Q", 100);
        jmsPlugin.start();
        TimeUnit.SECONDS.sleep(5);
        JmsKit.sendTopic("Q", "hello world", "100");
        TimeUnit.SECONDS.sleep(30);
    }
}

interface IMessage {
    public void setName(String name);
    public String getName();
}
class Message implements IMessage, Serializable {

    private String name;

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}


class ObjectAndByte {

    /**
     * 对象转数组
     * @param obj
     * @return
     */
    public byte[] toByteArray (Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray ();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }

    /**
     * 数组转对象
     * @param bytes
     * @return
     */
    public Object toObject (byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
            ObjectInputStream ois = new ObjectInputStream (bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return obj;
    }
}