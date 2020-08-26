package com.jfeat.partner.api;

import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.partner.model.PhysicalApplyTips;
import com.jfinal.ext.route.ControllerBind;

/**
 * Created by kang on 2017/6/23.
 */
@ControllerBind(controllerKey = "/rest/physical_apply_tips")
public class PhysicalApplyTipsController extends RestController {

    @Validation(rules = {"type=required"})
    public void index() {
        renderSuccess(PhysicalApplyTips.dao.findFirstByField(PhysicalApplyTips.Fields.TYPE.toString(), getPara("type")));
    }

}
