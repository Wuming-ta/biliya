package com.jfeat.plugintest.cache;

import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by jackyhuang on 17/1/3.
 */
public class CacheTest {

    @Before
    public void setup() {
        EhCachePlugin ehCachePlugin = new EhCachePlugin();
        ehCachePlugin.start();
    }

    @Test
    public void test1() {
        printCacheKey();
        TestModel.dao.findByName("ssa");
        printCacheKey();
    }

    @Test
    public void test() {
        printCacheKey();
        TestModel.dao.findById(0);
        printCacheKey();
        TestModel a = TestModel.dao.findById(0);
        printCacheKey();
        a.duang().delete();
        printCacheKey();

        TestModel b = TestModel.dao.findByName("B");
        printCacheKey();
        TestModel.dao.find(0, "good");
        printCacheKey();
        TestModel.dao.find(0, "good");
        printCacheKey();
        b.duang().update();
        printCacheKey();

        TestModel.dao.find(0, "good");
        printCacheKey();
        TestModel.dao.find(0, "bad");
        printCacheKey();
        TestModel.dao.findById(0);
        printCacheKey();
        TestModel.dao.findByName("B");
        printCacheKey();
        b.duang().delete();
        printCacheKey();
    }

    private void printCacheKey() {
        System.out.println("================");
        for (Object key : CacheKit.getKeys("J_MODEL_com.jfeat.plugintest.cache.TestModel")){
            System.out.println(key);
        }
    }
}
