/*
 * Copyright (C) 2014-2015 by ehngjen @ www.jfeat.com
 *
 *  The program may be used and/or copied only with the written permission
 *  from JFeat.com, or in accordance with the terms and
 *  conditions stipulated in the agreement/contract under which the program
 *  has been supplied.
 *
 *  All rights reserved.
 */

package com.jfeat.kit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

/**
 * Created by ehngjen on 10/15/2015.
 */
public class SqlUpdateTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testDelete() {
        SqlUpdate update = new SqlUpdate();
        update.delete("t_user");
        System.out.println(update.sql());
        assertEquals("DELETE FROM t_user;", update.sql());
    }

    @Test
    public void testDeleteWithWhere() {
        SqlUpdate update = new SqlUpdate();
        update.delete("t_user").where("id=1");
        System.out.println(update.sql());
        assertEquals("DELETE FROM t_user WHERE id=1;", update.sql());

        update = new SqlUpdate();
        update.delete("t_user").where("id=1");
        update.and("name='abc'");
        System.out.println(update.sql());
        assertEquals("DELETE FROM t_user WHERE id=1 AND name='abc';", update.sql());
    }

    @Test
    public void testUpdate() {
        SqlUpdate update = new SqlUpdate();
        update.update("t_user").set("name", "abc").set("desc", "123");
        System.out.println(update.sql());
        assertEquals("UPDATE t_user SET name=abc, SET desc=123;", update.sql());
    }

    @Test
    public void testUpdateWithWhere() {
        SqlUpdate update = new SqlUpdate();
        update.update("t_user").set("name", "abc").set("desc", "123");
        update.where("id=1");
        System.out.println(update.sql());
        assertEquals("UPDATE t_user SET name=abc, SET desc=123 WHERE id=1;", update.sql());
    }
}
