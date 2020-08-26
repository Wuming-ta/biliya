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

package com.jfeat.identity.controller;


import com.jfeat.core.BaseController;
import com.jfeat.core.PhotoGalleryConstants;
import com.jfeat.core.UploadedFile;
import com.jfeat.flash.Flash;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.interceptor.RefreshShiroUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.upload.filerenamepolicy.CustomParentDirFileRenamePolicy;
import com.jfinal.ext.plugin.upload.filerenamepolicy.NamePolicy;
import com.jfinal.upload.UploadFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Before(CurrentUserInterceptor.class)
public class ProfileController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Override
    @Before(Flash.class)
    public void index() {
        User currentUser = getAttr("currentUser");
        setAttr("user", currentUser);
    }

    @Override
    @Before(RefreshShiroUserInterceptor.class)
    public void update() {
        User currentUser = getAttr("currentUser");
        String subDir = "avatar";
        CustomParentDirFileRenamePolicy policy = new CustomParentDirFileRenamePolicy(subDir, NamePolicy.RANDOM_NAME);
        UploadFile avatar = getFile("avatar", PhotoGalleryConstants.me().getUploadPath(), policy);
        User user = getModel(User.class);
        if (user.getId() != currentUser.getId()) {
            String message = "User " + currentUser + " cannot update other user's profile.";
            logger.warn(message);
            setFlash("message", getRes().get("identity.profile.failed") + "<br>" + message);
        }
        else {
            if (avatar != null) {
                UploadedFile.remove(currentUser.getAvatar());
                String url = UploadedFile.buildUrl(avatar, subDir);
                user.setAvatar(url);
                user.setPassword("");
            }
            user.update();
            setAttr("user", user);
            setFlash("message", getRes().get("identity.profile.success"));
        }

        redirect("/profile");
    }

    public void changePassword() {

    }

    public void doChangePassword() {
        String oldPassword = getPara("oldPassword");
        String newPassword = getPara("newPassword");
        User currentUser = getAttr("currentUser");
        if (currentUser.verifyPassword(oldPassword)) {
            currentUser.setPassword(newPassword);
            currentUser.update();
            setFlash("message", getRes().get("identity.change_password.success"));
        }
        else {
            setFlash("message", getRes().get("identity.change_password.verify_failed"));
        }
        redirect("/profile");
    }

}
