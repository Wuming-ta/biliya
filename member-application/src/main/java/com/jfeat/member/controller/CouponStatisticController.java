package com.jfeat.member.controller;

import com.github.abel533.echarts.AxisPointer;
import com.github.abel533.echarts.axis.Axis;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.PointerType;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Bar;
import com.github.abel533.echarts.series.Series;
import com.jfeat.core.BaseController;
import com.jfeat.kit.DateKit;
import com.jfeat.member.model.Coupon;
import com.jfeat.member.model.CouponStatistic;
import com.jfeat.member.service.CouponStatisticService;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kang on 2017/3/13.
 */
public class CouponStatisticController extends BaseController {
    private CouponStatisticService couponStatisticService = new CouponStatisticService();

    @Override
    @RequiresPermissions(value = { "coupon.edit", "coupon_statistic.menu" }, logical = Logical.OR)
    public void index() {
        String startDate = getPara("startDate", DateKit.lastMonth("yyyy-MM-dd")) + " 00:00:00";
        String endDate = getPara("endDate", DateKit.today("yyyy-MM-dd")) + " 23:59:59";
        List<CouponStatistic> couponStatistics = couponStatisticService.queryCouponReal(startDate, endDate);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        GsonOption option = new GsonOption();
        option.grid().left("3%").right("4%").bottom("3%").containLabel(true);
        option.tooltip().trigger(Trigger.axis).axisPointer(new AxisPointer().type(PointerType.shadow));
        Axis xAxis = new CategoryAxis();
        xAxis.data();
        option.xAxis(xAxis);
        Axis yAxis = new ValueAxis();
        option.yAxis(yAxis);

        Series grantCountBar = new Bar();
        grantCountBar.name("发放数量");
        grantCountBar.data();
        grantCountBar.label().normal().show(true);

        Series usedCountBar = new Bar();
        usedCountBar.name("使用数量");
        usedCountBar.data();
        usedCountBar.label().normal().show(true);

        Series overdueCountBar = new Bar();
        overdueCountBar.name("过期数量");
        overdueCountBar.data();
        overdueCountBar.label().normal().show(true);

        Series givenByRegisterContBar = new Bar();
        givenByRegisterContBar.name("注册赠送数量");
        givenByRegisterContBar.data();
        givenByRegisterContBar.label().normal().show(true);

        Series takenByLinkCountBar = new Bar();
        takenByLinkCountBar.name("点链接赠送数量");
        takenByLinkCountBar.data();
        takenByLinkCountBar.label().normal().show(true);

        Series systemGivenCountBar = new Bar();
        systemGivenCountBar.name("系统赠送数量");
        systemGivenCountBar.data();
        systemGivenCountBar.label().normal().show(true);

        int grantCountTotal = 0, usedCountTotal = 0, overdueCountTotal = 0, givenByRegisterCountTotal = 0, takenByLinkCountTotal = 0, systemGivenCountTotal = 0;
        for (CouponStatistic couponStatistic : couponStatistics) {
            xAxis.data(simpleDateFormat.format(couponStatistic.getRealDate()));
            grantCountBar.data(couponStatistic.getGrantCount());
            usedCountBar.data(couponStatistic.getUsedCount());
            overdueCountBar.data(couponStatistic.getOverdueCount());
            givenByRegisterContBar.data(couponStatistic.getGivenByRegisterCount());
            takenByLinkCountBar.data(couponStatistic.getTakenByLinkCount());
            systemGivenCountBar.data(couponStatistic.getSystemGivenCount());

            grantCountTotal += couponStatistic.getGrantCount();
        }
        option.series(grantCountBar);
        option.series(usedCountBar);
        option.series(overdueCountBar);
        option.series(givenByRegisterContBar);
        option.series(takenByLinkCountBar);
        option.series(systemGivenCountBar);

        option.title("发放数量", "总计： " + grantCountTotal);

        setAttr("option", option);

        Map<String, Integer> couponCounts = new HashMap<>();
        for (Coupon.Status s : Coupon.Status.values()) {
            couponCounts.put(s.toString(), Coupon.dao.countCoupon(s.toString()));
        }
        setAttr("couponStatuses", Coupon.Status.values());
        setAttr("couponCounts", couponCounts);
        keepPara();
    }

}
