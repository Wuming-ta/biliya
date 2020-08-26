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

public class ProductDomainModelMapping {

    public static void mapping(Module module) {

        module.addModel(com.jfeat.product.model.Product.class);
        module.addModel(com.jfeat.product.model.ProductBrand.class);
        module.addModel(com.jfeat.product.model.ProductCategory.class);
        module.addModel(com.jfeat.product.model.ProductImage.class);
        module.addModel(com.jfeat.product.model.ProductFavorite.class);
        module.addModel(com.jfeat.product.model.ProductProperty.class);
        module.addModel(com.jfeat.product.model.ProductCategoryProperty.class);
        module.addModel(com.jfeat.product.model.ProductHitWord.class);
        module.addModel(com.jfeat.product.model.ProductSpecification.class);
        module.addModel(com.jfeat.product.model.ProductDescription.class);
        module.addModel(com.jfeat.product.model.CarryMode.class);
        module.addModel(com.jfeat.product.model.InclPostageProviso.class);
        module.addModel(com.jfeat.product.model.FareTemplate.class);
        module.addModel(com.jfeat.product.model.ProductPurchaseStrategy.class);
        module.addModel(com.jfeat.product.model.ProductPurchaseStrategyItem.class);
        module.addModel(com.jfeat.product.model.ProductSettlementProportion.class);
        module.addModel(com.jfeat.product.model.ProductTag.class);

    }

}