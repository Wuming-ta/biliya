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

import com.jfeat.identity.model.User;
import com.jfeat.member.model.Contact;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by jingfei on 2016/4/2.
 */
public class ContactApiTest extends ApiTestBase {

    private String url = baseUrl + "rest/contact";

    public Contact contact;

    public Contact init() {
        Contact contact = new Contact();
        contact.setZip("510520");
        contact.setStreetNumber("20");
        contact.setStreet("华贵路");
        contact.setProvince("广东");
        contact.setPhone("13567895478");
        contact.setCity("广州");
        contact.setContactUser("张三");
        contact.setDetail("2508");
        contact.setDistrict("越秀");
        contact.setIsDefault(1);
        return contact;
    }

    @Before
    public void setUp(){
        User user = User.dao.findByLoginName(testUserName);
        Contact contact = init();
        contact.setUserId(user.getId());
        contact.save();
        this.contact = contact;
    }

    @Test
    public void testSave() throws IOException {
        Response response = post(url, contact.toJson(), Response.class);
        assertEquals(SUCCESS, response.getStatusCode());
    }

    @Test
    public void testIndex() throws IOException {
        Response response = get(url, Response.class);
        assertEquals(SUCCESS, response.getStatusCode());
    }

    @Test
    public void testDelete() throws IOException {
        Response response = delete(url + "/" + contact.getId(), Response.class);
        assertEquals(SUCCESS, response.getStatusCode());
    }
}
