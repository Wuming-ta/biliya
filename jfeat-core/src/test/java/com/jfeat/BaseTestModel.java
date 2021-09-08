package com.jfeat;

import com.google.common.collect.Lists;
import com.jfeat.ext.plugin.cache.AutoCache;
import com.jfeat.ext.plugin.cache.AutoCacheInterceptor;
import com.jfeat.plugintest.cache.*;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by jackyhuang on 17/1/4.
 */
@Before(AutoCacheInterceptor.class)
public abstract class BaseTestModel<T> {

    private static Logger logger = LoggerFactory.getLogger(BaseTestModel.class);

    protected static List<com.jfeat.plugintest.cache.TestModel> store = Lists.newArrayList();
    static {
        store.add(new com.jfeat.plugintest.cache.TestModel(1, "A"));
        store.add(new com.jfeat.plugintest.cache.TestModel(2, "B"));
        store.add(new com.jfeat.plugintest.cache.TestModel(3, "C"));
    }

    public T duang() {
        return Enhancer.enhance(this);
    }

    @AutoCache
    public T findByName(String name) {
        logger.debug("findByName");
        return (T) store.get(1);
    }
}
