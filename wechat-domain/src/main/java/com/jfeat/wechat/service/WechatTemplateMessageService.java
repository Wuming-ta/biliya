package com.jfeat.wechat.service;

import com.jfeat.core.BaseService;
import com.jfeat.wechat.config.WxConfig;
import com.jfeat.wechat.model.WechatField;
import com.jfeat.wechat.model.WechatMessageType;
import com.jfeat.wechat.model.WechatMessageTypeProp;
import com.jfeat.wechat.model.WechatTemplateMessage;
import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.TemplateData;
import com.jfinal.weixin.sdk.api.TemplateMsgApi;

import java.util.Map;

/**
 * Created by kang on 2017/2/8.
 */
public class WechatTemplateMessageService extends BaseService {

    @Before(Tx.class)
    public void toggleEnable(WechatTemplateMessage wechatTemplateMessage, Integer enableOrDisable) {
        new WechatTemplateMessage().toggleEnableByTypeId(wechatTemplateMessage.getTypeId(), WechatTemplateMessage.DISABLED);
        wechatTemplateMessage.setEnabled(enableOrDisable);
        wechatTemplateMessage.update();
    }

    public Ret send(String openid, String messageTypeName, Map<String, String> params) {
        return send(openid, messageTypeName, params, null);
    }

    public Ret send(String openId, String messageTypeName, Map<String, String> params, String url) {
        if (StrKit.isBlank(openId)) {
            return failure("invalid.openid");
        }
        if (params == null) {
            return failure("invalid.map");
        }
        WechatMessageType wechatMessageType = WechatMessageType.dao.findByName(messageTypeName);
        WechatTemplateMessage wechatTemplateMessage = WechatTemplateMessage.dao.findEnabledByTypeId(wechatMessageType.getId());
        if (wechatTemplateMessage == null) {
            return failure("no.template.found");
        }

        TemplateData templateData = TemplateData.New()
                // 消息接收者
                .setTouser(openId)
                .setTemplate_id(wechatTemplateMessage.getTemplateId());

        if (url != null) {
            templateData.setUrl(url);
        }

        for (Map.Entry<String, String> entry : params.entrySet()) {
            WechatMessageTypeProp prop = WechatMessageTypeProp.dao.findFirstByTypeIdAndName(wechatMessageType.getId(), entry.getKey());
            if (prop == null) {
                logger.debug("prop not found for key {}, ignore it", entry.getKey());
                continue;
            }
            WechatField wechatField = WechatField.dao.findByPropIdAndTemplateMessageId(prop.getId(), wechatTemplateMessage.getId());
            if (wechatField != null) {
                String keyToSend = wechatField.getName();
                if (StrKit.notBlank(prop.getDisplayValue())) {
                    templateData.add(keyToSend, prop.getDisplayValue(), "#999");
                } else {
                    templateData.add(keyToSend, entry.getValue(), "#999");
                }
            }
        }

        ApiResult result = TemplateMsgApi.send(templateData.build());
        logger.debug("weixin template message result = {}", result.getJson());
        if (result.isSucceed()) {
            return success();
        }
        return failure(result.toString());
    }
}
