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

package com.jfeat.member.model;

import com.jfeat.identity.model.User;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacky on 12/24/15.
 */
public class Member {

    private static Logger logger = LoggerFactory.getLogger(Member.class);

    public static Member dao = new Member();

    public Page<Record> paginate(int pageNumber, int pageSize, Integer levelId, String user) {
        String select = "select u.*,m.point,m.level_id, " +
                "(select count(*) from t_coupon c where c.user_id=u.id and status='ACTIVATION') as activation_coupon_count ";
        StringBuilder builder = new StringBuilder();
        builder.append("from ");
        builder.append(User.dao.getTableName());
        builder.append(" as u inner join ");
        builder.append(MemberExt.dao.getTableName());
        builder.append(" as m on u.");
        builder.append(User.Fields.ID);
        builder.append("=m.");
        builder.append(MemberExt.Fields.USER_ID);
        String cond = " where ";
        List<Object> params = new ArrayList<>();
        if (levelId != null) {
            builder.append(cond);
            builder.append(MemberExt.Fields.LEVEL_ID.eq("?"));
            params.add(levelId);
            cond = " and ";
        }
        if (StrKit.notBlank(user)) {
            builder.append(cond);
            builder.append("u.name like ?");
            params.add("%" + user + "%");
        }
        return Db.paginate(pageNumber, pageSize, select, builder.toString(), params.toArray());
    }

}
