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

package com.jfeat.settlement.util;

import com.jfeat.kit.DateKit;

/**
 * Created by jingfei on 2016/3/24.
 */
public class DateUtil {

    public static String getSeasonStartDate() {
        Integer mon = Integer.valueOf(DateKit.currentMonth("MM"));
        String startDate;
        if (mon >= 1 && mon < 4) {
            startDate = DateKit.today("yyyy-01-01");
        } else if (mon >= 4 && mon < 7) {
            startDate = DateKit.today("yyyy-04-01");
        } else if (mon >= 7 && mon < 10) {
            startDate = DateKit.today("yyyy-07-01");
        } else {
            startDate = DateKit.today("yyyy-10-01");
        }
        return startDate;
    }

    public static String getSeasonEndDate(){
        Integer mon = Integer.valueOf(DateKit.currentMonth("MM"));
        String endDate;
        if (mon >= 1 && mon < 4) {
            endDate = DateKit.today("yyyy-03-31");
        } else if (mon >= 4 && mon < 7) {
            endDate = DateKit.today("yyyy-06-30");
        } else if (mon >= 7 && mon < 10) {
            endDate = DateKit.today("yyyy-09-30");
        } else {
            endDate = DateKit.today("yyyy-12-31");
        }
        return endDate;
    }
}
