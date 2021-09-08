package com.jfeat.ext.plugin.sms;

/**
 * @author jackyhuang
 * @date 2018/12/14
 */
public class SmsException extends RuntimeException {

    private String code;
    private String message;

    public SmsException(String message) {
        super(message);
        this.message = message;
    }

    public SmsException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
