package com.jfeat.identity.api;

import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.identity.model.User;
import com.jfinal.ext.route.ControllerBind;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/8/21
 */
@ControllerBind(controllerKey = "/rest/pub/user_info")
public class UserInfoController extends RestController {

    /**
     * 返回用户头像信息
     * POST /rest/pub/user_info
     * { "ids": [1, 2, 3] }
     */
    @Override
    @Validation(rules = { "ids = required "})
    public void save() {
        Map<String, Object> map = convertPostJsonToMap();
        List<Integer> ids = (List<Integer>) map.get("ids");
        if (ids == null || ids.isEmpty()) {
            renderSuccess(new ArrayList<>());
            return;
        }
        List<User> list = User.dao.findUserInfoByIds(ids);
        renderSuccess(list);
    }
}
