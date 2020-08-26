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

package com.jfeat.service.impl;

import com.jfeat.partner.model.Seller;
import com.jfeat.service.FriendsCountService;

/**
 * Created by jackyhuang on 16/10/13.
 */
public class FriendsCountServiceImpl implements FriendsCountService {
    @Override
    public long getFriendsCount(int userId) {
        return Seller.dao.queryChildrenCountFollowed(userId) + Seller.dao.queryChildrenCountUnFollowed(userId);
    }
}
