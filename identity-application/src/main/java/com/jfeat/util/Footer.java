package com.jfeat.util;

/**
 * @author jackyhuang
 * @date 2018/11/7
 */
public class Footer {
    private String content;
    private int fontSize = 20;
    private String font = "宋体";
    private int width;
    private int height;

    public String getFont() {
        return font;
    }

    public Footer setFont(String font) {
        this.font = font;
        return this;
    }

    public int getFontSize() {
        return fontSize;
    }

    public Footer setFontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public Footer setHeight(int height) {
        this.height = height;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Footer setContent(String content) {
        this.content = content;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public Footer setWidth(int width) {
        this.width = width;
        return this;
    }
}
