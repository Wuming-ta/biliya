package com.jfeat.util;

/**
 * @author jackyhuang
 * @date 2018/11/7
 */
public class QrCode {
    private String content;
    private int width;
    private int[] margin = new int[] {
            50, 50, 50, 50
    };

    public int[] getMargin() {
        return margin;
    }

    public QrCode setMargin(int[] margin) {
        this.margin = margin;
        return this;
    }

    public String getContent() {
        return content;
    }

    public QrCode setContent(String content) {
        this.content = content;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public QrCode setWidth(int width) {
        this.width = width;
        return this;
    }

}
