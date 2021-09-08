
package com.jfeat.kit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * HttpKit
 */
public class HttpKit {

    private static Logger logger = LoggerFactory.getLogger(HttpKit.class);

    private HttpKit() {}

    private static Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private static String CHARSET = "UTF-8";

    public static <T> T get(String url, Class<T> clazz, Map<String, String> queryParas, Map<String, String> headers) {
        return gson.fromJson(get(url, queryParas, headers), clazz);
    }

    public static <T> T get(String url, Class<T> clazz, Map<String, String> headers) {
        return gson.fromJson(get(url, (Map<String, String>)null, headers), clazz);
    }

    public static <T> T get(String url, Class<T> clazz) {
        return gson.fromJson(get(url), clazz);
    }

    public static <T> T post(String url, String json, Class<T> clazz, Map<String, String> headers) {
        return gson.fromJson(post(url, json, headers), clazz);
    }

    public static <T> T post(String url, String json, Class<T> clazz) {
        return gson.fromJson(post(url, json), clazz);
    }

    public static <T> T postForm(String url, Map<String, String> params, Class<T> clazz, Map<String, String> headers) {
        return gson.fromJson(postForm(url, params, headers), clazz);
    }

    public static <T> T postForm(String url, Map<String, String> params, Class<T> clazz) {
        return gson.fromJson(postForm(url, params), clazz);
    }


    public static <T> T put(String url, String json, Class<T> clazz, Map<String, String> headers) {
        return gson.fromJson(put(url, json, headers), clazz);
    }

    public static <T> T put(String url, String json, Class<T> clazz) {
        return gson.fromJson(put(url, json), clazz);
    }

    public static <T> T delete(String url, Class<T> clazz, Map<String, String> headers) {
        return gson.fromJson(delete(url, headers), clazz);
    }

    public static <T> T delete(String url, Class<T> clazz) {
        return gson.fromJson(delete(url), clazz);
    }

    public static String get(String url) {
        return get(url, (Map<String, String>)null);
    }

    public static String get(String url, Map<String, String> queryParas) {
        return get(url, queryParas, null);
    }

    public static String get(String url, Map<String, String> queryParas, Map<String, String> headers) {
        HttpUriRequest request = new HttpGet(buildUrlWithQueryString(url, queryParas));
        if (headers != null) {
            for (String header : headers.keySet()) {
                request.setHeader(header, headers.get(header));
            }
        }
        try {
            HttpResponse response = HttpClientBuilder.create().build().execute(request);
            String result = EntityUtils.toString(response.getEntity(), CHARSET);
            logger.debug("GET result = {}", result);
            handleException(response.getStatusLine().getStatusCode(), url);
            return result;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String post(String url, String json) {
        return post(url, json, (Map<String, String>)null);
    }

    public static String post(String url, String json, Map<String, String> headers) {
        HttpPost request = new HttpPost(url);
        if (headers != null) {
            for (String header : headers.keySet()) {
                request.setHeader(header, headers.get(header));
            }
        }
        logger.debug("POST json = {}", json);
        request.setEntity(new StringEntity(json, CHARSET));
        HttpClient httpClient = HttpClients.createDefault();
        try {
            HttpResponse response = httpClient.execute(request);
            String result = EntityUtils.toString(response.getEntity());
            logger.debug("POST result = {}", result);
            handleException(response.getStatusLine().getStatusCode(), url);
            return result;
        }catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String postForm(String url, Map<String, String> params) {
        return postForm(url, params, (Map<String, String>)null);
    }

    public static String postForm(String url, Map<String, String> params, Map<String, String> headers) {
        HttpPost request = new HttpPost(url);
        setHeaders(request, headers);
        try {
            request.setEntity(new UrlEncodedFormEntity(assembleRequestParams(params), CHARSET));
            HttpClient httpClient = HttpClients.createDefault();
            HttpResponse response = httpClient.execute(request);
            String result = EntityUtils.toString(response.getEntity(), CHARSET);
            logger.debug("POST result = {}", result);
            handleException(response.getStatusLine().getStatusCode(), url);
            return result;
        }catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String put(String url, String json) {
        return put(url, json, (Map<String, String>)null);
    }

    public static String put(String url, String json, Map<String, String> headers) {
        HttpPut request = new HttpPut(url);
        if (headers != null) {
            for (String header : headers.keySet()) {
                request.setHeader(header, headers.get(header));
            }
        }
        logger.debug("PUT json = {}", json);
        request.setEntity(new StringEntity(json, CHARSET));
        try {
            HttpResponse response = HttpClientBuilder.create().build().execute(request);
            String result = EntityUtils.toString(response.getEntity());
            logger.debug("PUT result = {}", result);
            handleException(response.getStatusLine().getStatusCode(), url);
            return result;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String delete(String url) {
        return delete(url, (Map<String, String>)null);
    }

    public static String delete(String url, Map<String, String> headers) {
        HttpDelete request = new HttpDelete(url);
        if (headers != null) {
            for (String header : headers.keySet()) {
                request.setHeader(header, headers.get(header));
            }
        }
        try {
            HttpResponse response = HttpClientBuilder.create().build().execute(request);
            String result = EntityUtils.toString(response.getEntity());
            logger.debug("DELETE result = {}", result);
            handleException(response.getStatusLine().getStatusCode(), url);
            return result;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 组装http请求参数
     *
     */
    private static List<NameValuePair> assembleRequestParams(Map<String, String> data) {
        List<NameValuePair> nameValueList = new ArrayList<NameValuePair>();

        Iterator<Map.Entry<String, String>> it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
            nameValueList.add(new BasicNameValuePair((String) entry.getKey(), (String) entry.getValue()));
        }

        return nameValueList;
    }


    private static void setHeaders(HttpRequest request, Map<String, String> headers) {
        if (headers != null) {
            for (String header : headers.keySet()) {
                request.setHeader(header, headers.get(header));
            }
        }
    }


    /**
     * Build queryString of the url
     */
    public static String buildUrlWithQueryString(String url, Map<String, String> queryParas) {
        if (queryParas == null || queryParas.isEmpty())
            return url;

        StringBuilder sb = new StringBuilder(url);
        boolean isFirst;
        if (url.indexOf("?") == -1) {
            isFirst = true;
            sb.append("?");
        }
        else {
            isFirst = false;
        }

        for (Map.Entry<String, String> entry : queryParas.entrySet()) {
            if (isFirst) isFirst = false;
            else sb.append("&");

            String key = entry.getKey();
            String value = entry.getValue();
            if (StrKit.notBlank(value)) {
                try {
                    value = URLEncoder.encode(value, CHARSET);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
            sb.append(key).append("=").append(value);
        }
        return sb.toString();
    }


    public static String readData(HttpServletRequest request) {
        BufferedReader br = null;
        try {
            StringBuilder result = new StringBuilder();
            br = request.getReader();
            for (String line=null; (line=br.readLine())!=null;) {
                result.append(line).append("\n");
            }

            return result.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    LogKit.error(e.getMessage(), e);
                }
            }
        }
    }

    private static void handleException(int statusCode, String url) {
        switch (statusCode) {
            case HttpStatus.SC_OK:
                break;
            case HttpStatus.SC_NOT_FOUND:
                throw new HttpNotFoundException(url);
            case HttpStatus.SC_INTERNAL_SERVER_ERROR:
                throw new HttpInternalErrorException(url);
            default:
                throw new HttpException(statusCode, url);
        }
    }
}



