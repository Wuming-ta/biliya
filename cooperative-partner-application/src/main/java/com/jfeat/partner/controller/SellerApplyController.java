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

import com.google.common.collect.Lists;
import com.jfeat.core.BaseController;
import com.jfeat.core.BaseService;
import com.jfeat.eventlog.service.EventLogService;
import com.jfeat.flash.Flash;
import com.jfeat.identity.model.User;
import com.jfeat.partner.constants.EventLogName;
import com.jfeat.partner.model.Apply;
import com.jfeat.partner.model.Seller;
import com.jfeat.partner.service.CopartnerService;
import com.jfeat.partner.service.PhysicalSellerService;
import com.jfeat.partner.service.SellerService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.plugin.shiro.ShiroMethod;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jingfei on 2016/4/6.
 */
public class SellerApplyController extends BaseController {

    private CopartnerService copartnerService = Enhancer.enhance(CopartnerService.class);
    private SellerService sellerService = Enhancer.enhance(SellerService.class);
    private PhysicalSellerService physicalSellerService = Enhancer.enhance(PhysicalSellerService.class);
    private EventLogService eventLogService = Enhancer.enhance(EventLogService.class);

    @RequiresPermissions(value = {"physical.seller.view", "CooperativePartnerApplication.view"}, logical = Logical.OR)
    @Before(Flash.class)
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        String uid = getPara("uid");
        String type = getPara("type");
        String status = getPara("status");
        setAttr("apply", Apply.dao.paginate(pageNumber, pageSize, uid, type, status));
        keepPara();
    }

    @Before(Tx.class)
    @RequiresPermissions(value = {"physical.seller.edit", "CooperativePartnerApplication.edit"}, logical = Logical.OR)
    public void approve() {
        Integer id = getParaToInt();
        Apply apply = Apply.dao.findById(id);
        if (apply == null) {
            redirect("/seller_apply");
            return;
        }

        Ret ret = Ret.create();
        if (ShiroMethod.hasPermission("CooperativePartnerApplication.edit")) {
            if (apply.getType().equals(Apply.Type.SELLER.toString())) {
                ret = sellerService.approveSellerApply(id);
            }
        }
        if (ShiroMethod.hasPermission("physical.seller.edit")) {
            if (apply.getType().equals(Apply.Type.PHYSICAL.toString())) {
                ret = physicalSellerService.approvePhysicalSellerShip(id);
            }
            if (apply.getType().equals(Apply.Type.CROWN.toString())) {
                ret = physicalSellerService.approveTempCrownShip(id);
            }
            if (apply.getType().equalsIgnoreCase(Apply.Type.COPARTNER.toString())) {
                ret = copartnerService.agree(id);
            }
        }


        logger.debug("ret = {}", ret.getData());
        if (BaseService.isSucceed(ret)) {
            setFlash("message", getRes().get(BaseService.getMessage(ret)));
            Map<String, Object> data = new HashMap<>();
            data.put("apply", apply);
            recordEvent(EventLogName.COOPERATIVE_PARTNER_SELLER_APPLY_APPROVE_KEY, data);
        }

        redirect("/seller_apply");
    }

    @RequiresPermissions(value = {"physical.seller.edit", "CooperativePartnerApplication.edit"}, logical = Logical.OR)
    public void reject() {
        Integer id = getParaToInt();
        Apply apply = Apply.dao.findById(id);
        if (apply == null) {
            redirect("/seller_apply");
            return;
        }

        Ret ret = Ret.create();
        if (ShiroMethod.hasPermission("CooperativePartnerApplication.edit")) {
            if (apply.getType().equals(Apply.Type.SELLER.toString())) {
                ret = sellerService.rejectSellerApply(id);
            }
        }
        if (ShiroMethod.hasPermission("physical.seller.edit")) {
            if (apply.getType().equals(Apply.Type.PHYSICAL.toString())) {
                ret = physicalSellerService.rejectPhysicalSellerShip(id);
            }
            if (apply.getType().equals(Apply.Type.CROWN.toString())) {
                ret = physicalSellerService.rejectPhysicalCrownShip(id);
            }
            if (apply.getType().equalsIgnoreCase(Apply.Type.COPARTNER.toString())) {
                ret = copartnerService.reject(id);
            }
        }

        logger.debug("ret = {}", ret.getData());
        if (BaseService.isSucceed(ret)) {
            setFlash("message", getRes().get(BaseService.getMessage(ret)));
            Map<String, Object> data = new HashMap<>();
            data.put("apply", apply);
            recordEvent(EventLogName.COOPERATIVE_PARTNER_SELLER_APPLY_REJECT_KEY, data);
        }
        redirect("/seller_apply");
    }

    @Before(Tx.class)
    @RequiresPermissions(value = {"physical.seller.edit", "CooperativePartnerApplication.edit"}, logical = Logical.OR)
    public void batchApprove() {
        List<Apply> applies = getModels(Apply.class);
        List<Apply> approvedList = Lists.newArrayList();
        for (Apply applyEach : applies) {
            applyEach = Apply.dao.findById(applyEach.getId());
            Ret ret = Ret.create();
            if (ShiroMethod.hasPermission("CooperativePartnerApplication.edit")) {
                if (applyEach.getType().equals(Apply.Type.SELLER.toString())) {
                    ret = sellerService.approveSellerApply(applyEach.getId());
                }
            }
            if (ShiroMethod.hasPermission("physical.seller.edit")) {
                if (applyEach.getType().equals(Apply.Type.PHYSICAL.toString())) {
                    ret = physicalSellerService.approvePhysicalSellerShip(applyEach.getId());
                }
                if (applyEach.getType().equals(Apply.Type.CROWN.toString())) {
                    ret = physicalSellerService.approveTempCrownShip(applyEach.getId());
                }
            }
            if (BaseService.isSucceed(ret)) {
                approvedList.add(applyEach);
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("applyList", approvedList);
        recordEvent(EventLogName.COOPERATIVE_PARTNER_SELLER_APPLY_APPROVE_KEY, data);

        setFlash("message", getRes().get("partner.seller.approve.success"));
        redirect("/seller_apply");
    }

    @Before(Tx.class)
    @RequiresPermissions(value = {"physical.seller.edit", "CooperativePartnerApplication.edit"}, logical = Logical.OR)
    public void batchReject() {
        List<Apply> applies = getModels(Apply.class);
        List<Apply> rejectList = Lists.newArrayList();
        for (Apply applyEach : applies) {
            applyEach = Apply.dao.findById(applyEach.getId());
            Ret ret = Ret.create();
            if (ShiroMethod.hasPermission("CooperativePartnerApplication.edit")) {
                if (applyEach.getType().equals(Apply.Type.SELLER.toString())) {
                    ret = sellerService.rejectSellerApply(applyEach.getId());
                }
            }
            if (ShiroMethod.hasPermission("physical.seller.edit")) {
                if (applyEach.getType().equals(Apply.Type.PHYSICAL.toString())) {
                    ret = physicalSellerService.rejectPhysicalSellerShip(applyEach.getId());
                }
                if (applyEach.getType().equals(Apply.Type.CROWN.toString())) {
                    ret = physicalSellerService.rejectPhysicalCrownShip(applyEach.getId());
                }
            }
            if (BaseService.isSucceed(ret)) {
                rejectList.add(applyEach);
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("applyList", rejectList);
        recordEvent(EventLogName.COOPERATIVE_PARTNER_SELLER_APPLY_REJECT_KEY, data);

        setFlash("message", getRes().get("partner.seller.reject.success"));
        redirect("/seller_apply");
    }

    @RequiresPermissions(value = {"physical.seller.view", "CooperativePartnerApplication.view"}, logical = Logical.OR)
    public void applyCount() {
        long count = Apply.dao.countInitApplies();
        String result = count == 0 ? "" : String.valueOf(count);
        renderText(result);
    }

    /**
     * ajax view apply property
     */
    @RequiresPermissions(value = {"physical.seller.view", "CooperativePartnerApplication.view"}, logical = Logical.OR)
    public void view() throws Exception {
        Apply apply = Apply.dao.findById(getParaToInt());
        if (apply.getType().equalsIgnoreCase(Apply.Type.PHYSICAL.toString())) {
            Seller seller = physicalSellerService.getApplyingPhysicalSeller(apply);
            Seller parent = physicalSellerService.getApplyingPhysicalParentSeller(apply);
            setAttr("seller", seller);
            setAttr("parent", parent);
        }
        setAttr("props", com.jfeat.kit.JsonKit.convertToMap(apply.getProperties()));
        render("_view.html");
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
