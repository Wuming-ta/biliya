package com.jfeat.api;

import com.google.zxing.WriterException;
import com.jfeat.kit.qrcode.QrcodeKit;
import com.jfeat.util.*;
import org.junit.Ignore;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by jackyhuang on 16/8/2.
 */
public class DrawProfileTest {


    @Test
    @Ignore
    public void test() throws IOException, WriterException {
        String logoUrl = null;//"http://www.softto.com.cn/templets/default/images/logo_g.jpg";
        String avatarUrl = null;//"http://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83erFzia6apicQiaR33hL8pan6JjeTZlYgFtey4Kfo5Fzic1I2PMict1kXujoBBnd3IFdgibsSIalA4swMItQ/132";

        String infoUrl = null;//"http://120.79.77.207:8080/app/static/static/noTest.f3c8af24.png";
        String qrcodeContent = "http://www.kequandian.net/app/app?invite_code=23432423rw3232423";
        String[] textContents = null;//{"邀你分享商城,", "攒积分免费领取商品就那么简单!"};
        String footerContent = null;//"长按识别二维码";

        BufferedImage combinedImage = DrawKit.drawQrcode(qrcodeContent, logoUrl, avatarUrl, infoUrl, footerContent, textContents);
        // Save as new image
        ImageIO.write(combinedImage, "JPEG", new File("out.png"));
    }
}
