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
import com.jfeat.marketing.piece.model.PieceGroupPurchase;
import com.jfeat.marketing.piece.model.PieceGroupPurchaseMaster;
import com.jfeat.marketing.piece.service.PieceGroupPurchaseConfigService;
import com.jfeat.marketing.piece.service.PieceGroupPurchaseService;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductSpecification;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by jackyhuang on 17/5/3.
 */
public class PieceGroupJointMarketing implements Marketing {
    private static final Logger logger = LoggerFactory.getLogger(PieceGroupJointMarketing.class);

    public static final String NAME = "PIECE-GROUP-JOINT";
    public static final String DISPLAY_NAME = "参团";

    private PieceGroupPurchaseService service = new PieceGroupPurchaseService();

    private Integer pieceGroupPurchaseMasterId;
    private Integer userId;
    private PieceGroupPurchase pieceGroupPurchase;
    private PieceGroupPurchaseMaster pieceGroupPurchaseMaster;

    @Override
    public void init(Integer marketingId, Integer userId, String province, String city, String district) {
        this.pieceGroupPurchaseMasterId = marketingId;
        this.userId = userId;
        this.pieceGroupPurchaseMaster = PieceGroupPurchaseMaster.dao.findById(pieceGroupPurchaseMasterId);
        if (this.pieceGroupPurchaseMaster != null) {
            this.pieceGroupPurchase = this.pieceGroupPurchaseMaster.getPieceGroupPurchase();
        }
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
        if (service.checkPieceGroupJointAvailable(pieceGroupPurchaseMasterId)) {
            if (productIds == null || productSpecificationIds == null || productIds.size() > 1 || productSpecificationIds.size() > 1) {
                logger.warn("invalid product.");
                return false;
            }
            if (this.pieceGroupPurchaseMaster.getUserId().equals(userId)) {
                logger.warn("cannot join your own piece group purchase. masterId={}, userId={}", pieceGroupPurchaseMasterId, userId);
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
        Ret ret = service.joinGroup(pieceGroupPurchaseMasterId, userId, orderNumber);
        return BaseService.isSucceed(ret);
    }

    @Override
    public String getDescription() {
        return pieceGroupPurchase.getMarketingName();
    }

    @Override
    public String getAdminUrl() {
        return "/piece_group_purchase/memberList/" + pieceGroupPurchaseMasterId;
    }

    @Override
    public Integer getId() {
        return this.pieceGroupPurchaseMasterId;
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
