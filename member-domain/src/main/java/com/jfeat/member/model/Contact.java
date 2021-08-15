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

/*
 * This file is automatically generated by tools.
 * It defines the model for the table. All customize operation should 
 * be written here. Such as query/update/delete.
 * The controller calls this object.
 */
package com.jfeat.member.model;

import com.jfeat.kit.SqlQuery;
import com.jfeat.kit.SqlUpdate;
import com.jfeat.member.model.base.ContactBase;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Db;

import java.util.List;

@TableBind(tableName = "t_contact")
public class Contact extends ContactBase<Contact> {

    /**
     * Only use for query.
     */
    public static Contact dao = new Contact();

    public static final int DEFAULT_ADDR = 1;

    public List<Contact> findByUserId(int userId) {
        SqlQuery query = new SqlQuery();
        query.from(getTableName());
        query.where(Fields.USER_ID.eq("?"));
        return find(query.sql(), userId);
    }

    public Contact findDefaultByUserId(int userId) {
        SqlQuery query = new SqlQuery();
        query.from(getTableName());
        query.where(Fields.USER_ID.eq("?"));
        query.and(Fields.IS_DEFAULT.eq("?"));
        return findFirst(query.sql(), userId, DEFAULT_ADDR);
    }

    public boolean save() {
        if (getIsDefault() != null && getIsDefault() == DEFAULT_ADDR) {
            clearDefault(getUserId());
        }
        remove(Fields.ID.toString());
        return super.save();
    }

    public boolean update() {
        if (getIsDefault() != null && getIsDefault() == DEFAULT_ADDR) {
            clearDefault(getUserId());
        }
        return super.update();
    }

    private void clearDefault(int userId) {
        SqlUpdate clear = new SqlUpdate();
        clear.update(getTableName());
        clear.set(Fields.IS_DEFAULT.toString(), 0);
        clear.where(Fields.USER_ID.eq("?"));
        Db.update(clear.sql(), userId);
    }
}