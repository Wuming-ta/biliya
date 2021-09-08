package com.jfeat.kit;

import org.junit.Test;

/**
 * Created by jacky on 2/11/15.
 */
public class DateKitTest {

    @Test
    public void test() throws java.text.ParseException {
        System.out.println(DateKit.today());
        System.out.println(DateKit.today("yyyy-MM-dd hh:mm"));
        System.out.println(DateKit.today("yyyy-MM-dd hh:mm:ss"));
        System.out.println(DateKit.yesterday());
        System.out.println(DateKit.yesterday("yyyy-MM-dd 00:00:00"));
        System.out.println(DateKit.tomorrow());
        System.out.println(DateKit.tomorrow("yyyy-MM-dd 23:59:59"));
        
        String stringDate = "2015-2-14 00:00:00";
        System.out.println(DateKit.toDate(stringDate));
        
        System.out.println("lastMonth: " + DateKit.lastMonth());
        System.out.println("lastMonth: " + DateKit.lastMonth("yyyy-MM"));
        System.out.println("lastMonth: " + DateKit.lastMonth("yyyy-MM-01 00:00:00"));
        System.out.println("lastYear: " + DateKit.lastYear());
        
        
        System.out.println("lastDay: " + DateKit.lastDay(DateKit.toDate("2015-02-01"), "yyyy-MM-dd 23:59:59"));
        System.out.println("lastDay: " + DateKit.lastDay(DateKit.toDate("2015-03-01")));
        
        System.out.println("currentMonth: " + DateKit.currentMonth("yyyy-MM-01"));

        System.out.println("11 ays ago: " + DateKit.daysAgo(11));
        System.out.println("40 ays ago: " + DateKit.daysAgo(40));
    }
}
