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

package com.jfeat.core.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfinal.handler.Handler;
 
/**
 * �޸� url:test;jsessionid=XXXXXXXXXXX ��ʽurl�Ự��ʧ����
 * @author axhack
 */
public class SessionHandler extends Handler {  
 
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        int index = target.lastIndexOf(";jsessionid");
        if (index == -1) {
            index = target.lastIndexOf(";JSESSIONID");
        }
        target = index==-1?target:target.substring(0, index);
        nextHandler.handle(target, request, response, isHandled);
    }
}
