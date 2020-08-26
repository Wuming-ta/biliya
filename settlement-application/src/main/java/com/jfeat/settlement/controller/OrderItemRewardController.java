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

package com.jfeat.settlement.controller;

import com.jfeat.core.BaseController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.kit.DateKit;
import com.jfeat.settlement.model.OrderItemReward;
import com.jfeat.settlement.task.CopartnerSellerSettlementJob;
import com.jfeat.settlement.task.PhysicalAgentBonusJob;
import com.jfeat.settlement.task.PhysicalAgentSettlementJob;
import com.jfeat.settlement.task.PhysicalSellerSettlementJob;
import com.jfeat.settlement.util.DateUtil;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.quartz.JobExecutionException;

import java.text.ParseException;

/**
 * Created by jingfei on 2016/3/18.
 */
@Before(CurrentUserInterceptor.class)
public class OrderItemRewardController extends BaseController {

    @Override
    @RequiresPermissions(value = { "SettlementApplication.view", "settlement.orderItemReward.menu" }, logical = Logical.OR)
    public void index() {
        String fistDayOfYear = DateKit.today("yyyy-01-01");
        String fistDayOfSeason = DateUtil.getSeasonStartDate();
        String fistDayOfMonth = DateKit.today("yyyy-MM-01");
        String now = DateKit.today("yyyy-MM-dd");
        setAttr("fistDayOfYear", fistDayOfYear);
        setAttr("fistDayOfSeason", fistDayOfSeason);
        setAttr("fistDayOfMonth", fistDayOfMonth);
        setAttr("now", now);

        String startDate = getPara("startDate", fistDayOfMonth);
        String endDate = getPara("endDate", now);
        String state = getPara("state");
        String type = getPara("type");

        setAttr("startDate", startDate);
        setAttr("endDate", endDate);

        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 150);
        String orderNumber = getPara("orderNumber");
        keepPara();

        Integer userId = null;
        String userName = getPara("id");
        if (StrKit.notBlank(userName) && StringUtils.isNumeric(userName)) {
            userId = Integer.parseInt(userName);
        }

        setAttr("orderItemRewards", OrderItemReward.dao.paginate(pageNumber, pageSize, userId, userName,
                startDate + " 00:00:00", endDate + " 23:59:59", state, type, orderNumber));

        if (userId != null) {
            setAttr("rewardPendingSettlement", OrderItemReward.dao.queryRewardByState(userId, OrderItemReward.State.PENDING_SETTLEMENT));
            setAttr("rewardSettled", OrderItemReward.dao.queryRewardByState(userId, OrderItemReward.State.SETTLED));
        }

        setAttr("states", OrderItemReward.State.values());
        setAttr("types", OrderItemReward.Type.values());

    }

    /**
     * For testing
     */
    @RequiresRoles("admin")
    public void triggerPhysicalSettlement() throws JobExecutionException {
        PhysicalSellerSettlementJob job = new PhysicalSellerSettlementJob();
        String month = getPara("month", DateKit.currentMonth("yyyy-MM-01"));
        job.execute(month);
        renderText("Running");
    }

    @RequiresRoles("admin")
    public void triggerPhysicalAgentSettlement() throws ParseException {
        PhysicalAgentSettlementJob job = new PhysicalAgentSettlementJob();
        String month = getPara("month", DateKit.currentMonth("yyyy-MM-01"));
        job.execute(month);
        renderText("Running");
    }

    @RequiresRoles("admin")
    public void triggerPhysicalAgentBonus() throws ParseException {
        PhysicalAgentBonusJob job = new PhysicalAgentBonusJob();
        job.execute();
        renderText("Running");
    }

    @RequiresRoles("admin")
    public void triggerCopartnerSettlement() throws ParseException {
        CopartnerSellerSettlementJob job = new CopartnerSellerSettlementJob();
        String month = getPara("month", DateKit.lastMonth("yyyy-MM"));
        job.execute(month);
        renderText("Running");
    }
}
