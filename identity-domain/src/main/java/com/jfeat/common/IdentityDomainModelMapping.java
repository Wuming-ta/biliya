/*
 *   Copyright (C) 2014-2019 www.kequandian.net
 *
 *    The program may be used and/or copied only with the written permission
 *    from www.kequandian.net or in accordance with the terms and
 *    conditions stipulated in the agreement/contract under which the program
 *    has been supplied.
 *
 *    All rights reserved.
 *
 */
package com.jfeat.common;

import com.jfeat.core.Module;

public class IdentityDomainModelMapping {

    public static void mapping(Module module) {

        module.addModel(com.jfeat.identity.model.User.class);
        module.addModel(com.jfeat.identity.model.Role.class);
        module.addModel(com.jfeat.identity.model.Permission.class);
        module.addModel(com.jfeat.identity.model.PermissionDefinition.class);
        module.addModel(com.jfeat.identity.model.PermissionGroupDefinition.class);
        module.addModel(com.jfeat.identity.model.UserJoinNotify.class);

    }

}