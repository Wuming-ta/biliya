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
import com.jfeat.identity.api.RegisterController;
import com.jfeat.identity.model.User;
import com.jfeat.partner.constants.EventLogName;
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
 * Created by jackyhuang on 16/8/25.
 */
public class CrownController extends BaseController {

    private SellerService sellerService = Enhancer.enhance(SellerService.class);
    private EventLogService eventLogService = Enhancer.enhance(EventLogService.class);

    @Before(Flash.class)
    @RequiresPermissions("CooperativePartnerApplication.view")
    public void index() {
        String userName = getPara("sellerId");
        Integer sellerId = null;
        if (StrKit.notBlank(userName) && StringUtils.isNumeric(userName)) {
            sellerId = Integer.parseInt(userName);
        }
        String uid = getPara("uid");
        setAttr("sellers", Seller.dao.findAllCrown(sellerId, userName, uid));
        keepPara();
    }

    /**
     * 设置用户为皇冠
     */
    @RequiresPermissions("CooperativePartnerApplication.edit")
    public void assign() {
        String returnUrl = getPara("returnUrl", "/crown");
        int sellerId = getParaToInt("sellerId");
        Seller seller = Seller.dao.findById(sellerId);
        if (seller == null) {
            renderError(404);
            return;
        }
        Ret ret = sellerService.assignCrownRight(sellerId);
        if (BaseService.isSucceed(ret)) {
            setFlash("message", getRes().get("partner.crown.create.success"));
            Map<String, Object> data = new HashMap<>();
            data.put("seller", seller);
            recordEvent(EventLogName.COOPERATIVE_PARTNER_CROWNSHIP_ASSIGN_KEY, data);
        }

        redirect(returnUrl);
    }

    @RequiresPermissions("CooperativePartnerApplication.delete")
    @Before(PwdValidator.class)
    public void delete() {
        Integer sellerId = getParaToInt("id");
        Ret ret = sellerService.resetCrownRight(sellerId);
        if (BaseService.isSucceed(ret)) {
            setFlash("message", getRes().get("partner.crown.delete.success"));
            Map<String, Object> data = new HashMap<>();
            data.put("sellerId", sellerId);
            recordEvent(EventLogName.COOPERATIVE_PARTNER_CROWNSHIP_DELETE_KEY, data);
        }
        redirect("/crown");
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
