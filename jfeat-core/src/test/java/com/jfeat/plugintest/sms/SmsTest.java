package com.jfeat.plugintest.sms;

import com.jfeat.ext.plugin.sms.ConfigData;
import com.jfeat.ext.plugin.sms.SmsKit;
import com.jfeat.ext.plugin.sms.SmsSender;
import com.jfeat.ext.plugin.sms.SmsSenderQCloudImpl;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author jackyhuang
 * @date 2018/5/18
 */
public class SmsTest {

    @Test
    @Ignore
    public void testSendQCloud() {
        //SmsKit.init("v", "1400271045", "76f97e1709f0716416d56b52460a7b00", "447473", "十美优品", "tencent");
        //SmsKit.send("v", Arrays.asList("1234", "3"), "13922112130");

        ConfigData config = new ConfigData("1400271045", "76f97e1709f0716416d56b52460a7b00", "447473", "十美优品", "tencent");
        SmsSender sender = new SmsSenderQCloudImpl(config);
        Map<String, String> params = new LinkedHashMap<>();
        params.put("code", "123456");
        params.put("ttl", "4");
        sender.addPhone("13922112130");
        sender.addParams(params).send();
    }

    @Test
    @Ignore
    public void testSendAliyun() {
        SmsKit.init("v", "LTAIEzfEXxUYkYX5", "REQUcFgGoFIV4qadxXhbXMXnG5C1aX", "SMS_138505040", "Muaskin智慧美妆", "Aliyun");
        SmsKit.send("v", "code", "test", "13922112130");
    }

    //@Test
    public void t() {
        for (int i = 0; i < 1000; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("code", "abc");
            map.put("ttl", "123");
            assertEquals(map.values().toArray(new String[0])[1], "123");
        }
    }
}
