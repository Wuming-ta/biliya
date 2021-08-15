package com.jfeat.captcha.api;

import com.jfeat.captcha.CaptchaKit;
import com.jfeat.common.SmsConfigKit;
import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.sms.SmsException;
import com.jfeat.ext.plugin.sms.SmsKit;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * TODO: 增加防刷机制
 * @author jackyhuang
 * @date 2018/5/18
 */
@ControllerBind(controllerKey = "/rest/pub/sms")
public class SmsController extends RestController {

    @Override
    @Validation(rules = { "phone = required" })
    public void save() {
        Map<String, Object> maps = convertPostJsonToMap();
        String phone = (String) maps.get("phone");
        String name = (String) maps.get("name");

        String code = CaptchaKit.getCode(phone);
        try {
            Map<String, String> params = new LinkedHashMap<>();
            params.put("code", code);
            if (StrKit.notBlank(SmsConfigKit.getTtl(name))) {
                params.put("ttl", SmsConfigKit.getTtl(name));
            }
            SmsKit.send(name, params, phone);
            renderSuccess("ok");
        }
        catch (SmsException ex) {
            renderFailure(ex.getMessage());
        }
    }
}
