package com.jfeat.partner.api;

import com.jfeat.core.RestController;
import com.jfeat.partner.model.PhysicalPurchaseJournal;
import com.jfeat.partner.model.PhysicalSeller;
import com.jfeat.partner.model.Seller;
import com.jfeat.validator.PhysicalCrownAuthorityValidator;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kang on 2017/6/20.
 */
@ControllerBind(controllerKey = "/rest/physical_purchase_journal")
public class PhysicalPurchaseJournalController extends RestController {

    @Before({PhysicalCrownAuthorityValidator.class})
    public void show() {
        String month = getPara("month");
        Seller recommendedSeller = getAttr("recommendedSeller");
        PhysicalSeller.CrownParentLevel level = getAttr("level");
        List<PhysicalPurchaseJournal> list = PhysicalPurchaseJournal.dao.findBySellerIdMonth(recommendedSeller.getId(), month).stream().map(item -> {
            if (level == PhysicalSeller.CrownParentLevel.LEVEL_ONE) {
                item.setExpectedReward(item.getExpectedRewardLv1());
                item.setProductSettlementProportion(item.getSettlementProportionLv1());
            }
            else if (level == PhysicalSeller.CrownParentLevel.LEVEL_TWO) {
                item.setExpectedReward(item.getExpectedRewardLv2());
                item.setProductSettlementProportion(item.getSettlementProportionLv2());
            }
            return item;
        }).collect(Collectors.toList());
        renderSuccess(list);
    }

}
