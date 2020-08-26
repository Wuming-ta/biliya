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
import com.jfeat.member.model.base.MemberExtBase;
import com.jfeat.member.model.base.MemberLevelBase;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Db;

import java.util.List;

@TableBind(tableName = "t_member_level")
public class MemberLevel extends MemberLevelBase<MemberLevel> {

    /**
     * Only use for query.
     */
    public static MemberLevel dao = new MemberLevel();

    public boolean hasMember() {
        SqlQuery query = new SqlQuery();
        query.select("count(*)");
        query.from(MemberExt.dao.getTableName());
        query.where(MemberExt.Fields.LEVEL_ID.eq("?"));
        return Db.queryLong(query.sql(), getId()) > 0;
    }

    public MemberLevel findFirstLevel() {
        SqlQuery query = new SqlQuery();
        query.from(getTableName());
        query.orderBy(Fields.POINT.toString());
        return findFirst(query.sql());
    }

    public MemberLevel findByName(String name) {
        SqlQuery query = new SqlQuery();
        query.from(getTableName());
        query.orderBy(Fields.NAME.eq("?"));
        return findFirst(query.sql(), name);
    }

    public List<MemberLevel> findAllOrderByDesc(String field) {
        SqlQuery query = new SqlQuery();
        query.from(getTableName());
        query.orderByDesc(field);
        return find(query.sql());
    }
}
