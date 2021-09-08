package com.jfeat.kit.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by jackyhuang on 16/7/30.
 */
public class QrcodeKit {

    private static final String format = "PNG";

    public static byte[] encodeToBytes(String contents) throws IOException, WriterException {
        return encodeToBytes(contents, 500, 500);
    }

    public static byte[] encodeToBytes(String contents, int width, int height) throws IOException, WriterException {
        BufferedImage image = encode(contents, width, height);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, format, outputStream);
        return outputStream.toByteArray();
    }

    public static void encode(String contents, int width, int height, String imagePath) throws WriterException, IOException {
        BufferedImage image = encode(contents, width, height);
        OutputStream outputStream = new FileOutputStream(imagePath);
        ImageIO.write(image, format, outputStream);
    }

    public static BufferedImage encode(String contents, int width, int height) throws WriterException {
        return encode(contents, width, height, 0);
    }

    public static BufferedImage encode(String contents, int width, int height, int margin) throws WriterException {
        Map<EncodeHintType, Object> hints = new Hashtable<>();
        // 指定纠错等级
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        // 指定编码格式
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 边框为0
        hints.put(EncodeHintType.MARGIN, margin);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, width, height, hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

}
