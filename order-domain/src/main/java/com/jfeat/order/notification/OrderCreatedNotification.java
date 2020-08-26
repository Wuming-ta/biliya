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
public class OrderCreatedNotification extends AbstractNotification {

    //和 t_wechat_message_type 里的name字段对应, 通过该字段关联消息类型
    public static final String NAME = "order-created";
    public static final String TITLE = "title";
    public static final String ORDER_NUMBER = "order-number";
    public static final String ORDER_PRICE = "order-price";
    public static final String CONTACT_USER = "contact-user";
    public static final String CONTACT_ADDRESS = "contact-address";
    public static final String CONTACT_PHONE = "contact-phone";
    public static final String REMARK = "remark";

    private static final String title = "您已成功下单";
    private static final String remark = "如有疑问请联系客服!";

    public OrderCreatedNotification(String openid) {
        super(openid, NAME);
        param(TITLE, title).param(REMARK, remark);
    }
}
