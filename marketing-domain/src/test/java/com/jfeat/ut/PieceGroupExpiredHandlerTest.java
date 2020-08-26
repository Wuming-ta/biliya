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

import com.jfeat.AbstractTestCase;
import com.jfeat.ext.plugin.redis.RedisSubscriberThread;
import com.jfeat.identity.model.User;
import com.jfeat.marketing.handler.MarketingExpiredSubscriber;
import com.jfeat.marketing.handler.PieceGroupExpiredHandler;
import com.jfeat.marketing.piece.model.PieceGroupPurchase;
import com.jfeat.marketing.piece.service.PieceGroupPurchaseService;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductCategory;
import com.jfinal.ext.kit.RandomKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.plugin.redis.RedisPlugin;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * Created by jackyhuang on 17/5/4.
 */
public class PieceGroupExpiredHandlerTest extends AbstractTestCase {

    private PieceGroupPurchaseService service = new PieceGroupPurchaseService();

    @Before
    public void setup() throws InterruptedException {

        RedisPlugin plugin = new RedisPlugin("test", "localhost");
        plugin.start();

        User user = new User();
        user.setLoginName(RandomKit.randomStr());
        user.setName(RandomKit.randomStr());
        user.setPassword(RandomKit.randomStr());
        user.save();

        ProductCategory category = new ProductCategory();
        category.setName(RandomKit.randomStr());
        category.save();
        Product product = new Product();
        product.setCategoryId(category.getId());
        product.setPrice(new BigDecimal(1999));
        product.setName(RandomKit.randomStr());
        product.setShortName(RandomKit.randomStr());
        product.setStatus(Product.Status.ONSELL.toString());
        product.save();

        PieceGroupPurchase pieceGroupPurchase = new PieceGroupPurchase();
        pieceGroupPurchase.setDuration(60);
        pieceGroupPurchase.setMarketingName(RandomKit.randomStr());
        pieceGroupPurchase.setPrice(new BigDecimal(1200));
        pieceGroupPurchase.setSuggestedPrice(new BigDecimal(2000));
        pieceGroupPurchase.setMinParticipatorCount(4);
        pieceGroupPurchase.setProductId(product.getId());
        Ret ret = service.createPieceGroupPurchase(pieceGroupPurchase, null);
        logger.debug("ret = {}", ret.getData());

        pieceGroupPurchase.setStatus(PieceGroupPurchase.Status.ONSELL.toString());
        pieceGroupPurchase.update();


        for (int i = 1; i < 10; i++) {
            ret = service.openGroup(pieceGroupPurchase.getId(), user.getId(), RandomKit.randomMD5Str());
            logger.debug("ret = {}", ret.getData());
        }
    }

    /**
     * 先启动redis
     */
    @Test
    @Ignore
    public void testInit() throws InterruptedException {

        MarketingExpiredSubscriber subscriber = new MarketingExpiredSubscriber(Redis.use(), "__keyevent@0__:expired");
        PieceGroupExpiredHandler.init(Redis.use(), true);
        Thread thread = new RedisSubscriberThread(subscriber);
        thread.start();
        thread.join();
    }
}
