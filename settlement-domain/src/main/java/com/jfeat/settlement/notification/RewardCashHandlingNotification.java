package com.jfeat.settlement.notification;

import com.jfeat.wechat.notification.AbstractNotification;

/**
 * Created by kang on 2017/2/22.
 */
public class RewardCashHandlingNotification extends AbstractNotification {
    //和 t_wechat_message_type 里的name字段对应, 通过该字段关联消息类型
    public static final String NAME = "reward-cash-handling";
    public static final String TITLE = "title";
    public static final String REMARK = "remark";

    private static final String title = "提现申请正在处理";
    private static final String remark = "请耐心等待，如有疑问请联系客服!";

    public RewardCashHandlingNotification(String openid) {
        super(openid, NAME);
        param(TITLE, title).param(REMARK, remark);
    }
}
