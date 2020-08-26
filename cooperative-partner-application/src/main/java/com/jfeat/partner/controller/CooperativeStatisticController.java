package com.jfeat.partner.controller;


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
import com.jfeat.partner.model.CooperativeStatistic;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.text.SimpleDateFormat;
import java.util.List;


/**
 * Created by kang on 2017/4/15.
 */
public class CooperativeStatisticController extends BaseController {

    @RequiresPermissions("CooperativePartnerApplication.view")
    public void index() {
        //show a diagram
        String startDate = getPara("startDate", DateKit.lastMonth("yyyy-MM-dd")) + " 00:00:00";
        String endDate = getPara("endDate", DateKit.today("yyyy-MM-dd")) + " 23:59:59";
        List<CooperativeStatistic> cooperativeStatistics = CooperativeStatistic.dao.find(startDate, endDate);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        GsonOption option = new GsonOption();
        option.grid().left("3%").right("4%").bottom("3%").containLabel(true);
        option.tooltip().trigger(Trigger.axis).axisPointer(new AxisPointer().type(PointerType.shadow));
        Axis xAxis = new CategoryAxis();
        xAxis.data();
        option.xAxis(xAxis);
        Axis yAxis = new ValueAxis();
        option.yAxis(yAxis);

        Series customerCountBar = new Bar();
        customerCountBar.name(getRes().get("cooperative_statistic.customer_count"));
        customerCountBar.data();
        customerCountBar.label().normal().show(true);

        Series sellerCountBar = new Bar();
        sellerCountBar.name(getRes().get("cooperative_statistic.seller_count"));
        sellerCountBar.data();
        sellerCountBar.label().normal().show(true);

        Series partnerCountBar = new Bar();
        partnerCountBar.name(getRes().get("cooperative_statistic.partner_count"));
        partnerCountBar.data();
        partnerCountBar.label().normal().show(true);

        Series crownCountBar = new Bar();
        crownCountBar.name(getRes().get("cooperative_statistic.crown_count"));
        crownCountBar.data();
        crownCountBar.label().normal().show(true);

        Series physicalSellerCountBar = new Bar();
        physicalSellerCountBar.name(getRes().get("cooperative_statistic.physical_count"));
        physicalSellerCountBar.data();
        physicalSellerCountBar.label().normal().show(true);

        for (CooperativeStatistic cooperativeStatistic : cooperativeStatistics) {
            xAxis.data(simpleDateFormat.format(cooperativeStatistic.getRealDate()));
            customerCountBar.data(cooperativeStatistic.getCustomerCount());
            sellerCountBar.data(cooperativeStatistic.getSellerCount());
            partnerCountBar.data(cooperativeStatistic.getPartnerCount());
            crownCountBar.data(cooperativeStatistic.getCrownCount());
            physicalSellerCountBar.data(cooperativeStatistic.getPhysicalCount());
        }
        option.series(customerCountBar);
        option.series(sellerCountBar);
        option.series(partnerCountBar);
        option.series(crownCountBar);
        option.series(physicalSellerCountBar);
        setAttr("option", option);

        //show a table
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        setAttr("cooperativeStatistics", CooperativeStatistic.dao.paginate(pageNumber, pageSize,startDate,endDate));

        keepPara();
    }
}
