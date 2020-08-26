package com.jfeat.wechat.sdk.api;

import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import com.jfinal.weixin.sdk.utils.JsonUtils;
import com.jfinal.wxaapp.api.WxaAccessTokenApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * @author jackyhuang
 * @date 2019/11/9
 */
public class ExpressApi {

    private static final Logger logger = LoggerFactory.getLogger(ExpressApi.class);

    private static String addOrderUrl = "https://api.weixin.qq.com/cgi-bin/express/business/order/add?access_token=";
    //private static String getOrderUrl = "https://api.weixin.qq.com/cgi-bin/express/business/order/get?access_token=";
    private static String getPathUrl = "https://api.weixin.qq.com/cgi-bin/express/business/path/get?access_token=";
    private static String cancelOrderUrl = "https://api.weixin.qq.com/cgi-bin/express/business/order/cancel?access_token=";
    private static String testUpdateOrderUrl = "https://api.weixin.qq.com/cgi-bin/express/business/test_update_order?access_token=";
    private static String updatePrinterUrl = "https://api.weixin.qq.com/cgi-bin/express/business/printer/update?access_token=";
    private static String getPrinterUrl = "https://api.weixin.qq.com/cgi-bin/express/business/printer/getall?access_token=";

    public ExpressApi() {
    }

    /**
     * 小程序物流接口
     * https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/express/by-business/logistics.addOrder.html
     *
     * @return
     * order_id    string 	订单ID，下单成功时返回
     * waybill_id 	string 	运单ID，下单成功时返回
     * waybill_data 	Array.<Object> 	运单信息，下单成功时返回
     * errcode 	number 	微信侧错误码，下单失败时返回
     * errmsg 	string 	微信侧错误信息，下单失败时返回
     * delivery_resultcode 	number 	快递侧错误码，下单失败时返回
     * delivery_resultmsg 	string 	快递侧错误信息，下单失败时返回
     */
    public static ApiResult addOrder(String orderNumber,
                                     String openid,
                                     String deliveryId,
                                     String bizId,
                                     ExpressUser sender,
                                     ExpressUser receiver,
                                     ExpressCargo cargo,
                                     ExpressShop shop,
                                     Integer serviceType,
                                     String serviceName) {
        String accessToken = WxaAccessTokenApi.getAccessTokenStr();
        HashMap params = new HashMap();
        params.put("add_source", 0); //0为小程序订单
        params.put("order_id", orderNumber);
        params.put("openid", openid);
        params.put("delivery_id", deliveryId);
        params.put("biz_id", bizId);

        params.put("sender", sender);
        params.put("receiver", receiver);

        HashMap insured = new HashMap();
        insured.put("use_insured", 0); //是否保价，0 表示不保价，1 表示保价
        insured.put("insured_value", 0);
        params.put("insured", insured);

        //包裹信息，将传递给快递公司
        params.put("cargo", cargo);

        //商品信息，会展示到物流服务通知和电子面单中
        params.put("shop", shop);

        HashMap service = new HashMap();
        service.put("service_type", serviceType); //服务类型ID，详见已经支持的快递公司基本信息
        service.put("service_name", serviceName); //服务名称，详见已经支持的快递公司基本信息
        params.put("service", service);

        String jsonResult = HttpUtils.post(addOrderUrl + accessToken, JsonUtils.toJson(params));
        return new ApiResult(jsonResult);
    }

    /**
     * 查询运单轨迹
     * https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/express/by-business/logistics.getPath.html
     *
     * @param orderNumber
     * @param openid
     * @param deliveryId
     * @param waybillId
     * @return
     * openid    string 	用户openid
     * delivery_id 	string 	快递公司 ID
     * waybill_id 	string 	运单 ID
     * path_item_num 	number 	轨迹节点数量
     * path_item_list 	Array.<Object> 	轨迹节点列表
     */
    public static ApiResult getPath(String orderNumber,
                                    String openid,
                                    String deliveryId,
                                    String waybillId) {
        String accessToken = WxaAccessTokenApi.getAccessTokenStr();
        HashMap params = new HashMap();
        params.put("order_id", orderNumber);
        params.put("openid", openid);
        params.put("delivery_id", deliveryId);
        params.put("waybill_id", waybillId);
        String jsonResult = HttpUtils.post(getPathUrl + accessToken, JsonUtils.toJson(params));
        return new ApiResult(jsonResult);
    }

    /**
     * https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/express/by-business/logistics.cancelOrder.html
     *
     * @param orderNumber
     * @param openid
     * @param deliveryId
     * @param waybillId
     * @return
     */
    public static ApiResult cancelOrder(String orderNumber,
                                    String openid,
                                    String deliveryId,
                                    String waybillId) {
        String accessToken = WxaAccessTokenApi.getAccessTokenStr();
        HashMap params = new HashMap();
        params.put("order_id", orderNumber);
        params.put("openid", openid);
        params.put("delivery_id", deliveryId);
        params.put("waybill_id", waybillId);
        String jsonResult = HttpUtils.post(cancelOrderUrl + accessToken, JsonUtils.toJson(params));
        return new ApiResult(jsonResult);
    }

    /**
     * https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/express/by-business/logistics.testUpdateOrder.html
     *
     * biz_id 	string 		是 	商户id,需填test_biz_id
     * order_id 	string 		是 	订单号
     * delivery_id 	string 		是 	快递公司id,需填TEST
     * waybill_id 	string 		是 	运单号
     * action_time 	number 		是 	轨迹变化 Unix 时间戳
     * action_type 	number 		是 	轨迹变化类型
     * action_msg 	string 		是 	轨迹变化具体信息说明,使用UTF-8编码
     * @return
     */
    public static ApiResult testUpdateOrder(String orderNumber,
                                            String bizId,
                                            String deliveryId,
                                            String waybillId,
                                            Integer actionType,
                                            String actionMsg) {
        String accessToken = WxaAccessTokenApi.getAccessTokenStr();
        HashMap params = new HashMap();
        params.put("order_id", orderNumber);
        params.put("biz_id", bizId);
        params.put("delivery_id", deliveryId);
        params.put("waybill_id", waybillId);
        params.put("action_type", actionType);
        params.put("action_msg", actionMsg);
        params.put("action_time", System.currentTimeMillis() / 1000);
        logger.debug("POST {}, data = {}", testUpdateOrderUrl, params);
        String jsonResult = HttpUtils.post(testUpdateOrderUrl + accessToken, JsonUtils.toJson(params));
        return new ApiResult(jsonResult);
    }

    /**
     * https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/express/by-business/logistics.updatePrinter.html
     *
     * @param openid
     * @param updateType bind, unbind
     * @return
     */
    public static ApiResult updatePrinter(String openid,
                                            String updateType) {
        String accessToken = WxaAccessTokenApi.getAccessTokenStr();
        HashMap params = new HashMap();
        params.put("openid", openid);
        params.put("update_type", updateType);
        String jsonResult = HttpUtils.post(updatePrinterUrl + accessToken, JsonUtils.toJson(params));
        return new ApiResult(jsonResult);
    }

    /**
     * https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/express/by-business/logistics.getPrinter.html
     *
     * @return
     * {
     *  "count": 2,
     *  "openid": [
     *    "oABC",
     *    "oXYZ"
     *  ],
     *  "tagid_list": [
     *    "123",
     *    "456"
     *  ]
     * }
     *
     */
    public static ApiResult getPrinter() {
        String accessToken = WxaAccessTokenApi.getAccessTokenStr();
        String jsonResult = HttpUtils.get(getPrinterUrl + accessToken);
        return new ApiResult(jsonResult);
    }
}
