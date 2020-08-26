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
package com.jfeat.ut;

import com.jfeat.AbstractTestCase;
import com.jfeat.kit.DateKit;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ModuleTest extends AbstractTestCase {

    @Test
    public void test() {
        int year = Integer.parseInt(DateKit.lastYear());
        execute(year);
    }

    private void execute(int year) {
        Calendar calendar = new GregorianCalendar();

        calendar.set(year, Calendar.JANUARY, 1, 0, 0, 0);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTime = dateFormat.format(calendar.getTime());
        calendar.set(Calendar.MONTH, calendar.getActualMaximum(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        String endTime = dateFormat.format(calendar.getTime());
        System.out.println(startTime);
        System.out.print(endTime);
    }
}

