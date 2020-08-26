package com.jfeat.marketing.piece.api.validator;

import com.jfeat.core.RestController;
import com.jfeat.marketing.piece.service.PieceGroupPurchaseConfigService;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

/**
 * Created by kang on 2017/6/5.
 */
public class PieceGroupPurchaseConfigValidator extends Validator {
    @Override
    protected void validate(Controller c) {
        setShortCircuit(true);
        if (!(c instanceof RestController)) {
            return;
        }
        if (!PieceGroupPurchaseConfigService.isEnabled()) {
            addError("error", "piece_group_purchase.is.not.enabled");
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
