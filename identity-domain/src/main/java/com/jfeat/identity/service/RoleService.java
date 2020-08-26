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

package com.jfeat.identity.service;

import com.jfeat.core.BaseService;
import com.jfeat.identity.model.Role;
import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * Created by jacky on 3/8/16.
 */
public class RoleService extends BaseService {

    @Before(Tx.class)
    public Ret createRole(Role role, String... permissions) {
        role.save();
        role.updatePermission(permissions);
        return success();
    }

    @Before(Tx.class)
    public Ret updateRole(Role role, String... permissions) {
        role.update();
        role.updatePermission(permissions);
        return success();
    }

    public Ret deleteRole(int roleId) {
        Role role = Role.dao.findById(roleId);
        if (role == null) {
            return failure("role.not.found.");
        }
        if (role.getUsers().size() > 0) {
            return failure("role.has.users");
        }
        if (role.isSystemRole()) {
            return failure("role.system.cannot.delete");
        }
        role.delete();
        return success();
    }
}
