/*
 *   Copyright (C) 2014-2016 www.kequandian.net
 *
 *    The program may be used and/or copied only with the written permission
 *    from www.kequandian.net or in accordance with the terms and
 *    conditions stipulated in the agreement/contract under which the program
 *    has been supplied.
 *
 *    All rights reserved.
 *
 */

package com.jfeat.api;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.jfinal.kit.JsonKit;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ApiTestBase {
    private static Logger logger = LoggerFactory.getLogger(ApiTestBase.class);
    private static Server server;
    private static int port = 9990;
    private static String webDir = "src/main/webapp";
    private static String contextPath = "/";
    protected static String baseUrl = "http://localhost:" + port + contextPath;

    protected static String testUserName = "admin";
    protected static String testPassword = "admin";
    private static String accessToken;

    protected static int SUCCESS = 0;
    protected static int FAILURE = 1;

    static void startJetty() throws Exception {
        String path = ApiTestBase.class.getResource("/").getFile();
        System.setProperty("jfeat.config.properties", path + "config.properties");
        if (server == null) {
            server = new Server();
            SelectChannelConnector connector = new SelectChannelConnector();
            connector.setPort(port);
            server.addConnector(connector);
            WebAppContext webApp = new WebAppContext();
            webApp.setContextPath(contextPath);
            webApp.setResourceBase(path + "../../" + webDir);
            webApp.setTempDirectory(new File("target"));
            webApp.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
            webApp.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");

            server.setHandler(webApp);
            server.start();
        }
    }


    public static void register() throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("username", testUserName);
        params.put("password", testPassword);
        HttpPost request = new HttpPost(baseUrl + "rest/register");
        request.setEntity(new StringEntity(JsonKit.toJson(params), "UTF-8"));
        HttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = httpClient.execute(request);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    public static void login() throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("username", testUserName);
        params.put("password", testPassword);
        HttpPost request = new HttpPost(baseUrl + "rest/login");
        request.setEntity(new StringEntity(JsonKit.toJson(params), "UTF-8"));
        HttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = httpClient.execute(request);
        assertEquals(200, response.getStatusLine().getStatusCode());
        HttpEntity httpEntity = response.getEntity();
        String result = EntityUtils.toString(httpEntity);
        LoginResult loginResult = new GsonBuilder().create().fromJson(result, LoginResult.class);
        accessToken = loginResult.getData().getAccessToken();
    }

    public String toJson(Object src) {
        return new GsonBuilder().create().toJson(src);
    }

    public <T> T get(String url, Class<T> clazz) throws IOException {
        return new GsonBuilder().create().fromJson(get(url), clazz);
    }

    public <T> T post(String url, String json, Class<T> clazz) throws IOException {
        return new GsonBuilder().create().fromJson(post(url, json), clazz);
    }

    public <T> T put(String url, String json, Class<T> clazz) throws IOException {
        return new GsonBuilder().create().fromJson(put(url, json), clazz);
    }

    public <T> T delete(String url, Class<T> clazz) throws IOException {
        return new GsonBuilder().create().fromJson(delete(url), clazz);
    }

    public String get(String url) throws IOException {
        //Given
        HttpUriRequest request = new HttpGet(url);
        request.setHeader("Authorization", accessToken);
        //When
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        //Then
        assertEquals(200, response.getStatusLine().getStatusCode());
        String json = EntityUtils.toString(response.getEntity());
        logger.debug(json);
        return json;
    }

    public String post(String url, String json) throws IOException {
        HttpPost request = new HttpPost(url);
        request.setHeader("Authorization", accessToken);
        request.setEntity(new StringEntity(json, "UTF-8"));
        HttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = httpClient.execute(request);
        assertEquals(200, response.getStatusLine().getStatusCode());
        String jsonResult = EntityUtils.toString(response.getEntity());
        logger.debug(jsonResult);
        return jsonResult;
    }

    public String put(String url, String json) throws IOException {
        HttpPut request = new HttpPut(url);
        request.setHeader("Authorization", accessToken);
        request.setEntity(new StringEntity(json, "UTF-8"));
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        assertEquals(200, response.getStatusLine().getStatusCode());
        return EntityUtils.toString(response.getEntity());
    }

    public String delete(String url) throws IOException {
        HttpDelete request = new HttpDelete(url);
        request.setHeader("Authorization", accessToken);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        //Then
        assertEquals(200, response.getStatusLine().getStatusCode());
        String json = EntityUtils.toString(response.getEntity());
        logger.debug(json);
        return json;
    }

    @BeforeClass
    public static void start() throws Exception {
        startJetty();
        register();
        login();
    }

    class LoginResult {
        @SerializedName("status_code")
        private Integer statusCode;
        private LoginData data;

        public Integer getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
        }

        public LoginData getData() {
            return data;
        }

        public void setData(LoginData data) {
            this.data = data;
        }
    }

    class LoginData {
        @SerializedName("access_token")
        private String accessToken;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
    }
}
