package com.jfeat.merchant.service;

import com.jfeat.config.model.Config;
import com.jfeat.core.BaseService;
import com.jfeat.merchant.model.SettledMerchant;
import com.jfeat.merchant.model.SettledMerchantIntroduction;
import com.jfeat.merchant.model.UserSettledMerchant;

import java.util.List;

/**
 * Created by kang on 2017/3/24.
 */
public class MerchantApplyService extends BaseService {
    private String uploadDir = "m";
    private static final String ALLOW_APPLY_KEY = "merchant.allow_apply";

    public boolean isAllowApply() {
        Config config = Config.dao.findByKey(ALLOW_APPLY_KEY);
        if (config != null) {
            return config.getValueToBoolean();
        }
        return false;
    }

    public String getUploadDir() {
        return this.uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public boolean save(SettledMerchant settledMerchant, SettledMerchantIntroduction settledMerchantIntroduction, Integer userId) {
        List<UserSettledMerchant> list = UserSettledMerchant.dao.findByUserId(userId);
        if (list.size() > 0) {
            return false;
        }
        settledMerchant.save();

        settledMerchantIntroduction.setMerchantId(settledMerchant.getId());
        settledMerchantIntroduction.save();

        settledMerchant.updateUser(userId);
        return true;
    }
}
