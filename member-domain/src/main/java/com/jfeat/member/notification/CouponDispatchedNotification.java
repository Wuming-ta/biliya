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

package com.jfeat.member.notification;

import com.jfeat.wechat.notification.AbstractNotification;

/**
 * Created by kang on 17/2/14.
 */
public class CouponDispatchedNotification extends AbstractNotification {

    //和 t_wechat_message_type 里的name字段对应, 通过该字段关联消息类型
    public static final String NAME = "coupon-dispatched";
    public static final String TITLE = "title";
    public static final String MESSAGE = "message";
    public static final String REMARK = "remark";
    public static final String VALID_DATE = "valid-date";

    private static final String title = "恭喜您获得优惠券!";
    private static final String message = "%s";
    private static final String remark = "优惠多多, 马上登录商城购物吧!";

    public CouponDispatchedNotification(String openid, int value, int count) {
        super(openid, NAME);
        String messageStr;
        if (value > 0) {
            messageStr = String.format(message, "价值" + value + "元");
        } else {
            messageStr = String.format(message, count + "张");
        }
        param(TITLE, title).param(MESSAGE, messageStr).param(REMARK, remark);
    }
}
