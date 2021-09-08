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
public @interface AutoCache {
    /**
     * 指定方法的参数参与构建缓存key, 使用 name:index 方式
     * name是参数名称, index是参数的索引,从0开始
     * 如:
     * @AutoCache(value = {"id:0", "name:1"})
     * public List find(int id, String name)
     */
    String[] value() default {};

    /**
     * 需要配合mapKey使用。
     */
    String saveKey() default "";
}
