package com.jfeat.partner.notification;

import com.jfeat.wechat.notification.AbstractNotification;

/**
 * Created by kang on 2017/6/27.
 */
public class TempCrownApprovedNotification extends AbstractNotification {
    public static final String NAME = "temp-crown-approved";

    public static final String TITLE = "title";
    public static final String ASSIGNOR = "assignor";
    public static final String ASSIGNEE = "assignee";
    public static final String STATUS = "status";
    public static final String REMARK = "remark";

    private static final String title = "皇冠商申请通过";
    private static final String remark = "如有疑问请联系客服!";
    private static final String status = "已通过";

    public TempCrownApprovedNotification(String openid) {
        super(openid, NAME);
        param(TITLE, title).param(REMARK, remark).param(STATUS, status);
    }
}
