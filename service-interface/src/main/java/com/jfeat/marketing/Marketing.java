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

package com.jfeat.marketing;

import java.math.BigDecimal;
import java.util.List;

/**
 * 营销活动
 * Created by jackyhuang on 17/4/26.
 */
public interface Marketing {

    static final String MARKETING_COUPON_PREFIX = "MARKETING_";

    /**
     * 营销活动ID, 实现类应该根据这个ID去查找相应的营销
     * @param marketingId
     */
    void init(Integer marketingId, Integer userId, String province, String city, String district);

    /**
     * 该活动是否启动
     * @return
     */
    boolean isEnabled();

    /**
     * 该活动是否还有效
     * 参数作用: 校验订单里面的订单项是否合法. 三个参数一一对应, 如果没有规格则为null。
     * @return
     */
    boolean available(List<Integer> productIds, List<Integer> productSpecificationIds, List<Integer> quantities);

    /**
     * 判断整个订单是否有效
     * @return
     */
    CheckResult checkOrderRequest(BigDecimal orderTotalAmount);

    /**
     * 是否可以用优惠券
     * @return
     */
    CouponUsage getCouponUsage(String couponType);

    /**
     * 是否包邮
     * @return
     */
    ShippingType getShippingType();

    /**
     * 返回该活动的定价
     * @return
     */
    BigDecimal getPrice();

    /**
     * 活动处理
     * @return
     */
    boolean process(String orderNumber);

    /**
     * 活动描述
     * @return
     */
    String getDescription();

    /**
     * 返回管理后台查看地址
     * @return
     */
    String getAdminUrl();

    /**
     * 返回当前用户参与活动后生产的ID
     * @return
     */
    Integer getId();

    /**
     * 该营销订单能否退款
     * @param orderId
     * @return
     */
    Boolean canRefund(Integer orderId);

    /**
     * 返回活动的名称
     * @return
     */
    String getMarketingName();

    /**
     * 支付后是否改变订单状态为 待发货
     * @return
     */
    boolean shouldChangeOrderStatusAfterPaid();

    /**
     * 出错时的错误信息
     * @return
     */
    String getErrorMessage();
}
