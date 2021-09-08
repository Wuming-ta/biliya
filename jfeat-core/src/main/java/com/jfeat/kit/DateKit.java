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

package com.jfeat.kit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jacky on 2/11/15.
 */
public class DateKit {
    private static String format = "yyyy-MM-dd";
    
    public static String today() {
        return today(format);
    }
    
    public static String today(String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        return sf.format(c.getTime());
    }

    public static String tomorrow() {
        return tomorrow(format);
    }

    public static String tomorrow(String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        return sf.format(c.getTime());
    }

    public static String yesterday() {
        return yesterday(format);
    }

    public static String yesterday(String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -1);
        return sf.format(c.getTime());
    }
    
    public static String lastDay(Date date, String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, -1);
        return sf.format(c.getTime());
    }
    
    public static String lastDay(Date date) {
        return lastDay(date, format);
    }

    public static String lastMonth(String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        return sf.format(c.getTime());
    }
    
    public static String lastMonth() {
        return lastMonth(format);
    }
    
    public static String currentMonth(String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        return sf.format(c.getTime());
    }
    

    public static String lastYear() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -1);
        return sf.format(c.getTime());
    }

    public static Date daysLater(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, days);
        return c.getTime();
    }

    public static String daysLaterStr(int days, String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(daysLater(days));
    }

    public static String daysLaterStr(int days) {
        return daysLaterStr(days, "yyyy-MM-dd");
    }

    public static Date daysAgo(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -days);
        return c.getTime();
    }

    public static String daysAgoStr(int days) {
        return daysAgoStr(days, "yyyy-MM-dd HH:mm:ss");
    }

    public static String daysAgoStr(int days, String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(daysAgo(days));
    }

    /**
     * Calendar.HOUR  12 小时制
     * Calendar.HOUR_OF_DAY 24 小时制
     * @param hours
     * @return
     */
    public static Date hoursAgo(int hours) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR_OF_DAY, -hours);
        return c.getTime();
    }

    public static String hoursAgoStr(int hours) {
        return hoursAgoStr(hours, "yyyy-MM-dd HH:mm:ss");
    }

    public static String hoursAgoStr(int hours, String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(hoursAgo(hours));
    }

    public static Date toDate(String stringDate) throws java.text.ParseException {
        return toDate(stringDate, format);
    }
    
    public static Date toDate(String stringDate, String format) throws java.text.ParseException {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.parse(stringDate);
    }


    /**
     * 生日转为年龄，计算法定年龄
     * @param birthDay 生日
     * @return 年龄
     * @throws Exception
     */
    public static int ageOfNow(Date birthDay) {
        return age(birthDay, new Date());
    }

    /**
     * 计算相对于dateToCompare的年龄，长用于计算指定生日在某年的年龄
     * @param birthDay 生日
     * @param dateToCompare 需要对比的日期
     * @return 年龄
     * @throws Exception
     */
    public static int age(Date birthDay, Date dateToCompare) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateToCompare);

        if (cal.before(birthDay)) {
            throw new IllegalArgumentException("Birthday is after date " + dateToCompare);
        }

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        cal.setTime(birthDay);
        int age = year - cal.get(Calendar.YEAR);

        int monthBirth = cal.get(Calendar.MONTH);
        if (month == monthBirth) {
            int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
            if (dayOfMonth < dayOfMonthBirth) {
                //如果生日在当月，但是未达到生日当天的日期，年龄减一
                age--;
            }
        } else if (month < monthBirth){
            //如果当前月份未达到生日的月份，年龄计算减一
            age--;
        }

        return age;
    }

}
