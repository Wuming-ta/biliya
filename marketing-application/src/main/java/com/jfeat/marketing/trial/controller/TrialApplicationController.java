package com.jfeat.marketing.trial.controller;

import com.jfeat.core.BaseController;
import com.jfeat.core.BaseService;
import com.jfeat.flash.Flash;
import com.jfeat.marketing.Marketing;
import com.jfeat.marketing.MarketingHolder;
import com.jfeat.marketing.trial.model.Trial;
import com.jfeat.marketing.trial.model.TrialApplication;
import com.jfeat.marketing.trial.service.TrialApplicationService;
import com.jfeat.order.model.Express;
import com.jfeat.order.model.Order;
import com.jfeat.order.model.OrderProcessLog;
import com.jfeat.order.service.OrderService;
import com.jfeat.pcd.model.Pcd;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

public class TrialApplicationController extends BaseController {

    private TrialApplicationService trialApplicationService = Enhancer.enhance(TrialApplicationService.class);

    @Override
    @RequiresPermissions(value = { "marketing.trial_application.view", "trial_application.menu" }, logical = Logical.OR)
    @Before(Flash.class)
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        Integer trialId = getParaToInt("trial_id");
        String trialName = getPara("trial_name");
        String status = getPara("status");
        String applicantName = getPara("applicant_name");
        String startTime = getPara("start_time");
        String endTime = getPara("end_time");
        Integer version = getParaToInt("version");
        endTime = StrKit.notBlank(endTime) ? endTime + " 23:59:59" : null;

        setAttr("expresses", Express.dao.findAllEnabled());
        setAttr("statuses", TrialApplication.Status.values());
        setAttr("trialApplications", TrialApplication.dao.paginate(pageNumber, pageSize, trialId, trialName, status, startTime, endTime, null, version));

        if (trialId != null) {
            setAttr("trial", Trial.dao.findById(trialId));
        }
        keepPara();
    }

    @RequiresPermissions(value = { "marketing.trial_application.view", "trial_application.menu" }, logical = Logical.OR)
    @Before(Flash.class)
    public void detail() {
        TrialApplication trialApplication = TrialApplication.dao.findById(getParaToInt());
        if (trialApplication == null) {
            renderError(404);
            return;
        }
        setAttr("trialApplication", trialApplication);
        Trial trial = trialApplication.getTrial();
        setAttr("trial", trial == null ? new Trial() : trial);

        Order order = trialApplication.getOrder();
        if (order == null) {
            renderError(404);
            return;
        }
        Marketing marketing = MarketingHolder.me().getMarketing(order.getMarketing(),
                order.getMarketingId(),
                order.getUserId(),
                order.getProvince(),
                order.getCity(),
                order.getDistrict());
        if (marketing != null) {
            order.put("marketing_admin_url", marketing.getAdminUrl());
        }
        setAttr("order", order);
        setAttr("expresses", Express.dao.findAllEnabled());
        setAttr("pcds", Pcd.dao.findByParentId(null));
        setAttr("orderProcessLogs", OrderProcessLog.dao.findByOrderId(order.getId()));
        setAttr("nameMap", MarketingHolder.me().getNameMap());
    }

    @Override
    @RequiresPermissions("marketing.trial_application.delete")
    public void delete() {
        new TrialApplication().deleteById(getParaToInt());
        String returnUrl = getPara("returnUrl", "/trial_application");
        redirect(urlDecode(returnUrl));
    }

    @RequiresPermissions("marketing.trial_application.edit")
    public void expressInfo() {
        TrialApplication trialApplication = TrialApplication.dao.findById(getParaToInt());
        if (trialApplication == null || !trialApplication.getStatus().equals(TrialApplication.Status.DELIVERING.toString())) {
            return;
        }
        Order order = trialApplication.getOrder();
        OrderService orderService = new OrderService();
        Integer expressId = getParaToInt("expressId");
        String expressNumber = getPara("expressNumber");
        Express express = Express.dao.findById(expressId);
        order.setExpressCompany(express.getName());
        order.setExpressCode(express.getCode());
        order.setExpressNumber(expressNumber);
        order.setStatus(Order.Status.DELIVERED_CONFIRM_PENDING.toString());
        orderService.updateOrder(order);
        trialApplication.setStatus(TrialApplication.Status.DELIVERED.toString());
        trialApplication.update();
        String returnUrl = getPara("returnUrl", "/trial_application");
        redirect(urlDecode(returnUrl));
    }

    @RequiresPermissions("marketing.trial_application.edit")
    public void agree() {
        Ret ret = trialApplicationService.agree(getParaToInt());
        setFlash("message", ret.get(BaseService.MESSAGE));
        String returnUrl = getPara("returnUrl", "/trial_application");
        redirect(urlDecode(returnUrl));
    }

    @RequiresPermissions("marketing.trial_application.edit")
    public void reject() {
        TrialApplication trialApplication = getModel(TrialApplication.class);
        trialApplication.setId(getParaToInt());
        Ret result = trialApplicationService.reject(trialApplication);
        String returnUrl = getPara("returnUrl", "/trial_application");
        redirect(urlDecode(returnUrl));
    }

    private String urlDecode(String url) {
        return StringEscapeUtils.unescapeHtml4(url);
    }

}
