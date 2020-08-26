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

package com.jfeat.settlement.service;

import com.jfeat.config.model.Config;
import com.jfeat.core.BaseService;
import com.jfeat.identity.model.User;
import com.jfeat.kit.DateKit;
import com.jfeat.partner.model.Seller;
import com.jfeat.payment.Payment;
import com.jfeat.payment.PaymentHolder;
import com.jfeat.settlement.model.OwnerBalance;
import com.jfeat.settlement.model.RewardCash;
import com.jfeat.settlement.model.WithdrawAccount;
import com.jfeat.settlement.notification.RewardCashApplyingNotification;
import com.jfeat.settlement.notification.RewardCashCompletedNotification;
import com.jfeat.settlement.notification.RewardCashHandlingNotification;
import com.jfeat.settlement.notification.RewardCashRejectedNotification;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by jacky on 4/27/16.
 */
public class WithdrawService extends BaseService {

    private OwnerBalanceService ownerBalanceService = Enhancer.enhance(OwnerBalanceService.class);
    private static final String WITHDRAW_CONDITION_KEY = "mall.drawing_conditions";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Before(Tx.class)
    public Ret apply(int userId, BigDecimal cash, int withdrawAccountId) {
        WithdrawAccount account = WithdrawAccount.dao.findById(withdrawAccountId);
        if (account == null || account.getUserId() != userId) {
            logger.debug("invalid.account.id. id={}, userId={}", withdrawAccountId, userId);
            return failure("invalid.account.id");
        }

        //check the balance if it has enough money to withdraw.
        OwnerBalance balance = ownerBalanceService.queryBalance(account.getUserId());
        if (balance.getBalance().compareTo(cash) < 0) {
            logger.debug("not.enough.balance. balance={}, cash={}", balance.getBalance(), cash);
            return failure("not.enough.balance");
        }

//        Seller seller = Seller.dao.findByUserId(userId);
//        if (!seller.isPartnerShip()) {
//            logger.error("user_id {} is not a partner. only partner can withdraw.", userId);
//            return failure("only.partner.can.withdraw");
//        }

        //检查提现金额是否大于限定最底提现标准。
        Config config = Config.dao.findByKey(WITHDRAW_CONDITION_KEY);
        BigDecimal withdrawLimit = new BigDecimal(100);
        if (config != null) {
            withdrawLimit = new BigDecimal(config.getValueToInt());
        }
        if (!account.getType().equalsIgnoreCase(WithdrawAccount.Type.WALLET.toString())
                && cash.compareTo(withdrawLimit) < 0) {
            logger.debug("not.reach.withdraw.quota. quota={}, cash={}", withdrawLimit, cash);
            return failure("not.reach.withdraw.quota");
        }

        User user = User.dao.findById(account.getUserId());
        if (ownerBalanceService.subtractReward(account.getUserId(), cash)) {
            RewardCash rewardCash = new RewardCash();
            rewardCash.setBankName(account.getBankName());
            rewardCash.setAccountName(account.getOwnerName());
            rewardCash.setAccountNumber(account.getAccount());
            rewardCash.setAccountType(account.getType());
            rewardCash.setOwnerId(account.getUserId());
            rewardCash.setCash(cash);
            rewardCash.save();
            logger.debug("RewardCash saved. {}", rewardCash);
            new RewardCashApplyingNotification(user.getWeixin())
                    .param(RewardCashApplyingNotification.AMOUNT, rewardCash.getCash().toPlainString())
                    .param(RewardCashApplyingNotification.APPLY_TIME, DateKit.today(DATE_TIME_FORMAT))
                    .send();

            // 直接转入零钱钱包，直接完成该申请
            if (account.getType().equalsIgnoreCase(WithdrawAccount.Type.WALLET.toString())) {
                logger.debug("withdraw to wallet, directly agree and complete. {}", rewardCash.toJson());
                agree(rewardCash.getId());
                //TimeUnit.SECONDS.sleep(1L);
                complete(rewardCash.getId(), "");
            }

            return success();
        }

        return failure("balance.subtract.failure");
    }

    public Ret agree(int rewardCashId) {
        RewardCash rewardCash = RewardCash.dao.findById(rewardCashId);
        if (rewardCash == null) {
            return failure("invalid.rewardcash");
        }

        if (rewardCash.getStatus().equals(RewardCash.Status.APPLYING.toString())) {
            rewardCash.setStatus(RewardCash.Status.HANDLING.toString());
            rewardCash.update();
            new RewardCashHandlingNotification(rewardCash.getUser().getWeixin()).send();
        }
        return success();
    }

    /**
     * 拒绝申请, 退回到余额账户
     *
     * @param rewardCashId
     * @return
     */
    @Before(Tx.class)
    public Ret reject(int rewardCashId, String reason) {
        RewardCash rewardCash = RewardCash.dao.findById(rewardCashId);
        if (rewardCash == null) {
            return failure("invalid.rewardcash");
        }

        if ((rewardCash.getStatus().equals(RewardCash.Status.APPLYING.toString())
                || rewardCash.getStatus().equals(RewardCash.Status.HANDLING.toString()))
                && ownerBalanceService.addReward(rewardCash.getOwnerId(), rewardCash.getCash())) {
            rewardCash.setStatus(RewardCash.Status.REJECTED.toString());
            rewardCash.setRejectTime(new Date());
            rewardCash.setNote(reason);
            rewardCash.update();
            new RewardCashRejectedNotification(rewardCash.getUser().getWeixin())
                    .param(RewardCashRejectedNotification.REJECTED_TIME, DateKit.today(DATE_TIME_FORMAT))
                    .param(RewardCashRejectedNotification.REASON, rewardCash.getNote())
                    .send();
        }
        return success();
    }

    @Before(Tx.class)
    public Ret complete(int rewardCashId, String clientIp) {
        RewardCash rewardCash = RewardCash.dao.findById(rewardCashId);
        if (rewardCash == null) {
            return failure("invalid.rewardcash");
        }

        if (rewardCash.getStatus().equals(RewardCash.Status.HANDLING.toString())) {
            Payment payment = PaymentHolder.me().getPayment(rewardCash.getAccountType());
            User user = rewardCash.getUser();
            Ret ret = payment.transfer(rewardCash.getAccountNumber(),
                    rewardCash.getId().toString(),
                    rewardCash.getCash(),
                    user.getRealName(),
                    clientIp);
            if (!((Boolean) ret.get("result"))) {
                return failure(ret.get("message"));
            }

            rewardCash.setStatus(RewardCash.Status.COMPLETED.toString());
            rewardCash.setCompleteTime(new Date());
            rewardCash.update();
            new RewardCashCompletedNotification(rewardCash.getUser().getWeixin())
                    .param(RewardCashCompletedNotification.AMOUNT, rewardCash.getCash().toPlainString())
                    .param(RewardCashCompletedNotification.COMPLETED_TIME, DateKit.today(DATE_TIME_FORMAT))
                    .send();
        }
        return success();
    }

}
