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

package com.jfeat.eventlog.controller;

import com.jfeat.core.BaseController;
import com.jfeat.eventlog.model.EventLog;
import com.jfeat.flash.Flash;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Created by jingfei on 2016/4/9.
 */
@RequiresPermissions("EventLogApplication.view")
public class EventLogController extends BaseController {

    @Before(Flash.class)
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 10);
        String user = getPara("user");
        String startTime = getPara("startTime", "");
        String endTime = getPara("endTime", "");
        String eventType = getPara("eventType", "");
        Page<EventLog> logs;
        logs = EventLog.dao.subPaginate(pageNumber, pageSize, eventType, user, startTime, endTime);
        setAttr("logs", logs);
        setAttr("startTime", startTime);
        setAttr("endTime", endTime);
        setAttr("eventType", eventType);
        keepPara();
    }

    public void show() throws Exception {
        EventLog log = EventLog.dao.findById(getParaToInt());
        ObjectMapper mapper = new ObjectMapper();
        try {
            Object json = mapper.readValue(log.getData(), Object.class);
            log.setData(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
        }catch(Exception ex) {
        }
        setAttr("log", log);
    }

}
