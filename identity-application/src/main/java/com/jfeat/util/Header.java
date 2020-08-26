package com.jfeat.util;

/**
 * @author jackyhuang
 * @date 2018/11/7
 */
public class Header {
    private int width = 0;
    private String logoUrl;
    private String avatarUrl;
    private String subject;

    private int logoLayout = Config.LAYOUT_LEFT;
    private int avatarLayout = Config.LAYOUT_RIGHT;
    private int subjectLayout = Config.LAYOUT_CENTER;

    private int avatarWidth = 0;
    private int logoWidth = 0;

    public int getLogoWidth() {
        return logoWidth;
    }

    public Header setLogoWidth(int logoWidth) {
        this.logoWidth = logoWidth;
        return this;
    }

    public int getAvatarWidth() {
        return avatarWidth;
    }

    public Header setAvatarWidth(int avatarWidth) {
        this.avatarWidth = avatarWidth;
        return this;
    }


    public int getWidth() {
        return width;
    }

    public Header setWidth(int width) {
        this.width = width;
        return this;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public Header setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
        return this;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public Header setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public Header setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public int getLogoLayout() {
        return logoLayout;
    }

    public Header setLogoLayout(int logoLayout) {
        this.logoLayout = logoLayout;
        return this;
    }

    public int getAvatarLayout() {
        return avatarLayout;
    }

    public Header setAvatarLayout(int avatarLayout) {
        this.avatarLayout = avatarLayout;
        return this;
    }

    public int getSubjectLayout() {
        return subjectLayout;
    }

    public Header setSubjectLayout(int subjectLayout) {
        this.subjectLayout = subjectLayout;
        return this;
    }
}
