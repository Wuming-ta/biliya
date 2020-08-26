package com.jfeat.common;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jackyhuang
 * @date 2018/9/28
 */
public class CouponConfigHolder {
    private static CouponConfigHolder me = new CouponConfigHolder();
    private CouponConfigHolder() {
    }

    private List<String> excludedStrategyTypes = new ArrayList<>();
    private List<String> excludedTemplateTypes = new ArrayList<>();

    public static CouponConfigHolder me() {
        return me;
    }

    public List<String> getExcludedStrategyTypes() {
        return excludedStrategyTypes;
    }

    public CouponConfigHolder addExcludedStrategyType(String type) {
        this.excludedStrategyTypes.add(type);
        return this;
    }

    public List<String> getExcludedTemplateTypes() {
        return excludedTemplateTypes;
    }

    public CouponConfigHolder addExcludedTemplateType(String type) {
        this.excludedTemplateTypes.add(type);
        return this;
    }
}
