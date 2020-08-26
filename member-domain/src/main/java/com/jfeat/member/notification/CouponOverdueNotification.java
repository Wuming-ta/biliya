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
public class CouponOverdueNotification extends AbstractNotification {

    //和 t_wechat_message_type 里的name字段对应, 通过该字段关联消息类型
    public static final String NAME = "coupon-overdue";
    public static final String TITLE = "title";
    public static final String REMARK = "remark";
    public static final String MESSAGE = "message";
    public static final String OVERDUE_DATE = "overdue-date";

    private static final String title = "优惠券即将过期";
    private static final String remark = "如有疑问请联系客服!";

    public static final String MESSAGE_VAL = "您有%s张优惠券即将过期,请尽快使用！";

    public CouponOverdueNotification(String openid) {
        super(openid, NAME);
        param(TITLE, title).param(REMARK, remark);
    }
}
