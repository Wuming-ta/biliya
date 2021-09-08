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

package com.jfeat.kit.qiniu;

import com.jfeat.kit.Encodes;
import com.jfeat.kit.HttpKit;
import com.jfinal.kit.StrKit;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.Json;
import com.qiniu.util.StringMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/** 上传文件到七牛云
 * Created by huangjacky on 16/7/11.
 */
public class QiniuKit {

    private static Logger logger = LoggerFactory.getLogger(QiniuKit.class);

    //账号的ACCESS_KEY和SECRET_KEY
    private String ak;
    private String sk;
    //要上传的空间
    private String bucketName;
    private String url;
    private String tmpdir = System.getProperty("java.io.tmpdir");

    private boolean inited = false;

    public boolean isInited() {
        return inited;
    }

    public String getTmpdir() {
        return tmpdir;
    }

    public QiniuKit setTmpdir(String tmpdir) {
        this.tmpdir = tmpdir;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public QiniuKit setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getAk() {
        return ak;
    }

    public QiniuKit setAk(String ak) {
        this.ak = ak;
        return this;
    }

    public String getSk() {
        return sk;
    }

    public QiniuKit setSk(String sk) {
        this.sk = sk;
        return this;
    }

    public String getBucketName() {
        return bucketName;
    }

    public QiniuKit setBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    //密钥配置
    private Auth auth;
    //创建上传对象
    private UploadManager uploadManager = new UploadManager();

    private static QiniuKit me = new QiniuKit();

    public static QiniuKit me() {
        return me;
    }

    private QiniuKit() {

    }

    public void init() {
        if (StrKit.isBlank(ak) || StrKit.isBlank(sk)) {
            throw new RuntimeException("AK SK is null.");
        }
        if (StrKit.isBlank(bucketName)) {
            throw new RuntimeException("bucket name is not set.");
        }
        auth = Auth.create(ak, sk);
        inited = true;
    }

    /**
     * 获取文件类型
     */
    public static String getFileType(String fileName){
        return fileName.substring(fileName.lastIndexOf("."), fileName.length());
    }

    //简单上传，使用默认策略，只需要设置上传的空间名就可以了
    private String getUpToken(){
        return auth.uploadToken(bucketName);
    }

    public String getFullUrl(String name) {
        String finalUrl = getUrl();
        if (!finalUrl.endsWith("/")) {
            finalUrl += "/";
        }
        return finalUrl + name;
    }

    public String upload(String filePath) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        StringBuilder fileName = new StringBuilder(format.format(new Date()));
        fileName.append("-");
        fileName.append(RandomStringUtils.randomAlphanumeric(8));
        fileName.append(getFileType(filePath));
        return upload(filePath, fileName.toString());
    }

    /**
     * 上传文件, 成功就返回文件的名字,否则返回NULL
     * @param filePath
     * @param fileName
     * @return
     */
    public String upload(String filePath, String fileName) {
        if (!inited) {
            throw new RuntimeException("Not inited.");
        }

        try {
            //调用put方法上传
            Response res = uploadManager.put(filePath, fileName, getUpToken());
            logger.debug(res.bodyString());
            StringMap map = res.jsonToMap();
            return (String) map.get("key");
        } catch (QiniuException e) {
            Response r = e.response;
            logger.error(r.toString());
            return null;
        }
    }

    /**
     * 上传Base64 encoded图片
     * @param base64String
     * @return
     */
    public String putb64(String base64String) {
        if (base64String.contains(";base64,")) {
            base64String = base64String.split(";base64,")[1];
        }
        byte[] bytes = Encodes.decodeBase64(base64String);
        String url = "http://upload.qiniu.com/putb64/" + bytes.length;
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/octet-stream");
        headers.put("Authorization", "UpToken " + getUpToken());
        String result = HttpKit.post(url, base64String, headers);
        StringMap map = Json.decode(result);
        return (String) map.get("key");
    }

}
