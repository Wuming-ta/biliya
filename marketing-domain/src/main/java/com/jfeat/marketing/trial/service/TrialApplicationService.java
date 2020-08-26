package com.jfeat.marketing.trial.service;

import com.jfeat.core.BaseService;
import com.jfeat.marketing.trial.model.Trial;
import com.jfeat.marketing.trial.model.TrialApplication;
import com.jfeat.order.model.Order;
import com.jfeat.order.service.OrderService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.Date;

public class TrialApplicationService extends BaseService {

    private OrderService orderService = Enhancer.enhance(OrderService.class);

    /**
     * 生成申请表
     * @param trialId
     * @param userId
     * @param order
     * @return
     */
    public Ret apply(Integer trialId, Integer userId, Order order) {
        Trial trial = Trial.dao.findById(trialId);
        TrialApplication trialApplication = new TrialApplication();
        trialApplication.setTrialId(trialId);
        trialApplication.setUserId(userId);
        trialApplication.setCreatedTime(new Date());
        trialApplication.setOrderId(order.getId());
        trialApplication.setOrderNumber(order.getOrderNumber());
        trialApplication.setVersion(trial.getVersion());
        trialApplication.setStatus(TrialApplication.Status.APPLYING.toString());
        if (trialApplication.save()) {
            return success();
        }
        return failure();
    }

    @Before(Tx.class)
    public Ret agree(Integer id) {
        TrialApplication trialApplication = TrialApplication.dao.findById(id);
        if (!TrialApplication.Status.AUDITING.toString().equalsIgnoreCase(trialApplication.getStatus())) {
            return failure("invalid.status");
        }

        trialApplication.setStatus(TrialApplication.Status.DELIVERING.toString());

        if (trialApplication.update()) {
            Order order = Order.dao.findById(trialApplication.getOrderId());
            order.setStatus(Order.Status.CONFIRMED_DELIVER_PENDING.toString());
            orderService.updateOrder(order);
        }
        return success("审核成功");
    }

    public Ret reject(TrialApplication entity) {
        TrialApplication trialApplication = TrialApplication.dao.findById(entity.getId());
        if (!TrialApplication.Status.AUDITING.toString().equalsIgnoreCase(trialApplication.getStatus())) {
            return failure("invalid.status");
        }

        trialApplication.setStatus(TrialApplication.Status.REJECTED.toString());
        trialApplication.setNote(entity.getNote());
        if (trialApplication.update()) {
            Order order = Order.dao.findById(trialApplication.getOrderId());
            order.setStatus(Order.Status.CLOSED_CANCELED.toString());
            orderService.updateOrder(order);
        }
        return success("审核成功");
    }


}
