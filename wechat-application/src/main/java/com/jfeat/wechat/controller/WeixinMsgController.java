/*
 *   Copyright (C) 2014-2016 www.kequandian.net
 *
 *    The program may be used and/or copied only with the written permission
 *    from www.kequandian.net or in accordance with the terms and
 *    conditions stipulated in the agreement/contract under which the program
 *    has been supplied.
 *
 *    All rights reserved.
 *
 */

package com.jfeat.wechat.controller;


import com.jfeat.Global;
import com.jfeat.identity.model.User;
import com.jfeat.identity.service.DefaultRole;
import com.jfeat.identity.service.UserService;
import com.jfeat.ui.MenuInterceptor;
import com.jfeat.wechat.config.WxConfig;
import com.jfeat.wechat.service.WechatAutoreplyService;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Ret;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.CustomServiceApi;
import com.jfinal.weixin.sdk.api.UserApi;
import com.jfinal.weixin.sdk.jfinal.MsgControllerAdapter;
import com.jfinal.weixin.sdk.msg.in.InTextMsg;
import com.jfinal.weixin.sdk.msg.in.event.InFollowEvent;
import com.jfinal.weixin.sdk.msg.in.event.InMenuEvent;
import com.jfinal.weixin.sdk.msg.out.OutCustomMsg;
import com.jfinal.weixin.sdk.msg.out.OutMsg;
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 公众号消息处理
 * Created by jacky on 4/19/16.
 */
@ControllerBind(controllerKey = "/wxmsg")
@Clear({MenuInterceptor.class})
public class WeixinMsgController extends MsgControllerAdapter {

    private static Logger logger = LoggerFactory.getLogger(WeixinMsgController.class);
    WechatAutoreplyService wechatAutoreplyService = Enhancer.enhance(WechatAutoreplyService.class);

    @Override
    protected void renderDefault() {
        Global.setWeixinHosted(true);
        super.renderDefault();
    }

    @Override
    protected void processInFollowEvent(InFollowEvent inFollowEvent) {
        Global.setWeixinHosted(true);
        String event = inFollowEvent.getEvent();
        int followed = event.equals(InFollowEvent.EVENT_INFOLLOW_SUBSCRIBE) ? 0 : 1;

        UserService userService = new UserService();
        String openid = inFollowEvent.getFromUserName();
        User user = User.dao.findByWeixin(openid);
        ApiResult userInfoRes = UserApi.getUserInfo(openid);
        logger.debug("user infollow event, userInfo = {}", JsonKit.toJson(userInfoRes));
        String nickname = userInfoRes.get("nickname");
        String headimgurl = userInfoRes.get("headimgurl");
        String unionid = userInfoRes.get("unionid");
        Integer sex = userInfoRes.get("sex");
        if (user == null) {
            user = new User();
            user.setName(nickname);
            user.setWechatName(nickname);
            user.setAvatar(headimgurl);
            user.setWxUnionid(unionid);
            user.setSex(sex);
            user.setWeixin(openid);
            user.setLoginName(openid);
            user.setPassword(openid + System.currentTimeMillis());
            user.resetTokenExpiredDate();
            user.setFollowed(followed);
            user.setFollowTime(new Date());
            user.setAppUser(User.APP_USER);

            Integer roleId = DefaultRole.me().getRoleProvider().getDefault().getId();
            Integer[] roles = new Integer[]{roleId};
            Ret ret = userService.createUser(user, roles);
            logger.debug("user saved. ret={}, user={}", ret.getData(), user);
        } else {
            user.setName(nickname);
            user.setWechatName(nickname);
            user.setAvatar(headimgurl);
            user.setWxUnionid(unionid);
            user.setSex(sex);
            user.setFollowed(followed);
            user.setFollowTime(new Date());
            user.setPassword("");
            userService.updateUser(user, null);
        }

        //render with configured message
        OutMsg outMsg =wechatAutoreplyService.getSubscribeOutMsg(getInMsg());
        if (outMsg != null) {
            render(outMsg);
            return;
        }
        renderNull();
    }

    /**
     * 转发消息到多客服系统
     *
     * @param inTextMsg
     */
    @Override
    protected void processInTextMsg(InTextMsg inTextMsg) {
        Global.setWeixinHosted(true);
        OutMsg outMsg = wechatAutoreplyService.getKeywordMsg(wechatAutoreplyService.getKeywordToSend(inTextMsg.getContent()), inTextMsg);
        if (outMsg != null) {   //有keyword命中，则直接回复给用户
            render(outMsg);
            return;
        }
        //没有keyword命中，则看是否有客服在线
        ApiResult apiResult = CustomServiceApi.getOnlineKFList();
        if (apiResult.isSucceed() && apiResult.getList("kf_online_list").size() > 0) {
            outMsg = new OutCustomMsg(inTextMsg);
            outMsg.setMsgType("transfer_customer_service");
            render(outMsg);
            return;
        }
        //没有keyword命中，且无客服在线
        outMsg = wechatAutoreplyService.getMessageOutMsg(inTextMsg);
        if (outMsg != null) {
            render(outMsg);
            return;
        }
        renderNull();
    }

    @Override
    protected void processInMenuEvent(InMenuEvent inMenuEvent) {
        Global.setWeixinHosted(true);
        logger.debug("菜单事件：" + inMenuEvent.getFromUserName());
        OutTextMsg outMsg = new OutTextMsg(inMenuEvent);
        outMsg.setContent("菜单事件内容是：" + inMenuEvent.getEventKey());
        render(outMsg);
    }
//
//    @Override
//    /**
//     * 如果要支持多公众账号，只需要在此返回各个公众号对应的 ApiConfig 对象即可 可以通过在请求 url 中挂参数来动态从数据库中获取
//     * ApiConfig 属性值
//     */
//    public ApiConfig getApiConfig() {
//        ApiConfig ac = new ApiConfig();
//
//        // 配置微信 API 相关常量
//        ac.setToken(WxConfig.getToken());
//        ac.setAppId(WxConfig.getAppId());
//        ac.setAppSecret(WxConfig.getAppSecret());
//
//        /**
//         * 是否对消息进行加密，对应于微信平台的消息加解密方式： 1：true进行加密且必须配置 encodingAesKey
//         * 2：false采用明文模式，同时也支持混合模式
//         */
//        ac.setEncryptMessage(WxConfig.isEncryptMessage());
//        ac.setEncodingAesKey(WxConfig.getEncodingAesKey());
//        return ac;
//    }
}
