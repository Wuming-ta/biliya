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

package com.jfeat.partner.api;

import com.jfeat.config.model.Config;
import com.jfeat.core.BaseService;
import com.jfeat.core.RestController;
import com.jfeat.eventlog.service.EventLogService;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.identity.service.UserService;
import com.jfeat.partner.constants.EventLogName;
import com.jfeat.partner.model.Apply;
import com.jfeat.partner.model.PartnerLevel;
import com.jfeat.partner.model.Seller;
import com.jfeat.partner.service.PhysicalSellerService;
import com.jfeat.partner.service.SellerService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jingfei on 2016/3/31.
 */
@ControllerBind(controllerKey = "/rest/seller")
public class SellerController extends RestController {

    private static String CAN_APPLY_CROWN_KEY = "partner.can_apply_crown";

    private UserService userService = Enhancer.enhance(UserService.class);
    private SellerService sellerService = Enhancer.enhance(SellerService.class);
    private PhysicalSellerService physicalSellerService = Enhancer.enhance(PhysicalSellerService.class);
    private EventLogService eventLogService = Enhancer.enhance(EventLogService.class);

    /**
     * GET /rest/seller
     * 返回当前分销商及其下属分销商
     */
    @Override
    public void index() {
        User currentUser = getAttr("currentUser");
        Integer currentUserId = currentUser.getId();
        Seller seller = Seller.dao.findVerboseByUserId(currentUserId);
        if (seller == null) {
            renderFailure("seller.not.found");
            return;
        }

        seller.put("children", seller.getVerboseChildren());
        seller.put("followed_children_count", Seller.dao.queryChildrenCountFollowed(seller.getId()));
        seller.put("unfollowed_children_count", Seller.dao.queryChildrenCountUnFollowed(seller.getId()));
        seller.put("agent_ship", seller.isAgent() ? 1 : 0);
        seller.put("partner_pool_count", seller.getPartnerPoolCount());
        PartnerLevel partnerLevel = seller.getPartnerLevel();
        if (partnerLevel != null) {
            seller.put("partner_level", partnerLevel);
            seller.put("next_partner_level", sellerService.getNextPartnerLevel(partnerLevel.getLevel()));
        }
        renderSuccess(seller);
    }

    /**
     * 申请成为分销商, 返回分销商状态, 1 申请成功并通过审核, 0 审核中
     * 前提：t_config的partner.can_apply_crown 为true
     * POST {"real_name": "abc", "phone":"130000000", "type": "CROWN"}
     * <p/>
     * REturn:
     * {
     * "status_code" 0,
     * "data": {"seller_ship": 1}
     * }
     */
    @Before({CurrentUserInterceptor.class, Tx.class})
    public void save() {
        User currentUser = getAttr("currentUser");
        Map<String, Object> map = convertPostJsonToMap();
        if (map == null) {
            renderFailure("invalid.input");
            return;
        }

        String type = (String) map.get("type");
        Apply.Type applyType = Apply.Type.SELLER;
        if (type != null) {
            try {
                applyType = Apply.Type.valueOf(type);
            } catch (Exception ex) {
                renderFailure("invalid.type");
                return;
            }
        }

        String realName = (String) map.get("real_name");
        String phone = (String) map.get("phone");
        if (StrKit.notBlank(realName) && StrKit.notBlank(phone)) {
            User user = User.dao.findById(currentUser.getId());
            User otherUser = User.dao.findByPhone(phone);
            if (otherUser != null && !otherUser.getId().equals(user.getId())) {
                renderFailure("phone.already.register");
                return;
            }
            user.setPhone(phone);
            user.setRealName(realName);
            user.setPassword("");
            userService.updateUser(user, null);
        }

        Ret ret;
        if (applyType == Apply.Type.CROWN) {
            Boolean canApplyCrown = false;
            Config canApplyCrownConfig = Config.dao.findByKey(CAN_APPLY_CROWN_KEY);
            if (canApplyCrownConfig == null
                    || canApplyCrownConfig.getValueToBoolean() == null
                    || !canApplyCrownConfig.getValueToBoolean()) {
                renderFailure("apply.crown.feature.is.off");
                return;
            }
            ret = physicalSellerService.applyPhysicalCrownShip(currentUser.getId());
            if (BaseService.isSucceed(ret)) {
                //上面申请成功，则这里不管自动审核是否通过，该api调用的返回结果都应该是success

                //这里先看次数能不能达到自动审核的要求，不能达到就直接告诉前端 “您的授权申请已提交人工审核，请耐心等待”
                Config autoAuditPhysicalCrownCfg = Config.dao.findByKey(PhysicalSellerService.AUTO_AUDIT_PHYSICAL_CROWN);
                Config autoAuditPhysicalCrownTimesCfg = Config.dao.findByKey(PhysicalSellerService.AUTO_AUDIT_PHYSICAL_CROWN_TIMES);
                if (autoAuditPhysicalCrownCfg != null
                        && autoAuditPhysicalCrownCfg.getValueToBoolean().booleanValue()
                        && autoAuditPhysicalCrownTimesCfg != null
                        && autoAuditPhysicalCrownTimesCfg.getValueToInt() != null) {
                    Seller currentSeller = Seller.dao.findByUserId(currentUser.getId());
                    int times = autoAuditPhysicalCrownTimesCfg.getValueToInt().intValue();
                    int failureTimes = currentSeller.getCrownApplyFailureTimes().intValue();
                    if (failureTimes >= times) {
                        renderSuccessMessage("apply.success.please.waiting.for.manual.audit");
                        return;
                    }
                } else {
                    renderSuccessMessage("apply.success.please.waiting.for.manual.audit");
                    return;
                }
                Apply crownApply = Apply.dao.findByUserIdTypeStatus(currentUser.getId(), Apply.Type.CROWN.toString(), Apply.Status.INIT.toString());
                physicalSellerService.autoAuditCrownShip(crownApply.getId());
            }
        } else {
            ret = sellerService.applySellerShip(currentUser.getId());
        }
        logger.debug("seller apply ret = {}", ret.getData());
        if (BaseService.isSucceed(ret)) {
            String eventType = getRes().get(EventLogName.COOPERATIVE_PARTNER_EVENT_TYPE_KEY);
            String eventName = getRes().get(EventLogName.COOPERATIVE_PARTNER_SELLER_APPLY_KEY);
            String ip = getRequest().getRemoteAddr();
            String userAgent = getRequest().getHeader("User-Agent");
            eventLogService.record(eventType, eventName, currentUser.getName(), ip, userAgent, "user_id" + currentUser.getId());

            Seller seller = Seller.dao.findByUserId(currentUser.getId());
            Map<String, Object> result = new HashMap<>();
            result.put("seller_ship", seller.getSellerShip());
            renderSuccess(result);
            return;
        }
        renderFailure("apply.failure");
    }

    /**
     * GET /rest/seller/id
     * 查找某个seller信息及其下级分销商。
     * 当且仅当这个seller和当前user存在上下级关系才有效。
     */
    @Before(CurrentUserInterceptor.class)
    public void show() {
        User currentUser = getAttr("currentUser");
        Seller currentSeller = Seller.dao.findVerboseByUserId(currentUser.getId());
        Integer parentId = currentSeller.getId();
        Integer currentId = getParaToInt();
        if (parentId.equals(currentId)) {
            currentSeller.put("children", currentSeller.getVerboseChildren());
            renderSuccess(currentSeller);
            return;
        }

        if (sellerService.isChild(parentId, currentId)) {
            Seller seller = Seller.dao.findVerboseById(currentId);
            seller.put("children", seller.getVerboseChildren());
            renderSuccess(seller);
        } else {
            renderFailure("illegal access");
        }
    }

}
