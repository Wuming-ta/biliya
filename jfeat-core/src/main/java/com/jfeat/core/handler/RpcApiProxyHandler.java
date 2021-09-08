package com.jfeat.core.handler;

import com.jfeat.kit.Encodes;
import com.jfinal.handler.Handler;
import com.jfinal.kit.StrKit;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author jackyhuang
 * @date 2019/10/17
 */
public class RpcApiProxyHandler extends Handler {

    private static final Logger logger = LoggerFactory.getLogger(RpcApiProxyHandler.class);

    private String proxyPrefix;
    private String proxyHost;
    private Integer proxyPort;

    public RpcApiProxyHandler(String proxyHost, Integer proxyPort, String proxyPrefix) {
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.proxyPrefix = proxyPrefix;
    }

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

        if (StrKit.isBlank(proxyHost) || StrKit.isBlank(proxyPrefix) || proxyPort == null) {
            next.handle(target, request, response, isHandled);
            return;
        }

        if (target.startsWith(proxyPrefix)) {
            logger.debug("proxy hit for url: {}", request.getRequestURI());

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            OkHttpClient client = builder
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .build();

            String url = this.proxyHost + ":" + this.proxyPort + "/" + target.substring(6);
            if (StrKit.notBlank(request.getQueryString())) {
                url += "?" + request.getQueryString();
            }
            String authorization = request.getHeader("authorization");
            if (StrKit.isBlank(authorization)) {
                logger.debug("authorization token missing.");
                response.setStatus(401);
                isHandled[0] = true;
                return;
            }
            if (authorization.startsWith("Bearer ")) {
                authorization = authorization.substring(7);
            }
            String str = new String(Encodes.decodeBase64(authorization));
            logger.debug(str);
            Map<String, Object> map;
            try {
                map = com.jfeat.kit.JsonKit.convertToMap(str);
            } catch (Exception e) {
                logger.error("error occurred while decode token. {}", e.getMessage());
                response.setStatus(500);
                isHandled[0] = true;
                return;
            }
            Request.Builder reqBuilder = new Request.Builder()
                    .url(url)
                    .header("authorization", request.getHeader("authorization"))
                    .header("X-USER-ID", (String) map.get("id"));

            try {
                if (request.getMethod().equalsIgnoreCase("POST")
                        || request.getMethod().equalsIgnoreCase("PUT")
                        || request.getMethod().equalsIgnoreCase("DELETE")) {
                    MediaType parse = MediaType.parse("application/json");
                    RequestBody body = RequestBody.create(parse, IOUtils.toString(request.getInputStream(), "UTF-8"));
                    reqBuilder.method(request.getMethod(), body);
                }

                Response resp = client.newCall(reqBuilder.build()).execute();
                String bodyStr = resp.body().string();
                logger.debug("proxy resp: code {}, body = {}", resp.code(), bodyStr);
                response.setStatus(resp.code());
                response.setCharacterEncoding("utf-8");
                response.setContentType("application/json");
                PrintWriter writer = response.getWriter();
                writer.write(bodyStr);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                logger.error("error occurred while calling proxy {}, {}", url, e.getMessage());
                response.setStatus(500);
            }
            isHandled[0] = true;
            return;
        }

        next.handle(target, request, response, isHandled);
    }

}
