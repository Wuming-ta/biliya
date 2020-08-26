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

package com.jfeat.settlement.api;

import com.jfeat.core.RestController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.kit.DateKit;
import com.jfeat.partner.model.Seller;
import com.jfeat.settlement.model.OrderItemReward;
import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.shiro.ShiroMethod;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jingfei on 2016/3/31.
 */
@ControllerBind(controllerKey = "/rest/order_item_reward")
public class OrderItemRewardController extends RestController {

    /**
     *
     * GET /rest/order_item_reward?start_date=2016-05-01&end_date=2016-05-21
     */
    @Override
    @Before(CurrentUserInterceptor.class)
    public void index() {
        String phone = getPara("phone");
        User currentUser = getAttr("currentUser");
        Integer userId = currentUser.getId();
        if (StrKit.notBlank(phone)) {
            User targetUser = User.dao.findByPhone(phone);
            if (targetUser == null) {
                renderFailure("user.not.found");
                return;
            }
            if (ShiroMethod.lacksPermission("SettlementApplication.view")) {
                renderFailure("lack.of.permission");
                return;
            }
            userId = targetUser.getId();
        }

        String startDate = getPara("start_date", DateKit.currentMonth("yyyy-MM-01")) + " 00:00:00";
        String endDate = getPara("end_date", DateKit.today("yyyy-MM-dd")) + " 23:59:59";
        Map<String, Object> result = new HashMap<>();
        List<OrderItemReward> orderItemRewardPage = OrderItemReward.dao.find(userId,startDate,endDate,null,null, null);
        BigDecimal settledReward = OrderItemReward.dao.queryRewardByTime(userId, startDate, endDate, OrderItemReward.State.SETTLED);
        BigDecimal pendingSettlementReward = OrderItemReward.dao.queryRewardByTime(userId, startDate, endDate, OrderItemReward.State.PENDING_SETTLEMENT);
        Long settledOrderCount = OrderItemReward.dao.queryOrdersByTime(userId, startDate, endDate, OrderItemReward.State.SETTLED);
        Long pendingOrderCount = OrderItemReward.dao.queryOrdersByTime(userId, startDate, endDate, OrderItemReward.State.PENDING_SETTLEMENT);
        Long totalOrderCount = settledOrderCount + pendingOrderCount;
        result.put("order_item_rewards", orderItemRewardPage);
        result.put("settled_reward",settledReward);
        result.put("pending_reward",pendingSettlementReward);
        result.put("total_order_count",totalOrderCount);
        result.put("settled_order_count", settledOrderCount);
        result.put("pending_order_count", pendingOrderCount);

        boolean isSeller = false;
        boolean isPartner = false;
        boolean isAgent = false;
        Seller currentSeller = Seller.dao.findByUserId(userId);
        if (currentSeller != null){
            isSeller = currentSeller.getSellerShip()== Seller.SellerShip.YES.getValue();
            isPartner = currentSeller.getPartnerShip()==Seller.PartnerShip.YES.getValue();
            isAgent = currentSeller.isAgent();
        }

        result.put("is_seller",isSeller);
        result.put("is_partner",isPartner);
        result.put("is_agent", isAgent);
        renderSuccess(result);
    }

}
