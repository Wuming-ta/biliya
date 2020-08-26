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

import com.jfeat.core.JFeatConfig;
import com.jfeat.core.Module;
import com.jfeat.misc.api.UploadImageXController;
import com.jfeat.misc.controller.*;

public class MiscApplicationModule extends Module {

    public MiscApplicationModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        MiscApplicationModelMapping.mapping(this);

        addXssExcluded("/faq");
        addXssExcluded("/system_announcement");

        // 1. register your controllers
        // addController(YourDefinedController.class);
        addController(AboutMallController.class);
        addController(FaqController.class);
        addController(FeedbackController.class);
        addController(AdController.class);
        addController(CustomerServiceTypeController.class);
        addController(KfQqController.class);
        addController(SystemAnnouncementController.class);
        addController(FallbackUrlController.class);
        addController(com.jfeat.misc.api.AboutMallController.class);
        addController(com.jfeat.misc.api.FeedbackController.class);
        addController(com.jfeat.misc.api.FaqTypeController.class);
        addController(com.jfeat.misc.api.FaqController.class);
        addController(com.jfeat.misc.api.AdController.class);
        addController(com.jfeat.misc.api.CustomerServiceTypeController.class);
        addController(com.jfeat.misc.api.UploadImageController.class);
        addController(UploadImageXController.class);
        addController(com.jfeat.misc.api.KfQqController.class);
        addController(com.jfeat.misc.api.SystemAnnouncementController.class);

        // 3. config the module you dependencied.
        // new YouDependenciedModule(jfeatConfig);

        new IdentityApplicationModule(jfeatConfig);
        new MiscDomainModule(jfeatConfig);
        new ProductDomainModule(jfeatConfig);
    }

}
