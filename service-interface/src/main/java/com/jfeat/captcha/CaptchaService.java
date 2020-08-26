package com.jfeat.captcha;

/**
 * @author jackyhuang
 * @date 2018/6/22
 */
public interface CaptchaService {

    public String getCode(String source);

    public boolean verifyCode(String source, String code);
}
