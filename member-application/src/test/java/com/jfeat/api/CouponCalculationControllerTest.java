package com.jfeat.api;

import com.jfeat.identity.model.User;
import com.jfeat.kit.JsonKit;
import com.jfeat.member.model.Coupon;
import com.jfeat.member.model.CouponTemplate;
import com.jfeat.member.model.CouponType;
import com.jfeat.member.service.CouponService;
import com.jfinal.kit.StrKit;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Created by kang on 2016/11/24.
 */
public class CouponCalculationControllerTest extends ApiTestBase {
    private String url = baseUrl + "rest/coupon_calculation";
    private User user = User.dao.findByLoginName(testUserName);
    private CouponType orderCouponType;
    private CouponType productCouponType;
    private CouponService service = new CouponService();

    @Before
    public void before() {
        addOrderCouponType();
        addProductCouponType();
        service.createCoupon(user.getId(), orderCouponType, Coupon.Source.SYSTEM);
        service.createCoupon(user.getId(), productCouponType, Coupon.Source.SYSTEM);
    }

    private String replaceTemplate(String condition, String productId, Integer discount, Integer money, Integer limit) {
        if (StrKit.notBlank(condition)) {
            condition = condition.replace("#id#", productId);
            if (discount != null) {
                condition = condition.replace("#discount#", discount.toString());
            }
            if (money != null) {
                condition = condition.replace("#money#", money.toString());
            }
            if (limit != null) {
                condition = condition.replace("#totalPrice#", limit.toString());
            }
        }
        return condition;
    }

    //创建1张10元的订单优惠券
    private void addOrderCouponType() {
        CouponTemplate template = CouponTemplate.dao.findById(6);//无限制型订单代金券
        orderCouponType = new CouponType();
        orderCouponType.setValidDays(30);
        orderCouponType.setType(CouponType.Type.ORDER.toString());
        orderCouponType.setName("order money");
        orderCouponType.setMoney(10);
        String condition = replaceTemplate(template.getCond(),
                orderCouponType.getProductId() != null ? String.valueOf(orderCouponType.getProductId()) : "",
                orderCouponType.getDiscount(),
                orderCouponType.getMoney(),
                orderCouponType.getUpTo());
        orderCouponType.setCond(condition);
        orderCouponType.save();
    }

    //创建1张10元的产品优惠券
    private void addProductCouponType() {
        CouponTemplate template = CouponTemplate.dao.findById(2);//无限制型产品代金券
        productCouponType = new CouponType();
        productCouponType.setValidDays(30);
        productCouponType.setType(CouponType.Type.PRODUCT.toString());
        productCouponType.setName("product money");
        productCouponType.setMoney(10);
        productCouponType.setProductId(1);
        String condition = replaceTemplate(template.getCond(),
                productCouponType.getProductId() != null ? String.valueOf(productCouponType.getProductId()) : "",
                productCouponType.getDiscount(),
                productCouponType.getMoney(),
                productCouponType.getUpTo());
        productCouponType.setCond(condition);
        productCouponType.save();
    }

    @Test
    public void testSave() throws Exception {
        String json = "[ {\"product_id\": 1, \"price\": 100}," +
                " {\"product_id\": 2, \"price\": 59.9}]";
        String jsonResult = post(url, json);
        Map<String, Object> map = JsonKit.convertToMap(jsonResult);
        Assert.assertEquals(0, map.get("status_code"));
        List<Map<String, Object>> data = (List) map.get("data");
        for (Map<String, Object> m : data) {
            Assert.assertEquals(149.90,m.get("final_price"));
        }
    }
}
