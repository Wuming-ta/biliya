/*
 *   Copyright (C) 2014-2017 www.kequandian.net
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

public class MiscDomainModelMapping {

    public static void mapping(Module module) {

        module.addModel(com.jfeat.misc.model.Feedback.class);
        module.addModel(com.jfeat.misc.model.FeedbackImage.class);
        module.addModel(com.jfeat.misc.model.Faq.class);
        module.addModel(com.jfeat.misc.model.AboutMall.class);
        module.addModel(com.jfeat.misc.model.FaqType.class);
        module.addModel(com.jfeat.misc.model.AdLinkDefinition.class);
        module.addModel(com.jfeat.misc.model.Ad.class);
        module.addModel(com.jfeat.misc.model.AdGroup.class);
        module.addModel(com.jfeat.misc.model.CustomerServiceType.class);
        module.addModel(com.jfeat.misc.model.KfQq.class);
        module.addModel(com.jfeat.misc.model.SystemAnnouncement.class);

    }

}