package com.jfeat.core;

/**
 * Created by jacky on 12/6/15.
 */
public final class PhotoGalleryConstants {
    private String host = "";
    private String uploadPath;

    private static PhotoGalleryConstants me = new PhotoGalleryConstants();

    private PhotoGalleryConstants() {

    }

    public static PhotoGalleryConstants me() {
        return me;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }
}
