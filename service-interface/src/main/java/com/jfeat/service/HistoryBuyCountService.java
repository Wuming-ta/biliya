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
 * Created by jackyhuang on 16/10/14.
 */
public interface HistoryBuyCountService extends Service {
    long getHistoryBuyCount(int productId, int userId, int lastDays);
}
