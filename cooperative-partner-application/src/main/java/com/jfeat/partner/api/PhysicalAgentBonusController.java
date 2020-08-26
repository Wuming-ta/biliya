package com.jfeat.partner.api;

import com.jfeat.core.RestController;
import com.jfeat.identity.model.User;
import com.jfeat.partner.model.PhysicalAgentBonus;
import com.jfeat.partner.model.PhysicalSeller;
import com.jfeat.partner.model.Seller;
import com.jfinal.ext.route.ControllerBind;

/**
 * Created by Administrator on 2017/8/1.
 */
@ControllerBind(controllerKey = "/rest/physical_agent_bonus")
public class PhysicalAgentBonusController extends RestController {

    public void index() {
        Integer pcdId = getParaToInt("pcd_id");
        if (pcdId == null) {
            renderFailure("pcd_id.is.required");
            return;
        }
        User currentUser = getAttr("currentUser");
        Seller seller = Seller.dao.findByUserId(currentUser.getId());
        PhysicalSeller physicalSeller = PhysicalSeller.dao.findBySellerId(seller.getId());
        if (physicalSeller == null) {
            renderFailure("seller.is.not.a.physical");
            return;
        }
        if (!seller.isAgent()) {
            renderFailure("seller.is.not.a.agent");
            return;
        }
        renderSuccess(PhysicalAgentBonus.dao.findByPcdId(pcdId));
    }
}
