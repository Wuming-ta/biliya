package com.jfeat.member.api;

import com.jfeat.core.RestController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.member.model.Wallet;
import com.jfeat.member.service.WalletService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.plugin.shiro.ShiroMethod;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;

/**
 * 用户查看 我的钱包
 * @author jackyhuang
 * @date 2018/8/7
 */
@ControllerBind(controllerKey = "/rest/wallet")
public class WalletController extends RestController {

    private WalletService walletService = Enhancer.enhance(WalletService.class);

    @Override
    @Before(CurrentUserInterceptor.class)
    public void index() {
        String phone = getPara("phone");
        User currentUser = getAttr("currentUser");
        Integer userId = currentUser.getId();
        if (StrKit.notBlank(phone)) {
            User targetUser = User.dao.findByPhone(phone);
            if (targetUser == null) {
                renderFailure("user.not.found");
                return;
            }
            if (ShiroMethod.lacksPermission("member.edit")) {
                renderFailure("lack.of.permission");
                return;
            }
            userId = targetUser.getId();
        }

        Wallet wallet = walletService.getWallet(userId);
        renderSuccess(wallet);
    }
}

