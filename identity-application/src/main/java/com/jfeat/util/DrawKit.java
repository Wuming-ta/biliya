package com.jfeat.util;

import com.google.zxing.WriterException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author jackyhuang
 * @date 2018/11/7
 */
public class DrawKit {

    public static byte[] imageToBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "JPEG", outputStream);
        return outputStream.toByteArray();
    }

    public static BufferedImage drawQrcode(String qrcodeContent,
                                           String logoUrl,
                                           String avatarUrl,
                                           String infoUrl,
                                           String footerContent,
                                           String[] textContents) throws IOException, WriterException {

        int width = 750;
        DrawProfile drawProfile = new DrawProfile();

        Header header = new Header();
        header.setWidth(width)
                .setLogoUrl(logoUrl).setLogoWidth(200).setLogoLayout(Config.LAYOUT_CENTER)
                .setAvatarUrl(avatarUrl).setAvatarWidth(100);

        Desc desc = new Desc();
        desc.setWidth(width)
                .setContents(textContents).setContentFontSize(40).setContentHeight(100)
                .setInfoUrl(infoUrl).setInfoWidth(width);

        QrCode qrCode = new QrCode();
        qrCode.setWidth(width - 200).setMargin(new int[] { 100, 10, 100, 10 }).setContent(qrcodeContent);

        Footer footer = new Footer();
        footer.setWidth(width).setHeight(50).setContent(footerContent).setFontSize(38);

        drawProfile.setWidth(width).setTextColor(new Color(115, 66, 132))
                .setHeader(header)
                .setDesc(desc)
                .setQrCode(qrCode)
                .setFooter(footer);

        return drawProfile.drawImage();
    }

    public static String upload(BufferedImage bufferedImage) throws IOException {
        return UploadImage.upload(imageToBytes(bufferedImage));
    }

    public static String upload(InputStream inputStream) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        return upload(bufferedImage);
    }
}
