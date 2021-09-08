/*
 * Copyright (C) 2014-2015 by ehngjen @ www.jfeat.com
 *
 *  The program may be used and/or copied only with the written permission
 *  from JFeat.com, or in accordance with the terms and
 *  conditions stipulated in the agreement/contract under which the program
 *  has been supplied.
 *
 *  All rights reserved.
 */

package com.jfeat.flash;

import com.jfeat.core.BaseController;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by ehngjen on 5/4/2015.
 */
public class Flash implements Interceptor {
    @Override
    /**
     * 该拦截器取得当前ActionPath，从Cache中检查是否有传送给当前Action的Flash对象Map
     * 若有，则遍历Map，并将所有key，value注入到当前的request请求中。
     */
    public void intercept(Invocation ai) {
        BaseController c = (BaseController) ai.getController();
        HttpSession session = c.getSession(false);
        if(null == session){
            return;
        }
        StringBuilder curAction = new StringBuilder(ai.getActionKey());
        if (StrKit.notBlank(c.getPara())) {
            curAction.append("/");
            curAction.append(c.getPara());
        }
        Map<String, Object> flashMap = c.getFlashManager().getFlash(session, curAction.toString());
        if(flashMap != null){
            for(Map.Entry<String,Object> flashEntry: flashMap.entrySet()){
                c.setAttr(flashEntry.getKey(), flashEntry.getValue());
            }
        }
        ai.invoke();
    }
}
