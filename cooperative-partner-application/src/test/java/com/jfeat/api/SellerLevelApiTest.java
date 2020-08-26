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

import com.jfeat.partner.model.Seller;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by jingfei on 2016/4/1.
 */
public class SellerLevelApiTest extends ApiTestBase{

    private String url = baseUrl + "rest/seller_level";

    @Before
    public void init() {
        for (Seller seller : Seller.dao.findAll()){
            seller.delete();
        }
        Seller seller = Seller.dao.findByUserId(1);
        if (null == seller){
            Seller seller1 = new Seller();
            seller1.setId(1);
            seller1.setUserId(1);
            seller1.save();
        }
        for (int i=0;i<3;i++) {
            Seller seller2 = new Seller();
            seller2.setId(2+i);
            seller2.setUserId(1);
            seller2.setParentId(1);
            seller2.save();
        }
        for (int i=0;i<4;i++) {
            Seller seller2 = new Seller();
            seller2.setId(5+i);
            seller2.setUserId(1);
            seller2.setParentId(2);
            seller2.save();
        }
    }

    @After
    public void clean() {
        for (Seller seller : Seller.dao.findAll()){
            seller.delete();
        }
    }

    @Test
    public void testIndex() throws IOException {
        Response response = get(url, Response.class);
        assertEquals(SUCCESS, response.getStatusCode());
    }

}
