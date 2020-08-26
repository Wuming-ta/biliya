package com.jfeat.wechat.sys.api;

import com.jfeat.config.model.Config;
import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.wechat.sdk.api.ExpressApi;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.weixin.sdk.api.ApiResult;

import java.util.Map;

/**
 * @author jackyhuang
 * @date 2019/11/17
 */
@ControllerBind(controllerKey = "/sys/rest/wxa/express/order/test")
public class WxaExpressOrderTestController extends RestController {

    @Override
    public void index() {

    }

    /**
     * 模拟快递公司更新订单状态.
     * action_type 的合法值
     * 值 	    说明
     * 100001 	揽件阶段-揽件成功
     * 100002 	揽件阶段-揽件失败
     * 100003 	揽件阶段-分配业务员
     * 200001 	运输阶段-更新运输轨迹
     * 300002 	派送阶段-开始派送
     * 300003 	派送阶段-签收成功
     * 300004 	派送阶段-签收失败
     * 400001 	异常阶段-订单取消
     * 400002 	异常阶段-订单滞留
     *
     * PUT /sys/rest/wxa/express/order/test/:orderNumber
     * {
     *     action_type: 100001,
     *     action_msg: "揽件阶段",
     *     waybill_id: "1233344"
     * }
     */
    @Override
    @Validation(rules = {
            "action_type = required",
            "action_msg = required",
            "waybill_id = required"
    })
    public void update() {
        String orderNumber = getPara();
        Map<String, Object> maps = convertPostJsonToMap();
        Integer actionType = (Integer) maps.get("action_type");
        String actionMsg = (String) maps.get("action_msg");
        String waybillId = (String) maps.get("waybill_id");

        ApiResult result = ExpressApi.testUpdateOrder(orderNumber,
                "test_biz_id",
                "TEST",
                waybillId,
                actionType,
                actionMsg);
        renderSuccess(result);
    }
}
