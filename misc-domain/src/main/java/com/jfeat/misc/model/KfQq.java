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

import com.jfeat.misc.model.base.KfQqBase;
import com.jfinal.ext.plugin.tablebind.TableBind;

import java.util.List;

@TableBind(tableName = "t_kf_qq")
public class KfQq extends KfQqBase<KfQq> {

    /**
     * Only use for query.
     */
    public static KfQq dao = new KfQq();

    public List<KfQq> findAllEnabled() {
        return find("select * from t_kf_qq where enabled=1");
    }
}
