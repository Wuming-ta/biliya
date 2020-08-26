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
import com.jfeat.eventlog.service.EventLogService;
import com.jfeat.flash.Flash;
import com.jfeat.identity.model.User;
import com.jfeat.partner.constants.EventLogName;
import com.jfeat.partner.model.*;
import com.jfeat.partner.service.SellerService;
import com.jfeat.ui.model.Widget;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.StrKit;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * Created by jingfei on 2016/3/14.
 */
public class SellerController extends BaseController {

    private SellerService sellerService = Enhancer.enhance(SellerService.class);
    private EventLogService eventLogService = Enhancer.enhance(EventLogService.class);

    @RequiresPermissions("CooperativePartnerApplication.view")
    @Before(Flash.class)
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        String userName = getPara("sellerId");
        Integer sellerId = null;
        if (StrKit.notBlank(userName) && StringUtils.isNumeric(userName)) {
            sellerId = Integer.parseInt(userName);
        }
        String uid = getPara("uid");
        setAttr("sellers", Seller.dao.paginate(pageNumber, pageSize, sellerId, userName, uid));
        setAttr("totalSellerCount", Seller.dao.querySellerCountTotal());
        setAttr("totalCustomerCount", Seller.dao.queryCustomerCountTotal());
        setAttr("totalPartnerCount", Seller.dao.queryPartnerCountTotal());
        setAttr("totalLevel", Seller.dao.queryTotalLevel());
        keepPara();
    }

    @RequiresPermissions("CooperativePartnerApplication.view")
    public void detail() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        int sellerId = getParaToInt("sellerId");
        Seller seller = Seller.dao.findById(sellerId);
        if (seller == null) {
            renderError(404);
            return;
        }
        setAttr("seller", seller);
        setAttr("children", seller.getVerboseChildren());
        setAttr("level", sellerService.queryLevelCount(sellerId));
        setAttr("physicalSeller", PhysicalSeller.dao.findBySellerId(sellerId));
        setAttr("alliance", Alliance.dao.findByUserId(seller.getUserId()));
        setAttr("copartner", Copartner.dao.findBySellerId(sellerId));
    }

    /**
     * 设置用户为分销商
     */
    @RequiresPermissions("CooperativePartnerApplication.edit")
    public void assign() {
        String returnUrl = getPara("returnUrl", "/seller");
        int sellerId = getParaToInt("sellerId");
        Seller seller = Seller.dao.findById(sellerId);
        if (seller == null) {
            renderError(404);
            return;
        }
        if (sellerService.assignSellerRight(sellerId)) {
            String eventType = getRes().get(EventLogName.COOPERATIVE_PARTNER_EVENT_TYPE_KEY);
            String eventName = getRes().get(EventLogName.COOPERATIVE_PARTNER_ASSIGN_SELLER_KEY);
            User currentUser = getAttr("currentUser");
            String ip = getRequest().getRemoteAddr();
            String userAgent = getRequest().getHeader("User-Agent");
            eventLogService.record(eventType, eventName, currentUser.getName(), ip, userAgent, seller.toJson());
        }
        redirect(returnUrl);
    }

    @RequiresPermissions("CooperativePartnerApplication.view")
    public void widget() {
        Long customerTotal = Seller.dao.queryCustomerCountTotal();
        Long sellerTotal = Seller.dao.querySellerCountTotal();
        Long partnerTotal = Seller.dao.queryPartnerCountTotal();
        Long agentTotal = Agent.dao.queryAgentCountTotal();
        GsonOption option = new GsonOption();
        //option.title(getRes().get("partner.percentage"));
        option.grid().left("3%").right("4%").bottom("3%").containLabel(true);
        option.tooltip().trigger(Trigger.axis).axisPointer(new AxisPointer().type(PointerType.shadow));
        Axis xAxis = new ValueAxis();
        xAxis.boundaryGap(0, 0.1);
        option.xAxis(xAxis);
        Axis yAxis = new CategoryAxis();
        yAxis.data(
                getRes().get("partner.customer"),
                getRes().get("partner.agent"),
                getRes().get("partner.seller"),
                getRes().get("partner.partner"),
                getRes().get("partner.total")
        );
        option.yAxis(yAxis);
        Series series = new Bar();
        series.data(
                customerTotal, agentTotal, sellerTotal, partnerTotal, sellerTotal + customerTotal
        );
        option.series(series);
        setAttr("option", option);
        Widget widget = Widget.dao.findFirstByField(Widget.Fields.NAME.toString(), "partner.overview");
        setAttr("partnerWidgetName", widget.getName());
        setAttr("partnerWidgetDisplayName", widget.getDisplayName());

    }

}
