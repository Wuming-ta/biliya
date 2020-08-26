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

package com.jfeat.mall.controller.link;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by jackyhuang on 2017/5/27.
 */
public class MarketingShareLinkHolder {
    private static final MarketingShareLinkHolder me = new MarketingShareLinkHolder();

    private Map<String, Class<? extends MarketingShareLink>> map = Maps.newConcurrentMap();
    private MarketingShareLink dummy = new DummyShareLink();

    private MarketingShareLinkHolder() {
        map.put("PIECE-GROUP", PieceGroupShareLink.class);
        map.put("PIECE-GROUP-JOINT", PieceGroupShareLink.class);
    }

    public static MarketingShareLinkHolder me() {
        return me;
    }

    public MarketingShareLink getMarketingShareLink(String marketingType, Integer marketingId) {
        if (map.get(marketingType) != null) {
            Class<MarketingShareLink> clazz = (Class<MarketingShareLink>) map.get(marketingType);
            try {
                MarketingShareLink marketingShareLink = clazz.newInstance();
                marketingShareLink.init(marketingId);
                return marketingShareLink;
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return dummy;
    }
}
