package com.jfeat.core.handler;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * Created by jackyhuang on 16/12/20.
 */
public class HtmlFilter {
    public static String getBasicHtmlandimage(String html) {
        if (html == null)
            return null;
        return Jsoup.clean(html, Whitelist.basicWithImages());
    }
}
