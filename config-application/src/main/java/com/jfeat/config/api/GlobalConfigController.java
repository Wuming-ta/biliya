package com.jfeat.config.api;

import com.jfeat.config.model.Config;
import com.jfeat.core.RestController;
import com.jfinal.ext.route.ControllerBind;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jackyhuang on 16/8/13.
 */
@ControllerBind(controllerKey = "/rest/global_config")
public class GlobalConfigController extends RestController {

    public void index() {
        Map<String, Object> result = new HashMap<>();
        Config exchangeRateConfig = Config.dao.findByKey("mall.point_exchange_rate");
        result.put("point_exchange_rate", exchangeRateConfig.getValueToInt());
        Config drawingConditionConfig = Config.dao.findByKey("mall.drawing_conditions");
        result.put("drawing_condition", drawingConditionConfig.getValueToInt());
        Config autoSelectCouponConfig = Config.dao.findByKey("mall.auto_select_coupon");
        result.put("auto_select_coupon", autoSelectCouponConfig.getValueToBoolean());
        renderSuccess(result);
    }
}
