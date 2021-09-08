package com.jfeat.kit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jackyhuang on 16/9/22.
 */
public class EmojiFilterKit {

    /* 要过滤的特殊字符-emoji */
    private static Pattern emoji = Pattern.compile ("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]|[\ue000-\uefff]",
            Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE ) ;

    /**
     * 是否包含emoji特殊字符
     * @param source
     * @param emoji
     * @return
     */
    private static boolean containsEmoji(String source,Pattern emoji){
        CharSequence cs = null;
        cs = source;
        Matcher emojiMatcher = emoji.matcher(cs);
        if (emojiMatcher.find()) {
            return true;
        }
        return false;
    }

    /**
     * 将字符串中得特殊字符转为空
     * @param source   原字符串, 不需要特殊处理
     * @return
     */
    public static String replaceEmoji(String source){
        if(containsEmoji(source, emoji)){
            CharSequence cs = null;
            cs = source;
            Matcher emojiMatcher = emoji.matcher(cs);
            if (emojiMatcher.find()) {
                return emojiMatcher.replaceAll("*");
            }
        }
        return source;
    }
}
