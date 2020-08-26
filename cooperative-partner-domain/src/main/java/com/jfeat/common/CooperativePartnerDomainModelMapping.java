/*
 *   Copyright (C) 2014-2019 www.kequandian.net
 *
 *    The program may be used and/or copied only with the written permission
 *    from www.kequandian.net or in accordance with the terms and
 *    conditions stipulated in the agreement/contract under which the program
 *    has been supplied.
 *
 *    All rights reserved.
 *
 */
package com.jfeat.common;

import com.jfeat.core.Module;

public class CooperativePartnerDomainModelMapping {

    public static void mapping(Module module) {

        module.addModel(com.jfeat.partner.model.Seller.class);
        module.addModel(com.jfeat.partner.model.Agent.class);
        module.addModel(com.jfeat.partner.model.MerchantOptions.class);
        module.addModel(com.jfeat.partner.model.PcdQualify.class);
        module.addModel(com.jfeat.partner.model.AgentPcdQualify.class);
        module.addModel(com.jfeat.partner.model.SettlementProportion.class);
        module.addModel(com.jfeat.partner.model.Apply.class);
        module.addModel(com.jfeat.partner.model.PartnerLevel.class);
        module.addModel(com.jfeat.partner.model.PhysicalSeller.class);
        module.addModel(com.jfeat.partner.model.PhysicalPurchaseJournal.class);
        module.addModel(com.jfeat.partner.model.PhysicalPurchaseSummary.class);
        module.addModel(com.jfeat.partner.model.AgentSummary.class);
        module.addModel(com.jfeat.partner.model.PhysicalSettlementProportion.class);
        module.addModel(com.jfeat.partner.model.PhysicalSettlementDefinition.class);
        module.addModel(com.jfeat.partner.model.CooperativeStatistic.class);
        module.addModel(com.jfeat.partner.model.PhysicalAgentBonus.class);
        module.addModel(com.jfeat.partner.model.PhysicalApplyTips.class);
        module.addModel(com.jfeat.partner.model.AgentPurchaseJournal.class);
        module.addModel(com.jfeat.partner.model.Copartner.class);
        module.addModel(com.jfeat.partner.model.CopartnerSettlement.class);
        module.addModel(com.jfeat.partner.model.Alliance.class);

    }

}