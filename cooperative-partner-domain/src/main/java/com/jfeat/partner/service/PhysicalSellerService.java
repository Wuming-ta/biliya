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

package com.jfeat.partner.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfeat.config.model.Config;
import com.jfeat.core.BaseService;
import com.jfeat.core.Service;
import com.jfeat.core.ServiceContext;
import com.jfeat.identity.model.User;
import com.jfeat.kit.DateKit;
import com.jfeat.partner.handler.PhysicalCrownShipExpiredHandler;
import com.jfeat.partner.model.*;
import com.jfeat.partner.notification.PhysicalSellerApprovedNotification;
import com.jfeat.partner.notification.TempCrownApprovedNotification;
import com.jfeat.partner.notification.TempCrownResettedNotification;
import com.jfeat.service.WholesaleValidationService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by jackyhuang on 17/1/10.
 */
public class PhysicalSellerService extends BaseService {

    private SellerService sellerService = Enhancer.enhance(SellerService.class);
    private CopartnerService copartnerService = Enhancer.enhance(CopartnerService.class);

    private static String CAN_APPLY_CROWN_KEY = "partner.can_apply_crown";
    public static final String WHOLESALE_AMOUNT = "partner.new_physical_seller_wholesale_amount";
    public static final String WHOLESALE_TIME = "partner.new_physical_seller_wholesale_time";
    public static final String AUTO_AUDIT_PHYSICAL_CROWN = "partner.auto_audit_physical_crown";
    public static final String AUTO_AUDIT_PHYSICAL_CROWN_TIMES = "partner.auto_audit_physical_crown_times";

    public boolean createPhysicalSeller(Integer sellerId, Integer parentId, String province, String city, String district) {
        PhysicalSeller physicalSeller = new PhysicalSeller();
        physicalSeller.setSellerId(sellerId);
        physicalSeller.setParentSellerId(parentId);
        physicalSeller.setProvince(province);
        physicalSeller.setCity(city);
        physicalSeller.setDistrict(district);
        return physicalSeller.save();
    }

    public boolean deletePhysicalSeller(Integer sellerId) {
        return new PhysicalSeller().deleteBySellerId(sellerId);
    }

    /**
     * 返回申请里面的被邀请者
     *
     * @param apply
     * @return
     */
    public Seller getApplyingPhysicalSeller(Apply apply) {
        try {
            Map<String, Object> properties = com.jfeat.kit.JsonKit.convertToMap(apply.getProperties());
            Integer sellerId = (Integer) properties.get("seller_id");
            return Seller.dao.findById(sellerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回申请里面的邀请者
     *
     * @param apply
     * @return
     */
    public Seller getApplyingPhysicalParentSeller(Apply apply) {
        try {
            Map<String, Object> properties = com.jfeat.kit.JsonKit.convertToMap(apply.getProperties());
            Integer parentId = (Integer) properties.get("parent_id");
            return Seller.dao.findById(parentId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 申请线下资格
     *
     * @param userId
     * @param sellerId
     * @param parentId
     * @return
     */
    public Ret applyPhysicalSellerShip(Integer userId, Integer sellerId, Integer parentId) {
        PhysicalSeller physicalSeller = PhysicalSeller.dao.findBySellerId(sellerId);
        if (physicalSeller != null) {
            return failure("seller.already.physical");
        }

        Apply apply = Apply.dao.findByUserIdType(userId, Apply.Type.PHYSICAL.toString());
        if (apply != null && apply.getStatus().equals(Apply.Status.INIT.toString())) {
            return failure("apply.already.exist");
        }
        apply = new Apply();
        apply.setUserId(userId);
        apply.setType(Apply.Type.PHYSICAL.toString());
        apply.setStatus(Apply.Status.INIT.toString());
        apply.setApplyDate(new Date());
        Map<String, Object> properties = Maps.newHashMap();
        properties.put("seller_id", sellerId);
        properties.put("parent_id", parentId);
        apply.setProperties(JsonKit.toJson(properties));
        if (apply.save()) {
            return success("partner.physical_seller.apply.success").put("apply_id", apply.getId());
        }
        return failure("partner.physical_seller.apply.failure");
    }

    @Before(Tx.class)
    public Ret approvePhysicalSellerShip(int applyId) {
        Apply apply = Apply.dao.findById(applyId);
        String status = apply.getStatus();
        String type = apply.getType();
        if (status.equals(Apply.Status.INIT.toString()) && type.equals(Apply.Type.PHYSICAL.toString())) {
            try {
                Map<String, Object> properties = com.jfeat.kit.JsonKit.convertToMap(apply.getProperties());
                Integer sellerId = (Integer) properties.get("seller_id");
                Integer parentId = (Integer) properties.get("parent_id");
                String province = (String) properties.get("province");
                String city = (String) properties.get("city");
                String district = (String) properties.get("district");

                createPhysicalSeller(sellerId, parentId, province, city, district);

                // 如果是合伙人，则加入到他下面
                Ret ret = copartnerService.addTeamMember(parentId, sellerId);
                logger.debug("copartner add team member ret = {}", ret.getData());

                apply.setStatus(Apply.Status.APPROVE.toString());
                apply.setApproveDate(new Date());
                apply.update();

                ret = sellerService.assignPartnerRight(sellerId);
                logger.debug("assign partner right ret = {}", ret.getData());

                User user = User.dao.findById(apply.getUserId());
                PhysicalSeller physicalSeller = PhysicalSeller.dao.findBySellerId(sellerId);
                Seller parentSeller = physicalSeller.getParentSeller();
                if (parentSeller != null && parentId.equals(parentSeller.getId())) {
                    User parentUser = parentSeller.getUser();
                    new PhysicalSellerApprovedNotification(user.getWeixin())
                            .param(PhysicalSellerApprovedNotification.ASSIGNOR, parentUser.getRealName())
                            .param(PhysicalSellerApprovedNotification.ASSIGNEE, user.getRealName())
                            .send();
                    new PhysicalSellerApprovedNotification(parentUser.getWeixin())
                            .param(PhysicalSellerApprovedNotification.ASSIGNOR, parentUser.getRealName())
                            .param(PhysicalSellerApprovedNotification.ASSIGNEE, user.getRealName())
                            .send();
                } else {
                    logger.error("physicalSeller = {} 's parent_seller not found.", physicalSeller);
                }
                return success("partner.physical_seller.approve.success");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return failure("partner.physical_seller.approve.failure");
    }

    public Ret rejectPhysicalSellerShip(int applyId) {
        Apply apply = Apply.dao.findById(applyId);
        String status = apply.getStatus();
        String type = apply.getType();
        if (status.equals(Apply.Status.INIT.toString()) && type.equals(Apply.Type.PHYSICAL.toString())) {
            apply.setRejectDate(new Date());
            apply.setStatus(Apply.Status.REJECT.toString());
            apply.update();
            return success("partner.physical_seller.reject.success");
        }
        return failure("partner.physical_seller.reject.failure");
    }


    /**
     * 线下经销商 申请成为 临时线下皇冠商
     *
     * @param userId
     * @return
     */
    public Ret applyPhysicalCrownShip(int userId) {
        User user = User.dao.findById(userId);
        if (user == null) {
            return failure("invalid.user");
        }
        Seller seller = Seller.dao.findByUserId(userId);
        if (seller == null) {
            return failure("invalid.seller");
        }
        if (seller.isCrownShip()) {
            return failure("seller.already.crownship");
        }

        Apply apply = Apply.dao.findByUserIdType(userId, Apply.Type.CROWN.toString());
        if (apply != null && apply.getStatus().equals(Apply.Status.INIT.toString())) {
            return failure("apply.already.exist");
        }
        apply = new Apply();
        apply.setUserId(userId);
        apply.setType(Apply.Type.CROWN.toString());
        apply.setStatus(Apply.Status.INIT.toString());
        apply.setProperties("{}");
        apply.setApplyDate(new Date());
        //若申请记录保存成功 并且 用户已经关注公众号
        if (!apply.save()) {
            return failure("apply.save.failure");
        }
        return success("apply.save.success");
    }

    /**
     * 根据自动审核线下皇冠商机制来自动给予线下资格（不需要考虑失败次数，只要开关打开，即审批通过；也不需要发模板消息）
     */
    public Ret autoAuditPhysicalSellerShip(int applyId) {
        Apply apply = Apply.dao.findById(applyId);
        if (apply == null) {
            logger.debug("applyId = {} not found", applyId);
            return failure("apply.not.found");
        }
        User user = User.dao.findById(apply.getUserId());
        if (user == null) {
            logger.debug("userId = {} not found", apply.getUserId());
            return failure("user.not.found");
        }
        if (!user.getFollowed().equals(User.INFOLLOW_SUBSCRIBE)) {
            logger.debug("userId = {} hasn't followed wechat", user.getId());
            return failure("user.hasn't.followed.wechat");
        }
        Config autoAuditPhysicalCrownCfg = Config.dao.findByKey(AUTO_AUDIT_PHYSICAL_CROWN);
        if (autoAuditPhysicalCrownCfg != null && autoAuditPhysicalCrownCfg.getValueToBoolean()) {
            Ret ret = approvePhysicalSellerShip(applyId);
            if (BaseService.isSucceed(ret)) {
                return success("autoAuditPhysicalSellerShip.success");
            } else {
                logger.debug(BaseService.getMessage(ret));
                return failure(BaseService.getMessage(ret));
            }
        } else {
            logger.debug("autoAuditPhysicalCrownCfg is null or its value is false");
            return failure("autoAuditPhysicalCrownCfg.is.null.or.its.value.is.false");
        }
    }


    /**
     * 根据自动审核线下皇冠商机制来自动给予临时皇冠商资格
     *
     * @param applyId
     * @return
     */
    public Ret autoAuditCrownShip(int applyId) {
        Apply apply = Apply.dao.findById(applyId);
        if (apply == null) {
            logger.debug("applyId = {} not found", applyId);
            return failure("apply.not.found");
        }
        User user = User.dao.findById(apply.getUserId());
        if (user == null) {
            logger.debug("userId = {} not found", apply.getUserId());
            return failure("user.not.found");
        }
        if (!user.getFollowed().equals(User.INFOLLOW_SUBSCRIBE)) {
            logger.debug("userId = {} hasn't followed wechat", user.getId());
            return failure("user.hasn't.followed.wechat");
        }
        Seller seller = Seller.dao.findByUserId(apply.getUserId());
        Config autoAuditPhysicalCrownCfg = Config.dao.findByKey(AUTO_AUDIT_PHYSICAL_CROWN);
        Config autoAuditPhysicalCrownTimesCfg = Config.dao.findByKey(AUTO_AUDIT_PHYSICAL_CROWN_TIMES);
        if (autoAuditPhysicalCrownCfg != null && autoAuditPhysicalCrownCfg.getValueToBoolean() && autoAuditPhysicalCrownTimesCfg != null && autoAuditPhysicalCrownTimesCfg.getValueToInt() != null) {
            int times = autoAuditPhysicalCrownTimesCfg.getValueToInt();
            int failureTimes = seller.getCrownApplyFailureTimes();
            if (failureTimes < times) {
                Ret ret = approveTempCrownShip(applyId);
                if (BaseService.isSucceed(ret)) {
                    return success("autoApprovePhysicalCrownShip.success");
                } else {
                    logger.debug(BaseService.getMessage(ret));
                    return failure(BaseService.getMessage(ret));
                }
            } else {
                logger.debug("sellerId = {},applyId = {},failureTimes = {} >= times = {}, autoApprovePhysicalCrownShip failure.", seller.getId(), applyId, failureTimes, times);
                return failure(String.format("autoApprovePhysicalCrownShip.failure:failureTimes = %s >= times = %s", failureTimes, times));
            }
        } else {
            logger.debug("autoAuditPhysicalCrownCfg is null or autoAuditPhysicalCrownTimesCfg is null or autoAuditPhysicalCrownTimesCfg.getValueToInt() is null");
            return failure("autoAuditPhysicalCrownCfg.is.null.or.autoAuditPhysicalCrownTimesCfg.is.null.or.autoAuditPhysicalCrownTimesCfg.getValueToInt().is.null");
        }
    }

    /**
     * 批准成为临时皇冠商
     *
     * @param applyId
     * @return
     */
    public Ret approveTempCrownShip(int applyId) {
        Apply apply = Apply.dao.findById(applyId);
        String status = apply.getStatus();
        String type = apply.getType();
        if (status.equals(Apply.Status.INIT.toString()) && type.equals(Apply.Type.CROWN.toString())) {
            Seller seller = Seller.dao.findByUserId(apply.getUserId());
            Ret ret = sellerService.assignCrownRight(seller.getId());
            if (BaseService.isSucceed(ret)) {
                Seller s = Seller.dao.findByUserId(apply.getUserId());
                s.setCrownShipTemp(Seller.CrownShipTemp.YES.getValue());
                s.update();

                try {
                    Map<String, Object> properties = com.jfeat.kit.JsonKit.convertToMap(apply.getProperties());
                    String province = (String) properties.get("province");
                    String city = (String) properties.get("city");
                    String district = (String) properties.get("district");
                    PhysicalSeller physicalSeller = PhysicalSeller.dao.findBySellerId(s.getId());
                    physicalSeller.setProvince(province);
                    physicalSeller.setCity(city);
                    physicalSeller.setDistrict(district);
                    physicalSeller.update();
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                }

                apply.setStatus(Apply.Status.APPROVE.toString());
                apply.setApproveDate(new Date());
                apply.update();

                //添加过期事件
                PhysicalCrownShipExpiredHandler.add(Seller.dao.findByUserId(apply.getUserId()).getId());

                //发送微信模板消息
                User user = User.dao.findById(apply.getUserId());
                PhysicalSeller physicalSeller = PhysicalSeller.dao.findBySellerId(seller.getId());
                Seller parentSeller = physicalSeller.getParentSeller();
                if (parentSeller != null) {
                    User parentUser = parentSeller.getUser();
                    new TempCrownApprovedNotification(user.getWeixin())
                            .param(TempCrownApprovedNotification.ASSIGNOR, parentUser.getRealName())
                            .param(TempCrownApprovedNotification.ASSIGNEE, user.getRealName())
                            .send();
                    new TempCrownApprovedNotification(parentUser.getWeixin())
                            .param(TempCrownApprovedNotification.ASSIGNOR, parentUser.getRealName())
                            .param(TempCrownApprovedNotification.ASSIGNEE, user.getRealName())
                            .send();
                } else {
                    logger.error("physicalSeller = {} 's parent_seller not found.", physicalSeller);
                }

                return success("partner.crown.approve.success");
            }
        }
        return failure("partner.crown.approve.failure");
    }

    public Ret rejectPhysicalCrownShip(int applyId) {
        Apply apply = Apply.dao.findById(applyId);
        String status = apply.getStatus();
        String type = apply.getType();
        if (status.equals(Apply.Status.INIT.toString()) && type.equals(Apply.Type.CROWN.toString())) {
            apply.setRejectDate(new Date());
            apply.setStatus(Apply.Status.REJECT.toString());
            apply.update();
            return success("partner.crown.reject.success");
        }
        return failure("partner.crown.reject.failure");
    }

    public Ret updatePhysicalSettlementProportions(List<PhysicalSettlementProportion> physicalSettlementProportions) {
        List<PhysicalSettlementProportion> originalProportions = PhysicalSettlementProportion.dao.findAll();
        List<PhysicalSettlementProportion> toAddList = Lists.newArrayList();
        List<PhysicalSettlementProportion> toDeleteList = Lists.newArrayList();
        List<PhysicalSettlementProportion> toUpdateList = Lists.newArrayList();

        if (physicalSettlementProportions == null) {
            physicalSettlementProportions = Lists.newArrayList();
        }
        for (PhysicalSettlementProportion proportion : physicalSettlementProportions) {
            if (proportion.getId() == null) {
                toAddList.add(proportion);
            } else {
                for (PhysicalSettlementProportion originalProportion : originalProportions) {
                    if (proportion.getId().equals(originalProportion.getId())) {
                        toUpdateList.add(proportion);
                    }
                }
            }
        }

        for (PhysicalSettlementProportion originalProportion : originalProportions) {
            boolean found = false;
            for (PhysicalSettlementProportion proportion : physicalSettlementProportions) {
                if (proportion.getId() != null && proportion.getId().equals(originalProportion.getId())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                toDeleteList.add(originalProportion);
            }
        }

        Db.batchSave(toAddList, 20);
        Db.batchUpdate(toUpdateList, 20);
        for (PhysicalSettlementProportion toDeleteProportion : toDeleteList) {
            toDeleteProportion.delete();
        }

        return success("physical.settlement.proportion.update.success");
    }

    private BigDecimal getSettlementProportionPercentage(BigDecimal amount) {
        List<PhysicalSettlementProportion> list = PhysicalSettlementProportion.dao.findAll();
        for (int i = list.size() - 1; i >= 0; i--) {
            PhysicalSettlementProportion proportion = list.get(i);
            if (amount.compareTo(proportion.getMinAmount()) >= 0
                    && (proportion.getMaxAmount().compareTo(BigDecimal.valueOf(-1)) <= 0
                    || amount.compareTo(proportion.getMaxAmount()) <= 0)) {
                return proportion.getPercentage();
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * 线下经销商有进货，更新其及其推荐人相应的明细和汇总 （只有批发进货才调用此方法）
     */
    @Before(Tx.class)
    public boolean updatePurchase(PhysicalSeller physicalSeller, Integer orderId,
                                  String orderNumber, List<Integer> orderItemIds,
                                  List<String> productNames, List<BigDecimal> amounts,
                                  List<Integer> settlementProportions, List<BigDecimal> expectedRewards,
                                  String note) throws ParseException {
        if (physicalSeller == null || orderId == null || StrKit.isBlank(orderNumber)
                || orderItemIds == null || productNames == null || amounts == null
                || settlementProportions == null || expectedRewards == null) {
            throw new RuntimeException("some.para.is.null");
        }
        if (orderItemIds.size() != amounts.size() || orderItemIds.size() != productNames.size()
                || orderItemIds.size() != settlementProportions.size() || orderItemIds.size() != expectedRewards.size()) {
            throw new RuntimeException("the.size.of.orderItemIds,amounts,productNames,settlementProportions,expectedRewards.must.be.the.same.");
        }

        // 计算上级皇冠，上上级皇冠的分成比例
        BigDecimal levelOneCrownPercentage = BigDecimal.ZERO;
        BigDecimal levelTwoCrownPercentage = BigDecimal.ZERO;
        PhysicalSeller levelOnePhysicalSeller = physicalSeller.getParent();
        PhysicalSeller levelTwoPhysicalSeller = null;
        if (levelOnePhysicalSeller != null) {
            SettlementProportion pLv1 = SettlementProportion.dao.findByPhysicalCrown(1);
            SettlementProportion pLv2 = SettlementProportion.dao.findByPhysicalCrown(2);
            levelTwoPhysicalSeller = levelOnePhysicalSeller.getParent();
            if (pLv1 != null && pLv2 != null) {
                levelOneCrownPercentage = BigDecimal.valueOf(pLv1.getProportionObject().getValue());
                levelTwoCrownPercentage = BigDecimal.valueOf(pLv2.getProportionObject().getValue());
            }
        }

        logger.debug("physicalSeller: {}, levelOneCrownPercentage = {}, levelTwoCrownPercentage = {}", physicalSeller, levelOneCrownPercentage, levelTwoCrownPercentage);

        Date now = new Date();
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalExpectedAmount = BigDecimal.ZERO;
        for (int i = 0; i < orderItemIds.size(); i++) {
            // 保存进货明细记录
            PhysicalPurchaseJournal journal = new PhysicalPurchaseJournal();
            journal.setAmount(amounts.get(i));
            journal.setCreatedDate(now);
            journal.setSellerId(physicalSeller.getSellerId());
            journal.setOrderNumber(orderNumber);
            journal.setOrderId(orderId);
            journal.setOrderItemId(orderItemIds.get(i));
            journal.setProductName(productNames.get(i));
            journal.setProductSettlementProportion(BigDecimal.valueOf(settlementProportions.get(i)));
            journal.setExpectedReward(expectedRewards.get(i));
            journal.setExpectedRewardLv1(expectedRewards.get(i).multiply(levelOneCrownPercentage).divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP));
            journal.setExpectedRewardLv2(expectedRewards.get(i).multiply(levelTwoCrownPercentage).divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP));
            journal.setSettlementProportionLv1(levelOneCrownPercentage.multiply(BigDecimal.valueOf(settlementProportions.get(i))).divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP));
            journal.setSettlementProportionLv2(levelTwoCrownPercentage.multiply(BigDecimal.valueOf(settlementProportions.get(i))).divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP));
            journal.setNote(note);
            journal.save();
            totalAmount = totalAmount.add(amounts.get(i));
            totalExpectedAmount = totalExpectedAmount.add(expectedRewards.get(i));
        }

        logger.info("totalExpectedAmount = {}", totalExpectedAmount);

        // 更新进货汇总记录
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-01");
        String month = simpleDateFormat.format(now);
        //进货人是physicalSeller，此处更新其月进货额和 提成比例（提成比例根据自己的月进货额来决定）
        PhysicalPurchaseSummary summary = acquirePurchaseSummary(physicalSeller, month, totalAmount);

        // 1. 计算自己的（自己保存上级的期望分成，用levelOne，保存上上级的期望分成，用levelTwo）
        BigDecimal percentage = summary.getSettlementProportion().divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP);
        BigDecimal settledAmount = calcMonthSettlement(physicalSeller, percentage, month);
        BigDecimal expectedAmount = summary.getMonthlyExpectedSettledAmount()
                .add(totalExpectedAmount.multiply(levelOneCrownPercentage).divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP));
        summary.setMonthlyExpectedSettledAmount(expectedAmount);
        BigDecimal expectedAmountLv2 = summary.getMonthlyExpectedSettledAmountLv2()
                .add(totalExpectedAmount.multiply(levelTwoCrownPercentage).divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP));
        summary.setMonthlyExpectedSettledAmountLv2(expectedAmountLv2);
        summary.setMonthlySettledAmount(settledAmount);
        summary.update();
        logger.info("seller_id {} 's expected settled mount = {}, settled amount = {}", physicalSeller.getSellerId(), expectedAmount, settledAmount);

        // 计算上级皇冠分成
        if (levelOnePhysicalSeller != null) {
            PhysicalPurchaseSummary parentSummary = acquirePurchaseSummary(levelOnePhysicalSeller, month, BigDecimal.ZERO);
            BigDecimal parentPercentage = parentSummary.getSettlementProportion().divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP);
            BigDecimal parentSettledAmount = calcMonthSettlement(levelOnePhysicalSeller, parentPercentage, month);
            //BigDecimal parentExpectedAmount = parentSummary.getMonthlyExpectedSettledAmount().add(totalExpectedAmount.multiply(levelTwoCrownPercentage).divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP));
            //parentSummary.setMonthlyExpectedSettledAmount(parentExpectedAmount);
            parentSummary.setMonthlySettledAmount(parentSettledAmount);
            parentSummary.update();
            logger.info("seller_id {} 's settled amount = {}", levelOnePhysicalSeller.getSellerId(), parentSettledAmount);
        }

        // 计算上上级皇冠分成
        if (levelTwoPhysicalSeller != null) {
            PhysicalPurchaseSummary parentSummary = acquirePurchaseSummary(levelTwoPhysicalSeller, month, BigDecimal.ZERO);
            BigDecimal parentPercentage = parentSummary.getSettlementProportion().divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP);
            BigDecimal parentSettledAmount = calcMonthSettlement(levelTwoPhysicalSeller, parentPercentage, month);
            parentSummary.setMonthlySettledAmount(parentSettledAmount);
            parentSummary.update();
            logger.info("seller_id {} 's settled amount = {}", levelOnePhysicalSeller.getSellerId(), parentSettledAmount);
        }

        return true;
    }

    /**
     * 通过保存在下级的expected amount进行汇总，计算该月他的分成
     *
     * @param physicalSeller
     * @param percentage
     * @param month
     * @return
     * @throws ParseException
     */
    private BigDecimal calcMonthSettlement(PhysicalSeller physicalSeller, BigDecimal percentage, String month) throws ParseException {
        BigDecimal totalExpectedAmount = BigDecimal.ZERO;
        for (PhysicalSeller childPhysicalSeller : physicalSeller.getChildren()) {
            Seller childSeller = childPhysicalSeller.getSeller();
            if (childSeller.isCrownShip()) {
                PhysicalPurchaseSummary childSummary = acquirePurchaseSummary(childPhysicalSeller, month, BigDecimal.ZERO);
                totalExpectedAmount = totalExpectedAmount.add(childSummary.getMonthlyExpectedSettledAmount());
                for (PhysicalSeller grandsonPhysicalSeller : childPhysicalSeller.getChildren()) {
                    Seller grandsonSeller = grandsonPhysicalSeller.getSeller();
                    if (grandsonSeller.isCrownShip()) {
                        PhysicalPurchaseSummary grandsonSummary = acquirePurchaseSummary(grandsonPhysicalSeller, month, BigDecimal.ZERO);
                        totalExpectedAmount = totalExpectedAmount.add(grandsonSummary.getMonthlyExpectedSettledAmountLv2());
                    }
                }
            }
        }
        logger.debug("sellerId {}, totalExpectedAmount = {}", physicalSeller.getSellerId(), totalExpectedAmount.doubleValue());

        BigDecimal settledAmount = totalExpectedAmount.multiply(percentage).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal definitionAmount = PhysicalSettlementDefinition.dao.getDefault().getAmount();
        if (definitionAmount.compareTo(BigDecimal.ONE.negate()) == 0) {
            logger.debug("physical seller {} settlement unlimited. settled amount = {}", physicalSeller.getSellerId(), settledAmount);
            return settledAmount;
        }

        //quotaAmount为剩余提成限额 = 总提成限额 - 目前提成限额
        BigDecimal quotaAmount = definitionAmount.subtract(physicalSeller.getTotalSettledAmount());
        if (quotaAmount.compareTo(BigDecimal.ZERO) > 0) {
            logger.debug("physical seller {} can settle.", physicalSeller.getSellerId());
            if (settledAmount.compareTo(quotaAmount) <= 0) {
                return settledAmount;
            } else {
                return quotaAmount;
            }
        }

        return BigDecimal.ZERO;
    }

    /**
     * 取得相应月份的汇总记录，如果没有则创建。monthlyAmount=原有monthlyAmount+newAmount,并根据新的monthlyAmount更新settlementProportion
     *
     * @param physicalSeller
     * @param month
     * @param newAmount
     * @return
     * @throws ParseException
     */
    public PhysicalPurchaseSummary acquirePurchaseSummary(PhysicalSeller physicalSeller, String month, BigDecimal newAmount) throws ParseException {
        PhysicalPurchaseSummary summary = PhysicalPurchaseSummary.dao.findBySellerIdAndMonth(physicalSeller.getSellerId(), month);
        if (summary == null) {
            summary = new PhysicalPurchaseSummary();
            summary.setMonthlySettledAmount(BigDecimal.ZERO);
            summary.setTransferred(PhysicalPurchaseSummary.UN_TRANSFERRED);
            summary.setTransferredAmount(BigDecimal.ZERO);
            summary.setMonthlyExpectedSettledAmount(BigDecimal.ZERO);
            summary.setMonthlyExpectedSettledAmountLv2(BigDecimal.ZERO);
            summary.setSellerId(physicalSeller.getSellerId());
            summary.setStatisticMonth(DateKit.toDate(month));
            summary.setMonthlyAmount(newAmount);
            summary.setSettlementProportion(getSettlementProportionPercentage(summary.getMonthlyAmount()));
            summary.save();
        } else {
            summary.setMonthlyAmount(summary.getMonthlyAmount().add(newAmount));
            summary.setSettlementProportion(getSettlementProportionPercentage(summary.getMonthlyAmount()));
            summary.update();
        }

        return summary;
    }

    @Before(Tx.class)
    public Ret handlePhysicalCrownShipExpired(Integer sellerId) {
        Seller seller = Seller.dao.findById(sellerId);
        if (seller == null) {
            return failure("seller.not.found");
        }
        if (!seller.isCrownShipTemp()) {
            logger.debug("seller {} is not temp crown ship. ignore it", sellerId);
            return failure("seller.is.not.temp.crown");
        }
        Service service = ServiceContext.me().getService(WholesaleValidationService.class.getName());
        if (service == null) {
            return failure("service.is.unavailable");
        }
        WholesaleValidationService wholesaleValidationService = (WholesaleValidationService) service;
        Config wholesaleAmountCfg = Config.dao.findByKey(WHOLESALE_AMOUNT);
        Integer temp = null;
        Integer targetAmount = (wholesaleAmountCfg != null && (temp = wholesaleAmountCfg.getValueToInt()) != null) ? temp : 0;
        Date approveTime = seller.getCrownShipTime();
        if (!wholesaleValidationService.completed(seller.getUserId(), approveTime, new Date(), BigDecimal.valueOf(targetAmount))) {
            sellerService.resetCrownRight(sellerId);
            seller.setCrownShipTemp(Seller.CrownShipTemp.NO.getValue());
            seller.update();
            seller.increaseCrownApplyFailureTimes();

            Config wholesaleTimeCfg = Config.dao.findByKey(WHOLESALE_TIME);
            new TempCrownResettedNotification(seller.getUser().getWeixin())
                    .param(TempCrownResettedNotification.RESETTED_TIME, DateKit.today("yyyy-MM-dd HH:mm:ss"))
                    .param(TempCrownResettedNotification.REASON, String.format(TempCrownResettedNotification.reason, wholesaleTimeCfg.getValueToInt(), wholesaleAmountCfg.getValueToInt()))
                    .send();
            return failure("sellerId:" + sellerId + " haven't.reached.the.targetAmount,crown right is reset.");
        } else {
            seller.setCrownShipTemp(Seller.CrownShipTemp.NO.getValue());
            seller.resetCrownApplyFailureTimes();
            seller.update();
        }
        return success();
    }

    /**
     * 检查指定“线下皇冠商”是否推荐了“另一个线下皇冠商”, 二级推荐也可以
     *
     * @param parentSellerId 推荐人的sellerId（注意不是physicalSellerId）
     * @param sellerId       被推荐人的sellerId（注意不是physicalSellerId）
     * @return
     */
    public PhysicalSeller.CrownParentLevel getCrownParentLevel(int parentSellerId, int sellerId) {
        PhysicalSeller physicalSeller = PhysicalSeller.dao.findBySellerId(sellerId);
        if (physicalSeller.getParentSellerId() != null) {
            if (physicalSeller.getParentSellerId().equals(parentSellerId)) {
                return PhysicalSeller.CrownParentLevel.LEVEL_ONE;
            }
            PhysicalSeller parentPhysicalSeller = PhysicalSeller.dao.findBySellerId(physicalSeller.getParentSellerId());
            if (parentPhysicalSeller != null &&
                    parentPhysicalSeller.getParentSellerId() != null &&
                    parentPhysicalSeller.getParentSellerId().equals(parentSellerId)) {
                return PhysicalSeller.CrownParentLevel.LEVEL_TWO;
            }
        }

        return PhysicalSeller.CrownParentLevel.NONE;
    }

    /**
     * 是否已计算分成
     *
     * @return
     */
    public boolean settlementCounted(int orderId) {
        List<PhysicalPurchaseJournal> journals = PhysicalPurchaseJournal.dao.findByField(PhysicalPurchaseJournal.Fields.ORDER_ID.toString(), orderId);
        return journals.size() > 0;
    }

    /**
     * @return 线下皇冠商首次进货最小额度
     */
    public Integer getWholesaleAmount() {
        Config config = Config.dao.findByKey(WHOLESALE_AMOUNT);
        if (config != null && config.getValueToInt() != null) {
            return config.getValueToInt();
        }
        return null;
    }

    /**
     * 判断订单总额是否已经达到配置的某个标准
     *
     * @return
     */
    public boolean amountUpToTheStandard(BigDecimal orderTotalAmount) {
        boolean result = false;
        Integer wholesaleAmount = getWholesaleAmount();
        if (wholesaleAmount != null && orderTotalAmount.compareTo(new BigDecimal(wholesaleAmount)) >= 0) {
            result = true;
        }
        return result;
    }
}
