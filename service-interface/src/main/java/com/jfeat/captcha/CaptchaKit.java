package com.jfeat.captcha;

/**
 * @author jackyhuang
 * @date 2018/5/19
 */
public class CaptchaKit {
    private static CaptchaService captchaService;
    private static boolean enabled = false;

    private static void checkInstance() {
        if(captchaService == null) {
            throw new RuntimeException("CaptchaService is not set.");
        }
    }

    public static void init(CaptchaService captchaService) {
        CaptchaKit.captchaService = captchaService;
    }

    public static void setEnabled(boolean enabled) {
        CaptchaKit.enabled = enabled;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static String getCode(String source) {
        checkInstance();
        return captchaService.getCode(source);
    }

    public static boolean verifyCode(String source, String code) {
        checkInstance();
        return captchaService.verifyCode(source, code);
    }
}
