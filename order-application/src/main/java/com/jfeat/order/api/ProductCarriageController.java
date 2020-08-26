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

package com.jfeat.order.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfeat.core.RestController;
import com.jfeat.order.model.Order;
import com.jfeat.order.service.OrderService;
import com.jfeat.product.model.CarryMode;
import com.jfeat.product.service.CarriageCalcResult;
import com.jfeat.product.service.PostageService;
import com.jfeat.product.service.ProductPurchasing;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运费计算
 * Created by jackyhuang on 16/8/31.
 */
@ControllerBind(controllerKey = "/rest/product_carriage")
public class ProductCarriageController extends RestController {

    private PostageService postageService = new PostageService();
    private OrderService orderService = new OrderService();

    /**
     * POST /rest/product_carriage
     {
        "delivery_type": "EXPRESS", //SELF_PICK, FLASH, EXPRESS. 默认EXPRESS.
        "province": "广东",
        "city": "广州",
        "data":[
            {
                "fare_id": 1,
                "price": 23.20,
                "quantity": 4,
                "weight": 500,
                "bulk": 100
            }
        ]
     }

     Return: carriage - 运费; delta - 距离满包邮的额
     {
        "status_code": 1,
        "data": {
            "carriage": 20.00,
            "delta": -20.00
        }
     }
     */
    public void save() {
        Map<String, Object> map = convertPostJsonToMap();
        String deliveryTypeStr = (String) map.get("delivery_type");

        Order.DeliveryType deliveryType = Order.DeliveryType.EXPRESS;
        if (StrKit.notBlank(deliveryTypeStr)) {
            deliveryType = Order.DeliveryType.valueOf(deliveryTypeStr);
        }

        Map<String, Object> result = new HashMap<>();
        if (deliveryType == Order.DeliveryType.SELF_PICK) {
            result.put("carriage", 0);
            renderSuccess(result);
            return;
        }
        if (deliveryType == Order.DeliveryType.FLASH) {
            result.put("carriage", orderService.getFlashFreight());
            renderSuccess(result);
            return;
        }

        String province = (String) map.get("province");
        String city = (String) map.get("city");
        List<Map<String, Object>> data = (List<Map<String, Object>>) map.get("data");
        if (StrKit.isBlank(province) || StrKit.isBlank(city) || data == null) {
            renderFailure("invalid.input");
            return;
        }

        List<ProductPurchasing> productPurchasings = Lists.newArrayList();
        for (Map<String, Object> dataMap : data) {
            Integer fareId = (Integer) dataMap.get("fare_id");
            Integer quantity = (Integer) dataMap.get("quantity");
            BigDecimal price = convertPrice(dataMap.get("price"));
            Integer weight = (Integer) dataMap.get("weight");
            Integer bulk = (Integer) dataMap.get("bulk");
            ProductPurchasing productPurchasing = new ProductPurchasing(fareId, quantity, price, weight, bulk);
            productPurchasings.add(productPurchasing);
        }
        CarriageCalcResult carriageCalcResult = postageService.calculate(productPurchasings, getRegion(province, city), CarryMode.CarryWay.EXPRESS);

        result.put("carriage", carriageCalcResult.getResult());
        result.put("delta", carriageCalcResult.getDelta());
        result.put("message", carriageCalcResult.getMessage());
        renderSuccess(result);
    }

    private BigDecimal convertPrice(Object price) {
        try {
            return BigDecimal.valueOf((Double) price);
        } catch (ClassCastException ex) {
            return BigDecimal.valueOf((Integer) price);
        }
    }

    private String getRegion(String province, String city) {
        return province + "-" + city;
    }
}
