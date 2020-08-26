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
package com.jfeat.common;

import com.jfeat.core.Module;
import com.jfeat.core.JFeatConfig;
import com.jfeat.product.purchase.*;

public class ProductDomainModule extends Module {

    public ProductDomainModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        ProductDomainModelMapping.mapping(this);

        // config the module you dependencied.
        // new YouDependenciedModule(jfeatConfig);
        new IdentityDomainModule(jfeatConfig);
        new ConfigDomainModule(jfeatConfig);
        new MerchantDomainModule(jfeatConfig);

        ProductPurchaseHolder.me().register(new WeixinFollowedStrategy());
        ProductPurchaseHolder.me().register(new ProductQuantityStrategy());
        ProductPurchaseHolder.me().register(new FriendsCountStrategy());
        ProductPurchaseHolder.me().register(new HistoryBuyCountStrategy());
    }
}
