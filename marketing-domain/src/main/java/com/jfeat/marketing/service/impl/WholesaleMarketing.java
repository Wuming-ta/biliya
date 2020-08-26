package com.jfeat.marketing.service.impl;

import com.jfeat.core.BaseService;
import com.jfeat.core.Service;
import com.jfeat.core.ServiceContext;
import com.jfeat.marketing.CheckResult;
import com.jfeat.marketing.CouponUsage;
import com.jfeat.marketing.Marketing;
import com.jfeat.marketing.ShippingType;
import com.jfeat.marketing.wholesale.model.Wholesale;
import com.jfeat.marketing.wholesale.model.WholesalePricing;
import com.jfeat.marketing.wholesale.service.WholesaleConfigService;
import com.jfeat.marketing.wholesale.service.WholesaleService;
import com.jfeat.order.model.Order;
import com.jfeat.partner.model.Seller;
import com.jfeat.partner.service.PhysicalSellerService;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductSpecification;
import com.jfeat.service.WholesaleAccessAuthorityService;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.StrKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017/5/25.
 */

public class WholesaleMarketing implements Marketing {
    public static final String NAME = "WHOLESALE";
    public static final String DISPLAY_NAME = "批发";

    private Integer wholesaleId;
    private Integer userId;
    private Wholesale wholesale;
    private String province;
    private String city;
    private String district;

    private WholesaleService service = new WholesaleService();
    private PhysicalSellerService physicalSellerService = Enhancer.enhance(PhysicalSellerService.class);
    Logger logger = LoggerFactory.getLogger(WholesaleMarketing.class);

    @Override
    public void init(Integer marketingId, Integer userId, String province, String city, String district) {
        this.wholesaleId = marketingId;
        this.userId = userId;
        this.wholesale = Wholesale.dao.findById(wholesaleId);
        this.province = province;
        this.city = city;
        this.district = district;
    }

    @Override
    public boolean isEnabled() {
        return WholesaleConfigService.isEnabled();
    }

    @Override
    public boolean available(List<Integer> productIds, List<Integer> productSpecificationIds, List<Integer> quantities) {
        if (!WholesaleConfigService.isEnabled()) {
            return false;
        }
        Service s = ServiceContext.me().getService(WholesaleAccessAuthorityService.class.getName());
        if (s != null) {
            WholesaleAccessAuthorityService wholesaleAccessAuthorityService = (WholesaleAccessAuthorityService) s;
            if (!wholesaleAccessAuthorityService.authorized(userId)) {
                return false;
            }
        }
        if (service.checkWholesaleAvailable(wholesaleId)) {
            if (productIds == null || productSpecificationIds == null || productIds.size() > 1 || productSpecificationIds.size() > 1) {
                return false;
            }
            Product product = wholesale.getProduct();
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
            if (!result) {
                return result;
            }

            //检查该产品是否在该地区可销售
            List<WholesalePricing> wholesalePricings = wholesale.getWholesalePricings();
            String pc = province + "-" + city;
            WholesalePricing defaultWholesalePricing = null;
            for (WholesalePricing wholesalePricing : wholesalePricings) {
                //指定地区设置中寻找
                if (wholesalePricing.getIsDefault().equals(WholesaleService.UNDEFAULT)) {
                    if (containRegion(wholesalePricing.getRegion(), pc)) {
                        return wholesalePricing.getEnabled().equals(WholesaleService.ENABLED);
                    }
                } else if (defaultWholesalePricing == null) {
                    defaultWholesalePricing = wholesalePricing;
                }
            }
            //指定地区设置中找不到。则找默认地区设置
            return defaultWholesalePricing != null && defaultWholesalePricing.getEnabled().equals(WholesaleService.ENABLED);
        }
        return false;
    }

    /**
     * source contains target.
     *
     * @param sourceRegion 广东-广州|广西-桂林|广东-深圳
     * @param targetRegion 广东-广州
     * @return
     */
    private boolean containRegion(String sourceRegion, String targetRegion) {
        if (StrKit.isBlank(sourceRegion) || StrKit.isBlank(targetRegion)) {
            return false;
        }
        if (sourceRegion.contains(targetRegion)) {
            return true;
        }
        String province = targetRegion.split("-")[0].trim();
        for (String region : sourceRegion.split("\\|")) {
            if (region.trim().equals(province)) {
                return true;
            }
        }

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
        //根据省市区数据查询价格
        List<WholesalePricing> wholesalePricings = wholesale.getWholesalePricings();
        String pc = province + "-" + city;
        WholesalePricing defaultWholesalePricing = null;
        for (WholesalePricing wholesalePricing : wholesalePricings) {
            //指定地区设置中寻找
            if (wholesalePricing.getIsDefault().equals(WholesaleService.UNDEFAULT)) {
                if (containRegion(wholesalePricing.getRegion(), pc)) {
                    if (wholesalePricing.getEnabled().equals(WholesaleService.ENABLED)) {
                        return wholesalePricing.getPrice();
                    } else {
                        return null;
                    }
                }
            } else if (defaultWholesalePricing == null) {
                defaultWholesalePricing = wholesalePricing;
            }
        }
        //指定地区设置中找不到。则找默认地区设置
        if (defaultWholesalePricing != null && defaultWholesalePricing.getEnabled().equals(WholesaleService.ENABLED)) {
            return defaultWholesalePricing.getPrice();
        }
        return null;
    }

    @Override
    public boolean process(String orderNumber) {
        return BaseService.isSucceed(service.createMember(wholesaleId, userId, orderNumber));
    }

    @Override
    public String getDescription() {
        return wholesale.getMarketingName();
    }

    @Override
    public String getAdminUrl() {
        return "/wholesale/wholesaleList/" + wholesaleId;
    }

    @Override
    public Integer getId() {
        return wholesaleId;
    }

    @Override
    public Boolean canRefund(Integer orderId) {
        Order order = Order.dao.findById(orderId);
        Order.Status status = Order.Status.valueOf(order.getStatus());
        return status == Order.Status.PAID_CONFIRM_PENDING || status == Order.Status.CONFIRMED_DELIVER_PENDING;
    }

    //临时线下皇冠商首次下单必须达到某个数额
    @Override
    public CheckResult checkOrderRequest(BigDecimal orderTotalAmount) {
        CheckResult checkResult = new CheckResult(true, null);
        Seller seller = Seller.dao.findByUserId(userId);
        //如果不是临时线下皇冠商
        if (!(seller.isPhysicalSeller() && seller.isCrownShip() && seller.isCrownShipTemp())) {
            return checkResult;
        }

        Integer wholesaleAmount = physicalSellerService.getWholesaleAmount();
        if (wholesaleAmount == null) {
            return checkResult;
        }

        Integer wholesaleOrderCount = Order.dao.findPaid(userId, NAME);
        //如果之前下过>=设置要求数额的批发订单，则通过
        if (wholesaleOrderCount > 0) {
            return checkResult;
        }
        //否则查看今次下的批发订单是否>=设置要求数额
        if (!physicalSellerService.amountUpToTheStandard(orderTotalAmount)) {
            checkResult.setResult(false);
            checkResult.setMessage("临时皇冠商首次批发不能少于" + wholesaleAmount + "元");
            logger.debug("the orderTotalAmout {} didn't reached the standard", orderTotalAmount);
        }
        return checkResult;
    }

    @Override
    public String getMarketingName() {
        return wholesale == null ? null : wholesale.getMarketingName();
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
