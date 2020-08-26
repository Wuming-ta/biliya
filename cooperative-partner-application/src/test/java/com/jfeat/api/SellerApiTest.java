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

package com.jfeat.api;

import com.jfeat.config.model.Config;
import com.jfeat.identity.model.User;
import com.jfeat.kit.DateKit;
import com.jfeat.partner.model.Apply;
import com.jfeat.partner.model.Seller;
import com.jfeat.partner.service.SellerService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by jingfei on 2016/3/31.
 */
public class SellerApiTest extends ApiTestBase{

    private String url = baseUrl + "rest/seller";
    private SellerService sellerService = new SellerService();

    List<User> users = new ArrayList<>();

    private User createUser(int i, Integer parentId) {
        User user = new User();
        user.setLoginName("testuser" + i);
        user.setPassword("testuser");
        user.setInvitationCode("testuser");
        user.setInviterId(parentId);
        user.save();
        return user;
    }

    private void init() throws InterruptedException {
        clean();
        Config config = Config.dao.findByKey("mall.seller_auto_sellership");
        config.setValue("true");
        config.update();

        User currentUser = User.dao.findByLoginName(testUserName);
        Seller seller = new Seller();
        seller.setUserId(currentUser.getId());
        sellerService.createSeller(seller, null);
        TimeUnit.SECONDS.sleep(1);

        users.add(createUser(1, currentUser.getId()));
        TimeUnit.SECONDS.sleep(1);
        users.add(createUser(2, users.get(0).getId()));
        for (int i=0;i<3;i++) {
            TimeUnit.SECONDS.sleep(1);
            users.add(createUser(3 + i, users.get(1).getId()));
        }
        TimeUnit.SECONDS.sleep(1);
    }

    @After
    public void clean() {
        for(Seller seller : Seller.dao.findAll()){
            seller.delete();
        }
        for (Apply apply : Apply.dao.findAll()){
            apply.delete();
        }
    }

    @Test
    public void testApplySellerShip() throws IOException, InterruptedException {
        clean();
        Config config = Config.dao.findByKey("mall.seller_auto_sellership");
        config.setValue("false");
        config.update();
        User currentUser = User.dao.findByLoginName(testUserName);
        Seller seller = new Seller();
        seller.setUserId(currentUser.getId());
        sellerService.createSeller(seller, null);
        TimeUnit.SECONDS.sleep(1);

        Response response = post(url, "{}", Response.class);
        assertEquals(SUCCESS, response.getStatusCode());
    }

    @Test
    public void testShow() throws IOException, InterruptedException {
        init();
        Seller seller = Seller.dao.findByUserId(users.get(1).getId());
        SellerResponse response = get(url + "/" + seller.getId(), SellerResponse.class);
        assertEquals(SUCCESS, response.getStatusCode());
        assertEquals(3, response.getData().valueSize());
    }

}
