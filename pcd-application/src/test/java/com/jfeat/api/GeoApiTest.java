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
 * Created by jingfei on 2016/4/1.
 */
public class GeoApiTest extends ApiTestBase {
    private String url = baseUrl + "rest/geo";

    @Test
    public void testIndex1() throws IOException {
        Response response = get(url + "?lng=113.24788673459&lat=23.133990536009", Response.class);
        assertEquals(SUCCESS, response.getStatusCode());
    }

    @Test
    public void testIndex2() throws IOException {
        Response response = get(url + "?lng=113.24788673459&lat=23.126990536009", Response.class);
        assertEquals(SUCCESS, response.getStatusCode());
    }
}
