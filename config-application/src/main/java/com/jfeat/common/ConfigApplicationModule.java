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
package com.jfeat.common;

import com.jfeat.config.controller.ConfigController;
import com.jfeat.config.sys.api.SysConfigController;
import com.jfeat.core.Module;
import com.jfeat.core.JFeatConfig;
import com.jfeat.config.api.GlobalConfigController;
import com.jfinal.config.Constants;
import com.jfinal.i18n.I18n;

public class ConfigApplicationModule extends Module {

    public ConfigApplicationModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        ConfigApplicationModelMapping.mapping(this);

        addXssExcluded("/config");

        // 1. register your controllers
        // addController(YourDefinedController.class);
        addController(ConfigController.class);
        addController(GlobalConfigController.class);
        addController(SysConfigController.class);

        // 3. config the module you dependencied.
        // new YouDependenciedModule(jfeatConfig);
        new ConfigDomainModule(jfeatConfig);
        new IdentityApplicationModule(jfeatConfig);
    }


}
