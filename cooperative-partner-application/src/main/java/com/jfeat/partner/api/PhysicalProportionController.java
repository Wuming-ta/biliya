package com.jfeat.partner.api;

import com.jfeat.core.RestController;
import com.jfeat.partner.model.PhysicalSettlementProportion;
import com.jfinal.ext.route.ControllerBind;

/**
 * Created by kang on 2017/6/19.
 */
@ControllerBind(controllerKey = "/rest/physical_proportion")
public class PhysicalProportionController extends RestController {
    public void index() {
        renderSuccess(PhysicalSettlementProportion.dao.findAll());
    }
}
