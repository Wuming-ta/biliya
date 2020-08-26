package com.jfeat.wechat.api;

import com.jfeat.core.BaseController;
import com.jfeat.core.Service;
import com.jfeat.core.ServiceContext;
import com.jfeat.service.PayService;
import com.jfeat.service.exception.RetrieveOrderException;
import com.jfeat.wechat.config.WxConfig;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/5/19
 */
@ControllerBind(controllerKey = "/rest/pub/wx/pay_notify")
public class PayNotifyController extends BaseController {

    private static final String PAY_SERVICE_SUFFIX = "PayService";

    @Override
    public void index() {
        String xmlMsg;
        try {
             xmlMsg = IOUtils.toString(this.getRequest().getInputStream(), "UTF-8");
            logger.info("pay_notify = {}", xmlMsg);
        }
        catch (IOException ex) {
            logger.error(ex.getMessage());
            renderError(500);
            return;
        }

        Map<String, String> params = PaymentKit.xmlToMap(xmlMsg);
        String resultCode = params.get("result_code");
        String totalFee = params.get("total_fee");
        String outTradeNo = params.get("out_trade_no");
        String transId = params.get("transaction_id");
        String timeEnd = params.get("time_end");
        String openid = params.get("openid");

        String[] outStrArray = outTradeNo.split("_");
        String orderType = outStrArray[0];
        String orderNumber = outStrArray[1];
        orderType = StrKit.firstCharToUpperCase(StrKit.toCamelCase(orderType));
        String payServiceName = orderType + PAY_SERVICE_SUFFIX;

        Service service = ServiceContext.me().getService(payServiceName);
        if (service == null) {
            logger.error("PayService not found.");
            throw new RuntimeException("PayService not found.");
        }

        if(PaymentKit.verifyNotify(params, WxConfig.getPartnerKey()) && "SUCCESS".equals(resultCode)) {
            logger.info("Update order info: orderNumber = {}", orderNumber);

            PayService orderPayService = (PayService) service;
            try {
                orderPayService.paidNotify(orderNumber, "WECHAT", transId, openid);
            } catch (RetrieveOrderException ex) {
                logger.error("paidNotify error. " + ex.getMessage());
                renderError(500);
                return;
            }

            Map<String, String> xml = new HashMap<>();
            xml.put("return_code", "SUCCESS");
            xml.put("return_msg", "OK");
            renderText(PaymentKit.toXml(xml));
            return;
        }

        logger.error("payment verify notify failed.");
        renderError(500);
    }
}
