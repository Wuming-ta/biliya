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

package com.jfeat.misc.api;

import com.jfeat.core.RestController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.misc.model.Feedback;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;

import java.util.List;
import java.util.Map;

/**
 * Created by jingfei on 2016/5/12.
 */
@ControllerBind(controllerKey = "/rest/feedback")
public class FeedbackController extends RestController {

    /**
     * POST /rest/feedback
     *
     * Data:
     * {
     *     "content":"xxvv",
     *     "images": [
     *          "http://host:port/image.jpg"
     *     ]
     * }
     */
    @Before(CurrentUserInterceptor.class)
    public void save() {
        Map<String, Object> map = convertPostJsonToMap();
        String content = (String) map.get("content");
        List<String> images = (List<String>) map.get("images");
        if (StrKit.isBlank(content)) {
            renderFailure("invalid.input.json");
            return;
        }
        User currentUser = getAttr("currentUser");
        Feedback feedback = new Feedback();
        feedback.setContent(content);
        feedback.setUserId(currentUser.getId());
        feedback.save();
        if (images != null && images.size() > 0) {
            for (String image : images) {
                feedback.addImage(image);
            }
        }
        renderSuccessMessage("feedback.created");
    }

}
