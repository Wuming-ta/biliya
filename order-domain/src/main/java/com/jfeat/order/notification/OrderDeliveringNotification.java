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

package com.jfeat.order.notification;

import com.jfeat.wechat.notification.AbstractNotification;

/**
 * Created by jackyhuang on 17/2/14.
 */
public class OrderDeliveringNotification extends AbstractNotification {

    //和 t_wechat_message_type 里的name字段对应, 通过该字段关联消息类型
    public static final String NAME = "order-delivering";
    public static final String TITLE = "title";
    public static final String ORDER_NUMBER = "order-number";
    public static final String EXPRESS_COMPANY = "express-company";
    public static final String EXPRESS_NUMBER = "express-number";
    public static final String CONTACT_USER = "contact-user";
    public static final String REMARK = "remark";

    private static final String title = "订单已发货";
    private static final String remark = "如有疑问请联系客服!";

    public OrderDeliveringNotification(String openid) {
        super(openid, NAME);
        param(TITLE, title).param(REMARK, remark);
    }
}
