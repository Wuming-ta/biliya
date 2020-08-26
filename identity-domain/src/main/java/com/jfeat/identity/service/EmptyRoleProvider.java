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

import com.jfeat.identity.model.Role;

/**
 * Created by jacky on 4/23/16.
 */
public class EmptyRoleProvider implements RoleProvider {
    @Override
    public Role getDefault() {
        return new Role();
    }
}
