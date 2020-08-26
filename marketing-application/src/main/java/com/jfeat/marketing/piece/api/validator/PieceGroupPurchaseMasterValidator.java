package com.jfeat.marketing.piece.api.validator;

import com.jfeat.core.RestController;
import com.jfeat.marketing.piece.model.PieceGroupPurchaseMaster;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;


/**
 * Created by kang on 2017/5/27.
 */
public class PieceGroupPurchaseMasterValidator extends Validator {

    @Override
    protected void validate(Controller c) {
        setShortCircuit(true);
        if (!(c instanceof RestController)) {
            return;
        }
        RestController restController = (RestController) c;
        String method = getActionMethod().getName();
        if ("show".equals(method)) {
            PieceGroupPurchaseMaster pieceGroupPurchaseMaster = PieceGroupPurchaseMaster.dao.findById(restController.getParaToInt());
            if (pieceGroupPurchaseMaster == null) {
                addError("error", "pieceGroupPurchaseMaster not found.");
            }
            c.setAttr("pieceGroupPurchaseMaster", pieceGroupPurchaseMaster);
        }
    }

    @Override
    protected void handleError(Controller c) {
        if (c instanceof RestController) {
            RestController restController = (RestController) c;
            restController.renderFailure(c.getAttr("error"));
        }
    }
}