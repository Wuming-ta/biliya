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

package com.jfeat.order.controller;

import com.github.abel533.echarts.AxisPointer;
import com.github.abel533.echarts.axis.Axis;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.PointerType;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Bar;
import com.github.abel533.echarts.series.Series;
import com.google.common.collect.Lists;
import com.jfeat.core.BaseController;
import com.jfeat.kit.DateKit;
import com.jfeat.order.model.OrderStatistic;
import com.jfeat.order.service.StatisticService;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by jackyhuang on 16/9/3.
 */
public class OrderStatisticController extends BaseController {

    private StatisticService statisticService = new StatisticService();

    @Override
    @RequiresPermissions("order.view")
    public void index() {
        String startDate = getPara("startDate", DateKit.lastMonth("yyyy-MM-dd 00:00:00"));
        String endDate = getPara("endDate", DateKit.today("yyyy-MM-dd 23:59:59"));
        List<OrderStatistic> orderStatistics = statisticService.queryOrderStatistic(startDate, endDate);

        GsonOption option = new GsonOption();
        option.grid().left("3%").right("4%").bottom("3%").containLabel(true);
        option.tooltip().trigger(Trigger.axis).axisPointer(new AxisPointer().type(PointerType.shadow));
        Axis xAxis = new CategoryAxis();
        xAxis.data();
        option.xAxis(xAxis);
        Axis yAxis = new ValueAxis();
        option.yAxis(yAxis);
        Series bar = new Bar();
        bar.data();
        bar.label().normal().show(true);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        BigDecimal total = BigDecimal.ZERO;
        for (OrderStatistic orderStatistic : orderStatistics) {
            xAxis.data(simpleDateFormat.format(orderStatistic.getCreatedDate()));
            bar.data(orderStatistic.getSalesAmount());
            total = total.add(orderStatistic.getSalesAmount());
        }
        option.series(bar);
        option.title("销售额", "总计: " + total);

        setAttr("option", option);
    }
}
