/*
 *   Copyright (C) 2014-2017 www.kequandian.net
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

public class MarketingModelMapping {

    public static void mapping(Module module) {

        module.addModel(com.jfeat.marketing.piece.model.PieceGroupPurchase.class);
        module.addModel(com.jfeat.marketing.piece.model.PieceGroupPurchasePricing.class);
        module.addModel(com.jfeat.marketing.piece.model.PieceGroupPurchaseMaster.class);
        module.addModel(com.jfeat.marketing.piece.model.PieceGroupPurchaseMember.class);
        module.addModel(com.jfeat.marketing.wholesale.model.Wholesale.class);
        module.addModel(com.jfeat.marketing.wholesale.model.WholesalePricing.class);
        module.addModel(com.jfeat.marketing.wholesale.model.WholesaleMember.class);
        module.addModel(com.jfeat.marketing.wholesale.model.WholesaleCategory.class);
        module.addModel(com.jfeat.marketing.common.model.MarketingConfig.class);

    }

}