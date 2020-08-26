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

import com.jfeat.core.BaseController;
import com.jfeat.core.BaseService;
import com.jfeat.eventlog.service.EventLogService;
import com.jfeat.flash.Flash;
import com.jfeat.identity.model.User;
import com.jfeat.partner.constants.EventLogName;
import com.jfeat.partner.model.PartnerLevel;
import com.jfeat.partner.model.Seller;
import com.jfeat.partner.service.SellerService;
import com.jfeat.validator.PwdValidator;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jingfei on 2016/3/28.
 */
public class PartnerController extends BaseController {

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
        setAttr("partnerLevels", PartnerLevel.dao.findAll());
        setAttr("sellers", Seller.dao.paginatePartner(pageNumber, pageSize, sellerId, userName, uid));
        setAttr("totalPartnerCount", Seller.dao.queryPartnerCountTotal());
        for (PartnerLevel level : PartnerLevel.dao.findAll()) {
            Seller.dao.queryPartnerCountTotalByPartnerLevelId(level.getId());
        }
        keepPara();
    }

    @Before(PwdValidator.class)
    @RequiresPermissions("CooperativePartnerApplication.delete")
    public void delete() {
        Integer sellerId = getParaToInt("id");
        Ret ret = sellerService.resetPartnerRight(sellerId);
        if (BaseService.isSucceed(ret)) {
            setFlash("message", getRes().get("partner.partner.delete.success"));
            Map<String, Object> data = new HashMap<>();
            data.put("sellerId", sellerId);
            recordEvent(EventLogName.COOPERATIVE_PARTNER_PARTNERSHIP_DELETE_KEY, data);
        }
        redirect("/partner");
    }

    /**
     * 更新合伙人级别
     */
    @RequiresPermissions("CooperativePartnerApplication.edit")
    public void updateLevel() {
        String returnUrl = getPara("returnUrl", "/partner");
        Integer sellerId = getParaToInt("sellerId");
        Integer level = getParaToInt("level");
        Seller seller = Seller.dao.findById(sellerId);
        if (seller == null) {
            renderError(404);
            return;
        }
        if (sellerService.updatePartnerLevel(sellerId, level)) {
            setFlash("message", getRes().get("partner.partner.update.success"));
            Map<String, Object> data = new HashMap<>();
            data.put("seller", seller);
            data.put("level", level);
            recordEvent(EventLogName.COOPERATIVE_PARTNER_PARTNERSHIP_ASSIGN_KEY, data);
        }

        redirect(returnUrl);
    }

    /**
     * 设置用户为合伙人
     */
    @RequiresPermissions("CooperativePartnerApplication.edit")
    public void assign() {
        String returnUrl = getPara("returnUrl", "/partner");
        int sellerId = getParaToInt("sellerId");
        Seller seller = Seller.dao.findById(sellerId);
        if (seller == null) {
            renderError(404);
            return;
        }
        Ret ret = sellerService.assignPartnerRight(sellerId);
        if (BaseService.isSucceed(ret)) {
            setFlash("message", getRes().get("partner.partner.create.success"));
            Map<String, Object> data = new HashMap<>();
            data.put("seller", seller);
            recordEvent(EventLogName.COOPERATIVE_PARTNER_PARTNERSHIP_ASSIGN_KEY, data);
        }

        redirect(returnUrl);
    }

    private void recordEvent(String eventNameKey, Map<String, Object> data) {
        String eventType = getRes().get(EventLogName.COOPERATIVE_PARTNER_EVENT_TYPE_KEY);
        String eventName = getRes().get(eventNameKey);
        User currentUser = getAttr("currentUser");
        String ip = getRequest().getRemoteAddr();
        String userAgent = getRequest().getHeader("User-Agent");
        eventLogService.record(eventType, eventName, currentUser.getName(), ip, userAgent, data);
    }
}
