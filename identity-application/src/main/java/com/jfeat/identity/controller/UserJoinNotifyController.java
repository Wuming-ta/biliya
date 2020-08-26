package com.jfeat.identity.controller;

import com.jfeat.core.BaseController;
import com.jfeat.identity.model.UserJoinNotify;
import com.jfeat.identity.service.UserService;
import com.jfinal.aop.Enhancer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author jackyhuang
 * @date 2018/8/24
 */
public class UserJoinNotifyController extends BaseController {

    private UserService userService = Enhancer.enhance(UserService.class);

    @Override
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        Integer isRead = getParaToInt("isRead");
        setAttr("notifies", UserJoinNotify.dao.paginate(pageNumber, pageSize, isRead));
        keepPara();
    }

    public void markRead() {
        Integer[] ids = getParaValuesToInt("id");
        if (ids != null && ids.length > 0) {
            List<Integer> list = new ArrayList<>();
            Collections.addAll(list, ids);
            userService.clearUserJoinNotify(list);
        }
        redirect("/user_join_notify?isRead=0");
    }

    public void markAllRead() {
        userService.clearUserJoinNotify();
        redirect("/user_join_notify");
    }

    public void unreadCount() {
        int count = UserJoinNotify.dao.queryUnreadCount();
        String result = count == 0 ? "" : String.valueOf(count);
        renderText(result);
    }
}
