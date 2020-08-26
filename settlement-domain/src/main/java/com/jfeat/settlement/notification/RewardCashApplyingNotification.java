package com.jfeat.settlement.notification;

import com.jfeat.wechat.notification.AbstractNotification;

/**
 * Created by kang on 2017/2/22.
 */
public class RewardCashApplyingNotification extends AbstractNotification {
    //和 t_wechat_message_type 里的name字段对应, 通过该字段关联消息类型
    public static final String NAME = "reward-cash-applying";
    public static final String TITLE = "title";
    public static final String AMOUNT = "amount";
    public static final String APPLY_TIME = "apply-time";
    public static final String REMARK = "remark";

    private static final String title = "您已成功提交提现申请";
    private static final String remark = "如有疑问请联系客服!";

    public RewardCashApplyingNotification(String openid) {
        super(openid, NAME);
        param(TITLE, title).param(REMARK, remark);
    }
}
