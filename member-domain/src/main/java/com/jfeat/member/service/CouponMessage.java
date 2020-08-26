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

package com.jfeat.member.service;

import com.jfinal.ext.kit.RandomKit;

/**
 * Created by jackyhuang on 16/11/28.
 */
public class CouponMessage {
    private static String[] messages = {
            "又有券可用了。",
            "又可以省钱啦。",

    };

    public static String randomMessage() {
        int i = RandomKit.random(0, messages.length);
        return messages[i];
    }
}
