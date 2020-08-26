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

package com.jfeat.settlement.api;

import com.jfeat.config.model.Config;
import com.jfeat.core.BaseService;
import com.jfeat.core.RestController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.partner.model.PartnerLevel;
import com.jfeat.partner.model.Seller;
import com.jfeat.partner.service.CopartnerService;
import com.jfeat.partner.service.SellerService;
import com.jfeat.settlement.api.model.WithdrawCashEntity;
import com.jfeat.settlement.model.OrderItemReward;
import com.jfeat.settlement.model.OwnerBalance;
import com.jfeat.settlement.model.WithdrawAccount;
import com.jfeat.settlement.service.OwnerBalanceService;
import com.jfeat.settlement.service.WithdrawService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.plugin.shiro.ShiroMethod;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Created by jingfei on 2016/3/31.
 */
@ControllerBind(controllerKey = "/rest/owner_balance")
public class OwnerBalanceController extends RestController {

    private WithdrawService withdrawService = Enhancer.enhance(WithdrawService.class);
    private OwnerBalanceService ownerBalanceService = Enhancer.enhance(OwnerBalanceService.class);
    private SellerService sellerService = Enhancer.enhance(SellerService.class);
    private CopartnerService copartnerService = Enhancer.enhance(CopartnerService.class);
    private String msg = "请于%s小时内完成%s元的批发，成为真正线下皇冠商才可以进入";
    private static final String WHOLESALE_TIME = "partner.new_physical_seller_wholesale_time";
    private static final String WHOLESALE_AMOUNT = "partner.new_physical_seller_wholesale_amount";

    /**
     * 普通用户查看自己的提成余额，管理员可通过 PHONE 参数查看其他人余额
     * GET /rest/owner_balance?phone=138000001
     */
    @Override
    @Before(CurrentUserInterceptor.class)
    public void index() {
        String phone = getPara("phone");
        User user = getAttr("currentUser");
        if (StrKit.notBlank(phone)) {
            User targetUser = User.dao.findByPhone(phone);
            if (targetUser == null) {
                renderFailure("user.not.found");
                return;
            }
            if (ShiroMethod.lacksPermission("SettlementApplication.view")) {
                renderFailure("lack.of.permission");
                return;
            }
            user = targetUser;
        }


        Seller currentSeller = Seller.dao.findByUserId(user.getId());
        boolean isSeller = false;
        boolean isPartner = false;
        boolean isAgent = false;
        boolean isCrown = false;
        boolean isCrownShipTemp = false;
        boolean isPhysical = false;
        boolean isCopartner = false;

        HashMap<String, Object> balanceMap = new HashMap<>();
        if (currentSeller != null) {
            isCopartner = copartnerService.isCopartner(currentSeller.getId());
            isSeller = currentSeller.isSellerShip();
            isPartner = currentSeller.isPartnerShip();
            isCrown = currentSeller.isCrownShip();
            isCrownShipTemp = currentSeller.isCrownShipTemp();
            isAgent = currentSeller.isAgent();
            isPhysical = currentSeller.isPhysicalSeller();
            PartnerLevel partnerLevel = currentSeller.getPartnerLevel();
            balanceMap.put("partner_pool_count", currentSeller.getPartnerPoolCount());
            if (partnerLevel != null) {
                balanceMap.put("partner_level", partnerLevel);
                balanceMap.put("next_partner_level", sellerService.getNextPartnerLevel(partnerLevel.getLevel()));
            }
        }

        OwnerBalance ownerBalance = ownerBalanceService.queryBalance(user.getId());
        BigDecimal balance = ownerBalance.getBalance();
        BigDecimal totalReward = OrderItemReward.dao.queryTotalReward(user.getId());

        balanceMap.put("balance", balance);
        balanceMap.put("is_seller", isSeller);
        balanceMap.put("is_partner", isPartner);
        balanceMap.put("is_agent", isAgent);
        balanceMap.put("is_crown", isCrown);
        balanceMap.put("is_crown_ship_temp", isCrownShipTemp);
        balanceMap.put("is_copartner", isCopartner);
        if (isCrownShipTemp) {
            Config wholesaleTimeCfg = Config.dao.findByKey(WHOLESALE_TIME);
            Integer intervalTemp = null;
            Integer interval = (wholesaleTimeCfg != null && (intervalTemp = wholesaleTimeCfg.getValueToInt()) != null) ? intervalTemp : 2;
            Config wholesaleAmountCfg = Config.dao.findByKey(WHOLESALE_AMOUNT);
            Integer amountTemp = null;
            Integer targetAmount = (wholesaleAmountCfg != null && (amountTemp = wholesaleAmountCfg.getValueToInt()) != null) ? amountTemp : 0;
            balanceMap.put("msg", String.format(msg, interval, targetAmount));
            balanceMap.put("wholesale_interval", interval);
            balanceMap.put("wholesale_target_amount", targetAmount);
        }
        balanceMap.put("is_physical", isPhysical);
        balanceMap.put("total_reward", totalReward);

        logger.debug("User {} 's balance: {}", user.getName(), JsonKit.toJson(balanceMap));
        renderSuccess(balanceMap);
    }

    /**
     * 提现申请或转存到零钱
     * POST /rest/owner_balance
     * {
     * "withdraw_type": "WECHAT",//默认WECHAT，可选 WALLET
     * "withdraw_account_id": 1, //optional
     * "withdraw_cash": 100.00
     * }
     */
    @Override
    @Before({CurrentUserInterceptor.class, Tx.class})
    public void save() {
        User currentUser = getAttr("currentUser");
        WithdrawCashEntity entity = getPostJson(WithdrawCashEntity.class);
        if (entity.getWithdraw_cash() <= 0) {
            renderFailure("invalid.withdraw.cash");
            return;
        }

        List<WithdrawAccount> withdrawAccountList = WithdrawAccount.dao.findByUserId(currentUser.getId());
        Integer withdrawAccountId = entity.getWithdraw_account_id();
        WithdrawAccount account = null;
        if (withdrawAccountId == null) {
            Optional<WithdrawAccount> withdrawAccountOptional = withdrawAccountList.stream()
                    .filter(item -> item.getType().equalsIgnoreCase(entity.getWithdraw_type()))
                    .findFirst();
             account = withdrawAccountOptional.orElseGet(() -> WithdrawAccount.Type.valueOf(entity.getWithdraw_type())
                    .createAccount(currentUser.getId(), currentUser.getName(), getOpenid(currentUser)));
            withdrawAccountId = account.getId();
        }

        if (account != null &&
                entity.getWithdraw_type().equalsIgnoreCase(WithdrawAccount.Type.WECHAT.toString())
                && StrKit.isBlank(account.getAccount())) {
            renderFailure("invalid.wechat.account");
            return;
        }

        logger.debug("user {} withdraw the owner balance.", currentUser.getName());
        Ret ret = withdrawService.apply(currentUser.getId(),
                BigDecimal.valueOf(entity.getWithdraw_cash()).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP),
                withdrawAccountId);
        if (!BaseService.isSucceed(ret)) {
            renderFailure(ret.get(BaseService.MESSAGE));
            return;
        }



        renderSuccessMessage("apply.success");
    }

    private String getOpenid(User user) {
        if (StrKit.notBlank(user.getWeixin())) {
            return user.getWeixin();
        }
        if (StrKit.notBlank(user.getWxaOpenid())) {
            return user.getWxaOpenid();
        }
        return "";
    }
}
