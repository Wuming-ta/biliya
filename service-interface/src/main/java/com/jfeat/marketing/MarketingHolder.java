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

package com.jfeat.marketing;

import com.jfinal.kit.StrKit;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jackyhuang on 17/4/26.
 */
public class MarketingHolder {

    private static MarketingHolder me = new MarketingHolder();
    private Map<String, Class<? extends Marketing>> marketingMap = new ConcurrentHashMap<>();
    private Map<String, String> nameMap = new ConcurrentHashMap<>();

    private MarketingHolder() {

    }

    public static MarketingHolder me() {
        return me;
    }

    public void register(String marketingType, String name, Class<? extends Marketing> clazz) {
        marketingMap.put(marketingType, clazz);
        nameMap.put(marketingType, name);
    }

    public Marketing getMarketing(String marketingType, Integer marketingId, Integer userId, String province, String city, String district) {
        if (StrKit.isBlank(marketingType) || marketingId == null) {
            return null;
        }
        Class<? extends Marketing> clazz = marketingMap.get(marketingType);
        if (clazz != null) {
            try {
                Marketing marketing = clazz.newInstance();
                marketing.init(marketingId, userId, province, city, district);
                return marketing;
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Map<String, String> getNameMap() {
        return nameMap;
    }

    public Map<String, String> getEnabledNameMap() {
        Map<String, String> result = new HashMap<>();
        nameMap.entrySet().forEach(entry -> {
            Class<? extends Marketing> clazz = marketingMap.get(entry.getKey());
            if (clazz != null) {
                try {
                    Marketing marketing = clazz.newInstance();
                    if (marketing.isEnabled()) {
                        result.put(entry.getKey(), entry.getValue());
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
        return result;
    }

    public String getName(String marketingType) {
        return nameMap.get(marketingType);
    }
}
