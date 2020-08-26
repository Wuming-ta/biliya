package com.jfeat.member.service;

import com.jfeat.core.BaseService;
import com.jfeat.ext.plugin.ApiResult;
import com.jfeat.ext.plugin.ExtPluginHolder;
import com.jfeat.ext.plugin.VipPlugin;
import com.jfeat.ext.plugin.vip.CreditApi;
import com.jfeat.member.model.Wallet;
import com.jfeat.member.model.WalletCharge;
import com.jfeat.member.model.WalletHistory;
import com.jfeat.service.exception.RetrieveOrderException;
import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/8/7
 */
public class WalletService extends BaseService implements WalletPayService {

    public Wallet getWallet(Integer userId) {
        Wallet wallet = Wallet.dao.findByUserId(userId);
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setUserId(userId);
            wallet.setAccumulativeAmount(BigDecimal.ZERO);
            wallet.setAccumulativeGiftAmount(BigDecimal.ZERO);
            wallet.setBalance(BigDecimal.ZERO);
            wallet.setGiftBalance(BigDecimal.ZERO);
            wallet.save();
        }
        return wallet;
    }

    /**
     * 校验密码
     * @param userId
     * @param password
     * @return
     */
    public boolean verifyPassword(Integer userId, String password) {
        Wallet wallet = Wallet.dao.findByUserId(userId);
        if (wallet == null) {
            logger.debug("wallet not found. userId = {}", userId);
            return false;
        }
        if (StrKit.isBlank(password) || StrKit.isBlank(wallet.getPassword())) {
            return false;
        }
        return wallet.verifyPassword(password);
    }

    /**
     * 重置密码
     * @param walletId
     * @param newPassword
     * @return
     */
    public boolean resetPassword(Integer walletId, String newPassword) {
        if (StrKit.isBlank(newPassword)) {
            logger.debug("password is empty.");
            return false;
        }
        Wallet wallet = Wallet.dao.findById(walletId);
        if (wallet == null) {
            logger.debug("wallet not found. walletid = {}", walletId);
            return false;
        }
        wallet.setPassword(newPassword);
        Wallet.entryptPassword(wallet);
        return true;
    }

    /**
     * 充值
     * @param userId
     * @param amount 实际充值额
     * @return WalletCharge
     */
    @Before(Tx.class)
    public WalletCharge charge(Integer userId, BigDecimal amount, BigDecimal giftAmount, String description) {
        Wallet wallet = getWallet(userId);
        WalletCharge walletCharge = new WalletCharge();
        walletCharge.setWalletId(wallet.getId());
        walletCharge.setAmount(amount);
        walletCharge.setGiftAmount(giftAmount);
        walletCharge.setDescription(description);
        walletCharge.save();
        return walletCharge;
    }

    @Before(Tx.class)
    public Ret pay(Integer userId, BigDecimal totalAmount, String note) {
        Wallet wallet = Wallet.dao.findByUserId(userId);
        if (wallet == null) {
            return failure("wallet.not.found");
        }
        BigDecimal balance = wallet.getBalance().add(wallet.getGiftBalance());
        if (balance.compareTo(totalAmount) < 0) {
            return failure("wallet.insufficient.balance");
        }
        BigDecimal giftAmount = BigDecimal.ZERO;
        BigDecimal amount = BigDecimal.ZERO;
        if (wallet.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            logger.debug("has balance. walletId = {}, balance = {}", wallet.getId(), wallet.getBalance());
            if (wallet.getBalance().compareTo(totalAmount) >= 0) {
                amount = totalAmount;
                logger.debug("balance is enough. walletId = {} will use {}", wallet.getId(), amount);
            }
            else {
                amount = wallet.getBalance();
                giftAmount = totalAmount.subtract(amount);
                logger.debug("balance is not enough, walletId = {} will use {} and use gift balance {}", wallet.getId(), amount, giftAmount);
            }
        }
        else {
            logger.debug("balance is zero. walletId = {} will use gift balance, giftBalance = {}", wallet.getId(), wallet.getGiftBalance());
            giftAmount = totalAmount;
        }

        logger.debug("decreasing balance walletId = {}, amount = {}, giftAmount = {}", wallet.getId(), amount, giftAmount);
        if (Wallet.decreaseBalance(wallet.getId(), amount, giftAmount)) {
            WalletHistory history = new WalletHistory();
            history.setWalletId(wallet.getId());
            history.setType(WalletHistory.Type.PAY.toString());
            history.setAmount(amount);
            history.setBalance(wallet.getBalance().subtract(amount));
            history.setGiftAmount(giftAmount);
            history.setGiftBalance(wallet.getGiftBalance().subtract(giftAmount));
            history.setNote(WalletHistory.Type.PAY.toChineseName() + ": " + note);
            history.save();

            updateCreditConsume(wallet.getUser().getLoginName(), amount);

            return success("wallet.pay.success").put("tradeNumber", history.getId());
        }
        return failure("wallet.pay.failure");
    }

    /**
     * 退款
     * @param userId
     * @param refundFee
     * @return
     */
    public Ret refund(Integer userId, BigDecimal totalFee, BigDecimal refundFee, String refundNumber) {
        logger.debug("userId {} is refunding, refund fee {}, total fee {}", userId, refundFee, totalFee);
        if (refundFee.compareTo(BigDecimal.ZERO) == 0 || refundFee.compareTo(totalFee) > 0) {
            logger.debug("refund invalid. userId {}, refund fee {}, total fee {}", userId, refundFee, totalFee);
            return failure("invalid.refund_fee");
        }
        Wallet wallet = getWallet(userId);
        Wallet.increaseBalance(wallet.getId(), refundFee, BigDecimal.ZERO);
        WalletHistory history = new WalletHistory();
        history.setWalletId(wallet.getId());
        history.setType(WalletHistory.Type.REFUND.toString());
        history.setAmount(refundFee);
        history.setBalance(wallet.getBalance().add(refundFee));
        history.setGiftAmount(BigDecimal.ZERO);
        history.setGiftBalance(wallet.getGiftBalance());
        history.setNote(WalletHistory.Type.REFUND.toChineseName() + ": No." + refundNumber);
        history.save();
        return success();
    }

    /**
     * 转入零钱
     * @param userId
     * @param amount
     * @return
     */
    public Ret transfer(Integer userId, BigDecimal amount) {
        logger.debug("userId {} is going to transfer {} into wallet.", userId, amount);
        Wallet wallet = getWallet(userId);
        Wallet.increaseBalance(wallet.getId(), amount, BigDecimal.ZERO);

        WalletHistory history = new WalletHistory();
        history.setWalletId(wallet.getId());
        history.setType(WalletHistory.Type.WITHDRAW.toString());
        history.setAmount(amount);
        history.setBalance(wallet.getBalance().add(amount));
        history.setGiftAmount(BigDecimal.ZERO);
        history.setGiftBalance(wallet.getGiftBalance());
        history.setNote(WalletHistory.Type.WITHDRAW.toChineseName());
        history.save();

        return success();
    }


    ///////////////////////// implement WalletPayService.
    @Override
    public Map<String, Object> retrieveToPayOrder(String orderNumber) throws RetrieveOrderException {
        Integer id = Integer.parseInt(orderNumber);
        WalletCharge walletCharge = WalletCharge.dao.findById(id);

        if (walletCharge == null) {
            logger.error("walletCharge not found. orderNumber = {}", orderNumber);
            throw new RetrieveOrderException("walletCharge not found. orderNumber = " + orderNumber);
        }
        if (walletCharge.getStatus().equals(WalletCharge.Status.PAY_PENDING.toString())) {
            Map<String, Object> result = new HashMap<>();
            result.put("total_price", walletCharge.getAmount());
            result.put("description", walletCharge.getDescription());
            return result;
        }
        logger.error("invalid walletCharge status {} for orderNumber {}", walletCharge.getStatus(), orderNumber);
        throw new RetrieveOrderException("invalid walletCharge status " + walletCharge.getStatus() + " for orderNumber {}" + orderNumber);
    }

    @Override
    public void paidNotify(String orderNumber, String paymentType, String tradeNumber, String payAccount) throws RetrieveOrderException {
        Integer id = Integer.parseInt(orderNumber);
        WalletCharge walletCharge = WalletCharge.dao.findById(id);

        if (walletCharge == null) {
            throw new RetrieveOrderException("walletCharge not found. orderNumber = " + orderNumber);
        }

        walletCharge.setStatus(WalletCharge.Status.PAID.toString());
        walletCharge.setPayType(paymentType);
        walletCharge.setOutTradeNo(tradeNumber);
        walletCharge.update();

        Wallet wallet = walletCharge.getWallet();

        Wallet.increaseBalance(walletCharge.getWalletId(), walletCharge.getAmount(), walletCharge.getGiftAmount());

        WalletHistory history = new WalletHistory();
        history.setWalletId(wallet.getId());
        history.setType(WalletHistory.Type.CHARGE.toString());
        history.setAmount(walletCharge.getAmount());
        history.setBalance(wallet.getBalance().add(walletCharge.getAmount()));
        history.setGiftAmount(BigDecimal.ZERO);
        history.setGiftBalance(wallet.getGiftBalance());
        history.setNote(WalletHistory.Type.CHARGE.toChineseName() + ": " + paymentType + tradeNumber);
        history.save();

        if (walletCharge.getGiftAmount().compareTo(BigDecimal.ZERO) > 0) {
            WalletHistory giftHistory = new WalletHistory();
            giftHistory.setWalletId(wallet.getId());
            giftHistory.setType(WalletHistory.Type.GIFT.toString());
            giftHistory.setAmount(BigDecimal.ZERO);
            giftHistory.setBalance(wallet.getBalance());
            giftHistory.setGiftAmount(walletCharge.getGiftAmount());
            giftHistory.setGiftBalance(wallet.getGiftBalance().add(walletCharge.getGiftAmount()));
            giftHistory.setNote(WalletHistory.Type.GIFT.toChineseName());
            giftHistory.save();
        }

        updateCreditCharge(wallet.getUser().getLoginName(), walletCharge.getAmount());
    }

    private void updateCreditCharge(String account, BigDecimal amount) {
        logger.debug("update credit charge account = {}, amount = {}", account, amount);
        try {
            if (amount.compareTo(BigDecimal.ZERO) > 0 && ExtPluginHolder.me().get(VipPlugin.class).isEnabled()) {
                logger.debug("vip plugin is enabled.");
                CreditApi creditApi = new CreditApi();
                ApiResult apiResult = creditApi.updateAccountCreditCharge(account, amount);
                logger.debug("update credit result : {}", apiResult.getJson());
            }
        }
        catch (Exception ex) {
            logger.error("update credit error: ", ex.getMessage());
            for (StackTraceElement stackTraceElement : ex.getStackTrace()) {
                logger.error("   {}:{}#{}", stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
            }
        }
    }

    private void updateCreditConsume(String account, BigDecimal amount) {
        logger.debug("update credit consume account = {}, amount = {}", account, amount);
        try {
            if (amount.compareTo(BigDecimal.ZERO) > 0 && ExtPluginHolder.me().get(VipPlugin.class).isEnabled()) {
                logger.debug("vip plugin is enabled.");
                CreditApi creditApi = new CreditApi();
                ApiResult apiResult = creditApi.updateAccountCreditConsume(account, amount);
                logger.debug("update credit result : {}", apiResult.getJson());
            }
        }
        catch (Exception ex) {
            logger.error("update credit error: ", ex.getMessage());
            for (StackTraceElement stackTraceElement : ex.getStackTrace()) {
                logger.error("   {}:{}#{}", stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
            }
        }
    }

}
