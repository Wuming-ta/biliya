package com.jfeat.identity.api.model;

public class PhoneCaptcha {
    private String phone;
    private String captcha;

    public PhoneCaptcha(){}
    public PhoneCaptcha(String phone, String captcha){
        this.phone = phone;
        this.captcha = captcha;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
