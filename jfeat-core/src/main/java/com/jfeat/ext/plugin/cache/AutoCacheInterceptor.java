package com.jfeat.ext.plugin.cache;

import com.google.common.collect.Lists;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by jackyhuang on 17/1/2.
 */
public class AutoCacheInterceptor implements Interceptor {

    private static Logger logger = LoggerFactory.getLogger(AutoCacheInterceptor.class);

    private static final String CACHE_NAME_PREFIX = "J_MODEL";

    private static final String AUTO_CREATE_CACHED_KEY_PREFIX = "J_CACHE_PREFIX";

    private static final String SEPARATOR = "_";

    @Override
    public void intercept(Invocation invocation) {
        Method method = invocation.getMethod();
        AutoCache autoCache = method.getAnnotation(AutoCache.class);
        AutoCacheDelete autoCacheDelete = method.getAnnotation(AutoCacheDelete.class);
        if (autoCache != null && autoCacheDelete != null) {
            throw new RuntimeException("Cannot use AutoCache and AutoCacheDelete at the same time.");
        }
        if (autoCache == null && autoCacheDelete == null) {
            invocation.invoke();
            return;
        }
        if (autoCache != null) {
            handleAutoCache(invocation, autoCache);
        }
        if (autoCacheDelete != null) {
            handleAutoCacheDelete(invocation, autoCacheDelete);
        }
    }

    private void handleAutoCacheDelete(Invocation invocation, AutoCacheDelete autoCacheDelete) {
        String cacheName = getCacheName(invocation);
        String mapKey = autoCacheDelete.mapKey();
        if (StrKit.isBlank(mapKey)) {
            CacheKit.removeAll(cacheName);
            logger.debug("remove all cache: [{}].", cacheName);
        }
        else {
            String keyPrefix = cacheKeyPrefix(mapKey).toString();
            for (Object key : CacheKit.getKeys(cacheName)) {
                if (key.toString().startsWith(keyPrefix)) {
                    CacheKit.remove(cacheName, key);
                    logger.debug("remove cache: [{} - {}]", cacheName, key);
                }
            }
        }
        invocation.invoke();
    }

    private void handleAutoCache(Invocation invocation, AutoCache autoCache) {
        String cacheName = getCacheName(invocation);
        String key = createCacheKey(invocation, autoCache.saveKey(), autoCache.value());
        logger.debug("{} - {}", cacheName, key);
        Object result = CacheKit.get(cacheName, key);
        if (result == null) {
            synchronized(key) {
                result = CacheKit.get(cacheName, key);
                if (result == null) {
                    logger.debug("cache not found [{} - {}]", cacheName, key);
                    invocation.invoke();
                    result = invocation.getReturnValue();
                    if (result != null) {
                        CacheKit.put(cacheName, key, result);
                        logger.debug("save cache: [{} - {}] for result {}", cacheName, key, result);
                    }
                    return;
                }
            }
        }

        invocation.setReturnValue(result);
    }

    private String getCacheName(Invocation invocation) {
        return CACHE_NAME_PREFIX + SEPARATOR + invocation.getTarget().getClass().getName();
    }

    // J_CACHE_PREFIX_TheModalClassName_saveKey_
    private StringBuilder cacheKeyPrefix(String key) {
        StringBuilder builder = new StringBuilder(AUTO_CREATE_CACHED_KEY_PREFIX);
        builder.append(SEPARATOR);
        builder.append(key);
        builder.append(SEPARATOR);
        return builder;
    }

    //key: J_CACHE_PREFIX_TheModalClassName_saveKey_methodName_arg1name_arg1value_arg2name_arg2value
    private String createCacheKey(Invocation invocation, String saveKey, String[] values) {
        StringBuilder builder = cacheKeyPrefix(saveKey);
        Object[] args = invocation.getArgs();
        try {
            builder.append(invocation.getMethodName());
            builder.append(SEPARATOR);
            if (values.length > 0) {
                for (String value : values) {
                    String[] array = value.split(":");
                    builder.append(array[0].trim());
                    if (array.length == 2) {
                        builder.append(SEPARATOR);
                        int index = Integer.parseInt(array[1].trim());
                        builder.append(args[index]);
                    }
                    builder.append(SEPARATOR);
                }
            }
            else {
                int i = 0;
                for (Object arg : args) {
                    builder.append("arg");
                    builder.append(i++);
                    builder.append(SEPARATOR);
                    builder.append(arg);
                    builder.append(SEPARATOR);
                }
            }
        }
        catch (Exception ex) {
            throw new RuntimeException("param index error. " + ex.getMessage());
        }
        return builder.toString();
    }
}
