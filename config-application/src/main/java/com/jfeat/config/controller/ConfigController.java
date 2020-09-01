/*
 *   Copyright (C) 2014-2016 GIMC
 *
 *    The program may be used and/or copied only with the written permission
 *    from GIMC or in accordance with the terms and
 *    conditions stipulated in the agreement/contract under which the program
 *    has been supplied.
 *
 *    All rights reserved.
 *
 */

package com.jfeat.config.controller;

import com.jfeat.config.model.Config;
import com.jfeat.config.model.ConfigGroup;
import com.jfeat.core.BaseController;
import com.jfeat.flash.Flash;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import validator.PwdValidator;

import java.util.List;

/**
 * Created by jingfei on 2016/4/2.
 */
public class ConfigController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(ConfigController.class);

    @Override
    @Before(Flash.class)
    public void index() {
        List<ConfigGroup> groups = ConfigGroup.dao.findAll();
        setAttr("groups", groups);
    }

    @Override
    @Before({Tx.class, PwdValidator.class})
    public void save() {
        List<Config> configs = getModels(Config.class);
        for (Config config : configs) {
            if (Config.ValueType.BOOLEAN.getType().equals(config.getValueType())) {
                if ("on".equals(config.getValue())) {
                    config.setValue("true");
                } else {
                    config.setValue("false");
                }
            }
            boolean result = config.update();
            logger.debug("config {} ,update result = {}", config, result);
        }
        setFlash("message", getRes().get("config.success"));
        String returnUrl = getPara("returnUrl", "/config");
        redirect(returnUrl);
    }

}
