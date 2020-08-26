package com.jfeat.marketing.wholesale.api.validator;

import com.jfeat.core.RestController;
import com.jfeat.core.Service;
import com.jfeat.core.ServiceContext;
import com.jfeat.identity.authc.ShiroUser;
import com.jfeat.service.WholesaleAccessAuthorityService;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * Created by kang on 2017/6/6.
 */
public class PhysicalCrownAuthorityValidator extends Validator {

    @Override
    protected void validate(Controller c) {
        setShortCircuit(true);
        if (!(c instanceof RestController)) {
            return;
        }
        RestController r = (RestController) c;

        Subject subject = SecurityUtils.getSubject();
        ShiroUser user = (ShiroUser) subject.getPrincipal();
        Service service = ServiceContext.me().getService(WholesaleAccessAuthorityService.class.getName());
        if (service != null) {
            WholesaleAccessAuthorityService wholesaleAccessAuthorityService = (WholesaleAccessAuthorityService) service;
            if (!wholesaleAccessAuthorityService.authorized(user.id)) {
                addError("error","you.are.not.a.physical.crown");
            }
        }
    }

    @Override
    protected void handleError(Controller c) {
        if (c instanceof RestController) {
            RestController r = (RestController) c;
            r.renderError(403);
        }
    }
}
