package com.jfeat.plugintest.cache;

import com.jfeat.BaseTestModel;
import com.jfeat.ext.plugin.cache.AutoCache;
import com.jfeat.ext.plugin.cache.AutoCacheDelete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jackyhuang on 17/1/3.
 */
public class TestModel extends BaseTestModel<TestModel> {

    private static Logger logger = LoggerFactory.getLogger(TestModel.class);

    public static TestModel dao = new TestModel().duang();

    private int id;
    private String name;

    public TestModel() {

    }

    public TestModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public TestModel find(String name) {
        logger.debug("find(name)");
        return findByName(name);
    }

    @AutoCache(saveKey = "abc")
    public TestModel findById(int id) {
        logger.debug("findById.");
        return store.get(id);
    }

    @AutoCache(saveKey = "abc", value = {"id: 0", "name: 1"})
    public TestModel find(int id, String name) {
        findByName("sss");
        logger.debug("find.");
        return store.get(id);
    }

    @AutoCacheDelete(mapKey = "abc")
    public boolean delete() {
        logger.debug("delete.");
        return true;
    }

    @AutoCacheDelete
    public boolean update() {
        logger.debug("update.");
        return true;
    }
}
