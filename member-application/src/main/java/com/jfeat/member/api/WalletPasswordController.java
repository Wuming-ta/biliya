package com.jfeat.member.api;

import com.jfeat.captcha.CaptchaKit;
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
@ControllerBind(controllerKey = "/rest/wallet_password")
public class WalletPasswordController extends RestController {

    private WalletService walletService = Enhancer.enhance(WalletService.class);

    /**
     * 检查是否已设置支付密码
     */
    @Override
    @Before(CurrentUserInterceptor.class)
    public void index() {
        User currentUser = getAttr("currentUser");
        Wallet wallet = walletService.getWallet(currentUser.getId());
        renderSuccess(StrKit.notBlank(wallet.getPassword()));
    }

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
        Wallet wallet = walletService.getWallet(currentUser.getId());
        if (walletService.resetPassword(wallet.getId(), password)) {
            renderSuccessMessage("ok");
            return;
        }
        renderFailure("reset.password.failure");
    }
}
