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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfeat.config.model.Config;
import com.jfeat.core.BaseService;
import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.identity.model.User;
import com.jfeat.partner.model.Apply;
import com.jfeat.partner.model.PhysicalSeller;
import com.jfeat.partner.model.Seller;
import com.jfeat.partner.service.PhysicalSellerService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by jackyhuang on 17/1/11.
 */
@ControllerBind(controllerKey = "/rest/physical_seller")
public class PhysicalSellerController extends RestController {

    private PhysicalSellerService physicalSellerService = Enhancer.enhance(PhysicalSellerService.class);
    private static final String CAN_CODE_APPLY_CROWN_KEY = "partner.can_code_apply_crown";
    private static final Integer LEVEL1_CROWN = 1;
    private static final Integer LEVEL2_CROWN = 2;

    /**
     * 查看线下经销商列表
     */
    public void index() {
        User currentUser = getAttr("currentUser");
        Seller seller = Seller.dao.findByUserId(currentUser.getId());
        PhysicalSeller physicalSeller = PhysicalSeller.dao.findBySellerId(seller.getId());
        if (physicalSeller == null) {
            renderFailure("you.are.not.a.physical.seller.");
            return;
        }
        if (seller.isCrownShip()) {
            List<PhysicalSeller> physicalSellerChildren = physicalSeller.getVerboseChildren();
            List<PhysicalSeller> crownChildren = filterCrownChildren(physicalSellerChildren); //只包含下级皇冠商
            List<PhysicalSeller> crownChildrenWithLevel = Lists.newArrayList(); //包含下级，下下级皇冠商（下级排前面）
            crownChildrenWithLevel.addAll(crownChildren);
            for (PhysicalSeller crownChild : crownChildren) {
                crownChild.put("level", LEVEL1_CROWN);
                List<PhysicalSeller> lv2CrownChildren = filterCrownChildren(crownChild.getVerboseChildren());
                for (PhysicalSeller lv2CrownChild : lv2CrownChildren) {
                    lv2CrownChild.put("level", LEVEL2_CROWN);
                    crownChildrenWithLevel.add(lv2CrownChild);
                }
            }

            physicalSeller.put("children", physicalSellerChildren);
            physicalSeller.put("children_count", physicalSellerChildren.size() + crownChildrenWithLevel.size());
            physicalSeller.put("crown_children", crownChildrenWithLevel);
            physicalSeller.put("crown_children_count", crownChildren.size()); //下级皇冠数量
            physicalSeller.put("crown_children_count_lv2", crownChildrenWithLevel.size() - crownChildren.size()); //下下级皇冠数量
        }
        physicalSeller.put("parent", physicalSeller.getVerboseParent());
        physicalSeller.put("user_name", currentUser.getName());
        physicalSeller.put("uid", currentUser.getUid());
        physicalSeller.put("avatar", currentUser.getAvatar());
        renderSuccess(physicalSeller);
    }

    /**
     * 传入线下经销商列表，把该列表中的皇冠商转移到皇冠商列表中
     *
     * @param physicalSellers 线下经销商的列表
     * @return 从线下经销商列表中找出来的皇冠商列表
     */
    private List<PhysicalSeller> filterCrownChildren(List<PhysicalSeller> physicalSellers) {
        ListIterator<PhysicalSeller> iterator = physicalSellers.listIterator();
        List<PhysicalSeller> crowns = Lists.newArrayList();
        while (iterator.hasNext()) {
            PhysicalSeller physicalSeller = iterator.next();
            int crownShip = physicalSeller.getInt("crown_ship");
            if (crownShip == Seller.CrownShip.YES.getValue()) {
                crowns.add(physicalSeller);
                iterator.remove();
            }
        }
        return crowns;
    }

    /**
     * 申请成为某皇冠商的"线下经销商"或"线下皇冠商"
     * 前提：t_config的partner.can_code_apply_crown 为true
     * 1.若申请人是一个Seller，申请内容是线下经销商，则递交成为线下经销商的申请,并根据自动审核线下皇冠商机制来自动给予线下资格
     * 2.若申请人本身是一个线下经销商，申请内容是皇冠商，此时会根据自动审核线下皇冠商机制来自动给予皇冠商资格
     * 3.若申请人是一个Seller,申请内容是线下皇冠，此时会先保存一条申请成为线下的记录，然后立刻通过，然后保存一条
     * 申请成为皇冠的记录，然后根据自动审核线下皇冠商机制来自动给予皇冠商资格
     * POST DATA:
     * {
     * "uid": "U00001", //required,推荐人的UID
     * "real_name": "黄", //required,申请人真实姓名
     * "phone": "13800000000", //required,申请人手机
     * "type": "CROWN", //optional，省略表示申请成为线下经销商，CROWN表示申请成为线下皇冠商
     * "province": "广东", //required
     * "city": "广州", //required
     * "district": "荔湾区" //required
     * }
     */
    @Before(Tx.class)
    @Validation(rules = {
            "province = required",
            "city = required",
            "district = required",
            "phone = required",
            "real_name = required",
            "uid = required"
    })
    public void save() {
        Map<String, Object> map = convertPostJsonToMap();
        String parentUid = (String) map.get("uid");
        User parentUser = User.dao.findByUid(parentUid);
        if (parentUser == null) {
            renderFailure("user.not.found");
            return;
        }
        //不能申请成为自己的线下
        User currentUser = getAttr("currentUser");
        if (currentUser.getId().equals(parentUser.getId())) {
            logger.debug("currentUser(id = {}) can't be his own child: parentUser(id = {})", currentUser.getId(), parentUser.getId());
            renderFailure("cannot.apply.yourself");
            return;
        }
        //推荐人必须是皇冠商，且不能是临时皇冠商
        Seller parentSeller = Seller.dao.findByUserId(parentUser.getId());
        if (parentSeller == null || !parentSeller.isCrownShip() || (parentSeller.getCrownShipTemp() != null && parentSeller.isCrownShipTemp())) {
            logger.debug("parentSeller(uid = {}, userId = {}) is null or is not a crown seller or is a temp crown seller", parentUid, parentUser.getId());
            renderFailure("user.is.not.crownship");
            return;
        }
        //使用传上来的real_name，phone来更新个人信息
        String realName = (String) map.get("real_name");
        String phone = (String) map.get("phone");
        //电话不能已被注册
        User otherUser = User.dao.findByPhone(phone);
        if (otherUser != null && !otherUser.getId().equals(currentUser.getId())) {
            logger.debug("phone = {} already registered", phone);
            renderFailure("phone.already.registered");
            return;
        }
        currentUser.setPhone(phone);
        currentUser.setRealName(realName);
        currentUser.setPassword("");
        currentUser.update();

        String type = (String) map.get("type");
        Seller currentSeller = Seller.dao.findByUserId(currentUser.getId());
        //不提供type，则默认是申请成为线下，提供，则必须是CROWN，意为申请成为线下皇冠
        Apply.Type t = Apply.Type.PHYSICAL;
        if (StrKit.notBlank(type)) {
            try {
                t = Apply.Type.valueOf(type);
            } catch (IllegalArgumentException e) {
                logger.debug("type must be crown or has not type");
                renderFailure("type.must.be.crown.or.has.not.type");
                return;
            }
        }

        Ret ret = null;
        if (t == Apply.Type.PHYSICAL) {
            ret = applyPhysicalSeller(currentUser, currentSeller, parentSeller);
        } else if (t == Apply.Type.CROWN) {
            ret = applyPhysicalCrown(currentUser, currentSeller, parentSeller);
        } else {
            logger.debug("invalid.type");
            renderFailure("invalid.type");
            return;
        }

        logger.debug("apply result = {}", ret.getData());

        if (BaseService.isSucceed(ret)) {
            String message = StrKit.isBlank(BaseService.getMessage(ret)) ? "apply.success" : BaseService.getMessage(ret);
            renderSuccessMessage(message);
            return;
        }

        renderFailure(BaseService.getMessage(ret));
    }

    private Ret applyPhysicalSeller(User currentUser, Seller currentSeller, Seller parentSeller) {
        Ret ret = physicalSellerService.applyPhysicalSellerShip(currentUser.getId(), currentSeller.getId(), parentSeller.getId());
        logger.debug("apply physical seller ship ret = {}", ret.getData());
        if (BaseService.isSucceed(ret)) {
            Apply physicalSellerApply = Apply.dao.findByUserIdTypeStatus(currentUser.getId(), Apply.Type.PHYSICAL.toString(), Apply.Status.INIT.toString());
            updatePcdToApplyProperty(physicalSellerApply);
            Ret phyRet = physicalSellerService.autoAuditPhysicalSellerShip(physicalSellerApply.getId());
            logger.debug("auto audit physical seller ship ret = {}", phyRet.getData());

            Ret followRet = Ret.create("result", true);
            if (!currentUser.getFollowed().equals(User.INFOLLOW_SUBSCRIBE)) {
                logger.debug("userId = {} hasn't followed wechat", currentSeller.getId());
                return followRet.put("message", "user.hasn't.followed.wechat");
            }
            return followRet;
        }
        return ret;
    }

    private Ret applyPhysicalCrown(User currentUser, Seller currentSeller, Seller parentSeller) {
        //假如通过扫码成为线下皇冠商的开关未打开，则申请失败
        Config canCodeApplyCrownCfg = Config.dao.findByKey(CAN_CODE_APPLY_CROWN_KEY);
        if (canCodeApplyCrownCfg == null
                || canCodeApplyCrownCfg.getValueToBoolean() == null
                || !canCodeApplyCrownCfg.getValueToBoolean()) {
            logger.debug("can_code_apply_crown isn't opening,can't apply to be a temp physical crown.");
            return Ret.create("message", "can_code_apply_crown.isn't.opening").put("result", false);
        }

        //假如不是线下，则先保存一条申请成为线下的记录，然后立刻通过，然后保存一条申请成为皇冠的记录，
        // 然后根据自动审核皇冠商机制来自动给予皇冠商资格
        if (!currentSeller.isPhysicalSeller()) {
            Ret ret = physicalSellerService.applyPhysicalSellerShip(currentUser.getId(), currentSeller.getId(), parentSeller.getId());
            logger.debug("apply physical seller ret = {}", ret.getData());
            if (!BaseService.isSucceed(ret)) {
                return ret;
            }

            Apply apply = Apply.dao.findByUserIdType(currentUser.getId(), Apply.Type.PHYSICAL.toString());
            if (apply == null || Apply.Status.valueOf(apply.getStatus()) != Apply.Status.INIT) {
                logger.debug("invalid.apply.status - apply = {}", apply != null ? apply.toJson() : "");
                return Ret.create("message", "invalid.apply.status").put("result", false);
            }

            // save pcd to the properties
            updatePcdToApplyProperty(apply);

            if (currentUser.getFollowed().equals(User.INFOLLOW_SUBSCRIBE)) {
                ret = physicalSellerService.approvePhysicalSellerShip(apply.getId());
                if (!BaseService.isSucceed(ret)) {
                    logger.debug("approve physical seller ship failed. ret = {}", ret.getData());
                    return ret;
                }
            }
        }

        if (currentSeller.isCrownShip()) {
            logger.debug("seller {} is already crown ship.", currentSeller.getId());
            return Ret.create("result", true);
        }

        Ret ret = physicalSellerService.applyPhysicalCrownShip(currentUser.getId());
        if (!BaseService.isSucceed(ret)) {
            logger.debug("apply physical crown ship failed. ret = {}", ret.getData());
            return ret;
        }

        Apply crownApply = Apply.dao.findByUserIdTypeStatus(currentUser.getId(), Apply.Type.CROWN.toString(), Apply.Status.INIT.toString());
        if (crownApply == null || Apply.Status.valueOf(crownApply.getStatus()) != Apply.Status.INIT) {
            logger.debug("invalid.apply.status - crownApply = {}", crownApply != null ? crownApply.toJson() : "");
            return Ret.create("message", "invalid.apply.status").put("result", false);
        }
        updatePcdToApplyProperty(crownApply);

        if (!currentUser.getFollowed().equals(User.INFOLLOW_SUBSCRIBE)) {
            logger.debug("userId = {} hasn't followed wechat", currentSeller.getId());
            return Ret.create("message", "user.hasn't.followed.wechat").put("result", true);
        }

        //这里先看次数能不能达到自动审核的要求，不能达到就直接告诉前端 “您的授权申请已提交人工审核，请耐心等待”
        Config autoAuditPhysicalCrownCfg = Config.dao.findByKey(PhysicalSellerService.AUTO_AUDIT_PHYSICAL_CROWN);
        Config autoAuditPhysicalCrownTimesCfg = Config.dao.findByKey(PhysicalSellerService.AUTO_AUDIT_PHYSICAL_CROWN_TIMES);
        if (autoAuditPhysicalCrownCfg != null
                && autoAuditPhysicalCrownCfg.getValueToBoolean()
                && autoAuditPhysicalCrownTimesCfg != null
                && autoAuditPhysicalCrownTimesCfg.getValueToInt() != null) {
            int times = autoAuditPhysicalCrownTimesCfg.getValueToInt();
            int failureTimes = currentSeller.getCrownApplyFailureTimes();
            if (failureTimes >= times) {
                return Ret.create("message", "apply.success.please.waiting.for.manual.audit").put("result", true);
            }
        }

        ret = physicalSellerService.autoAuditCrownShip(crownApply.getId());
        logger.debug("auto audit crown ship ret = {}", ret.getData());
        return Ret.create("message", "apply.success").put("result", true);
    }

    private void updatePcdToApplyProperty(Apply apply) {
        // save pcd to the properties
        Map<String, Object> map = convertPostJsonToMap();
        Map<String, Object> properties = Maps.newHashMap();
        properties.put("province", map.get("province"));
        properties.put("city", map.get("city"));
        properties.put("district", map.get("district"));
        Map<String, Object> originalProperties = Maps.newHashMap();
        try {
            originalProperties = com.jfeat.kit.JsonKit.convertToMap(apply.getProperties());
        } catch (Exception e) {
            e.printStackTrace();
        }
        properties.putAll(originalProperties);
        apply.setProperties(JsonKit.toJson(properties));
        apply.update();
    }

}
