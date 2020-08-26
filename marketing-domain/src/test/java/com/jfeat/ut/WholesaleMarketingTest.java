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

package com.jfeat.ut;

import com.google.common.collect.Lists;
import com.jfeat.AbstractTestCase;
import com.jfeat.identity.model.User;
import com.jfeat.marketing.service.impl.WholesaleMarketing;
import com.jfeat.marketing.wholesale.model.Wholesale;
import com.jfeat.marketing.wholesale.model.WholesalePricing;
import com.jfeat.marketing.wholesale.service.WholesaleService;
import com.jfeat.partner.model.Seller;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductCategory;
import com.jfinal.ext.kit.RandomKit;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.Bidi;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by jackyhuang on 2017/6/1.
 */
public class WholesaleMarketingTest extends AbstractTestCase {

    private WholesaleService wholesaleService = new WholesaleService();
    private Wholesale wholesale;
    private User user;
    private Product product;

    @Before
    public void before() throws InterruptedException {
        user = new User();
        user.setLoginName(RandomKit.randomStr());
        user.setPassword(RandomKit.randomStr());
        user.setName(RandomKit.randomStr());
        user.save();
        TimeUnit.SECONDS.sleep(5);
        Seller seller = Seller.dao.findByUserId(user.getId());
        seller.setSellerShip(1);
        seller.setPartnerShip(1);
        seller.setCrownShip(1);
        seller.update();

        ProductCategory category = new ProductCategory();
        category.setName(RandomKit.randomStr());
        category.save();

        product = new Product();
        product.setCategoryId(category.getId());
        product.setName(RandomKit.randomStr());
        product.setShortName(RandomKit.randomStr());
        product.setPrice(new BigDecimal(60.0));
        product.setCostPrice(new BigDecimal(10.0));
        product.setStatus(Product.Status.ONSELL.toString());
        product.save();

        wholesale = new Wholesale();
        wholesale.setMarketingName(RandomKit.randomStr());
        wholesale.setProductId(product.getId());

        List<WholesalePricing> pricings = Lists.newArrayList();
        WholesalePricing defaultPricing = new WholesalePricing();
        defaultPricing.setPrice(new BigDecimal(30.0));
        defaultPricing.setEnabled(1);
        defaultPricing.setIsDefault(1);
        pricings.add(defaultPricing);
        WholesalePricing pricing = new WholesalePricing();
        pricing.setIsDefault(0);
        pricing.setEnabled(1);
        pricing.setPrice(new BigDecimal(40.0));
        pricing.setRegion("广东-广州");
        pricings.add(pricing);

        WholesalePricing pricingExclude = new WholesalePricing();
        pricingExclude.setIsDefault(0);
        pricingExclude.setEnabled(0);
        pricingExclude.setPrice(new BigDecimal(40.0));
        pricingExclude.setRegion("香港");
        pricings.add(pricingExclude);

        wholesaleService.createWholeSale(wholesale, pricings);
        wholesale.setStatus(Wholesale.Status.ONSELL.toString());
        wholesale.update();

    }

    @Test
    public void testAvailable() {
        WholesaleMarketing wholesaleMarketing = new WholesaleMarketing();
        wholesaleMarketing.init(wholesale.getId(), user.getId(), "广东", "广州", "荔湾区");
        boolean result = wholesaleMarketing.available(Lists.newArrayList(product.getId()), Lists.<Integer>newArrayList(), Lists.newArrayList(2));
        assertTrue(result);

        wholesaleMarketing.init(wholesale.getId(), user.getId(), "香港", "香港", "九龙");
        result = wholesaleMarketing.available(Lists.newArrayList(product.getId()), Lists.<Integer>newArrayList(), Lists.newArrayList(2));
        assertFalse(result);
    }

    @Test
    public void testGetPrice() {
        WholesaleMarketing wholesaleMarketing = new WholesaleMarketing();

        wholesaleMarketing.init(wholesale.getId(), user.getId(), "广东", "广州", "荔湾区");
        BigDecimal price = wholesaleMarketing.getPrice();
        assertEquals(40.00, price.doubleValue(), 0.001);

        wholesaleMarketing.init(wholesale.getId(), user.getId(), "广东", "肇庆", "端州区");
        price = wholesaleMarketing.getPrice();
        assertEquals(30.00, price.doubleValue(), 0.001);

        wholesaleMarketing.init(wholesale.getId(), user.getId(), "广西", "南宁", "XX区");
        price = wholesaleMarketing.getPrice();
        assertEquals(30.00, price.doubleValue(), 0.001);

        wholesaleMarketing.init(wholesale.getId(), user.getId(), "香港", "香港", "九龙");
        price = wholesaleMarketing.getPrice();
        assertNull(price);
    }
}
