package com.jfeat.marketing.piece.service;

import com.google.common.collect.Lists;
import com.jfeat.core.BaseService;
import com.jfeat.identity.model.User;
import com.jfeat.marketing.common.model.MarketingConfig;
import com.jfeat.marketing.piece.model.PieceGroupPurchase;
import com.jfeat.marketing.piece.model.PieceGroupPurchaseMaster;
import com.jfeat.marketing.piece.model.PieceGroupPurchaseMember;
import com.jfeat.marketing.piece.model.PieceGroupPurchasePricing;
import com.jfeat.member.model.Coupon;
import com.jfeat.member.model.CouponStrategy;
import com.jfeat.member.service.CouponStrategyService;
import com.jfeat.order.model.Order;
import com.jfeat.order.model.OrderCustomerService;
import com.jfeat.order.service.OrderService;
import com.jfeat.order.service.StoreUtil;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by kang on 2017/4/21.
 */
public class PieceGroupPurchaseService extends BaseService {

    private static final String PIECE_GROUP_FAILURE_REASON = "拼团失败, 退款回原账户";
    private String uploadDir = "pgp";
    public static final int PROMOTED = 1;
    public static final int UNPROMOTED = 0;
    public static final int ENABLED = 1;
    public static final int DISABLED = 0;

    private OrderService orderService = Enhancer.enhance(OrderService.class);


    public String getUploadDir() {
        return uploadDir;
    }

    private Ret setEnabled(int enabled) {
        MarketingConfig marketingConfig = MarketingConfig.dao.findFirstByField(MarketingConfig.Fields.TYPE.toString(),
                MarketingConfig.Type.PIECEGROUP.toString());
        if (marketingConfig == null) {
            return failure("marketingConfig is null.");
        }
        marketingConfig.setEnabled(enabled);
        marketingConfig.update();
        return success();
    }

    public Ret enable() {
        return setEnabled(ENABLED);
    }

    public Ret disable() {
        return setEnabled(DISABLED);
    }


    @Before(Tx.class)
    public Ret createPieceGroupPurchase(PieceGroupPurchase pieceGroupPurchase, List<PieceGroupPurchasePricing> pieceGroupPurchasePricings) {
        if (pieceGroupPurchase == null) {
            return failure();
        }
        pieceGroupPurchase.save();

        if (pieceGroupPurchasePricings != null) {
            for (PieceGroupPurchasePricing pieceGroupPurchasePricing : pieceGroupPurchasePricings) {
                pieceGroupPurchasePricing.setPieceGroupPurchaseId(pieceGroupPurchase.getId());
                pieceGroupPurchasePricing.save();
            }
        }
        return success("piece_group_purchase.create.success");
    }

    @Before(Tx.class)
    public Ret updatePieceGroupPurchase(PieceGroupPurchase pieceGroupPurchase, List<PieceGroupPurchasePricing> pieceGroupPurchasePricings) {
        if (pieceGroupPurchase == null) {
            return failure();
        }
        pieceGroupPurchase.update();

        new PieceGroupPurchasePricing().deleteByField(PieceGroupPurchasePricing.Fields.PIECE_GROUP_PURCHASE_ID.toString(), pieceGroupPurchase.getId());
        if (pieceGroupPurchasePricings != null) {
            for (PieceGroupPurchasePricing pieceGroupPurchasePricing : pieceGroupPurchasePricings) {
                pieceGroupPurchasePricing.setPieceGroupPurchaseId(pieceGroupPurchase.getId());
                pieceGroupPurchasePricing.save();

            }
        }
        return success("piece_group_purchase.update.success");
    }

    @Before(Tx.class)
    public Ret handlePieceGroupMasterExpired(Integer pieceGroupPurchaseMasterId) {
        PieceGroupPurchaseMaster pieceGroupPurchaseMaster = PieceGroupPurchaseMaster.dao.findById(pieceGroupPurchaseMasterId);
        if (pieceGroupPurchaseMaster == null) {
            logger.error("pieceGroupPurchaseMaster not found with id:{}", pieceGroupPurchaseMasterId);
            return failure("piece_group_purchase_master.not.found");
        }
        PieceGroupPurchaseMaster.Status status = PieceGroupPurchaseMaster.Status.valueOf(pieceGroupPurchaseMaster.getStatus());
        if (status != PieceGroupPurchaseMaster.Status.OPENING) {
            return failure("invalid.status");
        }
        PieceGroupPurchase pieceGroupPurchase = pieceGroupPurchaseMaster.getPieceGroupPurchase();
        int minParticipatorCount = pieceGroupPurchase.getMinParticipatorCount();
        List<PieceGroupPurchaseMember> members = PieceGroupPurchaseMember.dao.findByMasterIdAndStatus(pieceGroupPurchaseMasterId, PieceGroupPurchaseMember.Status.PAID.toString());
        int realParticipatorCount = members.size();
        if (realParticipatorCount < minParticipatorCount) {
            logger.debug("piece group failure. id: {}, real participator count: {}, min participator count: {}",
                    pieceGroupPurchaseMasterId, realParticipatorCount, minParticipatorCount);
            pieceGroupPurchaseMaster.setStatus(PieceGroupPurchaseMaster.Status.FAIL.toString());
            pieceGroupPurchaseMaster.update();

            for (PieceGroupPurchaseMember member : members) {
                Order order = member.getOrder();
                logger.debug("refunding for order {}", order.getOrderNumber());
                logger.debug("==== step 1: create customer service, ordernumber = {}", order.getOrderNumber());
                OrderCustomerService orderCustomerService = new OrderCustomerService();
                orderCustomerService.setOrderId(order.getId());
                orderCustomerService.setReason(PIECE_GROUP_FAILURE_REASON);
                orderCustomerService.setRefundFee(order.getTotalPrice());
                orderCustomerService.setListToImages(null);
                orderCustomerService.setServiceType(OrderCustomerService.ServiceType.REFUND.toString());
                Ret ret = orderService.applyCustomerService(order, orderCustomerService, null, null);
                logger.info("applyCustomerService {} result = {}", order.getOrderNumber(), ret.getData());
                if (!BaseService.isSucceed(ret)) {
                    logger.error("create order customer service failure. member = {}", member.toJson());
                    continue;
                }
                orderCustomerService.setStatus(OrderCustomerService.Status.REFUND_PENDING.toString());
                ret = orderService.updateCustomerService(orderCustomerService);
                logger.info("update order customer service status to REFUND_PENDING. {}", ret.getData());

                logger.debug("==== step 2: refund order, ordernumber = {}", order.getOrderNumber());
                if (order.getTotalPrice().compareTo(BigDecimal.ZERO) > 0) {
                    try {
                        ret = orderService.refundOrder(orderCustomerService, false);
                        logger.info("refundOrder {} result = {}", order.getOrderNumber(), ret.getData());
                        if (!BaseService.isSucceed(ret)) {
                            logger.error("refund failure, member = {}", member.toJson());
                            continue;
                        }
                        User user = order.getUser();
                        String note = String.format("用户 %s 订单号 %s 退货单号 %s，回退库存", user.getName(), order.getOrderNumber(), orderCustomerService.getServiceNumber());
                        Long warehouseId = StoreUtil.getWarehouseId(order.getStoreId());
                        orderService.decreaseRefundProductSales(user.getId().longValue(), user.getLoginName(), orderCustomerService, note, warehouseId);
                    } catch (Exception ex) {
                        // refund may throw exception, so skip the rest steps.
                        // then the admin should refund the order manually.
                        ex.printStackTrace();
                        logger.error(ex.getMessage());
                        logger.error(ex.toString());
                        for (StackTraceElement element : ex.getStackTrace()) {
                            logger.error("    {}:{} {}", element.getFileName(), element.getLineNumber(), element.getMethodName());
                        }
                        continue;
                    }
                }

                logger.debug("==== step 3: update customer service to REFUNDED, {}", orderCustomerService.getId());
                orderCustomerService.setStatus(OrderCustomerService.Status.REFUNDED.toString());
                ret = orderService.updateCustomerService(orderCustomerService);
                logger.debug("order-customer-service = {}, result = {}", orderCustomerService, ret.getData());

                logger.debug("==== step 4: update order status to CLOSED_REFUNDED. ordernumber = {}", order.getOrderNumber());
                order.setStatus(Order.Status.CLOSED_REFUNDED.toString());
                ret = orderService.updateOrder(order);
                logger.debug("update order {} result = {}", order.getOrderNumber(), ret.getData());

                logger.debug("==== step 5: update piece group purchase member status to REFUND. member = {}", member);
                member.setStatus(PieceGroupPurchaseMember.Status.REFUND.toString());
                member.update();
                logger.debug("after updated: member = {}", member);
            }

        } else {
            logger.debug("piece group success. id: {}, real participator count: {}, min participator count: {}",
                    pieceGroupPurchaseMasterId, realParticipatorCount, minParticipatorCount);
            pieceGroupPurchaseMaster.setStatus(PieceGroupPurchaseMaster.Status.DEAL.toString());
            pieceGroupPurchaseMaster.update();

            //拼团成功, 根据价格阶梯定义进行差额退款 TODO
            List<PieceGroupPurchasePricing> pricings = pieceGroupPurchase.getPricings();
            Collections.reverse(pricings);
            BigDecimal finalPrice = pieceGroupPurchase.getPrice();
            for (PieceGroupPurchasePricing pricing : pricings) {
                if (realParticipatorCount >= pricing.getParticipatorCount()) {
                    finalPrice = pricing.getPrice();
                    break;
                }
            }
            logger.debug("final price: {}, piece group purchase price: {}", finalPrice, pieceGroupPurchase.getPrice());
            if (finalPrice.compareTo(pieceGroupPurchase.getPrice()) < 0) {
                logger.debug("start to refund");
                for (PieceGroupPurchaseMember member : members) {
                    //BigDecimal refundMoney = member.getOrder().getOrderItems();
                }
            }

            //拼团成功，根据免单优惠券赠送策略来赠送免单优惠券
            CouponGiveStrategy couponGiveStrategyService = CouponGiveStrategyHolder.me().getCouponStrategy(pieceGroupPurchase.getCouponStrategyServiceName());
            List<Integer> userIds = couponGiveStrategyService.getUsers(members);
            if (userIds != null && userIds.size() > 0) {
                CouponStrategyService couponStrategyService = new CouponStrategyService();
                for (Integer userId : userIds) {
                    List<CouponStrategy> couponStrategies = CouponStrategy.dao.findByType(CouponStrategy.Type.PIECE_GROUP);
                    for (CouponStrategy strategy : couponStrategies) {
                        Ret ret = couponStrategyService.dispatchCoupon(strategy, userId, Coupon.Source.ORDER);
                        logger.debug("Coupon dispatch result. Strategy = {}, Ret = {}", strategy.getName(), ret.getData());
                    }

                }
            }
        }
        return success();
    }

    /**
     * 开团前检查该拼团活动是否可用
     *
     * @param pieceGroupPurchaseId
     * @return
     */
    public boolean checkPieceGroupAvailable(Integer pieceGroupPurchaseId) {
        PieceGroupPurchase pieceGroupPurchase = PieceGroupPurchase.dao.findById(pieceGroupPurchaseId);
        if (pieceGroupPurchase == null) {
            return false;
        }
        if (!PieceGroupPurchase.Status.ONSELL.toString().equals(pieceGroupPurchase.getStatus())) {
            return false;
        }
        Integer duration = pieceGroupPurchase.getDuration();
        if (duration == null || duration <= 0) {
            return false;
        }
        return true;
    }

    /**
     * 加入拼团前检查该拼团活动是否可用
     *
     * @param pieceGroupPurchaseMasterId
     * @return
     */
    public boolean checkPieceGroupJointAvailable(Integer pieceGroupPurchaseMasterId) {
        PieceGroupPurchaseMaster pieceGroupPurchaseMaster = PieceGroupPurchaseMaster.dao.findById(pieceGroupPurchaseMasterId);
        if (pieceGroupPurchaseMaster == null) {
            logger.debug("piece group purchase master is null. {}", pieceGroupPurchaseMasterId);
            return false;
        }
        PieceGroupPurchase pieceGroupPurchase = pieceGroupPurchaseMaster.getPieceGroupPurchase();
        if (PieceGroupPurchase.Status.OFFSELL.toString().equals(pieceGroupPurchase.getStatus())) {
            logger.debug("piece group purchase is offsell. {}", pieceGroupPurchase);
            return false;
        }
        if (!PieceGroupPurchaseMaster.Status.OPENING.toString().equals(pieceGroupPurchaseMaster.getStatus())) {
            logger.debug("piece group purchse master is not opening. {}", pieceGroupPurchaseMaster);
            return false;
        }
        return true;
    }

    /**
     * 开团
     */
    @Before(Tx.class)
    public Ret openGroup(Integer pieceGroupPurchaseId, Integer userId, String orderNumber) {
        if (User.dao.findById(userId) == null) {
            return failure("invalid.user");
        }

        PieceGroupPurchase pieceGroupPurchase = PieceGroupPurchase.dao.findById(pieceGroupPurchaseId);
        logger.debug("pieceGroupPurchase = {}", pieceGroupPurchase);
        if (!PieceGroupPurchase.Status.ONSELL.toString().equals(pieceGroupPurchase.getStatus())) {
            return failure("piece.group.is.not.onsell");
        }
        Integer duration = pieceGroupPurchase.getDuration();
        if (duration == null || duration <= 0) {
            return failure("invalid.piece.group.duration");
        }
        PieceGroupPurchaseMaster master = new PieceGroupPurchaseMaster();
        master.setPieceGroupPurchaseId(pieceGroupPurchaseId);
        master.setUserId(userId);
        Date now = new Date();
        master.setStartTime(now);
        master.setEndTime(new Date(now.getTime() + duration * 1000));
        master.save();
        logger.debug("piece group purchase master: {}", master);
        PieceGroupPurchaseMember member = new PieceGroupPurchaseMember();
        member.setMasterId(master.getId());
        member.setUserId(userId);
        member.setOrderNumber(orderNumber);
        member.save();
        logger.debug("piece group purchase member: {}", member);
        return success().put("master_id", master.getId());
    }

    /**
     * 加入拼团
     */
    @Before(Tx.class)
    public Ret joinGroup(Integer pieceGroupPurchaseMasterId, Integer userId, String orderNumber) {
        if (User.dao.findById(userId) == null) {
            return failure("invalid.user");
        }
        PieceGroupPurchaseMaster pieceGroupPurchaseMaster = PieceGroupPurchaseMaster.dao.findById(pieceGroupPurchaseMasterId);
        if (pieceGroupPurchaseMaster == null) {
            return failure("invalid.master");
        }
        if (pieceGroupPurchaseMasterId.equals(userId)) {
            return failure("this.user.is.the.group.master");
        }

        if (PieceGroupPurchase.Status.OFFSELL.toString().equals(pieceGroupPurchaseMaster.getPieceGroupPurchase().getStatus())) {
            return failure("purchase.is.offsell");
        }
        if (!PieceGroupPurchaseMaster.Status.OPENING.toString().equals(pieceGroupPurchaseMaster.getStatus())) {
            return failure("purchase.master.is.not.opening");
        }
        PieceGroupPurchaseMember member = new PieceGroupPurchaseMember();
        member.setMasterId(pieceGroupPurchaseMasterId);
        member.setUserId(userId);
        member.setOrderNumber(orderNumber);
        member.save();
        logger.debug("piece group purchase member: {}", member);
        return success();
    }

    //随机得到一个已支付的推荐的团长
    public PieceGroupPurchaseMaster getRandomPromotedMaster(Integer userId) {
        List<PieceGroupPurchaseMaster> masters = userIdFilter(userId,
                PieceGroupPurchaseMaster.dao.findByStatus(PieceGroupPurchaseMaster.Status.OPENING.toString(), PROMOTED, PieceGroupPurchaseMember.Status.PAID.toString()));
        return masters.size() > 0 ? masters.get(new Random().nextInt(masters.size())) : null;
    }

    /**
     * 过滤掉userId为某个值的团长
     */

    public List<PieceGroupPurchaseMaster> userIdFilter(Integer userId, List<PieceGroupPurchaseMaster> promotedMastersBeforeFilter) {
        if (userId == null || promotedMastersBeforeFilter == null || promotedMastersBeforeFilter.size() == 0) {
            return promotedMastersBeforeFilter;
        }
        List<PieceGroupPurchaseMaster> masters = Lists.newLinkedList();
        for (PieceGroupPurchaseMaster master : promotedMastersBeforeFilter) {
            if (!userId.equals(master.getUserId())) {
                masters.add(master);
            }
        }
        return masters;
    }

    public List<PieceGroupPurchaseMaster> paidMembersReachedFilter(List<PieceGroupPurchaseMaster> masters) {
        if (masters == null || masters.size() == 0) {
            return masters;
        }
        List<PieceGroupPurchaseMaster> result = Lists.newLinkedList();
        for (PieceGroupPurchaseMaster master : masters) {
            int paidMembersCount = PieceGroupPurchaseMember.dao.findByMasterIdAndStatus(master.getId(), PieceGroupPurchaseMember.Status.PAID.toString()).size();
            if (master.getPieceGroupPurchase().getMinParticipatorCount().intValue() > paidMembersCount) {
                result.add(master);
            }
        }
        return result;
    }

}
