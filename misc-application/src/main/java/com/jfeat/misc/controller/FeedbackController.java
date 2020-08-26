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
import com.jfeat.misc.model.Feedback;
import com.jfinal.aop.Before;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * Created by jingfei on 2016/5/11.
 */
public class FeedbackController extends BaseController {

    @Override
    @RequiresPermissions("MiscApplication.view")
    @Before(Flash.class)
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        setAttr("feedbacks", Feedback.dao.paginate(pageNumber, pageSize));
        keepPara();
    }

    @RequiresPermissions("MiscApplication.view")
    public void detail() {
        Integer feedbackId = getParaToInt();
        Feedback feedback = Feedback.dao.findById(feedbackId);
        if (feedback.getUnread() == Feedback.UNREAD) {
            feedback.setUnread(Feedback.READ);
            feedback.update();
        }
        setAttr("feedback", feedback);
    }

    @Override
    @RequiresPermissions("MiscApplication.edit")
    public void delete() {
        Integer feedbackId = getParaToInt();
        Feedback.dao.deleteById(feedbackId);
        setFlash("message", getRes().get("misc.feedback.delete.success"));
        redirect("/feedback");
    }

    @RequiresPermissions("MiscApplication.view")
    public void feedbackCount() {
        long count = Feedback.dao.countUnreadedFeedbacks();
        renderText(count == 0 ? "" : String.valueOf(count));
    }

}
