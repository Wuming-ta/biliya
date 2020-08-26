package com.jfeat.util;

/**
 * @author jackyhuang
 * @date 2018/11/7
 */
public class Desc {
    private String infoUrl;
    private int infoWidth;

    private int width;
    private String[] contents;
    private int contentHeight = 30;
    private int contentFontSize = 16;
    private String contentFont;

    public String getContentFont() {
        return contentFont;
    }

    public Desc setContentFont(String contentFont) {
        this.contentFont = contentFont;
        return this;
    }

    public int getContentFontSize() {
        return contentFontSize;
    }

    public Desc setContentFontSize(int contentFontSize) {
        this.contentFontSize = contentFontSize;
        return this;
    }

    public int getContentHeight() {
        return contentHeight;
    }

    public Desc setContentHeight(int contentHeight) {
        this.contentHeight = contentHeight;
        return this;
    }

    public int getInfoWidth() {
        return infoWidth;
    }

    public Desc setInfoWidth(int infoWidth) {
        this.infoWidth = infoWidth;
        return this;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    public Desc setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public Desc setWidth(int width) {
        this.width = width;
        return this;
    }

    public String[] getContents() {
        return contents;
    }

    public Desc setContents(String[] contents) {
        this.contents = contents;
        return this;
    }
}
