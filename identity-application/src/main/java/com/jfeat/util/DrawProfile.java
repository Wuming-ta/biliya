package com.jfeat.util;

import com.google.zxing.WriterException;
import com.jfeat.kit.qrcode.QrcodeKit;
import com.jfinal.kit.StrKit;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;


/**
 * Created by jackyhuang on 16/8/2.
 */
public class DrawProfile {

    private Header header;
    private Desc desc;
    private QrCode qrCode;
    private Footer footer;
    private int width;

    public int getWidth() {
        return width;
    }

    public DrawProfile setWidth(int width) {
        this.width = width;
        return this;
    }

    public Desc getDesc() {
        return desc;
    }

    public DrawProfile setDesc(Desc desc) {
        this.desc = desc;
        return this;
    }

    public Header getHeader() {
        return header;
    }

    public DrawProfile setHeader(Header header) {
        this.header = header;
        return this;
    }

    public QrCode getQrCode() {
        return qrCode;
    }

    public DrawProfile setQrCode(QrCode qrCode) {
        this.qrCode = qrCode;
        return this;
    }

    public Footer getFooter() {
        return footer;
    }

    public DrawProfile setFooter(Footer footer) {
        this.footer = footer;
        return this;
    }

    private int margin = 5;
    private int headerHeight = 0;
    private int descHeight = 0;
    private int qrcodeHeight = 0;
    private int footerHeight = 0;



    private Color textColor = Color.RED;

    private String font = "微软雅黑";


    public DrawProfile setTextColor(Color textColor) {
        this.textColor = textColor;
        return this;
    }

    /**
     * 按指定尺寸缩放图片
     * @param image
     * @param width
     * @return
     */
    private BufferedImage scale(BufferedImage image, int width) {
        if (image == null) {
            return null;
        }
        int height = 0;
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();
        //if (originalWidth > originalHeight) {
            height = width * originalHeight / originalWidth;
        //}
        //if (originalHeight > originalWidth) {
        //    width = height * originalWidth / originalHeight;
        //}
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        result.getGraphics().drawImage(image.getScaledInstance(width, height, Image.SCALE_REPLICATE), 0, 0, null);
        return result;
    }

    private void drawString(Graphics g, String str, int xPos, int yPos, int containerWidth, int containerHeight) {
        drawString(g, str, xPos, yPos, containerWidth, containerHeight, true);
    }

    /**
     * 在graphics上画字符串, 如果超长则自动换行。默认居中显示。
     * @param g
     * @param str
     * @param xPos
     * @param yPos
     * @param containerWidth
     * @param containerHeight
     */
    private void drawString(Graphics g, String str, int xPos, int yPos, int containerWidth, int containerHeight, boolean center) {
        int strWidth = g.getFontMetrics().stringWidth(str);
        int strHeight = g.getFontMetrics().getHeight();
        if (strWidth <= containerWidth) {
            if (center) {
                xPos = xPos + (containerWidth - strWidth ) / 2;
            }
            yPos = yPos + (containerHeight + strHeight / 2) / 2;
            g.drawString(str, xPos, yPos);
        }
        else {
            int totalWidth = 0;
            StringBuilder stringBuilder = new StringBuilder();
            int i = 0;
            int len = str.length();
            while (i < len) {
                int width = g.getFontMetrics().stringWidth(str.substring(i, i + 1));
                if (totalWidth + width > containerWidth) {
                    //draw
                    yPos = yPos + strHeight;
                    g.drawString(stringBuilder.toString(), xPos, yPos);
                    totalWidth = 0;
                    stringBuilder = new StringBuilder();
                }
                else {
                    stringBuilder.append(str.charAt(i));
                    i++;
                    totalWidth += width;
                }
            }
            if (totalWidth > 0) {
                yPos = yPos + strHeight;
                g.drawString(stringBuilder.toString(), xPos, yPos);
            }

        }
    }

    private BufferedImage getBufferedImage(String imageUrl) throws IOException {
        BufferedImage bufferedImage = null;
        if (imageUrl != null && imageUrl.startsWith("http")) {
            URL url = new URL(imageUrl);
            bufferedImage = ImageIO.read(url);
        }
        else if (imageUrl != null) {
            File file = new File(imageUrl);
            bufferedImage = ImageIO.read(file);
        }
        return bufferedImage;
    }

    private BufferedImage drawHeaderImage() throws IOException {
        if (header == null) {
            return null;
        }

        int logoY = 0;
        int logoX = 0;
        int avatarY = 0;
        int avatarX = 0;
        BufferedImage avatarImage = getBufferedImage(header.getAvatarUrl());
        BufferedImage avatar = scale(avatarImage, header.getAvatarWidth());
        if (avatar != null) {
            headerHeight = avatar.getHeight();
        }
        BufferedImage logoImage = getBufferedImage(header.getLogoUrl());
        BufferedImage logo = scale(logoImage, header.getLogoWidth());
        if (logo != null) {
            headerHeight = headerHeight > logo.getHeight() ? headerHeight : logo.getHeight();
        }
        if (headerHeight == 0) {
            return null;
        }

        if (logo != null) {
            if (header.getLogoLayout() == Config.LAYOUT_CENTER) {
                logoX = (header.getWidth() - logo.getWidth()) / 2;
            }
            if (header.getLogoLayout() == Config.LAYOUT_RIGHT) {
                logoX = header.getWidth() - logo.getWidth();
            }
            logoY = logo.getHeight() < headerHeight ? (headerHeight - logo.getHeight()) / 2 : logoY;
        }
        if (avatar != null) {
            if (header.getAvatarLayout() == Config.LAYOUT_CENTER) {
                avatarX = (header.getWidth() - avatar.getWidth()) / 2;
            }
            if (header.getAvatarLayout() == Config.LAYOUT_RIGHT) {
                avatarX = header.getWidth() - avatar.getWidth();
            }
            avatarY = avatar.getHeight() < headerHeight ? (headerHeight - avatar.getHeight()) / 2 : avatarY;
        }

        BufferedImage profileImage = new BufferedImage(header.getWidth(), headerHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D profileImageGraphics = profileImage.createGraphics();
        profileImageGraphics.setColor(Color.white);//background-color
        profileImageGraphics.fillRect(0, 0, header.getWidth(), headerHeight);


        profileImageGraphics.drawImage(logo, logoX, logoY, null);
        profileImageGraphics.drawImage(avatar, avatarX, avatarY, null);
//        profileImageGraphics.setColor(Color.BLACK);//color
//        profileImageGraphics.setFont(new Font(font, Font.BOLD, userNameFontSize));
//        drawString(profileImageGraphics, userName, avatarWidth, 0, profileImageWidth - avatarWidth, avatarHeight);

        return profileImage;
    }

    private BufferedImage drawTextImage(String textContent, int textWidth, int textHeight, int textFontSize, String textFont) {
        if (textContent == null) {
            return null;
        }
        BufferedImage textImage = new BufferedImage(textWidth, textHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D textImageGraphics = textImage.createGraphics();
        textImageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        textImageGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
        textImageGraphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
        //textImageGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST, 140);
        textImageGraphics.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, 140);
        textImageGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        textImageGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
        textImageGraphics.setColor(Color.white);//background-color
        textImageGraphics.fillRect(0, 0, textWidth, textHeight);
        textImageGraphics.setColor(textColor);//color
        textImageGraphics.setFont(new Font(textFont, Font.PLAIN, textFontSize));
        drawString(textImageGraphics, textContent, 0, 0, textWidth, textHeight, false);
        return textImage;
    }

    private BufferedImage drawFooterImage() {
        if (footer == null || StrKit.isBlank(footer.getContent())) {
            return null;
        }
        footerHeight = footer.getHeight();
        BufferedImage footerImage = new BufferedImage(footer.getWidth(), footerHeight, BufferedImage.TYPE_INT_RGB);
        Graphics footerImageGraphics = footerImage.getGraphics();
        footerImageGraphics.setColor(Color.white);//background-color
        footerImageGraphics.fillRect(0, 0, footer.getWidth(), footerHeight);
        footerImageGraphics.setColor(Color.BLACK);//color
        footerImageGraphics.setFont(new Font(footer.getFont(), Font.PLAIN, footer.getFontSize()));
        drawString(footerImageGraphics, footer.getContent(), 0, 0, footer.getWidth(), footerHeight);
        return footerImage;
    }

    private BufferedImage drawDescImage() throws IOException {
        if (desc == null) {
            return null;
        }

        BufferedImage infoImage = getBufferedImage(desc.getInfoUrl());
        BufferedImage info = scale(infoImage, desc.getInfoWidth());
        if (info != null) {
            descHeight = info.getHeight();
        }
        List<BufferedImage> contentImages = new ArrayList<>();
        if (desc.getContents() != null) {
            String textFont = StrKit.isBlank(desc.getContentFont()) ? font : desc.getContentFont();
            for (String textContent : desc.getContents()) {
                BufferedImage textImage = drawTextImage(textContent, desc.getWidth(), desc.getContentHeight(), desc.getContentFontSize(), textFont);
                if (textImage != null) {
                    contentImages.add(textImage);
                    descHeight += textImage.getHeight();
                }
            }
        }

        if (descHeight == 0) {
            return null;
        }

        BufferedImage descImage = new BufferedImage(desc.getWidth(), descHeight, BufferedImage.TYPE_INT_RGB);
        Graphics profileImageGraphics = descImage.getGraphics();
        profileImageGraphics.setColor(Color.white);//background-color
        profileImageGraphics.fillRect(0, 0, desc.getWidth(), descHeight);

        int y = margin;
        if (info != null) {
            profileImageGraphics.drawImage(info, 0, y, null);
            y += info.getHeight();
        }
        for (BufferedImage image : contentImages) {
            profileImageGraphics.drawImage(image, 0, y, null);
            y += image.getHeight();
        }

        return descImage;
    }

    private BufferedImage drawQrCodeImage() throws WriterException {
        if (qrCode == null) {
            return null;
        }
        int[] qrCodeMargin = qrCode.getMargin();
        BufferedImage qrcodeImage = QrcodeKit.encode(qrCode.getContent(), qrCode.getWidth(), qrCode.getWidth());
        qrcodeHeight = qrcodeImage.getHeight() + 2 * margin + qrCodeMargin[1] + qrCodeMargin[3];

        int width = qrCode.getWidth() + qrCodeMargin[0] + qrCodeMargin[2];
        BufferedImage descImage = new BufferedImage(width, qrcodeHeight, BufferedImage.TYPE_INT_RGB);
        Graphics profileImageGraphics = descImage.getGraphics();
        profileImageGraphics.setColor(Color.white);//background-color
        profileImageGraphics.fillRect(0, 0, width, qrcodeHeight);

        profileImageGraphics.drawImage(qrcodeImage, qrCodeMargin[0], margin + qrCodeMargin[1], null);
        return descImage;
    }

    /**
     * 创建avatar/qrcode组成的图片
     * @return
     * @throws WriterException
     * @throws IOException
     */
    public BufferedImage drawImage() throws WriterException, IOException {
        BufferedImage profileImage = drawHeaderImage();
        BufferedImage descImage = drawDescImage();
        BufferedImage footerImage = drawFooterImage();
        BufferedImage qrcodeImage = drawQrCodeImage();

        // paint all images, preserving the alpha channels
        width += margin * 2;
        int height =  margin + headerHeight + descHeight + qrcodeHeight + footerHeight + margin;
        BufferedImage combinedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics combinedImageGraphics = combinedImage.getGraphics();
        combinedImageGraphics.setColor(Color.white);//background-color
        combinedImageGraphics.fillRect(0, 0, width, height);

        BufferedImage marginImage = new BufferedImage(width, margin, BufferedImage.TYPE_INT_RGB);
        Graphics marginImageGraphics = marginImage.getGraphics();
        marginImageGraphics.setColor(Color.white);//background-color
        marginImageGraphics.fillRect(0, 0, width, margin);
        combinedImageGraphics.drawImage(marginImage, 0, 0, null);

        int y = margin;
        if (profileImage != null) {
            combinedImageGraphics.drawImage(profileImage, margin, y, null);
            y += profileImage.getHeight();
        }

        if (descImage != null) {
            combinedImageGraphics.drawImage(descImage, margin, y, null);
            y += descImage.getHeight();
        }

        if (qrcodeImage != null) {
            combinedImageGraphics.drawImage(qrcodeImage, margin, y, null);
            y += qrcodeImage.getHeight();
        }
        if (footerImage != null) {
            combinedImageGraphics.drawImage(footerImage, margin, y, null);
        }

        BufferedImage tag = new BufferedImage(combinedImage.getWidth(), combinedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        tag.getGraphics().drawImage(combinedImage.getScaledInstance(combinedImage.getWidth(), combinedImage.getHeight(), Image.SCALE_SMOOTH), 0, 0, null);

        return tag;
    }

}
