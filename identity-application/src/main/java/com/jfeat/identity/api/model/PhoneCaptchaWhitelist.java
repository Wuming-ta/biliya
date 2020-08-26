package com.jfeat.identity.api.model;

import java.util.ArrayList;
import java.util.List;

public class PhoneCaptchaWhitelist {
    private static PhoneCaptchaWhitelist _inst;
    public static PhoneCaptchaWhitelist getInstance(){
        if(_inst==null){
            _inst = new PhoneCaptchaWhitelist();
        }
        return _inst;
    }

    private PhoneCaptchaWhitelist(){}

    private List<PhoneCaptcha> whitelist;

    public PhoneCaptchaWhitelist register(String phone, String captcha){
        if(whitelist==null){
            whitelist = new ArrayList<>();
        }
        whitelist.add(new PhoneCaptcha(phone, captcha));
        return this;
    }

    public void unregister(String phone){
        if(whitelist!=null && whitelist.size()>0){
            for (PhoneCaptcha item : whitelist){
                if(item.getPhone().compareTo(phone)==0){
                    whitelist.remove(item);
                    break;
                }
            }
        }
    }

    public int count(){
        if(whitelist!=null){
            return whitelist.size();
        }
        return 0;
    }

    public boolean check(String phone, String captcha){
        if(whitelist!=null && whitelist.size()>0){
            for (PhoneCaptcha item : whitelist){
                if(item.getPhone().compareTo(phone)==0 && item.getCaptcha().compareTo(captcha)==0){
                    return true;
                }
            }
        }
        return false;
    }
}
