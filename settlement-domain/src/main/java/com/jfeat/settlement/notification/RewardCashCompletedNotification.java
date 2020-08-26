package com.jfeat.settlement.notification;

import com.jfeat.wechat.notification.AbstractNotification;

/**
 * Created by kang on 2017/2/22.
 */
public class RewardCashCompletedNotification extends AbstractNotification {
    //和 t_wechat_message_type 里的name字段对应, 通过该字段关联消息类型
    public static final String NAME = "reward-cash-completed";
    public static final String TITLE = "title";
    public static final String AMOUNT = "amount";
    public static final String COMPLETED_TIME = "completed-time";
    public static final String REMARK = "remark";

    private static final String title = "您已成功提现";
    private static final String remark = "如有疑问请联系客服!";

    public RewardCashCompletedNotification(String openid) {
        super(openid, NAME);
        param(TITLE, title).param(REMARK, remark);
    }
}
