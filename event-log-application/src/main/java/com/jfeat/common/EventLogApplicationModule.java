/*
 *   Copyright (C) 2014-2016 www.kequandian.net
 *
 *    The program may be used and/or copied only with the written permission
 *    from www.kequandian.net or in accordance with the terms and
 *    conditions stipulated in the agreement/contract under which the program
 *    has been supplied.
 *
 *    All rights reserved.
 *
 */
package com.jfeat.common;

import com.jfeat.core.Module;
import com.jfeat.core.JFeatConfig;
import com.jfeat.eventlog.controller.EventLogController;
import com.jfinal.config.Constants;
import com.jfinal.i18n.I18n;

public class EventLogApplicationModule extends Module {

    public EventLogApplicationModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        EventLogApplicationModelMapping.mapping(this);

        // 1. register your controllers
        // addController(YourDefinedController.class);
        addController(EventLogController.class);

        // 3. config the module you dependencied.
        // new YouDependenciedModule(jfeatConfig);
        new EventLogDomainModule(jfeatConfig);
        new IdentityApplicationModule(jfeatConfig);

    }

}
