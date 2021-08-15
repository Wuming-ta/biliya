package com.jfeat.captcha.service;

import com.jfeat.core.BaseService;
import com.jfeat.captcha.CaptchaService;
import com.jfinal.ext.kit.RandomKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.redis.Cache;

/**
 * @author jackyhuang
 * @date 2018/5/18
 */
public class CaptchaServiceImpl extends BaseService implements CaptchaService {

    private Cache cache;
    private Long expiredSeconds = 60L; //seconds

    public CaptchaServiceImpl() {
    }

    public CaptchaServiceImpl(Cache cache) {
        this();
        this.cache = cache;
    }

    public CaptchaServiceImpl(Cache cache, Long expiredSeconds) {
        this(cache);
        this.expiredSeconds = expiredSeconds;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    @Override
    public String getCode(String source) {
        String storedCode = cache.get(source);
        if (StrKit.notBlank(storedCode)) {
            logger.debug("source = {}, storedCode = {}", source, storedCode);
            return storedCode;
        }

        String code = RandomKit.smsAuthCode(6);
        Long expiredTime = System.currentTimeMillis() / 1000 + expiredSeconds;
        String res = cache.set(source, code);
        logger.debug("Redis set - key:{}, value: {}, res:{}", source, code, res);
        Long result = cache.expireAt(source, expiredTime);
        logger.debug("Redis expireAt result = {}", result);
        return code;
    }

    @Override
    public boolean verifyCode(String source, String code) {
        String storedCode = cache.get(source);
        logger.debug("verifyCode: source = {}, storedCode = {}, toVerifyCode = {}", source, storedCode, code);
        return StrKit.notBlank(storedCode) && storedCode.equals(code);
    }
}
