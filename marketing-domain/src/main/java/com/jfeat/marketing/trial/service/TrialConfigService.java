package com.jfeat.marketing.trial.service;

import com.jfeat.marketing.common.model.MarketingConfig;
import com.jfeat.marketing.trial.model.Trial;

/**
 * Created by kang on 2018/6/29.
 */
public class TrialConfigService {
    public static boolean isEnabled() {
        MarketingConfig marketingConfig = MarketingConfig.dao.findFirstByField(MarketingConfig.Fields.TYPE.toString()
                , MarketingConfig.Type.TRIAL.toString());
        return marketingConfig != null && marketingConfig.getEnabled() != null
                && marketingConfig.getEnabled().equals(Trial.Enabled.YES.getValue());
    }
}
