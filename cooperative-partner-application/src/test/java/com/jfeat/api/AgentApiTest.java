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

import com.jfeat.partner.model.Apply;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by jingfei on 2016/3/31.
 */
public class AgentApiTest extends ApiTestBase {

    private String url = baseUrl + "rest/agent";

    @After
    @Before
    public void clean(){
        for (Apply apply : Apply.dao.findAll()){
            apply.delete();
        }
    }

    @Test
    public void testSave() throws IOException {
        Response response = post(url, "{}", Response.class);
        assertEquals(SUCCESS, response.getStatusCode());
    }

}
