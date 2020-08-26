package com.jfeat.marketing.wholesale.api.validator;

import com.jfeat.core.RestController;
import com.jfeat.marketing.wholesale.service.WholesaleConfigService;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

/**
 * Created by kang on 2017/6/5.
 */
public class WholesaleConfigValidator extends Validator {
    @Override
    protected void validate(Controller c) {
        setShortCircuit(true);
        if (!(c instanceof RestController)) {
            return;
        }
        if (!WholesaleConfigService.isEnabled()) {
            addError("error", "wholesale.is.not.enabled");
        }
    }

    @Override
    protected void handleError(Controller c) {
        if (c instanceof RestController) {
            RestController r = (RestController) c;
            c.renderError(404);
        }
    }
}
