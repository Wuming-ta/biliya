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

package com.jfeat.marketing.service.impl;

import com.jfeat.core.BaseService;
import com.jfeat.marketing.CheckResult;
import com.jfeat.marketing.CouponUsage;
import com.jfeat.marketing.Marketing;
import com.jfeat.marketing.ShippingType;
import com.jfeat.marketing.handler.PieceGroupExpiredHandler;
import com.jfeat.marketing.piece.model.PieceGroupPurchase;
import com.jfeat.marketing.piece.service.PieceGroupPurchaseConfigService;
import com.jfeat.marketing.piece.service.PieceGroupPurchaseService;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductSpecification;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;

import java.math.BigDecimal;
import java.util.List;

/**
 * 拼团营销
 * Created by jackyhuang on 17/5/3.
 */
public class PieceGroupMarketing implements Marketing {

    public static final String NAME = "PIECE-GROUP";
    public static final String DISPLAY_NAME = "开团";
    private static final String PIECE_GROUP_COUPON_TYPE_NAME = MARKETING_COUPON_PREFIX + "PIECE_GROUP";

    private PieceGroupPurchaseService service = new PieceGroupPurchaseService();

    private Integer pieceGroupPurchaseId;
    private Integer userId;
    private PieceGroupPurchase pieceGroupPurchase;
    private Integer masterId;

    @Override
    public void init(Integer marketingId, Integer userId, String province, String city, String district) {
        this.pieceGroupPurchaseId = marketingId;
        this.userId = userId;
        this.pieceGroupPurchase = PieceGroupPurchase.dao.findById(pieceGroupPurchaseId);
    }

    @Override
    public boolean isEnabled() {
        return PieceGroupPurchaseConfigService.isEnabled();
    }

    @Override
    public boolean available(List<Integer> productIds, List<Integer> productSpecificationIds, List<Integer> quantities) {
        if (!PieceGroupPurchaseConfigService.isEnabled()) {
            return false;
        }
        if (service.checkPieceGroupAvailable(pieceGroupPurchaseId)) {
            if (productIds == null || productSpecificationIds == null || productIds.size() > 1 || productSpecificationIds.size() > 1) {
                return false;
            }
            Product product = pieceGroupPurchase.getProduct();
            if (!product.getId().equals(productIds.get(0))) {
                return false;
            }
            List<ProductSpecification> specifications = product.getProductSpecifications();
            boolean result = true;
            if (specifications.size() > 0) {
                result = false;
                for (ProductSpecification specification : specifications) {
                    if (specification.getId().equals(productSpecificationIds.get(0))) {
                        result = true;
                        break;
                    }
                }
            }
            return result;
        }
        return false;
    }

    @Override
    public CouponUsage getCouponUsage(String couponTypeName) {
        if (pieceGroupPurchase.getCouponUsage() == CouponUsage.DISABLED.getValue()) {
            return CouponUsage.DISABLED;
        }
        if (pieceGroupPurchase.getCouponUsage() == CouponUsage.ENABLED_MARKETING.getValue()
                && pieceGroupPurchase.getMasterFree() == PieceGroupPurchase.MasterFree.YES.getValue()) {
            if (StrKit.notBlank(couponTypeName) && couponTypeName.equals(PIECE_GROUP_COUPON_TYPE_NAME)) {
                return CouponUsage.ENABLED_MARKETING;
            }
        }
        if (pieceGroupPurchase.getCouponUsage() == CouponUsage.ENABLED_SYSTEM.getValue()) {
            if (StrKit.notBlank(couponTypeName) && !couponTypeName.startsWith(MARKETING_COUPON_PREFIX)) {
                return CouponUsage.ENABLED_SYSTEM;
            }
        }
        return CouponUsage.DISABLED;
    }

    @Override
    public ShippingType getShippingType() {
        if (pieceGroupPurchase.getFreeShipping() == Product.FreeShipping.YES.getValue()) {
            return ShippingType.FREE;
        }
        return ShippingType.PRODUCT_BASED;
    }

    @Override
    public BigDecimal getPrice() {
        return pieceGroupPurchase.getPrice();
    }

    @Override
    public boolean process(String orderNumber) {
        Ret ret = service.openGroup(pieceGroupPurchaseId, userId, orderNumber);
        if (BaseService.isSucceed(ret)) {
            masterId = ret.get("master_id");
            PieceGroupExpiredHandler.add(masterId);
            return true;
        }
        return false;
    }

    @Override
    public String getDescription() {
        return pieceGroupPurchase.getMarketingName();
    }

    /**
     * 对于开团的情况，init()传入是masterid
     *
     * @return
     */
    @Override
    public String getAdminUrl() {
        return "/piece_group_purchase/memberList/" + pieceGroupPurchaseId;
    }

    @Override
    public Integer getId() {
        return masterId;
    }

    @Override
    public Boolean canRefund(Integer orderId) {
        return true;
    }

    @Override
    public CheckResult checkOrderRequest(BigDecimal bigDecimal) {
        return new CheckResult(true, null);
    }

    @Override
    public String getMarketingName() {
        return pieceGroupPurchase == null ? null : pieceGroupPurchase.getMarketingName();
    }

    @Override
    public boolean shouldChangeOrderStatusAfterPaid() {
        return true;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }
}
