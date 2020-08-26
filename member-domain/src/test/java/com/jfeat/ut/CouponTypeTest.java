package com.jfeat.ut;

import com.jfeat.AbstractTestCase;
import com.jfeat.member.model.CouponTemplate;
import com.jfeat.member.model.base.CouponTemplateBase;
import com.jfeat.ruleengine.Context;
import com.jfeat.ruleengine.MvelContext;
import com.jfeat.ruleengine.RuleEngineProcessor;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Created by kang on 2016/11/7.
 */
public class CouponTypeTest extends AbstractTestCase {
    private static final String LimitedProductVoucherMessageTemplate = "【限制型产品代金券】【条件：productId=%s，totalPrice>=%s】【优惠：抵扣%s元】";
    private static final String ProductVoucherMessageTemplate = "【无限制型产品代金券】【条件：productId=%s】【优惠：抵扣%s元】";
    private static final String LimitedProductCouponMessageTemplate = "【限制型产品折扣券】【条件：productId=%s,totalPrice>=%s】【优惠：打折%s】";
    private static final String ProductCouponMessageTemplate = "【无限制型产品折扣券】【条件：productId=%s】【优惠：打折%s】";

    @Test
    public void testProductCoupon() {

        Integer originalPrice = 20000; //原价
        Integer errorPrice = 8000; //不满足条件的原价
        String totalPriceCond = "10000"; // >=totalPriceCond 才符合条件
        String offsetAmount = "5000"; //抵扣金额
        String discount = "80"; //折扣，80表示8折

        {
            //限制型产品代金券
            RuleEngineProcessor processor = new RuleEngineProcessor();
            CouponTemplate couponTemplate = CouponTemplate.dao.findById(1);
            processor.init(couponTemplate.getCond().replace("#id#", "1").replace("#totalPrice#", totalPriceCond).replace("#money#", offsetAmount));
            Context context = new MvelContext();

            context.put("productId", 1);
            context.put("finalPrice", originalPrice);
            context.put("totalPrice", originalPrice);
            processor.process(context, "getFinalPrice");
            Number tempFinalPrice = context.get("finalPrice");
            System.out.println(String.format(LimitedProductVoucherMessageTemplate, 1, totalPriceCond, offsetAmount));
            System.out.println("原价：" + originalPrice);
            System.out.println("使用限制型产品代金券后：" + tempFinalPrice);
            assertEquals(originalPrice.intValue() - Integer.parseInt(offsetAmount), tempFinalPrice.intValue());
        }
        {
            //限制型产品代金券（不满足条件）
            RuleEngineProcessor processor = new RuleEngineProcessor();
            CouponTemplate couponTemplate = CouponTemplate.dao.findById(1);
            processor.init(couponTemplate.getCond().replace("#id#", "1").replace("#totalPrice#", totalPriceCond).replace("#money#", offsetAmount));
            Context context = new MvelContext();
            context = new MvelContext();
            context.put("productId", 1);
            context.put("finalPrice", errorPrice);
            context.put("totalPrice", errorPrice);
            processor.process(context, "getFinalPrice");
            Number tempFinalPrice = context.get("finalPrice");
            System.out.println(String.format(LimitedProductVoucherMessageTemplate, 1, totalPriceCond, offsetAmount));
            System.out.println("原价：" + errorPrice);
            System.out.println("使用限制型产品代金券后：" + tempFinalPrice);
            assertEquals(errorPrice.intValue(), tempFinalPrice.intValue());
        }
        {
            //无限制型产品代金券
            RuleEngineProcessor processor = new RuleEngineProcessor();
            CouponTemplate couponTemplate = CouponTemplate.dao.findById(2);
            processor.init(couponTemplate.getCond().replace("#id#", "1").replace("#money#", offsetAmount));
            Context context = new MvelContext();
            context.put("productId", 1);
            context.put("finalPrice", originalPrice);
            context.put("totalPrice", originalPrice);
            processor.process(context, "getFinalPrice");
            Number tempFinalPrice = context.get("finalPrice");
            System.out.println(String.format(ProductVoucherMessageTemplate, 1, offsetAmount));
            System.out.println("原价：" + originalPrice);
            System.out.println(tempFinalPrice);
            assertEquals(originalPrice - Integer.parseInt(offsetAmount), tempFinalPrice.intValue());
        }

        {
            //限制型产品折扣券
            RuleEngineProcessor processor = new RuleEngineProcessor();
            CouponTemplate couponTemplate = CouponTemplate.dao.findById(3);
            processor.init(couponTemplate.getCond().replace("#id#", "1").replace("#totalPrice#", totalPriceCond).replace("#discount#", discount));
            Context context = new MvelContext();
            context.put("productId", 1);
            context.put("finalPrice", originalPrice);
            context.put("totalPrice", originalPrice);
            processor.process(context, "getFinalPrice");
            Number tempFinalPrice = context.get("finalPrice");
            System.out.println(String.format(LimitedProductCouponMessageTemplate, 1, totalPriceCond, discount + "%"));
            System.out.println("原价：" + originalPrice);
            System.out.println(tempFinalPrice);
            assertEquals(originalPrice * Integer.parseInt(discount) / 100, tempFinalPrice.intValue());
        }
        {
            //无限制型产品折扣券
            RuleEngineProcessor processor = new RuleEngineProcessor();
            CouponTemplate couponTemplate = CouponTemplate.dao.findById(4);
            processor.init(couponTemplate.getCond().replace("#id#", "1").replace("#totalPrice#", totalPriceCond).replace("#discount#", discount));
            Context context = new MvelContext();
            context.put("productId", 1);
            context.put("finalPrice", originalPrice);
            context.put("totalPrice", originalPrice);
            processor.process(context, "getFinalPrice");
            Number tempFinalPrice = context.get("finalPrice");
            System.out.println(String.format(ProductCouponMessageTemplate, 1, discount + "%"));
            System.out.println("原价：" + originalPrice);
            System.out.println(tempFinalPrice);
            assertEquals(originalPrice * Integer.parseInt(discount) / 100, tempFinalPrice.intValue());
        }
    }

    @Test
    public void testMarketingCoupon() {
        RuleEngineProcessor processor = new RuleEngineProcessor();
        CouponTemplate couponTemplate = CouponTemplate.dao.findFirstByField(CouponTemplate.Fields.NAME.toString(), "拼团活动免单券");
        processor.init(couponTemplate.getCond());
        Context context = new MvelContext();
        context.put("type", "MARKETING_PIECE_GROUP");
        context.put("finalPrice", 100);
        context.put("totalPrice", 100);
        processor.process(context, "getFinalPrice");
        Number tempFinalPrice = context.get("finalPrice");
        assertEquals(0.0, tempFinalPrice);
    }

    @Test
    public void testMarketingCouponInvalid() {
        RuleEngineProcessor processor = new RuleEngineProcessor();
        CouponTemplate couponTemplate = CouponTemplate.dao.findFirstByField(CouponTemplate.Fields.NAME.toString(), "拼团活动免单券");
        processor.init(couponTemplate.getCond());
        Context context = new MvelContext();
        context.put("type", "invalidtype");
        context.put("finalPrice", 100);
        context.put("totalPrice", 100);
        processor.process(context, "getFinalPrice");
        Number tempFinalPrice = context.get("finalPrice");
        assertEquals(100, tempFinalPrice);
    }
}
