package com.jfeat.marketing.wholesale.service;

import com.jfeat.core.BaseService;
import com.jfeat.identity.model.User;
import com.jfeat.marketing.common.model.MarketingConfig;
import com.jfeat.marketing.exception.WholesalePricingException;
import com.jfeat.marketing.wholesale.model.Wholesale;
import com.jfeat.marketing.wholesale.model.WholesaleMember;
import com.jfeat.marketing.wholesale.model.WholesalePricing;
import com.jfeat.member.model.Contact;
import com.jfeat.order.model.Order;
import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.Date;
import java.util.List;

/**
 * Created by kang on 2017/5/17.
 */
public class WholesaleService extends BaseService {
    public static final int ENABLED = 1;
    public static final int DISABLED = 0;
    public static final int DEFAULT = 1;
    public static final int UNDEFAULT = 0;
    private String uploadDir = "ws";

    public String getUploadDir() {
        return uploadDir;
    }

    private Ret setEnabled(int enabled) {
        MarketingConfig marketingConfig = MarketingConfig.dao.findFirstByField(MarketingConfig.Fields.TYPE.toString(),
                MarketingConfig.Type.WHOLESALE.toString());
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
    public Ret createWholeSale(Wholesale wholesale, List<WholesalePricing> wholesalePricings) {
        if (wholesale == null) {
            return failure("wholesale.is.null");
        }
        if (wholesalePricings == null || wholesalePricings.size() == 0) {
            return failure("wholesale_pricings.is.required");
        }
        wholesale.save();
        for (WholesalePricing wholesalePricing : wholesalePricings) {
            wholesalePricing.setWholesaleId(wholesale.getId());
            wholesalePricing.save();
        }
        return success("wholesale.create.success");
    }

    @Before(Tx.class)
    public Ret updateWholesale(Wholesale wholesale, List<WholesalePricing> wholesalePricings) {
        if (wholesale == null) {
            return failure("wholesale.is.null");
        }
        if (wholesalePricings == null || wholesalePricings.size() == 0) {
            return failure("wholesale_pricings.is.required");
        }
        wholesale.update();
        new WholesalePricing().deleteByField(WholesalePricing.Fields.WHOLESALE_ID.toString(), wholesale.getId());
        for (WholesalePricing wholesalePricing : wholesalePricings) {
            wholesalePricing.setWholesaleId(wholesale.getId());
            wholesalePricing.save();
        }
        return success("wholesale.update.success");
    }

    /**
     * 检查批发活动是否可用
     *
     * @param wholesaleId
     * @return
     */
    public boolean checkWholesaleAvailable(Integer wholesaleId) {
        Wholesale wholesale = Wholesale.dao.findById(wholesaleId);
        if (wholesale == null) {
            return false;
        }
        if (!Wholesale.Status.ONSELL.toString().equals(wholesale.getStatus())) {
            return false;
        }
        return true;
    }

    public Ret createMember(Integer wholesaleId, Integer userId, String orderNumber) {
        Wholesale wholesale = Wholesale.dao.findById(wholesaleId);
        if (wholesale == null) {
            return failure("wholesaleId is null.");
        }
        User user = User.dao.findById(userId);
        if (user == null) {
            return failure("userId is null.");
        }
        Order order = Order.dao.findByOrderNumber(orderNumber);
        if (order == null) {
            return failure("order not found.");
        }
        WholesaleMember wholesaleMember = new WholesaleMember();
        wholesaleMember.setWholesaleId(wholesaleId);
        wholesaleMember.setUserId(userId);
        wholesaleMember.setCreatedTime(new Date());
        wholesaleMember.setOrderNumber(orderNumber);
        wholesaleMember.setUserName(user.getName());
        wholesaleMember.setUserRealName(user.getRealName());
        wholesaleMember.setTotalPrice(order.getTotalPrice());
        wholesaleMember.save();
        logger.debug("wholesale member: {}", wholesaleMember);
        return success();
    }

    public String getRegion(Integer userId) {
        if (User.dao.findById(userId) == null) {
            return null;
        }
        Contact contact = Contact.dao.findDefaultByUserId(userId);
        return contact == null ? null : contact.getProvince() + "-" + contact.getCity();
    }

    /**
     * 根据用户保存的默认配送地区计算出适用的WholesalePricing
     *
     * @param wholesale
     * @param region
     * @return
     */
    public WholesalePricing getWholesalePricing(Wholesale wholesale, String region) throws WholesalePricingException {
        if (!checkWholesaleAvailable(wholesale.getId())) {
            throw new WholesalePricingException("wholesale.is.not.available");
        }
        if (StrKit.isBlank(region)) {
            throw new WholesalePricingException("用户尚未设置默认配送区域");
        }
        List<WholesalePricing> wholesalePricings = wholesale.getWholesalePricings();
        WholesalePricing defaultWholesalePricing = null;
        for (WholesalePricing wholesalePricing : wholesalePricings) {
            //指定地区设置中寻找
            if (wholesalePricing.getIsDefault().equals(WholesaleService.UNDEFAULT)) {
                if (containRegion(wholesalePricing.getRegion(), region)) {
                    if (wholesalePricing.getEnabled().equals(WholesaleService.ENABLED)) {
                        return wholesalePricing;
                    } else {
                        throw new WholesalePricingException("不配送到此区域：" + region);
                    }
                }
            } else if (defaultWholesalePricing == null) {
                defaultWholesalePricing = wholesalePricing;
            }
        }
        //指定地区设置中找不到，则找默认地区设置
        if (defaultWholesalePricing != null && defaultWholesalePricing.getEnabled().equals(WholesaleService.ENABLED)) {
            return defaultWholesalePricing;
        }
        throw new WholesalePricingException(String.format("找不到与%s匹配的价格定义", region));
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

}