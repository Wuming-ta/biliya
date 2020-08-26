package com.jfeat.member.api;

import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.ExtPluginHolder;
import com.jfeat.ext.plugin.VipPlugin;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.ext.plugin.vip.VipApi;
import com.jfeat.ext.plugin.vip.bean.DepositPackage;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.member.model.WalletCharge;
import com.jfeat.member.service.WalletService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 充值
 * @author jackyhuang
 * @date 2018/8/7
 */
@ControllerBind(controllerKey = "/rest/wallet_charge")
public class WalletChargeController extends RestController {

    private WalletService walletService = Enhancer.enhance(WalletService.class);

    @Override
    @Before(CurrentUserInterceptor.class)
    @Validation(rules = { "amount = required", "amount = money", "description = required", "id = required" })
    public void save() {
        Map<String, Object> map = convertPostJsonToMap();
        String description = (String) map.get("description");
        BigDecimal amount = BigDecimal.valueOf(((Number) map.get("amount")).doubleValue());
        Object id = map.get("id");
        Long packageId;
        if (id instanceof Integer) {
            packageId = ((Integer) id).longValue();
        }
        else {
            packageId = Long.parseLong((String) id);
        }
        User currentUser = getAttr("currentUser");
        BigDecimal giftAmount = BigDecimal.ZERO;
        if (ExtPluginHolder.me().get(VipPlugin.class).isEnabled()) {
            logger.debug("vip plugin is enabled.");
            VipApi vipApi = new VipApi();
            DepositPackage depositPackage = vipApi.getDepositPackage(packageId);
            giftAmount = depositPackage.getDepositWinBonus();
            amount = depositPackage.getDepositBonusPlan();
        }
        WalletCharge walletCharge = walletService.charge(currentUser.getId(), amount, giftAmount, description);
        renderSuccess(walletCharge);
    }
}
