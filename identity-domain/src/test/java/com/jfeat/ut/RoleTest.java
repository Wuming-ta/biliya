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
import com.jfeat.identity.model.Permission;
import com.jfeat.identity.model.PermissionDefinition;
import com.jfeat.identity.model.Role;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Created by jacky on 3/7/16.
 */
public class RoleTest extends AbstractTestCase {
    private Integer roleId;

    @Before
    public void setup() {
        Role role = new Role();
        role.setName("admin");
        role.save();
        roleId = role.getId();
        List<String> permissions = new ArrayList<>();
        permissions.add("test.1");
        permissions.add("test.2");
        role.updatePermission(permissions.toArray(new String[0]));
    }

    @Test
    public void testGetPermission() {
        Role role = Role.dao.findById(roleId);
        assertNotNull(role);
        assertEquals(roleId, role.getId());
        assertEquals(2, role.getPermissions().size());
    }
}
