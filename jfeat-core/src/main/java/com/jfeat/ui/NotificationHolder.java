package com.jfeat.ui;

import com.google.common.collect.Lists;
import com.jfeat.ui.model.Notification;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by jackyhuang on 16/11/2.
 */
public class NotificationHolder implements Serializable {
    private List<Notification> notifications = Lists.newArrayList();
    private Map<String, IPrivilegeStrategy> privilegeStrategyMap;

    public List<Notification> getNotifications() {
        return notifications;
    }


    public Map<String, IPrivilegeStrategy> getPrivilegeStrategyMap() {
        return privilegeStrategyMap;
    }

    public void setPrivilegeStrategyMap(Map<String, IPrivilegeStrategy> privilegeStrategyMap) {
        this.privilegeStrategyMap = privilegeStrategyMap;
    }

    public void fetchNotifications() {
        notifications.clear();
        List<Notification> all = Notification.dao.findAll();
        for (Notification notification : all) {
            IPrivilegeStrategy privilegeStrategy = privilegeStrategyMap.get(notification.getIdentifier());
            if (privilegeStrategy == null || privilegeStrategy.isAllowed(notification.getIdentifier())) {
                notifications.add(notification);
            }
        }
    }
}
