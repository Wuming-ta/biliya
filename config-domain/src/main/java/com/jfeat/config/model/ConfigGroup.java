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
package com.jfeat.config.model;

import com.jfeat.config.model.base.ConfigGroupBase;
import com.jfinal.ext.plugin.tablebind.TableBind;

import java.util.List;

@TableBind(tableName = "t_config_group")
public class ConfigGroup extends ConfigGroupBase<ConfigGroup> {

    /**
     * Only use for query.
     */
    public static ConfigGroup dao = new ConfigGroup();

    public static final int PROTECTED = 1;
    public static final int UNPROTECTED = 0;

    public List<Config> getConfigs() {
        return Config.dao.findByGroupId(getId());
    }

    public boolean isProtected() {
        return getProtected() == PROTECTED;
    }

}
