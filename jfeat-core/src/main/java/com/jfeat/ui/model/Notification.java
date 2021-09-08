package com.jfeat.ui.model;

import com.jfeat.core.BaseModel;
import com.jfeat.ui.model.base.NotificationBase;
import com.jfinal.ext.plugin.tablebind.TableBind;

/**
 * Created by jackyhuang on 16/11/2.
 */
@TableBind(tableName = "t_notification")
public class Notification extends NotificationBase<Notification> {

    public static Notification dao = new Notification();
}
