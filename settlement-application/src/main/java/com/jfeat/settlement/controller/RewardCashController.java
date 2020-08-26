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
import com.jfeat.core.BaseService;
import com.jfeat.flash.Flash;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.kit.DateKit;
import com.jfeat.settlement.model.RewardCash;
import com.jfeat.settlement.service.WithdrawService;
import com.jfeat.settlement.util.DateUtil;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by jingfei on 2016/3/22.
 */
@Before(CurrentUserInterceptor.class)
public class RewardCashController extends BaseController {

    private WithdrawService withdrawService = Enhancer.enhance(WithdrawService.class);

    @Override
    @RequiresPermissions(value = { "SettlementApplication.view", "settlement.rewardCash.menu" }, logical = Logical.OR)
    @Before(Flash.class)
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
        String status = getPara("status");
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        keepPara();
        setAttr("startDate", startDate);
        setAttr("endDate", endDate);

        startDate += " 00:00:00";
        endDate += " 23:59:59";

        String userName = getPara("id");
        Integer userId = null;
        if (StrKit.notBlank(userName) && StringUtils.isNumeric(userName)) {
            userId = Integer.parseInt(userName);
            userName = null;
        }
        setAttr("rewardCashs", RewardCash.dao.queryRewardHistorical(pageNumber, pageSize, startDate, endDate, userId, userName, status));
        setAttr("statuses", RewardCash.Status.values());
    }

    @RequiresPermissions(value = { "SettlementApplication.view", "settlement.rewardCash.menu" }, logical = Logical.OR)
    public void detail() {
        RewardCash rewardCash = RewardCash.dao.findById(getParaToInt());
        setAttr("rewardCash", rewardCash);
    }

    @RequiresPermissions("SettlementApplication.edit")
    public void agree() {
        withdrawService.agree(getParaToInt());
        redirect("/reward_cash");
    }

    @RequiresPermissions("SettlementApplication.edit")
    public void reject() {
        withdrawService.reject(getParaToInt("id"), getPara("reason",null));
        redirect("/reward_cash");
    }

    @RequiresPermissions("SettlementApplication.edit")
    public void complete() {
        String ip = getRealIp(this.getRequest());
        Ret ret = withdrawService.complete(getParaToInt(), ip);
        if (!BaseService.isSucceed(ret)) {
            setFlash("message", BaseService.getMessage(ret));
        }
        redirect("/reward_cash");
    }

    /**
     * notification
     */
    public void countApplying() {
        long count = RewardCash.dao.countApplying();
        String result = count == 0 ? "" : String.valueOf(count);
        renderText(result);
    }

    private static String getRealIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.indexOf(",") != -1) {
            ip = ip.substring(ip.lastIndexOf(",") + 1, ip.length()).trim();
        }
        if (StrKit.isBlank(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }

}
