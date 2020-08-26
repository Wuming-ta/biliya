package com.jfeat.common;

import com.jfeat.captcha.CaptchaService;
import com.jfeat.core.BaseService;

/**
 * @author jackyhuang
 * @date 2018/6/22
 */
public class CaptchaServiceDummyImpl extends BaseService implements CaptchaService {

    public CaptchaServiceDummyImpl() {
        logger.debug("captcha service dummy impl");
    }

    private static final String dummyValue = "dummy";

    @Override
    public String getCode(String source) {
        return dummyValue;
    }

    @Override
    public boolean verifyCode(String source, String code) {
        return dummyValue.equals(code);
    }
}
