/*
 *   Copyright (C) 2014-2018 www.kequandian.net
 *
 *    The program may be used and/or copied only with the written permission
 *    from www.kequandian.net or in accordance with the terms and
 *    conditions stipulated in the agreement/contract under which the program
 *    has been supplied.
 *
 *    All rights reserved.
 *
 */

/*
 * This file is automatically generated by tools.
 * It defines the model for the table. All customize operation should 
 * be written here. Such as query/update/delete.
 * The controller calls this object.
 */
package com.jfeat.merchant.model;

import com.jfeat.merchant.model.base.SettledMerchantSettlementProportionBase;
import com.jfinal.ext.plugin.tablebind.TableBind;

import java.util.List;

@TableBind(tableName = "t_settled_merchant_settlement_proportion")
public class SettledMerchantSettlementProportion extends SettledMerchantSettlementProportionBase<SettledMerchantSettlementProportion> {

    /**
     * Only use for query.
     */
    public static SettledMerchantSettlementProportion dao = new SettledMerchantSettlementProportion();

    public SettledMerchantSettlementProportion getDefault() {
        return findFirst("select * from t_settled_merchant_settlement_proportion");
    }
}
