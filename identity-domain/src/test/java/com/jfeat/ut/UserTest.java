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
import com.jfeat.identity.model.User;
import com.jfeat.identity.model.param.UserParam;
import com.jfinal.plugin.activerecord.Page;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by jacky on 3/7/16.
 */
public class UserTest extends AbstractTestCase {

    @Before
    public void setup() {
        for (int i = 0; i < 15; i++) {
            User user = new User();
            user.setLoginName("user"+i);
            user.setName("user" + i);
            user.setPassword("user"+i);
            user.save();
        }
    }

    @After
    public void tearDown() {
        for (User user : User.dao.findAll()) {
            user.delete();
        }
    }

    @Test
    public void testFindByLoginName() {
        User user = User.dao.findByLoginName("user1");
        assertNotNull(user);
        assertNotNull(user.getId());
    }

    @Test
    public void testPaginate() {
        UserParam param = new UserParam(1, 4);
        param.setName("user");
        Page<User> users = User.dao.paginate(param);
        assertNotNull(users);
        assertEquals(4, users.getTotalPage());
    }
}
