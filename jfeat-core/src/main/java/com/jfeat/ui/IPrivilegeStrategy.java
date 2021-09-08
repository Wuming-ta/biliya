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

package com.jfeat.ui;

import java.util.List;

/**
 * Created by ehngjen on 4/14/2015.
 */
public interface IPrivilegeStrategy {
    public boolean isAllowed(String key);
    public void updatePermission(String... permissions);
    public List<String> getPermissions();
}
