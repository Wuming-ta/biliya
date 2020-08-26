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
package com.jfeat.product.api;

import com.jfeat.config.utils.ConfigUtils;
import com.jfeat.core.RestController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.merchant.model.SettledMerchant;
import com.jfeat.product.interceptor.ProductViewCountInterceptor;
import com.jfeat.product.model.*;
import com.jfeat.product.model.param.ProductParam;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;

import java.math.BigDecimal;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

@ControllerBind(controllerKey = "/rest/product")
public class ProductController extends RestController {

    /**
     * GET /rest/product?zone=1&tag=xxx&tag=yyy&promoted=-1
     * query the promoted products if no zone provided.
     * else query the zone products.
     */
    @Override
    @Before({ CurrentUserInterceptor.class })
    public void index() {
        User currentUser = getAttr("currentUser");
        Boolean all = getParaToBoolean("all", false);
        if (all) {
            renderSuccess(Product.dao.findAllOnSellRetail());
            return;
        }

        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        Integer zone = getParaToInt("zone");
        Integer promoted = getParaToInt("promoted");
        if (promoted == null) {
            promoted = zone == null ? Product.Promoted.YES.getValue() : null;
        }
        else if (promoted == -1) {
            promoted = null;
        }
        Integer presale = getParaToInt("presale");
        String[] tags = getParaValues("tag");
        String[] orderByList = getParaValues("orderBy");
        String[] orderByDescList = getParaValues("orderByDesc");
        ProductParam param = new ProductParam(pageNumber, pageSize);
        param.setStatus(Product.Status.ONSELL.toString())
                .setIsPresale(presale)
                .setPromoted(promoted)
                .setZone(zone)
                .setTags(tags)
                .setOrderByList(orderByList)
                .setOrderByDescList(orderByDescList);
        List<Product> promotedProducts = Product.dao.paginate(param).getList();
        ListIterator<Product> iterator = promotedProducts.listIterator();
        while (iterator.hasNext()) {
            Product product = iterator.next();
            if (currentUser != null) {
                Util.calcProductSettlement(product);
            }
            product.removeSecretAttrs();
            Integer wholesale = product.getCategory().getWholesale();
            if (wholesale != null && wholesale.equals(ProductCategory.WHOLESALE)) {
                iterator.remove();
            }
        }
        renderSuccess(promotedProducts);
    }

    @Override
    @Before({ ProductViewCountInterceptor.class, CurrentUserInterceptor.class })
    public void show() {
        User currentUser = getAttr("currentUser");
        Product product = Product.dao.findById(getParaToInt());
        if (product == null) {
            renderFailure("product.is.null");
            return;
        }
        Product.Status status = Product.Status.valueOf(product.getStatus());
        if (status == Product.Status.OFFSELL) {
            renderFailure("product.is.offsell");
            return;
        }
        if (status != Product.Status.ONSELL) {
            renderFailure("product.invalid.status");
            return;
        }

        FareTemplate fareTemplate = product.getFareTemplate();
        if (fareTemplate != null) {
            fareTemplate.put("carry_modes", fareTemplate.getCarryModes());
            fareTemplate.put("incl_postage_provisoes", fareTemplate.getInclPostageProvisoes());
        }

        List<ProductSpecification> productSpecifications = product.getProductSpecifications();

        String description = null;
        ProductDescription productDescription = product.getProductDescription();
        if (productDescription != null) {
            description = productDescription.getDescription();
        }

        SettledMerchant merchant = product.getMerchant();
        if (merchant != null) {
            merchant.removeSecretAttrs();
            product.put("merchant", merchant);
        }

        product.put("tags", product.getProductTags());
        product.put("videos", product.getVideos());
        product.put("covers", product.getCovers());
        product.put("properties", product.getProductProperties());
        product.put("specifications", productSpecifications);
        product.put("description", description);
        product.put("fare_template", fareTemplate);
        product.put("purchase_strategy", product.getPurchaseStrategy());
        product.put("favorited", 0);
        product.put("wx_url", ConfigUtils.getFallbackUrl("mall.url.product_detail", product.getId()));

        if (currentUser != null) {
            product.put("favorited", ProductFavorite.dao.find(currentUser.getId(), product.getId()) != null ? 1 : 0);
            Util.calcProductSettlement(product);
        }

        product.removeSecretAttrs();
        renderSuccess(product);
    }


}
