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

package com.jfeat.service;

import com.jfeat.core.Service;

/**
 * 用户进入批发区的权限判断。访问/rest/wholesale api时调用
 * Created by jackyhuang on 2017/6/3.
 */
public interface WholesaleAccessAuthorityService extends Service {
    boolean authorized(int userId);
}
