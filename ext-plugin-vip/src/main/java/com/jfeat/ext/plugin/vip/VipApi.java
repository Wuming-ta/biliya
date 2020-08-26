package com.jfeat.ext.plugin.vip;

import com.jfeat.ext.plugin.*;
import com.jfeat.ext.plugin.vip.bean.DepositPackage;
import com.jfeat.ext.plugin.vip.bean.Grade;
import com.jfeat.ext.plugin.vip.bean.VipAccount;
import com.jfeat.http.utils.HttpUtils;
import com.jfeat.http.utils.StrKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VipApi extends BaseApi {

    private static final Logger logger = LoggerFactory.getLogger(VipApi.class);

    private static final String UPDATE_VIP_ACCOUNT_URL = "/api/vip/accounts/0?agent=INTERNAL";
    private static final String LIST_VIP_ACCOUNT_URL = "/api/vip/accounts";
    private static final String GRADE_INFO_URL = "/api/vip/account/grades/%s";
    private static final String PACKAGE_INFO_URL = "/api/vip/deposit/package/%s";

    public VipApi() {
        BasePlugin vipPlugin = ExtPluginHolder.me().get(VipPlugin.class);
        init(vipPlugin);
    }

    /**
     * 返回充值套餐
     * @param packageId
     * @return
     */
    public DepositPackage getDepositPackage(Long packageId) {
        if (packageId == null) {
            return new DepositPackage();
        }
        String url = String.format(getBaseUrl() + PACKAGE_INFO_URL, packageId);
        String result = HttpUtils.get(url, null, getAuthorizationHeader());
        logger.debug("get url = {}, result = {}", url, result);
        ApiResult apiResult = ApiResult.create(result);
        if (!apiResult.isSucceed()) {
            throw new RuntimeException("get deposit package error. " + apiResult.getMessage());
        }
        return JsonKit.parseObject(JsonKit.toJson(apiResult.get("data")), DepositPackage.class);
    }

    /**
     * 添加用户的vip帐户信息
     *
     * @param vipAccount
     * @return
     */
    public ApiResult createVipAccount(VipAccount vipAccount) {
        String url = getBaseUrl() + LIST_VIP_ACCOUNT_URL;
        String data = JsonKit.toJson(vipAccount);
        String result = HttpUtils.post(url, data, getAuthorizationHeader());
        logger.debug("post url = {}, data = {}, result = {}", url, data, result);
        return ApiResult.create(result);
    }

    /**
     * 同步更新用户的vip帐户信息
     *
     * @param vipAccount
     * @return
     */
    public ApiResult updateVipAccount(VipAccount vipAccount) {
        String url = getBaseUrl() + UPDATE_VIP_ACCOUNT_URL;
        String data = JsonKit.toJson(vipAccount);
        String result = HttpUtils.put(url, data, getAuthorizationHeader());
        logger.debug("put url = {}, data = {}, result = {}", url, data, result);
        return ApiResult.create(result);
    }

    /**
     * 根据用户的登录帐户取得vip账号信息
     *
     * @param account
     * @return
     */
    public VipAccount getVipAccount(String account) {
        if (StrKit.isBlank(account)) {
            return new VipAccount();
        }
        String url = getBaseUrl() + LIST_VIP_ACCOUNT_URL;
        Map<String, String> param = new HashMap<>();
        param.put("account", account);
        String result = HttpUtils.get(url, param, getAuthorizationHeader());
        logger.debug("get url = {}, query = {}, result = {}", url, param, result);
        ApiResult apiResult = ApiResult.create(result);
        if (!apiResult.isSucceed()) {
            throw new RuntimeException("get vip account error. " + apiResult.getMessage());
        }
        Map<String, Object> data = apiResult.get("data");
        List<VipAccount> vipAccounts = JsonKit.parseArray(JsonKit.toJson(data.get("records")), VipAccount.class);
        if (vipAccounts == null || vipAccounts.size() != 1) {
            return new VipAccount();
        }
        return vipAccounts.get(0);
    }

    public Grade getGrade(Long gradeId) {
        if (gradeId == null) {
            return new Grade();
        }
        String url = String.format(getBaseUrl() + GRADE_INFO_URL, gradeId);
        String result = HttpUtils.get(url, null, getAuthorizationHeader());
        logger.debug("get url = {}, query = {}, result = {}", url, result);
        ApiResult apiResult = ApiResult.create(result);
        if (!apiResult.isSucceed()) {
            throw new RuntimeException("get grade error. " + apiResult.getMessage());
        }
        return JsonKit.parseObject(JsonKit.toJson(apiResult.get("data")), Grade.class);
    }

}
