package com.jfeat.ext.plugin.store;

import com.jfeat.ext.plugin.*;
import com.jfeat.ext.plugin.store.bean.Appointment;
import com.jfeat.ext.plugin.store.bean.Store;
import com.jfeat.http.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/8/24
 */
public class AppointmentApi extends BaseApi {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentApi.class);

    private static final String getAppointmentUrl = "/api/appointment/appointments";
    private static final String payAppointmentUrl = "/api/appointment/appointments/%s/action/pay";

    public AppointmentApi() {
        BasePlugin storePlugin = ExtPluginHolder.me().get(StorePlugin.class);
        init(storePlugin);
    }

    /**
     *
     * @param code
     * @return
     *
     * the appointment api return:
     *  {
     * 	"code":200,
     * 	"data":{
     * 		"current":1,
     * 		"pages":1,
     * 		"records":[
     * 			{
     * 				"appointmentTime":"2018-08-29 11:19:33",
     * 				"closeTime":"",
     * 				"code":"APT2018270800027Zqo",
     * 				"createTime":"2018-08-27 11:19:21",
     * 				"fee":0.00,
     * 				"fieldC":"17",
     * 				"id":"10",
     * 				"itemAddress":"",
     * 				"itemDescription":"",
     * 				"itemIcon":"",
     * 				"itemId":"",
     * 				"itemName":"",
     * 				"memberId":"1",
     * 				"memberName":"哦尼",
     * 				"memberPhone":"1360000000",
     * 				"receptionistId":"",
     * 				"receptionistName":"",
     * 				"serverId":"",
     * 				"serverName":"",
     * 				"status":"PAY_PENDING",
     * 				"type":"皮肤检测"
     * 			}
     * 		],
     * 		"size":10,
     * 		"total":1
     * 	},
     * 	"message":"操作成功"
     * }
     */
    public Appointment getAppointment(String code) {
        String url = getBaseUrl() + getAppointmentUrl;
        Map<String, String> param = new HashMap<>();
        param.put("code", code);
        String result = HttpUtils.get(url, param, getAuthorizationHeader());
        logger.debug("get url = {}, param = {}, result = {}", url, param, result);
        ApiResult apiResult = ApiResult.create(result);
        if (!apiResult.isSucceed()) {
            throw new RuntimeException("get appointment error. " + apiResult.getMessage());
        }
        Map<String, Object> data = apiResult.get("data");
        List<Appointment> appointments = JsonKit.parseArray(JsonKit.toJson(data.get("records")), Appointment.class);
        if (appointments == null || appointments.size() != 1) {
            throw new RuntimeException("appointment not found");
        }
        return appointments.get(0);
    }

    public boolean payNotify(String code, String paymentType, String tradeNumber) {
        Appointment appointment = getAppointment(code);
        String url = String.format(getBaseUrl() + payAppointmentUrl, appointment.getId());
        Map<String, Object> param = new HashMap<>();
        param.put("timestamp", new Date());
        param.put("method", paymentType);
        param.put("tradeNumber", tradeNumber);
        String data = JsonKit.toJson(param);
        String result = HttpUtils.post(url, data, getAuthorizationHeader());
        logger.debug("post url = {}, param = {}, result = {}", url, data, result);
        ApiResult apiResult = ApiResult.create(result);
        return apiResult.isSucceed();
    }
}
