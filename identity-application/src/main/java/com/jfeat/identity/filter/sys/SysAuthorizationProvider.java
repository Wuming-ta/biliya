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

package com.jfeat.identity.filter.sys;

import com.jfeat.core.Service;

import java.util.Collection;

/**
 * Created by jackyhuang on 17/2/16.
 */
public interface SysAuthorizationProvider extends Service {
    String getUserName();
    String getPassword();
    Collection<String> getAllowIps();
}
