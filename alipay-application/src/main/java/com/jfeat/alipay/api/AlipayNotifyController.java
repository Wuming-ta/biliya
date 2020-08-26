package com.jfeat.alipay.api;

import com.alipay.api.AlipayApiException;
import com.jfeat.alipay.config.AlipayException;
import com.jfeat.alipay.config.AlipayKit;
import com.jfeat.core.BaseController;
import com.jfeat.core.Service;
import com.jfeat.core.ServiceContext;
import com.jfeat.service.PayService;
import com.jfeat.service.exception.RetrieveOrderException;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/11/3
 */
@ControllerBind(controllerKey = "/rest/pub/ali/pay_notify")
public class AlipayNotifyController extends BaseController {

    private static final String PAY_SERVICE_SUFFIX = "PayService";

    @Override
    public void index() {

        //获取支付宝POST过来反馈信息
        Map<String, String> requestParams = convertRequestParamsToMap(getRequest());
        logger.debug("pay_notify: req params: {}", requestParams);
        try {
            boolean result = AlipayKit.verify(requestParams);
            if (result) {
                String tradeStatus = requestParams.get("trade_status");
                String outTradeNo = requestParams.get("out_trade_no");
                String tradeNo = requestParams.get("trade_no");

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

                //TRADE_FINISHED，TRADE_SUCCESS， TRADE_CLOSED 都会触发
                if("TRADE_SUCCESS".equals(tradeStatus)) {
                    PayService orderPayService = (PayService) service;
                    try {
                        orderPayService.paidNotify(orderNumber, "ALIPAY", tradeNo, null);
                    } catch (RetrieveOrderException ex) {
                        logger.error("paidNotify error. " + ex.getMessage());
                        renderError(500);
                        return;
                    }
                }
                renderText("success");
                return;
            }
            logger.warn("pay_notify verify failure.");
        } catch (AlipayException ex) {
            logger.error(ex.getMessage());
        }
        renderNull();
    }

    private Map<String, String> convertRequestParamsToMap(HttpServletRequest request) {
        Map<String,String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        return params;
    }
}
