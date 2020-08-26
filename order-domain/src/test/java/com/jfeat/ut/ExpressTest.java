package com.jfeat.ut;

import com.jfeat.AbstractTestCase;
import com.jfeat.config.model.Config;
import com.jfeat.config.model.ConfigGroup;
import com.jfeat.order.service.ExpressBasicService;
import com.jfeat.order.service.ExpressInfo;
import com.jfeat.order.service.ExpressService;
import com.jfeat.order.service.ExpressServiceHolder;
import com.jfinal.kit.JsonKit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by jacky on 5/13/16.
 */
public class ExpressTest extends AbstractTestCase {

    private ConfigGroup group;

    @Before
    public void setup() {
        group = new ConfigGroup();
        group.setName("test");
        group.save();
    }

    @After
    public void cleanup() {
        group.delete();
    }

    public void setupBasic() {
        Config config = new Config();
        config.setKeyName("express.key");
        config.setGroupId(group.getId());
        config.setValueType("String");
        config.setType("sys");
        config.setValue("5c939702fc86a217");
        config.save();
    }

    public void setupPro() {
        Config keyConfig = new Config();
        keyConfig.setKeyName("express.key");
        keyConfig.setGroupId(group.getId());
        keyConfig.setValueType("String");
        keyConfig.setType("sys");
        keyConfig.setValue("CkMIlfPt9603");
        keyConfig.save();
        Config customerConfig = new Config();
        customerConfig.setKeyName("express.customer");
        customerConfig.setGroupId(group.getId());
        customerConfig.setValueType("String");
        customerConfig.setType("sys");
        customerConfig.setValue("3FC23CED3ABE42DEAEE506AF770CFF9D");
        customerConfig.save();
    }

    @Test
    public void testValidNumberViaBasic() {
        setupBasic();
        ExpressService service = ExpressServiceHolder.me().getExpressService();
        ExpressInfo expressInfo = service.queryExpress("wanxiangwuliu", "2215330905490");
        logger.debug(JsonKit.toJson(expressInfo));
    }

    @Test
    public void testInvalidNumberViaBasic() {
        setupBasic();
        ExpressService service = ExpressServiceHolder.me().getExpressService();
        ExpressInfo expressInfo = service.queryExpress("wanxiangwuliu", "55555555555555");
        logger.debug(JsonKit.toJson(expressInfo));
    }

    //@Test
    public void testValidNumberViaPro() {
        setupPro();
        ExpressService service = ExpressServiceHolder.me().getExpressService();
        ExpressInfo expressInfo = service.queryExpress("wanxiangwuliu", "2215330905490");
        logger.debug(JsonKit.toJson(expressInfo));
    }

    //@Test
    public void testInvalidNumberViaPro() {
        setupPro();
        ExpressService service = ExpressServiceHolder.me().getExpressService();
        ExpressInfo expressInfo = service.queryExpress("wanxiangwuliu", "55555555555555");
        logger.debug(JsonKit.toJson(expressInfo));
    }
}

