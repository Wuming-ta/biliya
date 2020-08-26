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

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by jingfei on 2016/3/31.
 */
public class RewardCashApiTest extends ApiTestBase{

    private String url = baseUrl + "rest/reward_cash";

    @Test
    public void testIndex1() throws IOException {
        Response response = get(url, Response.class);
        assertEquals(SUCCESS, response.getStatusCode());
    }

    @Test
    public void testIndex2() throws IOException {
        Response response = get(url + "?page_number=&page_size=&start_date=&end_date=", Response.class);
        assertEquals(SUCCESS, response.getStatusCode());
    }

    @Test
    public void testIndex3() throws IOException {
        Response response = get(url + "?page_number=1&page_size=10&start_date=20160101&end_date=20160330", Response.class);
        assertEquals(SUCCESS, response.getStatusCode());
    }



}
