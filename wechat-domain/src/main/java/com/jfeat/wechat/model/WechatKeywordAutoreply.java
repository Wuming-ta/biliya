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

/*
 * This file is automatically generated by tools.
 * It defines the model for the table. All customize operation should 
 * be written here. Such as query/update/delete.
 * The controller calls this object.
 */
package com.jfeat.wechat.model;

import com.jfeat.wechat.model.base.WechatKeywordAutoreplyBase;
import com.jfinal.ext.plugin.tablebind.TableBind;

import java.util.List;

@TableBind(tableName = "t_wechat_keyword_autoreply")
public class WechatKeywordAutoreply extends WechatKeywordAutoreplyBase<WechatKeywordAutoreply> {

    /**
     * Only use for query.
     */
    public static WechatKeywordAutoreply dao = new WechatKeywordAutoreply();

    public WechatKeywordAutoreplyItem getText() {
        return WechatKeywordAutoreplyItem.dao.findFirst(getId(), WechatKeywordAutoreplyItem.Type.TEXT.toString());
    }

    public List<WechatKeywordAutoreplyItem> getNewses() {
        return WechatKeywordAutoreplyItem.dao.find(getId(), WechatKeywordAutoreplyItem.Type.NEWS.toString());
    }

    public WechatKeywordAutoreplyItem findFirstByTypeAndEnabled(String type, Integer enabled) {
        return WechatKeywordAutoreplyItem.dao.findFirst(getId(), type, enabled);
    }

    public List<WechatKeywordAutoreplyItem> findByTypeAndEnabled(String type, Integer enabled) {
        return WechatKeywordAutoreplyItem.dao.find(getId(), type, enabled);
    }
}