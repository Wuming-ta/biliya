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

package com.jfeat.member.controller;

import com.jfeat.core.BaseController;
import com.jfeat.flash.Flash;
import com.jfeat.member.model.MemberLevel;
import com.jfinal.aop.Before;

/**
 * Created by jacky on 12/24/15.
 */
public class MemberLevelController extends BaseController {

    @Override
    @Before(Flash.class)
    public void index() {
        setAttr("levels", MemberLevel.dao.findAll());
    }

    @Override
    public void add() {

    }

    @Override
    public void save() {
        MemberLevel level = getModel(MemberLevel.class);
        level.save();
        setFlash("message", getRes().get("member.level.create.success"));
        redirect("/member_level");
    }

    @Override
    public void edit() {
        setAttr("level", MemberLevel.dao.findById(getParaToInt()));
    }

    @Override
    public void update() {
        MemberLevel level = getModel(MemberLevel.class);
        level.update();
        setFlash("message", getRes().get("member.level.update.success"));
        redirect("/member_level");
    }

    @Override
    public void delete() {
        MemberLevel level = MemberLevel.dao.findById(getParaToInt());
        if (level == null) {
            renderError(404);
            return;
        }
        if (level.hasMember()) {
            setFlash("message", getRes().get("member.level.has_member"));
            redirect("/member_level");
            return;
        }
        level.delete();
        setFlash("message", getRes().get("member.level.delete.success"));
        redirect("/member_level");
    }

    /**
     * ajax check name
     */
    public void nameVerify() {
        String name = getPara("name");
        if (MemberLevel.dao.findByName(name) == null) {
            renderText("true");
        }
        else {
            renderText("false");
        }
    }
}
