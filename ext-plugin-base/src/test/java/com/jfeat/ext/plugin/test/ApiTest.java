package com.jfeat.ext.plugin.test;

import com.jfeat.ext.plugin.ApiResult;
import com.jfeat.ext.plugin.ExtPluginHolder;
import com.jfeat.http.utils.HttpUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author jackyhuang
 * @date 2018/6/13
 */
public class ApiTest {

    @Ignore
    @Test
    public void testGet() {
        ExtPlugin extPlugin = new ExtPlugin("MTIz");
        ExtPluginHolder.me().start(ExtPlugin.class, extPlugin);

        String url = "http://112.74.26.228:8080/api/pub/pcd?access_token=" + ExtPluginHolder.me().get(ExtPlugin.class).getAccessToken();
        System.out.println("url = " + url);
        String result = HttpUtils.get(url);
        System.out.println("result: " + result);
        ApiResult apiResult = ApiResult.create(result);
        System.out.println("apiResult: isSuccess = " + apiResult.isSucceed());
        System.out.println("apiResult: data = " + apiResult.getList("data"));
        System.out.println("apiResult: message = " + apiResult.getMessage());
    }

    @Test
    public void testGetWithoutPlugin() {
        System.out.println("isEnabled = " + ExtPluginHolder.me().get(ExtPlugin.class));
        String url = "http://112.74.26.228:8080/api/pub/pcd?access_token=" + ExtPluginHolder.me().get(ExtPlugin.class).getAccessToken();
        System.out.println("url = " + url);
    }
}
