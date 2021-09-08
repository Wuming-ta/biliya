package com.jfeat.core.handler;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by huangjacky on 16/5/23.
 */

public class HttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper{

    public HttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * 重写并过滤getParameter方法
     */
    @Override
    public String getParameter(String name) {
        return HtmlFilter.getBasicHtmlandimage(super.getParameter(name));
    }

    /**
     * 重写并过滤getParameterValues方法
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (null == values){
            return null;
        }
        for (int i = 0; i < values.length; i++) {
            values[i] = HtmlFilter.getBasicHtmlandimage(values[i]);
        }
        return values;
    }

    /**
     * 重写并过滤getParameterMap方法
     */
    @Override
    public Map getParameterMap() {
        Map paraMap = super.getParameterMap();
        Map newParaMap = new HashMap();
        // 对于paraMap为空的直接return
        if (null == paraMap || paraMap.isEmpty()) {
            return paraMap;
        }
        Iterator itor = paraMap.entrySet().iterator();
        while (itor.hasNext()) {
            Map.Entry entry = (Map.Entry) itor.next();
            String key = (String) entry.getKey();
            String[] values = (String[])entry.getValue();
            if (null == values) {
                continue;
            }
            String[] newValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                newValues[i] = HtmlFilter.getBasicHtmlandimage(values[i]);
            }
            newParaMap.put(key, newValues);
        }
        return newParaMap;
    }
}

