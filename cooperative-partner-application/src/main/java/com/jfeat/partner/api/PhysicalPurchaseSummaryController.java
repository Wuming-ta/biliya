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

package com.jfeat.partner.api;

import com.google.common.collect.Lists;
import com.jfeat.core.RestController;
import com.jfeat.identity.model.User;
import com.jfeat.partner.model.PhysicalPurchaseSummary;
import com.jfeat.partner.model.PhysicalSeller;
import com.jfeat.partner.model.Seller;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;

import java.util.List;

/**
 * Created by jackyhuang on 17/1/17.
 */
@ControllerBind(controllerKey = "/rest/physical_purchase_summary")
public class PhysicalPurchaseSummaryController extends RestController {
    private static final Integer LEVEL1_CROWN = 1;
    private static final Integer LEVEL2_CROWN=2;
    /**
     * get /rest/physical_purchase_summary?month=2017-06
     * <p>
     * return:
     *  {
     *      "status_code": 1,
     *      "data": [
     *          {
     *              "statistic_month": "2017-06-01", //统计月份
     *              "monthly_amount": 200000, //本月进货
     *              "monthly_settled_amount": 322.21, //提成金额
     *              "monthly_settlement_proportion": 2.5, //当月提成比例
     *              "transferred": 1, //是否已转积分系统
     *              "transferred_amount": 32221, //积分
     *              "total_amount": 4000000, //累计总进货. 当有month参数时才返回该项
     *              "total_settled_amount": 240000, //累计总提成. 当有month参数时才返回该项
     *              "my_recommended_sellers": [] //我的推荐线下经销商. 当有month参数时才返回该项
     *          }
     *      ]
     *  }
     *  </p>
     */
    public void index() {
        User currentUser = getAttr("currentUser");
        Seller seller = Seller.dao.findByUserId(currentUser.getId());
        PhysicalSeller physicalSeller = PhysicalSeller.dao.findBySellerId(seller.getId());
        if (physicalSeller == null) {
            renderFailure("seller.is.not.a.physical");
            return;
        }
        if (!seller.isCrownShip()) {
            renderFailure("seller.is.not.a.crown");
            return;
        }

        String month = getPara("month");
        if (StrKit.isBlank(month)) {
            renderSuccess(PhysicalPurchaseSummary.dao.findBySellerId(seller.getId()));
            return;
        }
        month = month + "-01";
        PhysicalPurchaseSummary monthSummary = PhysicalPurchaseSummary.dao.findBySellerIdAndMonth(seller.getId(), month);
        if (monthSummary == null) {
            monthSummary = new PhysicalPurchaseSummary();
        }
        monthSummary.put("total_amount", PhysicalPurchaseSummary.dao.queryTotalAmount(seller.getId()));
        monthSummary.put("total_settled_amount", PhysicalPurchaseSummary.dao.queryTotalSettledAmount(seller.getId()));
        List<PhysicalPurchaseSummary> myRecommendedSellers =  PhysicalPurchaseSummary.dao.findByParentSellerIdAndMonth(seller.getId(), month); //只有下级
        List<PhysicalPurchaseSummary> myRecommendedSellersWithLevel= Lists.newArrayList();//有下级，下下级
        myRecommendedSellersWithLevel.addAll(myRecommendedSellers);
        for(PhysicalPurchaseSummary myRecommendedSeller:myRecommendedSellers){
            myRecommendedSeller.put("level",LEVEL1_CROWN);
            List<PhysicalPurchaseSummary> myRecommendedSellersLv2= PhysicalPurchaseSummary.dao.findByParentSellerIdAndMonth(myRecommendedSeller.getSellerId(), month);
            if(myRecommendedSellersLv2.size()>0) {
                for(PhysicalPurchaseSummary plv2:myRecommendedSellersLv2){
                    plv2.put("level",LEVEL2_CROWN);
                }
                myRecommendedSellersWithLevel.addAll(myRecommendedSellersLv2);
            }
        }
        monthSummary.put("my_recommended_sellers",myRecommendedSellersWithLevel);
        renderSuccess(Lists.newArrayList(monthSummary));
    }
}
