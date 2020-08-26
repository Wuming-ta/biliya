package com.jfeat.wechat.sys.api;

import com.jfeat.config.model.Config;
import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.wechat.sdk.api.ExpressApi;
import com.jfeat.wechat.sdk.api.ExpressCargo;
import com.jfeat.wechat.sdk.api.ExpressShop;
import com.jfeat.wechat.sdk.api.ExpressUser;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.ApiResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2019/11/9
 */
@ControllerBind(controllerKey = "/sys/rest/wxa/express/order")
public class WxaExpressOrderController extends RestController {

    @Override
    public void index() {
        if (!Config.dao.findByKey("wx.express.enabled").getValueToBoolean()) {
            renderFailure("wx.express.disabled");
            return;
        }

        String openid = getPara("openid");
        String orderNumber = getPara("order_number");
        String waybillId = getPara("waybill_id");

        ApiResult result = ExpressApi.getPath(orderNumber,
                openid,
                Config.dao.findByKey("wx.express.delivery_id").getValueToStr(),
                waybillId);
        if (!result.isSucceed()) {
            renderFailure(result.getErrorMsg());
            return;
        }
        Map<String, Object> map = result.getAttrs();
        map.put("delivery_name", Config.dao.findByKey("wx.express.delivery_name").getValueToStr());
        renderSuccess(map);
    }

    /**
     * 小程序运单，添加订单操作。由订单模块调用。
     *
     * response:
     * {
     *     status_code: 0,
     *     data: {
     *     "succeed": true,
     *     "json": "{\"order_id\":\"19111413493105914\",\"waybill_id\":\"52123154807930\",\"waybill_data\":[{\"key\":\"BEST_markDestination\",\"value\":\"501\"},{\"key\":\"BEST_pkgCode\",\"value\":\"【省内】广州市内包\"},{\"key\":\"BEST_sortingCode\",\"value\":\"K25-00-05\"}]}",
     *     "errorCode": null,
     *     "accessTokenInvalid": false,
     *     "attrs": {
     *         "waybill_data": [
     *             {
     *                 "value": "501",
     *                 "key": "BEST_markDestination"
     *             },
     *             {
     *                 "value": "【省内】广州市内包",
     *                 "key": "BEST_pkgCode"
     *             },
     *             {
     *                 "value": "K25-00-05",
     *                 "key": "BEST_sortingCode"
     *             }
     *         ],
     *         "waybill_id": "52123154807930",
     *         "order_id": "19111413493105914"
     *     },
     *     "errorMsg": null
     * }
     * }
     */
    @Override
    @Validation(rules = {
            "order_number = required",
            "openid = required",
            "receiver_name = required",
            "receiver_mobile = required",
            "receiver_province = required",
            "receiver_city = required",
            "receiver_area = required",
            "receiver_address = required",
            "pkg_count = required",
            "pkg_weight = required",
            "pkg_space_x = required",
            "pkg_space_y = required",
            "pkg_space_z = required",
            "pkg_list = required",
            "shop_goods_count = required",
            "shop_goods_name = required",
            "shop_img_url = required",
            "shop_wxa_path = required"
    })
    public void save() {
        if (!Config.dao.findByKey("wx.express.enabled").getValueToBoolean()) {
            renderFailure("wx.express.disabled");
            return;
        }

        Map<String, Object> maps = convertPostJsonToMap();
        String orderNumber = (String) maps.get("order_number");
        String openid = (String) maps.get("openid");

        String senderCompany = (String) maps.get("sender_company");
        if (StrKit.isBlank(senderCompany)) {
            senderCompany = Config.dao.findByKey("wx.express.sender_company").getValueToStr();
        }
        String senderName = (String) maps.get("sender_name");
        if (StrKit.isBlank(senderName)) {
            senderName = Config.dao.findByKey("wx.express.sender_name").getValueToStr();
        }
        String senderMobile = (String) maps.get("sender_mobile");
        if (StrKit.isBlank(senderMobile)) {
            senderMobile = Config.dao.findByKey("wx.express.sender_mobile").getValueToStr();
        }
        String senderProvince = (String) maps.get("sender_province");
        if (StrKit.isBlank(senderProvince)) {
            senderProvince = Config.dao.findByKey("wx.express.sender_province").getValueToStr();
        }
        String senderCity = (String) maps.get("sender_city");
        if (StrKit.isBlank(senderCity)) {
            senderCity = Config.dao.findByKey("wx.express.sender_city").getValueToStr();
        }
        String senderArea = (String) maps.get("sender_area");
        if (StrKit.isBlank(senderArea)) {
            senderArea = Config.dao.findByKey("wx.express.sender_area").getValueToStr();
        }
        String senderAddress = (String) maps.get("sender_address");
        if (StrKit.isBlank(senderAddress)) {
            senderAddress = Config.dao.findByKey("wx.express.sender_address").getValueToStr();
        }
        ExpressUser sender = new ExpressUser();
        sender.setName(senderName)
                .setMobile(senderMobile)
                .setCompany(senderCompany)
                .setProvince(senderProvince)
                .setCity(senderCity)
                .setArea(senderArea)
                .setAddress(senderAddress);

        String receiverName = (String) maps.get("receiver_name");
        String receiverMobile = (String) maps.get("receiver_mobile");
        String receiverProvince = (String) maps.get("receiver_province");
        String receiverCity = (String) maps.get("receiver_city");
        String receiverArea = (String) maps.get("receiver_area");
        String receiverAddress = (String) maps.get("receiver_address");
        ExpressUser receiver = new ExpressUser();
        receiver.setName(receiverName)
                .setMobile(receiverMobile)
                .setProvince(receiverProvince)
                .setCity(receiverCity)
                .setArea(receiverArea)
                .setAddress(receiverAddress);

        Integer pkgCount = (Integer) maps.get("pkg_count");
        Integer pkgWeight = (Integer) maps.get("pkg_weight");
        Integer pkgSpaceX = (Integer) maps.get("pkg_space_x");
        Integer pkgSpaceY = (Integer) maps.get("pkg_space_y");
        Integer pkgSpaceZ = (Integer) maps.get("pkg_space_z");
        List<Map<String, Object>> pkgList = (List<Map<String, Object>>) maps.get("pkg_list");
        List<ExpressCargo.Detail> pkgDetailList = new ArrayList<>();
        for (Map<String, Object> obj : pkgList) {
            ExpressCargo.Detail detail = new ExpressCargo.Detail();
            detail.setCount((Integer) obj.get("count"));
            detail.setName((String) obj.get("name"));
            pkgDetailList.add(detail);
        }
        ExpressCargo cargo = new ExpressCargo();
        cargo.setCount(pkgCount)
                .setWeight(pkgWeight)
                .setSpace_x(pkgSpaceX)
                .setSpace_y(pkgSpaceY)
                .setSpace_z(pkgSpaceZ)
                .setDetail_list(pkgDetailList);

        Integer goodsCount = (Integer) maps.get("shop_goods_count");
        String goodsName = (String) maps.get("shop_goods_name");
        String imgUrl = (String) maps.get("shop_img_url");
        String wxaPath = (String) maps.get("shop_wxa_path");
        ExpressShop shop = new ExpressShop();
        shop.setGoods_count(goodsCount)
                .setGoods_name(goodsName)
                .setImg_url(imgUrl)
                .setWxa_path(wxaPath);

        ApiResult result = ExpressApi.addOrder(orderNumber,
                openid,
                Config.dao.findByKey("wx.express.delivery_id").getValueToStr(),
                Config.dao.findByKey("wx.express.biz_id").getValueToStr(),
                sender,
                receiver,
                cargo,
                shop,
                Config.dao.findByKey("wx.express.service_type").getValueToInt(),
                Config.dao.findByKey("wx.express.service_name").getValueToStr());

        renderSuccess(result);
    }

    /**
     * DELETE /wxa/express/order/:orderNumber?openid=xxxx&waybill_id=yyyy
     */
    @Override
    @Validation(rules = {
            "openid = required",
            "waybill_id = required"
    })
    public void delete() {
        String orderNumber = getPara();
        String openid = getPara("openid");
        String waybillId = getPara("waybill_id");

        ApiResult result = ExpressApi.cancelOrder(orderNumber,
                openid,
                Config.dao.findByKey("wx.express.delivery_id").getValueToStr(),
                waybillId);
        renderSuccess(result);
    }
}
