package com.jfeat.member.service;

import com.jfeat.core.BaseService;
import com.jfeat.kit.DateKit;
import com.jfeat.kit.JsonKit;
import com.jfeat.member.model.Coupon;
import com.jfeat.member.model.CouponStatistic;
import com.jfinal.kit.StrKit;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by kang on 2017/3/14.
 */
public class CouponStatisticService extends BaseService {
    public void statistic(String startDate, String endDate) throws Exception {
        List<Coupon> couponList = Coupon.dao.findBetween(startDate, endDate);
        int grantCount = 0;
        int givenByRegisterCount = 0;
        int systemGivenCount = 0;
        int takenByLinkCount = 0;
        int overdueCount = 0;
        int usedCount = 0;
        Date yesterday = DateKit.toDate(DateKit.yesterday("yyyy-MM-dd 00:00:00"));
        for (Coupon coupon : couponList) {
            String attribute = coupon.getAttribute();
            if (StrKit.notBlank(attribute)) {
                Map<String, Object> map = JsonKit.convertToMap(attribute);
                if (map != null && coupon.getCreatedDate().getTime() - yesterday.getTime() >= 0) {
                    grantCount++;
                    if (Coupon.Source.REGISTER.toString().equals(map.get(Coupon.AttributeNames.SOURCE.toString()))) {
                        givenByRegisterCount++;
                    }
                    if (Coupon.Source.SYSTEM.toString().equals(map.get(Coupon.AttributeNames.SOURCE.toString()))) {
                        systemGivenCount++;
                    }
                    if (Coupon.Source.LINK.toString().equals(map.get(Coupon.AttributeNames.SOURCE.toString()))) {
                        takenByLinkCount++;
                    }
                }
            }
            if (Coupon.Status.OVERDUE.toString().equals(coupon.getStatus())) {
                overdueCount++;
            }
            if (Coupon.Status.USED.toString().equals(coupon.getStatus())) {
                usedCount++;
            }
        }

        CouponStatistic couponStatistic = new CouponStatistic();
        couponStatistic.setStatisticDate(new Date());
        couponStatistic.setRealDate(DateKit.toDate(DateKit.yesterday("yyyy-MM-dd")));
        couponStatistic.setGrantCount(grantCount);
        couponStatistic.setGivenByRegisterCount(givenByRegisterCount);
        couponStatistic.setSystemGivenCount(systemGivenCount);
        couponStatistic.setTakenByLinkCount(takenByLinkCount);
        couponStatistic.setOverdueCount(overdueCount);
        couponStatistic.setUsedCount(usedCount);
        couponStatistic.save();
    }

    public List<CouponStatistic> queryCouponReal(String fromDate, String toDate) {
        return CouponStatistic.dao.queryReal(fromDate, toDate);
    }
}
