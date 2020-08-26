package com.jfeat.wechat.observer;

import com.jfeat.config.model.Config;
import com.jfeat.core.BaseModel;
import com.jfeat.observer.Observer;
import com.jfeat.observer.Subject;
import com.jfeat.wechat.config.WxConfig;
import com.jfinal.kit.JsonKit;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.wxaapp.WxaConfig;
import com.jfinal.wxaapp.WxaConfigKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jackyhuang
 * @date 2018/5/18
 */
public class WechatConfigUpdatedObserver implements Observer {
    private static final Logger logger = LoggerFactory.getLogger(WechatConfigUpdatedObserver.class);

    @Override
    public void invoke(Subject subject, int event, Object o) {
        if (subject instanceof Config && event == BaseModel.EVENT_UPDATE) {
            try {
                Config config = (Config) subject;
                logger.debug("config {} updated to {} ", config.getKeyName(), config.getValue());

                if (WxConfig.WXA_APPID_KEY.equals(config.getKeyName())) {
                    WxaConfig wc = WxConfig.getWxaConfig();
                    wc.setAppId(config.getValue());
                    WxaConfigKit.setWxaConfig(wc);
                }
                if (WxConfig.WXA_APP_SECRET_KEY.equals(config.getKeyName())) {
                    WxaConfig wc = WxConfig.getWxaConfig();
                    wc.setAppSecret(config.getValue());
                    WxaConfigKit.setWxaConfig(wc);
                }

                if (WxConfig.APP_ID_KEY.equals(config.getKeyName())) {
                    ApiConfig ac = ApiConfigKit.getApiConfig();
                    ac.setAppId(config.getValue());
                    ApiConfigKit.putApiConfig(ac);
                }
                if (WxConfig.APP_SECRET_KEY.equals(config.getKeyName())) {
                    ApiConfig ac = ApiConfigKit.getApiConfig();
                    ac.setAppSecret(config.getValue());
                    ApiConfigKit.putApiConfig(ac);
                }
                if (WxConfig.ENCODING_AES_KEY_KEY.equals(config.getKeyName())) {
                    ApiConfig ac = ApiConfigKit.getApiConfig();
                    ac.setEncodingAesKey(config.getValue());
                    ApiConfigKit.putApiConfig(ac);
                }
                if (WxConfig.ENCRYPT_MESSAGE_KEY.equals(config.getKeyName())) {
                    ApiConfig ac = ApiConfigKit.getApiConfig();
                    ac.setEncryptMessage(config.getValueToBoolean());
                    ApiConfigKit.putApiConfig(ac);
                }
                if (WxConfig.TOKEN_KEY.equals(config.getKeyName())) {
                    ApiConfig ac = ApiConfigKit.getApiConfig();
                    ac.setToken(config.getValue());
                    ApiConfigKit.putApiConfig(ac);
                }

            } catch (Exception ex) {
                logger.error(ex.getMessage());
                for (StackTraceElement element : ex.getStackTrace()) {
                    logger.error("    {}:{} - {}:{}", element.getFileName(), element.getLineNumber(), element.getClassName(), element.getMethodName());
                }
            }
        }
    }
}
