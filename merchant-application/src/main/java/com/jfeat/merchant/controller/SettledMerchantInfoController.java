package com.jfeat.merchant.controller;

import com.jfeat.config.model.Config;
import com.jfeat.core.BaseController;
import com.jfeat.identity.model.User;
import com.jfeat.merchant.model.SettledMerchant;
import com.jfeat.merchant.model.UserSettledMerchant;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * Created by kang on 2017/3/22.
 */
public class SettledMerchantInfoController extends BaseController {

    @RequiresPermissions("merchant.handle")
    public void index() {
        //如果没记录，则让其注册成为商家；
        //否则，1.状态是APPROVED，则允许其查看该页  2.状态是其他，则提示相关信息，让其联系管理员
        User user = getAttr("currentUser");
        UserSettledMerchant userSettledMerchant = UserSettledMerchant.dao.findFirstByField(UserSettledMerchant.Fields.USER_ID.toString(), user.getId());
        SettledMerchant settledMerchant = userSettledMerchant == null ? null : SettledMerchant.dao.findById(userSettledMerchant.getMerchantId());
        if (settledMerchant == null) {
            redirect("/merchant_apply");
            return;
        }
        settledMerchant.put("invite_code", user.getInvitationCode());
        setAttr("settledMerchant", settledMerchant);
        Config config = Config.dao.findByKey("wx.host");
        if (config != null) {
            setAttr("wxHost", config.getValue());
        }
        if (SettledMerchant.Status.INIT.toString().equals(settledMerchant.getStatus())) {
            setAttr("message", getRes().get("merchant.settled_merchant.status.init_message"));
        } else if (SettledMerchant.Status.APPROVING.toString().equals(settledMerchant.getStatus())) {
            setAttr("message", getRes().get("merchant.settled_merchant.status.approving_message"));
        } else if (SettledMerchant.Status.LOCKED.toString().equals(settledMerchant.getStatus())) {
            setAttr("message", getRes().get("merchant.settled_merchant.status.locked_message"));
        }
    }


}
