package com.jfeat.partner.api;

import com.jfeat.core.RestController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.kit.DateKit;
import com.jfeat.partner.model.*;
import com.jfeat.partner.service.CopartnerService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jackyhuang
 * @date 2018/8/23
 */
@ControllerBind(controllerKey = "/rest/copartner_settlement")
public class CopartnerSettlementController extends RestController {

    private CopartnerService copartnerService = Enhancer.enhance(CopartnerService.class);

    /**
     * get /rest/copartner_settlement?month=2018-06
     */
    @Override
    @Before(CurrentUserInterceptor.class)
    public void index() {
        User currentUser = getAttr("currentUser");
        Seller seller = Seller.dao.findByUserId(currentUser.getId());
        if (seller == null || !copartnerService.isCopartner(seller.getId())) {
            renderFailure("not.a.copartner");
            return;
        }

        Copartner copartner = Copartner.dao.findBySellerId(seller.getId());
        String month = getPara("month");
        if (StrKit.isBlank(month)) {
            List<CopartnerSettlement> settlements = CopartnerSettlement.dao.findByField(CopartnerSettlement.Fields.COPARTNER_ID.toString(),
                    copartner.getId(),
                    null,
                    new String[] { CopartnerSettlement.Fields.ID.toString() });
            renderSuccess(settlements);
            return;
        }
        month = month + "-01";

        CopartnerSettlement copartnerSettlement = CopartnerSettlement.dao.findByCond(copartner.getId(), month);
        if (copartnerSettlement == null) {
            SettlementProportion copartnerSettlementProportions = SettlementProportion.dao.findByCopartner();
            copartnerSettlement = new CopartnerSettlement();
            copartnerSettlement.setSettlementProportion(BigDecimal.valueOf(copartnerSettlementProportions.getProportionObject().getValue()));
        }
        renderSuccess(copartnerService.querySettlement(copartner, month, copartnerSettlement.getSettlementProportion()));
    }

    /**
     * GET  /rest/copartner_settlement/:sellerid?month=2018-06
     */
    @Override
    @Before(CurrentUserInterceptor.class)
    public void show() {
        User currentUser = getAttr("currentUser");
        Seller seller = Seller.dao.findByUserId(currentUser.getId());
        if (seller == null || !copartnerService.isCopartner(seller.getId())) {
            renderFailure("not.a.copartner");
            return;
        }

        String month = getPara("month", DateKit.currentMonth("yyyy-MM"));

        Copartner copartner = Copartner.dao.findBySellerId(seller.getId());
        CopartnerSettlement copartnerSettlement = CopartnerSettlement.dao.findByCond(copartner.getId(), month + "-01");
        SettlementProportion copartnerSettlementProportions = SettlementProportion.dao.findByCopartner();
        BigDecimal settlementProportionValue = copartnerSettlement == null ?
                BigDecimal.valueOf(copartnerSettlementProportions.getProportionObject().getValue()) : copartnerSettlement.getSettlementProportion();

        Seller recommendedSeller = Seller.dao.findById(getParaToInt());
        List<PhysicalPurchaseJournal> list = PhysicalPurchaseJournal.dao.findBySellerIdMonth(recommendedSeller.getId(), month)
                .stream().peek(item -> {
                    item.put("reward", item.getAmount().multiply(settlementProportionValue).divide(new BigDecimal(100)));
                    item.put("settlement_proportion", settlementProportionValue);
                }).collect(Collectors.toList());
        renderSuccess(list);
    }
}
