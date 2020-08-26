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

package com.jfeat.misc.controller;

import com.jfeat.core.BaseController;
import com.jfeat.flash.Flash;
import com.jfeat.misc.model.SystemAnnouncement;
import com.jfinal.aop.Before;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * Created by jackyhuang on 16/10/17.
 */
public class SystemAnnouncementController extends BaseController {

    @Override
    @RequiresPermissions("MiscApplication.view")
    @Before(Flash.class)
    public void index() {
        setAttr("announcements", SystemAnnouncement.dao.findAll());
    }

    @Override
    @RequiresPermissions("MiscApplication.edit")
    public void add() {

    }

    @Override
    @RequiresPermissions("MiscApplication.edit")
    public void save() {
        SystemAnnouncement announcement = getModel(SystemAnnouncement.class);
        announcement.save();
        redirect("/system_announcement");
    }

    @Override
    @RequiresPermissions("MiscApplication.edit")
    public void edit() {
        setAttr("announcement", SystemAnnouncement.dao.findById(getParaToInt()));
    }

    @Override
    @RequiresPermissions("MiscApplication.edit")
    public void update() {
        SystemAnnouncement announcement = getModel(SystemAnnouncement.class);
        announcement.update();
        redirect("/system_announcement");
    }

    @Override
    @RequiresPermissions("MiscApplication.delete")
    public void delete() {
        SystemAnnouncement announcement = SystemAnnouncement.dao.findById(getParaToInt());
        if (announcement != null) {
            announcement.delete();
        }
        redirect("/system_announcement");
    }

    @RequiresPermissions("MiscApplication.edit")
    public void enable() {
        SystemAnnouncement announcement = SystemAnnouncement.dao.findById(getParaToInt());
        if (announcement == null) {
            renderError(404);
            return;
        }
        announcement.setEnabled(SystemAnnouncement.ENABLED);
        announcement.update();
        setFlash("message", getRes().get("misc.system_announcement.update.success"));
        redirect("/system_announcement");
    }

    @RequiresPermissions("MiscApplication.edit")
    public void disable() {
        SystemAnnouncement announcement = SystemAnnouncement.dao.findById(getParaToInt());
        if (announcement == null) {
            renderError(404);
            return;
        }
        announcement.setEnabled(SystemAnnouncement.DISABLED);
        announcement.update();
        setFlash("message", getRes().get("misc.system_announcement.update.success"));
        redirect("/system_announcement");
    }

}
