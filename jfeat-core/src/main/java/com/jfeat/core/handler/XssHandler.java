package com.jfeat.core.handler;

import com.jfinal.handler.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by huangjacky on 16/5/23.
 */
public class XssHandler extends Handler {

    // 排除的url，使用的target.startsWith匹配的
    private List<String> excludes;

    public XssHandler(List<String> excludes) {
        this.excludes = excludes;

    }

    @Override
    public void handle(String target, HttpServletRequest request,
                       HttpServletResponse response, boolean[] isHandled) {
        // 对于非静态文件，和非指定排除的url实现过滤
        if (target.indexOf(".") == -1) {
            boolean found = false;
            for (String exclude : excludes) {
                if (target.startsWith(exclude)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                request = new HttpServletRequestWrapper(request);
            }
        }
        next.handle(target, request, response, isHandled);
    }
}