/*
 *   Copyright (C) 2014-2018 www.kequandian.net
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
package com.jfeat.marketing.trial.model;

import com.jfeat.marketing.trial.model.base.TrialImageBase;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfeat.marketing.trial.model.Trial;

import java.util.List;

@TableBind(tableName = "t_trial_image")
public class TrialImage extends TrialImageBase<TrialImage> {

    /**
     * Only use for query.
     */
    public static TrialImage dao = new TrialImage();

    public List<TrialImage> findByTrialId(Integer trialId) {
        return findByField(Fields.TRIAL_ID.toString(), trialId, new String[] { Fields.SORT_ORDER.toString() }, null);
    }

    public Trial getTrial() {
        return Trial.dao.findById(getTrialId());
    }

}
