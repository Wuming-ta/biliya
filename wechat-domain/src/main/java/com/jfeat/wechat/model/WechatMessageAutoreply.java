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

import com.google.common.collect.Lists;
import com.jfeat.wechat.model.base.WechatMessageAutoreplyBase;
import com.jfinal.ext.plugin.tablebind.TableBind;

import java.util.List;

@TableBind(tableName = "t_wechat_message_autoreply")
public class WechatMessageAutoreply extends WechatMessageAutoreplyBase<WechatMessageAutoreply> {

    /**
     * Only use for query.
     */
    public static WechatMessageAutoreply dao = new WechatMessageAutoreply();

    public enum Type {
        TEXT,
        IMAGE,
        NEWS
    }

    public WechatMessageAutoreply findFirstByTypeAndEnabled(String type, Integer enabled) {
        String sql = "select * from t_wechat_message_autoreply where type=?";
        List<Object> params = Lists.newLinkedList();
        params.add(type);
        if (enabled != null) {
            sql += " and enabled=?";
            params.add(enabled);
        }
        return findFirst(sql, params.toArray());
    }

    public List<WechatMessageAutoreply> findByTypeAndEnabled(String type, Integer enabled) {
        String sql = "select * from t_wechat_message_autoreply where type=?";
        List<Object> params = Lists.newLinkedList();
        params.add(type);
        if (enabled != null) {
            sql += " and enabled=?";
            params.add(enabled);
        }
        return find(sql, params.toArray());
    }
}
