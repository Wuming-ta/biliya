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

public class WechatDomainModelMapping {

    public static void mapping(Module module) {

        module.addModel(com.jfeat.wechat.model.WechatMessageType.class);
        module.addModel(com.jfeat.wechat.model.WechatMessageTypeProp.class);
        module.addModel(com.jfeat.wechat.model.WechatTemplateMessage.class);
        module.addModel(com.jfeat.wechat.model.WechatField.class);
        module.addModel(com.jfeat.wechat.model.WechatSubscribeAutoreply.class);
        module.addModel(com.jfeat.wechat.model.WechatMessageAutoreply.class);
        module.addModel(com.jfeat.wechat.model.WechatKeywordAutoreply.class);
        module.addModel(com.jfeat.wechat.model.WechatKeywordAutoreplyItem.class);

    }

}