package com.jfeat.captcha.api;

import com.jfeat.captcha.CaptchaKit;
import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.sms.SmsKit;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfinal.ext.route.ControllerBind;

import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/8/29
 */
@ControllerBind(controllerKey = "/rest/pub/sms_verify")
public class SmsVerifyController extends RestController {

    @Override
    @Validation(rules = { "phone = required", "captcha = required" })
    public void save() {
        Map<String, Object> maps = convertPostJsonToMap();
        String phone = (String) maps.get("phone");
        String captcha = (String) maps.get("captcha");

        if (CaptchaKit.verifyCode(phone, captcha)) {
            renderSuccess("ok");
            return;
        }

        renderFailure("failure");
    }
}
