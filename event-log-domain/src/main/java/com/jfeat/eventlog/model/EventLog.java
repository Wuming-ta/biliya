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
package com.jfeat.eventlog.model;

import com.jfeat.eventlog.model.base.EventLogBase;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@TableBind(tableName = "t_event_log")
public class EventLog extends EventLogBase<EventLog> {

    /**
     * Only use for query.
     */
    public static EventLog dao = new EventLog();

    @Override
    public boolean save() {
        setCreateTime(new Date());
        return super.save();
    }

    public Page<EventLog> subPaginate(int pageNumber, int pageSize, String eventType, String user, String startTime, String endTime) {
        StringBuilder sql = new StringBuilder();
        List<Object> param = new ArrayList<>();
        sql.append(" from t_event_log ");
        String cond = " where ";
        if (StrKit.notBlank(eventType)) {
            sql.append(cond);
            sql.append(" event_type=? ");
            param.add(eventType);
            cond = " and ";
        }
        if (StrKit.notBlank(user)) {
            sql.append(cond).append(" user = ? ");
            param.add(user);
            cond = " and ";
        }
        if (StrKit.notBlank(startTime)) {
            sql.append(cond);
            sql.append(" create_time>=? ");
            param.add(startTime);
            cond = " and ";
        }
        if (StrKit.notBlank(endTime)) {
            sql.append(cond);
            sql.append(" create_time<=? ");
            param.add(endTime);
            cond = " and ";
        }
        sql.append(" order by id desc");
        return paginate(pageNumber, pageSize, "select * ", sql.toString(), param.toArray());
    }

    public List<EventLog> getRecent() {
        return subPaginate(1, 10,null, null, null, null).getList();
    }


}