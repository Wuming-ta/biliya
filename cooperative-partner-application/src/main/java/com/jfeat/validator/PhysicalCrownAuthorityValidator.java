package com.jfeat.validator;

import com.jfeat.core.RestController;
import com.jfeat.core.Service;
import com.jfeat.core.ServiceContext;
import com.jfeat.identity.authc.ShiroUser;
import com.jfeat.partner.model.PhysicalSeller;
import com.jfeat.partner.model.Seller;
import com.jfeat.partner.service.PhysicalSellerService;
import com.jfeat.service.WholesaleAccessAuthorityService;
import com.jfinal.aop.Enhancer;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * Created by kang on 2017/6/20.
 */
public class PhysicalCrownAuthorityValidator extends Validator {
    private PhysicalSellerService physicalSellerService = Enhancer.enhance(PhysicalSellerService.class);

    //校验“当前用户”以及“传上来的seller_id对应的用户是否是一个线下皇冠”
    //校验“当前用户”推荐了“传上来的seller_id对应的用户”
    @Override
    protected void validate(Controller c) {
        setShortCircuit(true);
        if (!(c instanceof RestController)) {
            return;
        }
        RestController r = (RestController) c;
        Seller recommendedSeller = Seller.dao.findById(r.getParaToInt());
        if (recommendedSeller == null) {
            addError("error", "seller.not.found");
        }
        Subject subject = SecurityUtils.getSubject();
        ShiroUser user = (ShiroUser) subject.getPrincipal();
        Service service = ServiceContext.me().getService(WholesaleAccessAuthorityService.class.getName());
        if (service != null) {
            WholesaleAccessAuthorityService wholesaleAccessAuthorityService = (WholesaleAccessAuthorityService) service;
            if (!wholesaleAccessAuthorityService.authorized(user.id)) {
                addError("error", "you.are.not.a.physical.crown");
            }
            if (!wholesaleAccessAuthorityService.authorized(recommendedSeller.getUserId())) {
                addError("error", "the.seller.is.not.a.physical.crown");
            }
        }
        Seller currentSeller = Seller.dao.findById(user.id);
        PhysicalSeller.CrownParentLevel level = physicalSellerService.getCrownParentLevel(currentSeller.getId(), recommendedSeller.getId());
        if (level == PhysicalSeller.CrownParentLevel.NONE) {
            addError("error", "the.seller.is.not.your.recommended.seller");
        }
        r.setAttr("level", level);
        r.setAttr("recommendedSeller", recommendedSeller);
    }

    @Override
    protected void handleError(Controller c) {
        if (c instanceof RestController) {
            RestController r = (RestController) c;
            r.renderError(403);
        }
    }
}
