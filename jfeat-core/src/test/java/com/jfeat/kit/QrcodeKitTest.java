package com.jfeat.kit;

import com.google.zxing.WriterException;
import com.jfeat.kit.qrcode.QrcodeKit;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by jackyhuang on 16/7/30.
 */
public class QrcodeKitTest {

    //@Test
    public void testEncode() throws IOException, WriterException {
        QrcodeKit.encode("helloworld", 400, 400, "qrcode.png");
    }
}
