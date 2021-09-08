package com.jfeat.core;

import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by ehngjen on 1/14/2016.
 */
public class UploadedFile {
    private static Logger logger = LoggerFactory.getLogger(UploadedFile.class);

    public static void remove(String url) {
        if (StrKit.notBlank(url)) {
            int index = PhotoGalleryConstants.me().getHost().length();
            String path = PhotoGalleryConstants.me().getUploadPath() + url.substring(index);
            File file = new File(path).getAbsoluteFile();
            logger.debug("deleting file: " + path);
            logger.debug("exists=" + file.exists());
            logger.debug("delete result=" + FileUtils.deleteQuietly(file));
        }
    }

    public static String buildUrl(UploadFile uploadFile, String subDir) {
        if (uploadFile == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(PhotoGalleryConstants.me().getHost());
        builder.append("/");
        builder.append(subDir);
        builder.append("/");
        builder.append(uploadFile.getFileName());
        String url = builder.toString();
        logger.debug("XXX:URL=" +url);
        return url;
    }
}
