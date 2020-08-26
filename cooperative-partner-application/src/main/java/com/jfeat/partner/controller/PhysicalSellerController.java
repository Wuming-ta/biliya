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

import com.github.abel533.echarts.axis.Axis;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Line;
import com.github.abel533.echarts.series.Series;
import com.google.common.collect.Lists;
import com.jfeat.core.BaseController;
import com.jfeat.core.BaseService;
import com.jfeat.flash.Flash;
import com.jfeat.identity.model.User;
import com.jfeat.partner.model.*;
import com.jfeat.partner.service.PhysicalSellerService;
import com.jfeat.validator.PwdValidator;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by jackyhuang on 17/1/10.
 */
public class PhysicalSellerController extends BaseController {

    private PhysicalSellerService physicalSellerService = Enhancer.enhance(PhysicalSellerService.class);

    @RequiresPermissions("physical.seller.view")
    @Before(Flash.class)
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        String uid = getPara("uid");
        String realName = getPara("real_name");
        Integer pqId = getParaToInt("provinceQualify");
        Integer cqId = getParaToInt("cityQualify");
        Integer dqId = getParaToInt("districtQualify");
        String province = null, city = null, district = null;
        if (pqId != null) {
            province = PcdQualify.dao.findById(pqId).getPcd().getName();
        }
        if (cqId != null) {
            city = PcdQualify.dao.findById(cqId).getPcd().getName();
        }
        if (dqId != null) {
            district = PcdQualify.dao.findById(dqId).getPcd().getName();
        }
        setAttr("sellers", PhysicalSeller.dao.paginate(pageNumber, pageSize, uid, realName, province, city, district));
        keepPara();
    }

    @RequiresPermissions("physical.seller.view")
    @Before(Flash.class)
    public void detail() {
        PhysicalSeller physicalSeller = PhysicalSeller.dao.findById(getParaToInt("id"));
        setAttr("physicalSeller", physicalSeller);
    }

    @RequiresPermissions("physical.seller.edit")
    public void save() {
        Integer sellerId = getParaToInt("sellerId");
        String uid = getPara("uid");
        String returnUrl = getPara("returnUrl", "/physical_seller");
        PhysicalSeller parentPhysicalSeller = PhysicalSeller.dao.findBySellerId(sellerId);
        if (parentPhysicalSeller == null) {
            setFlash("message", getRes().get("partner.physical_seller.add.failure.not.physical"));
            redirect(returnUrl);
            return;
        }

        User user = User.dao.findByUid(uid);
        if (user == null) {
            setFlash("message", getRes().get("partner.physical_seller.add.failure.invalid.uid"));
            redirect(returnUrl);
            return;
        }

        Seller seller = Seller.dao.findByUserId(user.getId());
        PhysicalSeller physicalSeller = PhysicalSeller.dao.findBySellerId(seller.getId());
        if (physicalSeller != null) {
            setFlash("message", getRes().get("partner.physical_seller.add.failure.already.physical"));
            redirect(returnUrl);
            return;
        }

        if (seller.getId().equals(sellerId)) {
            setFlash("message", getRes().get("partner.physical_seller.add.failure.cannot.add.self"));
            redirect(returnUrl);
            return;
        }

        Ret ret = physicalSellerService.applyPhysicalSellerShip(user.getId(), seller.getId(), sellerId);
        if (BaseService.isSucceed(ret)) {
            Integer applyId = (Integer) ret.getData().get("apply_id");
            ret = physicalSellerService.approvePhysicalSellerShip(applyId);
            logger.debug("add child physical seller ret = {}", ret.getData());
        } else {
            setFlash("message", getRes().get("partner.physical_seller.add.failure"));
            redirect(returnUrl);
            return;
        }

        setFlash("message", getRes().get("partner.physical_seller.add.success"));
        redirect(returnUrl);
    }

    @RequiresPermissions("physical.seller.edit")
    public void assign() {
        Integer sellerId = getParaToInt("sellerId");
        String returnUrl = getPara("returnUrl", "/physical_seller");
        physicalSellerService.createPhysicalSeller(sellerId, null, null, null, null);
        redirect(returnUrl);
    }

    @RequiresPermissions("physical.seller.delete")
    @Before(PwdValidator.class)
    public void delete() {
        Integer sellerId = getParaToInt("id");
        String returnUrl = getPara("returnUrl", "/physical_seller");
        physicalSellerService.deletePhysicalSeller(sellerId);
        redirect(returnUrl);
    }

    ////////////////////////////////purchase journal////////////////////////////////////////
    @RequiresPermissions("physical.seller.view")
    public void purchaseJournalList() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        String startDate = getPara("startDate");
        String endDate = getPara("endDate");
        if (StrKit.notBlank(startDate)) {
            startDate += " 00:00:00";
        }
        if (StrKit.notBlank(endDate)) {
            endDate += " 23:59:59";
        }
        String uid = getPara("uid");
        Integer sellerId = null;
        if (StrKit.notBlank(uid)) {
            User user = User.dao.findByUid(uid);
            if (user != null) {
                sellerId = Seller.dao.findByUserId(user.getId()).getId();
            }
        }
        setAttr("historicalPurchaseJournal", PhysicalPurchaseJournal.dao.pagination(pageNumber, pageSize, startDate, endDate, sellerId));
        render("purchase_journal_list.html");
        keepPara();
    }

    @RequiresPermissions("physical.seller.edit")
    @Before(Flash.class)
    public void addPurchaseJournal() {
        Seller seller = Seller.dao.findById(getParaToInt());
        User user = seller.getUser();
        setAttr("seller", seller);
        setAttr("user", user);
        setAttr("historicalPurchaseJournal", PhysicalPurchaseJournal.dao.pagination(1, 50, null, null, seller.getId()).getList());
        render("purchase_journal_add.html");
    }

    @RequiresPermissions("physical.seller.edit")
    public void savePurchaseJournal() {
        String returnUrl = getPara("returnUrl", "/physical_seller");
        PhysicalPurchaseJournal physicalPurchaseJournal = getModel(PhysicalPurchaseJournal.class);
        if (physicalPurchaseJournal.getCreatedDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(physicalPurchaseJournal.getCreatedDate());
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            calendar = Calendar.getInstance();
            int currentMonth = calendar.get(Calendar.MONTH);
            int currentYear = calendar.get(Calendar.YEAR);
            if (month != currentMonth || year != currentYear) {
                setFlash("message", getRes().get("partner.physical_seller.purchase_journal.invalid.time"));
                redirect(returnUrl);
                return;
            }
        }
        if (physicalPurchaseJournal.getCreatedDate() == null) {
            physicalPurchaseJournal.setCreatedDate(new Date());
        }

        boolean result = false;
        PhysicalSeller physicalSeller = PhysicalSeller.dao.findBySellerId(physicalPurchaseJournal.getSellerId());
        try {
            List<Integer> orderItemIds = Lists.newLinkedList();
            orderItemIds.add(0);
            List<BigDecimal> amounts = Lists.newLinkedList();
            amounts.add(physicalPurchaseJournal.getAmount());
            List<Integer> settlementProportions = Lists.newLinkedList();
            settlementProportions.add(0);
            List<BigDecimal> expectedRewards = Lists.newLinkedList();
            expectedRewards.add(BigDecimal.ZERO);
            List<String> productNames = Lists.newLinkedList();
            productNames.add("-");

            result = physicalSellerService.updatePurchase(physicalSeller,
                    0,
                    "-",
                    orderItemIds,
                    productNames,
                    amounts,
                    settlementProportions,
                    expectedRewards,
                    physicalPurchaseJournal.getNote());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (result) {
            setFlash("message", getRes().get("partner.physical_seller.purchase_journal.add_success"));
        } else {
            setFlash("message", getRes().get("partner.physical_seller.purchase_journal.add_failure"));
        }

        redirect(returnUrl);
    }


    ////////////////////////////////purchase summary////////////////////////////////////////
    @RequiresPermissions("physical.seller.view")
    public void purchaseSummaryList() {
        String uid = getPara("uid");
        if (StrKit.isBlank(uid)) {
            renderError(404);
            return;
        }
        User user = User.dao.findByUid(uid);
        if (user == null) {
            renderError(404);
            return;
        }

        Seller seller = Seller.dao.findByUserId(user.getId());
        PhysicalSeller physicalSeller = PhysicalSeller.dao.findBySellerId(seller.getId());
        setAttr("seller", seller);
        setAttr("user", user);
        setAttr("physicalSeller", physicalSeller);

        List<PhysicalPurchaseSummary> purchaseSummaryList = PhysicalPurchaseSummary.dao.findBySellerId(seller.getId());
        setAttr("purchaseSummaryList", purchaseSummaryList);

        GsonOption option = new GsonOption();
        option.grid().left("3%").right("4%").bottom("3%").containLabel(true);
        option.tooltip().trigger(Trigger.item).formatter("{a} <br/>{b}: {c} 元");
        option.legend().data("月入货额", "月提成额").left();

        Axis xAxis = new CategoryAxis();
        xAxis.name("统计月份");
        xAxis.data();
        xAxis.splitLine().show(true);
        option.xAxis(xAxis);

        Axis yAxis = new ValueAxis();
        yAxis.name("元");
        option.yAxis(yAxis);

        Series monthlyAmountLine = new Line();
        monthlyAmountLine.name("月入货额");
        monthlyAmountLine.data();

        Series monthlySettledAmountLine = new Line();
        monthlySettledAmountLine.name("月提成额");
        monthlySettledAmountLine.data();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        for (PhysicalPurchaseSummary summary : purchaseSummaryList) {
            xAxis.data(simpleDateFormat.format(summary.getStatisticMonth()));
            monthlyAmountLine.data(summary.getMonthlyAmount());
            monthlySettledAmountLine.data(summary.getMonthlySettledAmount());
        }
        option.series(monthlyAmountLine, monthlySettledAmountLine);

        setAttr("option", option);

        render("purchase_summary_list.html");
        keepPara();
    }

    @RequiresPermissions("physical.seller.view")
    public void agentSummaryList() {
        String uid = getPara("uid");
        if (StrKit.isBlank(uid)) {
            renderError(404);
            return;
        }
        User user = User.dao.findByUid(uid);
        if (user == null) {
            renderError(404);
            return;
        }

        Seller seller = Seller.dao.findByUserId(user.getId());
        PhysicalSeller physicalSeller = PhysicalSeller.dao.findBySellerId(seller.getId());
        setAttr("seller", seller);
        setAttr("user", user);
        setAttr("physicalSeller", physicalSeller);

        List<AgentSummary> agentSummaryList = AgentSummary.dao.findBySellerId(seller.getId());
        setAttr("agentSummaryList", agentSummaryList);

        GsonOption option = new GsonOption();
        option.grid().left("3%").right("4%").bottom("3%").containLabel(true);
        option.tooltip().trigger(Trigger.item).formatter("{a} <br/>{b}: {c} 元");
        option.legend().data("入货额", "提成额").left();

        Axis xAxis = new CategoryAxis();
        xAxis.name("统计月份");
        xAxis.data();
        xAxis.splitLine().show(true);
        option.xAxis(xAxis);

        Axis yAxis = new ValueAxis();
        yAxis.name("元");
        option.yAxis(yAxis);

        Series amountLine = new Line();
        amountLine.name("入货额");
        amountLine.data();

        Series settledAmountLine = new Line();
        settledAmountLine.name("提成额");
        settledAmountLine.data();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        for (AgentSummary summary : agentSummaryList) {
            xAxis.data(simpleDateFormat.format(summary.getStatisticMonth()));
            amountLine.data(summary.getAmount());
            settledAmountLine.data(summary.getSettledAmount());
        }
        option.series(amountLine, settledAmountLine);

        setAttr("option", option);

        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 10);
        Page<AgentPurchaseJournal> agentPurchaseJournals = AgentPurchaseJournal.dao.paginateBySellerId(pageNumber, pageSize, seller.getId());
        setAttr("agentPurchaseJournals", agentPurchaseJournals);
        render("agent_summary_list.html");
        keepPara();
    }

    @Before(Flash.class)
    public void physicalApplyTips() {
        setAttr("physicalCrownTips", PhysicalApplyTips.dao.findFirstByField(PhysicalApplyTips.Fields.TYPE.toString(), PhysicalApplyTips.Type.CROWN.toString()));
        setAttr("physicalStarTips", PhysicalApplyTips.dao.findFirstByField(PhysicalApplyTips.Fields.TYPE.toString(), PhysicalApplyTips.Type.STAR.toString()));
        setAttr("announceTips", PhysicalApplyTips.dao.findFirstByField(PhysicalApplyTips.Fields.TYPE.toString(), PhysicalApplyTips.Type.ANNOUNCE.toString()));
    }

    public void updatePhysicalCrownTips() {
        PhysicalApplyTips tips = getModel(PhysicalApplyTips.class);
        tips.update();
        setFlash("message", getRes().get("partner.physical_seller.physical_crown_tips.update.success"));
        redirect("/physical_seller/physicalApplyTips");
    }

    public void updatePhysicalStarTips() {
        PhysicalApplyTips tips = getModel(PhysicalApplyTips.class);
        tips.update();
        setFlash("message", getRes().get("partner.physical_seller.physical_star_tips.update.success"));
        redirect("/physical_seller/physicalApplyTips");
    }

    public void updateAnnounceTips() {
        PhysicalApplyTips tips = getModel(PhysicalApplyTips.class);
        tips.update();
        setFlash("message", getRes().get("partner.physical_seller.physical_announce_tips.update.success"));
        redirect("/physical_seller/physicalApplyTips");
    }

}
