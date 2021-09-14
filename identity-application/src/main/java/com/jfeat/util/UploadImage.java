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

package com.jfeat.util;

import com.jfeat.core.PhotoGalleryConstants;
import com.jfeat.kit.DateKit;
import com.jfeat.kit.Encodes;
import com.jfeat.kit.qiniu.QiniuKit;
import com.jfinal.kit.StrKit;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by jackyhuang on 17/1/7.
 */
public class UploadImage {
    private static Logger logger = LoggerFactory.getLogger(UploadImage.class);

    public static String upload(byte[] bytes) throws IOException {
        if (QiniuKit.me().isInited()) {
            String base64String = Encodes.encodeBase64(bytes);
            String key = QiniuKit.me().putb64(base64String);
            if (StrKit.notBlank(key)) {
                return QiniuKit.me().getFullUrl(key);
            }
            return null;
        }

        String uploadPath = PhotoGalleryConstants.me().getUploadPath();
        String subDir = "/upload/" + DateKit.today("yyyy-MM-dd") + "/";
        File targetDir = new File(uploadPath + subDir);
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        String imgFileName = DateKit.today("yyyyMMddHHmmss") + "-" + RandomStringUtils.randomNumeric(5) + ".png";
        String imgFilePath = uploadPath + subDir + imgFileName;
        //String url = PhotoGalleryConstants.me().getHost() + subDir + imgFileName;
        String url = PhotoGalleryConstants.me().getUploadPath() + subDir + imgFileName;

        logger.debug("save uploaded file to {}", imgFilePath);

        //生成jpeg图片
        OutputStream out = new FileOutputStream(imgFilePath);
        out.write(bytes, 0, bytes.length);
        out.flush();
        out.close();

        return url;
    }
}
