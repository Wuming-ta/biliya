package com.jfeat.order.notification;

import com.jfeat.wechat.notification.AbstractNotification;

/**
 * Created by kang on 2017/3/31.
 */
public class OrderServiceCreatedNotification extends AbstractNotification {
    //和 t_wechat_message_type 里的name字段对应, 通过该字段关联消息类型
    public static final String NAME = "order-service-created";
    public static final String TITLE = "title";
    public static final String ORDER_NUMBER = "order-number";
    public static final String ORDER_PRICE = "order-price";

    public static final String REMARK = "remark";

    private static final String title = "您的申请已成功提交";
    private static final String remark = "如有疑问请联系客服!";

    public OrderServiceCreatedNotification(String openid) {
        super(openid, NAME);
        param(TITLE, title).param(REMARK, remark);
    }

}
