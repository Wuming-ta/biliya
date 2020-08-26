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

package com.jfeat.identity.service;

import com.jfeat.core.BaseService;
import com.jfeat.identity.model.User;
import com.jfeat.identity.model.UserJoinNotify;
import com.jfeat.identity.mq.StaffUpdatedNotifier;
import com.jfeat.identity.mq.UserUpdatedNotifier;
import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.ArrayList;
import java.util.List;

/**
 * The caller should use
 * UserService service = Enhancer.enhance(UserService.class);
 * to enable AOP for this service.
 * <p>
 * Created by jacky on 3/7/16.
 */
public class UserService extends BaseService {

    @Before(Tx.class)
    public Ret createUser(User user, Integer[] roles) {
        if (User.dao.findByLoginName(user.getLoginName()) != null) {
            return failure();
        }
        if (StrKit.isBlank(user.getPassword())) {
            return failure();
        }

        user.save();
        if (roles != null) {
            user.updateRoles(roles);
        }

        if (StrKit.notBlank(user.getPhone())
                && user.getAppUser() == User.APP_USER
                && !UserJoinNotify.dao.notified(user.getId())) {
            UserJoinNotify notify = new UserJoinNotify();
            notify.setUserId(user.getId());
            notify.save();
        }

        if (user.getAppUser() == User.APP_USER) {
            UserUpdatedNotifier.sendUserUpdatedNotify(user, UserUpdatedNotifier.Type.REGISTER);
        }

        return success();
    }

    @Before(Tx.class)
    public Ret updateUser(User user, Integer[] roles) {
        User originalUser = User.dao.findById(user.getId());
        user.update();
        logger.debug("Going to update user: originUser = {}, newUser = {}", originalUser, user);
        if (roles != null) {
            user.updateRoles(roles);
        }
        if (originalUser.getFollowed() != null
                && user.getFollowed() != null
                && !originalUser.getFollowed().equals(user.getFollowed())) {
            if (user.getFollowed() == User.INFOLLOW_SUBSCRIBE) {
                user.userInfollowSubscribeNotify();
                UserUpdatedNotifier.sendUserUpdatedNotify(user, UserUpdatedNotifier.Type.SUBSCRIBED);
            } else {
                user.userInfollowUnSubscribeNotify();
                UserUpdatedNotifier.sendUserUpdatedNotify(user, UserUpdatedNotifier.Type.UNSUBSCRIBED);
            }
        }

        if (StrKit.isBlank(originalUser.getPhone())
                && StrKit.notBlank(user.getPhone())
                && originalUser.getAppUser() == User.APP_USER
                && !UserJoinNotify.dao.notified(user.getId())) {
            UserJoinNotify notify = new UserJoinNotify();
            notify.setUserId(user.getId());
            notify.save();
        }

        if ((originalUser.getInviterId() == null && user.getInviterId() != null)
                || (originalUser.getInviterId() != null && !originalUser.getInviterId().equals(user.getInviterId()))) {
            UserUpdatedNotifier.sendUserUpdatedNotify(user, UserUpdatedNotifier.Type.INVITER_UPDATED);
        }

        if ((originalUser.getPhone() == null && user.getPhone() != null)
                || (originalUser.getPhone() != null && !originalUser.getPhone().equals(user.getPhone()))) {
            UserUpdatedNotifier.sendUserUpdatedNotify(user, UserUpdatedNotifier.Type.PHONE_UPDATED);
        }

        if (StrKit.isBlank(originalUser.getStoreCode()) && StrKit.notBlank(user.getStoreCode()) ||
                StrKit.isBlank(originalUser.getAssistantCode()) && StrKit.notBlank(user.getAssistantCode())) {
            UserUpdatedNotifier.sendUserUpdatedNotify(user, UserUpdatedNotifier.Type.STORE_UPDATED);
        }

        if (user.getAppUser() == User.ADMIN_USER) {
            StaffUpdatedNotifier.sendStaffUpdatedNotify(user);
        }

        return success();
    }

    /**
     * 批量设置新加入用户通知为已读
     *
     * @param ids
     */
    public void clearUserJoinNotify(List<Integer> ids) {
        if (ids != null && !ids.isEmpty()) {
            List<UserJoinNotify> list = new ArrayList<>();
            ids.forEach(id -> {
                UserJoinNotify notify = new UserJoinNotify();
                notify.setId(id);
                notify.setIsRead(UserJoinNotify.READ.shortValue());
                list.add(notify);
            });
            Db.batchUpdate(list, 100);
        }
    }

    /**
     * 批量设置新加入用户通知为已读
     */
    public void clearUserJoinNotify() {
        UserJoinNotify userJoinNotify = new UserJoinNotify();
        userJoinNotify.markAllAsRead();
    }

    public Ret deleteUser(int userId) {
        User user = User.dao.findById(userId);
        if (user == null) {
            return failure("user.not.found");
        }
        user.delete();
        UserUpdatedNotifier.sendUserUpdatedNotify(user, UserUpdatedNotifier.Type.DELETED);
        return success();
    }

    public Ret updatePhone(int userId, String phone) {
        logger.debug("update user' phone. userid = {}, phone = {}", userId, phone);
        User user = User.dao.findById(userId);
        user.setPhone(phone);
        user.setPassword("");
        return updateUser(user, null);
    }

    public void invalidInvitationQrcodeUrl() {
        User user = new User();
        user.invalidInvitationQrcodeUrl();
    }
}
