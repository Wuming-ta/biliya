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
package com.jfeat.misc.model;

import com.jfeat.misc.model.base.SystemAnnouncementBase;
import com.jfinal.ext.plugin.tablebind.TableBind;

import java.util.Date;
import java.util.List;

@TableBind(tableName = "t_system_announcement")
public class SystemAnnouncement extends SystemAnnouncementBase<SystemAnnouncement> {

    public static final int ENABLED = 1;
    public static final int DISABLED = 0;

    /**
     * Only use for query.
     */
    public static SystemAnnouncement dao = new SystemAnnouncement();

    public boolean save() {
        Date date = new Date();
        setCreatedDate(date);
        setLastModifiedDate(date);
        return super.save();
    }

    public boolean update() {
        setLastModifiedDate(new Date());
        return super.update();
    }

    public List<SystemAnnouncement> findByEnabled() {
        return find("select * from t_system_announcement where enabled=1 order by last_modified_date desc");
    }
}
