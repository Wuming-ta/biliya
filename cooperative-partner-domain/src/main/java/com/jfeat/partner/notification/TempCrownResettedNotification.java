package com.jfeat.partner.notification;

import com.jfeat.wechat.notification.AbstractNotification;

/**
 * Created by kang on 2017/7/3.
 */
public class TempCrownResettedNotification extends AbstractNotification {

    public static final String NAME = "temp-crown-resetted";
    public static final String TITLE = "title";
    public static final String RESETTED_TIME = "resetted-time";
    public static final String REASON = "reason";
    public static final String REMARK = "remark";

    private static final String title = "临时皇冠资格撤销通知";
    private static final String remark = "您的临时皇冠资格已被撤销!";
    public static final String reason = "您在%s小时内没有完成%s元的批发额";

    public TempCrownResettedNotification(String openid) {
        super(openid, NAME);
        param(TITLE, title).param(REMARK, remark);
    }
}
