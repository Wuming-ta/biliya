package com.jfeat.partner.service;


import com.jfeat.core.BaseService;
import com.jfeat.partner.model.CooperativeStatistic;
import com.jfeat.partner.model.PhysicalSeller;
import com.jfeat.partner.model.Seller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kang on 2017/4/15.
 */
public class CooperativeStatisticService extends BaseService {

    /*
         statisticDate - 统计时间
         startTime~endTime - 此次统计的是 startTime ~ endTime 这个时间段的数据
     */
    public void statistic(Date statisticDate, String startTime, String endTime) throws ParseException {
        CooperativeStatistic cooperativeStatistic = new CooperativeStatistic();
        cooperativeStatistic.setCustomerCount(Seller.dao.getCustomerCount(startTime, endTime));
        cooperativeStatistic.setSellerCount(Seller.dao.getSellerCount(startTime, endTime));
        cooperativeStatistic.setPartnerCount(Seller.dao.getPartnerCount(startTime, endTime));
        cooperativeStatistic.setCrownCount(Seller.dao.getCrownCount(startTime, endTime));
        cooperativeStatistic.setPhysicalCount(PhysicalSeller.dao.getPhysicalSellerCount(startTime, endTime));
        cooperativeStatistic.setStatisticDate(statisticDate);
        cooperativeStatistic.setRealDate(new SimpleDateFormat("yyyy-MM-dd").parse(startTime));
        cooperativeStatistic.save();
    }
}
