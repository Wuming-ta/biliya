package com.jfeat.kit;

import com.jfinal.kit.JsonKit;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/7/24
 */
public class HttpKitTest {

    @Test
    @Ignore
    public void testPost() {
        String url = "https://www.muaskin.com/rest/login_wx";
        String data = "{\"access_token\":\"12_lhNjuGVkG2jvlOpm3dk0E2RmWOCGWs_luDuXjN-e1kH77p9WOy293KzA5tH2smMySOnRTn_z7kgm4iw5zIUZzw\",\"unionid\":\"ono6H1Dpmgl-fjQ9qcTOftQZ2fz4\",\"openid\":\"o3v8q1CJVV_UeDq3tN177bPrLjV4\",\"sex\":1,\"nickname\":\"Jacky.D.H\",\"avatar\":\"http://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83erFzia6apicQiaR33hL8pan6JjeTZlYgFtey4Kfo5Fzic1I2PMict1kXujoBBnd3IFdgibsSIalA4swMItQ/132\"}";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        String res = HttpKit.post(url, data, headers);
        System.out.println(res);
    }
}
