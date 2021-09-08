package com.jfeat.ext.plugin.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by jackyhuang on 17/1/2.
 */
@Target({ElementType.METHOD})   //作用在方法上面
@Retention(RetentionPolicy.RUNTIME) //在运行时可用
public @interface AutoCacheDelete {
    /**
     * 对应于AutoCache的saveKey, 如果指定了mapKey,那么就会删除缓存里以该关键字开头的key
     * 比如缓存里存在一下key:
     *  J_CACHE_PREFIX_mykey_id_1_name_good_
     *  J_CACHE_PREFIX_mykey_id_1_
     *  J_CACHE_PREFIX_mykey_name_bad_
     *
     * 如果一个方法使用了AutoCacheDelete(mapKey = "mykey"), 那么就会删除J_CACHE_PREFIX_mykey_*
     *
     */
    String mapKey() default "";
}

