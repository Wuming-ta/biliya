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

package com.jagregory.shiro.freemarker;

/**
 * Created by ehngjen on 1/4/15.
 */
public class HasAnyPermissionsTag extends PermissionTag {
    private static final String PERMISSION_NAMES_DELIMETER = ",";

    @Override
    protected boolean showTagBody(String permissions) {
        boolean hasAnyPermission = false;
        // Iterate through roles and check to see if the user has one of the roles
        for (String permission : permissions.split(PERMISSION_NAMES_DELIMETER)) {
            if (isPermitted(permission.trim())) {
                hasAnyPermission = true;
                break;
            }
        }

        return hasAnyPermission;
    }
}
