package com.jfeat.partner.api;

import com.jfeat.core.RestController;
import com.jfeat.identity.model.User;
import com.jfeat.partner.model.Alliance;
import com.jfinal.ext.route.ControllerBind;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2019/10/10
 */
@ControllerBind(controllerKey = "/rest/alliance")
public class AllianceController extends RestController {


    /**
     * GET /rest/alliance
     * 返回当前盟友及其邀请列表
     * status_code = 0  是盟友
     * status_code = 1  不是盟友/没记录
     * status_code = 2  待付款/申请中
     * status_code = 3  待绑定
     * status_code = 4  支付超时
     * status_code = 5  状态错误
     * status_code = 6  已支付
     * status_code = 7  已注销
     */
    @Override
    public void index() {
        User currentUser = getAttr("currentUser");
        Integer currentUserId = currentUser.getId();
        Alliance alliance = Alliance.dao.findByUserId(currentUserId);
        if (alliance == null) {
            renderFailure("alliance.not.found", 1);
            return;
        }
        if (alliance.getAllianceShip() == Alliance.AllianceShip.REGULAR.getValue()) {
            alliance.put("children", alliance.getChildren());
            renderSuccess(alliance);
            return;
        }

        renderFailure("alliance.not.regular", alliance.getAllianceShip());
    }
}

