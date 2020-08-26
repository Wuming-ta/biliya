package com.jfeat.member.api;

import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.member.model.Wallet;
import com.jfeat.member.service.WalletService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;

import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/8/29
 */
@ControllerBind(controllerKey = "/rest/wallet_verify_password")
public class WalletVerifyPasswordController extends RestController {

    private WalletService walletService = Enhancer.enhance(WalletService.class);

    /**
     * 设置支付密码
     */
    @Override
    @Before(CurrentUserInterceptor.class)
    @Validation(rules = { "password = required" })
    public void save() {
        Map<String, Object> map = convertPostJsonToMap();
        String password = (String) map.get("password");
        User currentUser = getAttr("currentUser");
        if (!walletService.verifyPassword(currentUser.getId(), password)) {
            renderFailure("incorrect.old.password");
            return;
        }

        renderSuccessMessage("verify.password.passed");
    }
}
