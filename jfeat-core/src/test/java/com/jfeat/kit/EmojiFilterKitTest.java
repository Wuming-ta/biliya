package com.jfeat.kit;

import org.junit.Test;

/**
 * Created by jackyhuang on 16/9/22.
 */
public class EmojiFilterKitTest {
    @Test
    public void test() {
        String text = "This is a smiley \ue415 \uD83C\uDFA6 face \uD83C\uDFA6 \uD860\uDD5D \uD860\uDE07 \uD860\uDEE2 \uD863\uDCCA \uD863\uDCCD \uD863\uDCD2 \uD867\uDD98 ";
        System.out.println(text);
        System.out.println(EmojiFilterKit.replaceEmoji(text));
    }
}
