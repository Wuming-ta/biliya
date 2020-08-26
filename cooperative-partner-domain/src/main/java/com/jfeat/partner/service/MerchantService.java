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

package com.jfeat.partner.service;

import com.jfeat.core.BaseService;
import com.jfeat.partner.model.MerchantOptions;
import com.jfeat.partner.model.SettlementProportion;
import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.List;

/**
 * Created by jacky on 3/24/16.
 */
public class MerchantService extends BaseService {

    @Before(Tx.class)
    public Ret updateMerchantOptions(MerchantOptions merchantOptions, List<SettlementProportion> settlementProportions) {
        merchantOptions.update();
        Db.batchUpdate(settlementProportions, 10);
        return success();
    }

    @Before(Tx.class)
    public Ret updateSettlementProportions(List<SettlementProportion> settlementProportions) {
        Db.batchUpdate(settlementProportions, 10);
        return success();
    }

    @Before(Tx.class)
    public Ret updateSettlementProportions(SettlementProportion settlementProportion) {
        settlementProportion.update();
        return success();
    }

    public List<SettlementProportion> findBySellerType() {
        return SettlementProportion.dao.findByType(SettlementProportion.Type.SELLER);
    }

    public List<SettlementProportion> findByAgentType() {
        return SettlementProportion.dao.findByType(SettlementProportion.Type.AGENT);
    }

    public List<SettlementProportion> findByPhysicalAgentType() {
        return SettlementProportion.dao.findByType(SettlementProportion.Type.PHYSICAL_AGENT);
    }

    public List<SettlementProportion> findByPartnerType() {
        return SettlementProportion.dao.findByType(SettlementProportion.Type.PARTNER);
    }
}
