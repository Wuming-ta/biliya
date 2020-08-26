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

package com.jfeat.misc.api;

import com.jfeat.core.PhotoGalleryConstants;
import com.jfeat.core.RestController;
import com.jfeat.kit.DateKit;
import com.jfeat.kit.Encodes;
import com.jfeat.kit.qiniu.QiniuKit;
import com.jfinal.ext.kit.RandomKit;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by huangjacky on 16/6/30.
 */
@ControllerBind(controllerKey = "/rest/upload_image")
public class UploadImageController extends RestController {

    /**
     * POST /rest/upload_image
     * data: base64 encoded image file
     */
    public void save() {
        try {
            String data = IOUtils.toString(this.getRequest().getInputStream(), "UTF-8");
            if  (StrKit.isBlank(data)) {
                renderFailure("invalid.data");
                return;
            }

            String[] dataArray = data.split(",");
            //data:image/jpeg;base64
            String magic = dataArray[0];
            logger.debug(magic);
            String imageTag = "data:image/";
            if (!magic.startsWith(imageTag)) {
                renderFailure("invalid.data.type");
                return;
            }

            if (QiniuKit.me().isInited()) {
                String key = QiniuKit.me().putb64(data);
                if (StrKit.notBlank(key)) {
                    renderSuccess(QiniuKit.me().getFullUrl(key));
                    return;
                }
                renderFailure("upload.failure");
                return;
            }


            int start = imageTag.length();
            int end = magic.indexOf(";base64");
            String suffix = magic.substring(start, end);
            byte[] bytes = Encodes.decodeBase64(dataArray[1]);
            for(int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {
                    //调整异常数据
                    bytes[i] += 256;
                }
            }
            String uploadPath = PhotoGalleryConstants.me().getUploadPath();
            String subDir = "/upload/" + DateKit.today("yyyy-MM-dd") + "/";
            File targetDir = new File(uploadPath + subDir);
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            String imgFileName = DateKit.today("yyyyMMddHHmmss") + "-" + RandomStringUtils.randomNumeric(5) + "." + suffix;
            String imgFilePath = uploadPath + subDir + imgFileName;
            String url = PhotoGalleryConstants.me().getHost() + subDir + imgFileName;

            logger.debug("save uploaded file to {}", imgFilePath);

            //生成jpeg图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(bytes, 0, bytes.length);
            out.flush();
            out.close();
            renderSuccess(url);
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage());
        }

        renderFailure("upload.error");
    }
}
