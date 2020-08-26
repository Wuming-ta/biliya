package com.jfeat.ext.plugin.vip;

import com.jfeat.ext.plugin.*;
import com.jfeat.ext.plugin.vip.bean.VipAccount;
import com.jfeat.http.utils.HttpUtils;
import com.jfeat.http.utils.StrKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreditApi extends BaseApi {

    private static final Logger logger = LoggerFactory.getLogger(CreditApi.class);

    private static final String UPDATE_VIP_CREDIT_URL = "/api/vip/accounts/credit";

    public enum Type {
        //充值
        CHARGE,
        //消费
        CONSUME,
        //抵扣
        CASH,
        //回退
        REFUND
    }

    public CreditApi() {
        BasePlugin vipPlugin = ExtPluginHolder.me().get(VipPlugin.class);
        init(vipPlugin);
    }

    /**
     * 充值/消费 通知更新会员积分
     *
     * @param
     * @return
     */
    private ApiResult updateAccountCredit(String account, Type type, BigDecimal amount) {
        String url = getBaseUrl() + UPDATE_VIP_CREDIT_URL;
        Map<String, Object> param = new HashMap<>();
        param.put("account", account);
        param.put("changedType", type.toString());
        param.put("amount", amount);
        String data = JsonKit.toJson(param);
        logger.debug(data);
        String result = HttpUtils.post(url, data, getAuthorizationHeader());
        logger.debug("post url = {}, data = {}, result = {}", url, data, result);
        return ApiResult.create(result);
    }

    /**
     * 消费 通知更新会员积分
     *
     * @param
     * @return
     */
    public ApiResult updateAccountCreditConsume(String account, BigDecimal amount) {
        return updateAccountCredit(account, Type.CONSUME, amount);
    }

    /**
     * 充值 通知更新会员积分
     *
     * @param
     * @return
     */
    public ApiResult updateAccountCreditCharge(String account, BigDecimal amount) {
        return updateAccountCredit(account, Type.CHARGE, amount);
    }

    /**
     * 消费抵扣会员积分
     *
     * @param
     * @return
     */
    public ApiResult consumeAccountCredit(String account, BigDecimal amount, BigDecimal cashAmount, int modifiedCredit, String tradeNumber) {
        String url = getBaseUrl() + UPDATE_VIP_CREDIT_URL;
        Map<String, Object> param = new HashMap<>();
        param.put("account", account);
        param.put("changedType", Type.CASH.toString());
        param.put("amount", amount);
        param.put("cashAmount", cashAmount);
        param.put("modifiedCredit", modifiedCredit);
        param.put("tradeNumber", tradeNumber);
        String data = JsonKit.toJson(param);
        logger.debug(data);
        String result = HttpUtils.post(url, data, getAuthorizationHeader());
        logger.debug("post url = {}, data = {}, result = {}", url, data, result);
        return ApiResult.create(result);
    }

    /**
     * 回退会员积分
     *
     * @param
     * @return
     */
    public ApiResult refundAccountCredit(String account, int modifiedCredit) {
        String url = getBaseUrl() + UPDATE_VIP_CREDIT_URL;
        Map<String, Object> param = new HashMap<>();
        param.put("account", account);
        param.put("changedType", Type.REFUND.toString());
        param.put("modifiedCredit", modifiedCredit);
        String data = JsonKit.toJson(param);
        logger.debug(data);
        String result = HttpUtils.post(url, data, getAuthorizationHeader());
        logger.debug("post url = {}, data = {}, result = {}", url, data, result);
        return ApiResult.create(result);
    }
}
