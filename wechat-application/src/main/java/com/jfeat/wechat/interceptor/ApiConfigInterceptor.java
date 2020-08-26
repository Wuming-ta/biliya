///*
// *   Copyright (C) 2014-2016 www.kequandian.net
// *
// *    The program may be used and/or copied only with the written permission
// *    from www.kequandian.net or in accordance with the terms and
// *    conditions stipulated in the agreement/contract under which the program
// *    has been supplied.
// *
// *    All rights reserved.
// *
// */
//
//package com.jfeat.wechat.interceptor;
//
//import com.jfeat.wechat.controller.WechatBaseController;
//import com.jfinal.aop.Interceptor;
//import com.jfinal.aop.Invocation;
//import com.jfinal.core.Controller;
//import com.jfinal.weixin.sdk.api.ApiConfigKit;
//import com.jfinal.weixin.sdk.jfinal.ApiController;
//
///**
// * Created by jackyhuang on 16/9/1.
// */
//public class ApiConfigInterceptor implements Interceptor {
//    @Override
//    public void intercept(Invocation invocation) {
//        Controller controller = invocation.getController();
//        if(!(controller instanceof WechatBaseController)) {
//            throw new RuntimeException("控制器需要继承 WechatBaseController");
//        } else {
//            try {
//                ApiConfigKit.setThreadLocalApiConfig(((WechatBaseController)controller).getApiConfig());
//                invocation.invoke();
//            } finally {
//                ApiConfigKit.removeThreadLocalApiConfig();
//            }
//
//        }
//    }
//}
