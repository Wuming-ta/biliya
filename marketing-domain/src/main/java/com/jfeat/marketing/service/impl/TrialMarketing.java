package com.jfeat.marketing.service.impl;

import com.jfeat.core.BaseService;
import com.jfeat.marketing.CheckResult;
import com.jfeat.marketing.CouponUsage;
import com.jfeat.marketing.Marketing;
import com.jfeat.marketing.ShippingType;
import com.jfeat.marketing.trial.model.Trial;
import com.jfeat.marketing.trial.service.TrialApplicationService;
import com.jfeat.marketing.trial.service.TrialConfigService;
import com.jfeat.marketing.trial.service.TrialService;
import com.jfeat.order.model.Order;
import com.jfeat.partner.service.PhysicalSellerService;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductSpecification;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.StrKit;
import org.mvel2.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by kang on 2018/6/29.
 */

public class TrialMarketing implements Marketing {
    public static final String NAME = "TRIAL";
    public static final String DISPLAY_NAME = "试用装";

    private Integer trialId;
    private Integer userId;
    private Trial trial;
    private String error;

    private TrialService service = new TrialService();
    private TrialApplicationService trialApplicationService = new TrialApplicationService();
    Logger logger = LoggerFactory.getLogger(TrialMarketing.class);

    @Override
    public void init(Integer marketingId, Integer userId, String province, String city, String district) {
        this.trialId = marketingId;
        this.userId = userId;
        this.trial = Trial.dao.findById(trialId);
    }

    @Override
    public boolean isEnabled() {
        return TrialConfigService.isEnabled();
    }

    /**
     * @param productIds
     * @param productSpecificationIds
     * @param quantities
     * @return
     */
    @Override
    public boolean available(List<Integer> productIds, List<Integer> productSpecificationIds, List<Integer> quantities) {
        if (!TrialConfigService.isEnabled()) {
            return false;
        }

        if (service.checkTrialAvailable(trialId)) {
            if (productIds == null || productSpecificationIds == null || quantities == null
                    || productIds.size() > 1 || productSpecificationIds.size() > 1 || quantities.size() > 1) {
                error = "产品不能超过1件";
                return false;
            }
            //检查是否重复试用
            if (service.isPartaken(trial, userId)) {
                error = "不能重复参加";
                return false;
            }

            Product product = trial.getProduct();
            if (!product.getId().equals(productIds.get(0))) {
                return false;
            }
            List<ProductSpecification> specifications = product.getProductSpecifications();
            boolean result = true;
            if (!specifications.isEmpty()) {
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
        error = "试用活动已停止";
        return false;
    }

    @Override
    public CouponUsage getCouponUsage(String couponTypeName) {
        return CouponUsage.DISABLED;
    }

    @Override
    public ShippingType getShippingType() {
        return ShippingType.FREE;
    }

    @Override
    public BigDecimal getPrice() {
        if (trial != null) {
            return trial.getPrice();
        }
        return null;
    }

    @Override
    public boolean process(String orderNumber) {
        Order order = Order.dao.findByOrderNumber(orderNumber);
        trialApplicationService.apply(trialId, userId, order);
        return true;
    }

    @Override
    public String getDescription() {
        return trial.getName();
    }

    @Override
    public String getAdminUrl() {
        return "/trial_application?trial_id=" + trialId;
    }

    @Override
    public Integer getId() {
        return trialId;
    }

    @Override
    public Boolean canRefund(Integer orderId) {
        return false;
    }

    @Override
    public CheckResult checkOrderRequest(BigDecimal orderTotalAmount) {
        return new CheckResult(true, null);
    }

    @Override
    public String getMarketingName() {
        return trial == null ? null : trial.getName();
    }

    /**
     * 支付后不改变订单状态为 待发货
     * @return
     */
    @Override
    public boolean shouldChangeOrderStatusAfterPaid() {
        return false;
    }

    @Override
    public String getErrorMessage() {
        return error;
    }
}
