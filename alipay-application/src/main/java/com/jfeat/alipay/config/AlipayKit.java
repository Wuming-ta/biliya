package com.jfeat.alipay.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.jfeat.ext.plugin.JsonKit;
import com.jfinal.kit.StrKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/11/3
 */
public class AlipayKit {

    private static final Logger logger = LoggerFactory.getLogger(AlipayKit.class);

    public static final String CHARSET = "utf-8";

    public static final String SIGN_TYPE = "RSA2";

    public static final String FORMAT = "json";

    public static final String DEV_SERVER_URL = "https://openapi.alipaydev.com/gateway.do";

    public static final String SERVER_URL = "https://openapi.alipay.com/gateway.do";

    public static boolean DEV = false;

    public static String DEV_NOTIFY_URL;

    public static String getServerUrl() {
        if (DEV) {
            return DEV_SERVER_URL;
        }
        return SERVER_URL;
    }

    public static String appPay(String title, String body, String orderNum, BigDecimal totalAmount, String notifyUrl) {

        String appId = AliConfig.getAppId();
        String appPrivateKey = AliConfig.getAppSecret();
        String alipayPublicKey = AliConfig.getAlipayPublicKey();
        checkConfig(appId, appPrivateKey, alipayPublicKey);

        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(getServerUrl(), appId, appPrivateKey, FORMAT, CHARSET, alipayPublicKey, SIGN_TYPE);
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody(body);
        model.setSubject(title);
        model.setOutTradeNo(orderNum);
        model.setTimeoutExpress("30m");
        model.setTotalAmount(totalAmount.toString());
        request.setBizModel(model);
        request.setNotifyUrl(notifyUrl);
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            //就是orderString 可以直接给客户端请求，无需再做处理。
            return (response.getBody());
        } catch (AlipayApiException e) {
            logger.error("apppay error. " + e.getErrCode());
            throw new AlipayException(e.getErrMsg());
        }
    }

    public static Map prePay(String orderNum, BigDecimal totalAmount, String title, String notifyUrl) {

        String appId = AliConfig.getAppId();
        String appPrivateKey = AliConfig.getAppSecret();
        String alipayPublicKey = AliConfig.getAlipayPublicKey();
        checkConfig(appId, appPrivateKey, alipayPublicKey);

        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(getServerUrl(), appId, appPrivateKey, FORMAT, CHARSET, alipayPublicKey, SIGN_TYPE);

        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        Map<String, Object> bizContent = new HashMap<>();
        bizContent.put("out_trade_no", orderNum);
        bizContent.put("total_amount", totalAmount);
        bizContent.put("subject", title);
        request.setBizContent(JsonKit.toJson(bizContent));
        request.setNotifyUrl(notifyUrl);

        try {
            AlipayTradePrecreateResponse response = alipayClient.execute(request);
            Map result = JsonKit.parseObject(response.getBody(), Map.class);
            logger.debug("prepay result = {}", result);
            return (Map) result.get("alipay_trade_precreate_response");
        } catch (AlipayApiException e) {
            logger.error("prepay error. " + e.getErrCode());
            throw new AlipayException(e.getErrMsg());
        }
    }

    public static boolean verify(Map<String, String> params) {

        String appId = AliConfig.getAppId();
        String appPrivateKey = AliConfig.getAppSecret();
        String alipayPublicKey = AliConfig.getAlipayPublicKey();
        checkConfig(appId, appPrivateKey, alipayPublicKey);

        //切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
        boolean flag = false;
        try {
            flag = AlipaySignature.rsaCheckV1(params, alipayPublicKey, CHARSET, SIGN_TYPE);
        } catch (AlipayApiException e) {
            logger.error("verify error. " + e.getMessage() + e.getErrCode() + e.getErrMsg());
            throw new AlipayException(e.getMessage());
        }
        return flag;
    }

    public static boolean refund(String orderNum, String refundNum, BigDecimal refundAmount) throws AlipayApiException {
        String appId = AliConfig.getAppId();
        String appPrivateKey = AliConfig.getAppSecret();
        String alipayPublicKey = AliConfig.getAlipayPublicKey();
        checkConfig(appId, appPrivateKey, alipayPublicKey);

        AlipayClient alipayClient = new DefaultAlipayClient(getServerUrl(), appId, appPrivateKey, FORMAT, CHARSET, alipayPublicKey, SIGN_TYPE);
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        Map<String, Object> bizContent = new HashMap<>();
        bizContent.put("out_trade_no", orderNum);
        bizContent.put("refund_amount", refundAmount);
        bizContent.put("out_request_no", refundNum);
        request.setBizContent(JsonKit.toJson(bizContent));

        AlipayTradeRefundResponse response = alipayClient.execute(request);
        logger.debug("refund result = {}", JsonKit.toJson(response));
        return response.isSuccess();
    }

    private static void checkConfig(String appId, String appPrivateKey, String alipayPublicKey) {
        if (StrKit.isBlank(appId) || StrKit.isBlank(appPrivateKey) || StrKit.isBlank(alipayPublicKey)) {
            throw new AlipayException("invalid ali config.");
        }
    }
}
