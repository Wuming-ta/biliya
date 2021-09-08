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
 * Created by ehngjen on 10/14/2015.
 */
public class SqlQueryTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testNormal() {
        SqlQuery query = new SqlQuery();
        query.from("t_user").where("a=1").and("b=2").or("c=3").orderBy("a").orderByDesc("b");
        System.out.println(query.sql());
        assertEquals("SELECT * FROM t_user WHERE a=1 AND b=2 OR c=3 ORDER BY a ASC, b DESC;", query.sql());
    }

    @Test
    public void testNormalWithSelect() {
        SqlQuery query = new SqlQuery();
        query.select("id", "name").from("t_user");
        System.out.println(query.sql());
        assertEquals("SELECT id, name FROM t_user;", query.sql());
    }

    @Test
    public void testNormalWithJoin() {
        SqlQuery query = new SqlQuery();
        query.from("t_user u").join("t_department d").on("u.did=d.id");
        System.out.println(query.sql());
        assertEquals("SELECT * FROM t_user u JOIN t_department d ON u.did=d.id;", query.sql());
    }

    @Test
    public void testAndWithoutWhere() {
        thrown.expect(IllegalStateException.class);
        SqlQuery query = new SqlQuery();
        query.from("t_user").and("a=1");
    }

    @Test
    public void testOrWithoutWhere() {
        thrown.expect(IllegalStateException.class);
        SqlQuery query = new SqlQuery();
        query.from("t_user").or("a=1");
    }

    @Test
    public void testWithoutFrom() {
        thrown.expect(IllegalStateException.class);
        SqlQuery query = new SqlQuery();
        query.or("a=1");
    }
}
