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

package com.jfeat.marketing.piece.sys.api;

import com.jfeat.core.RestController;
import com.jfeat.marketing.piece.model.PieceGroupPurchaseMaster;
import com.jfinal.ext.route.ControllerBind;

/**
 * Created by jackyhuang on 2017/5/27.
 */
@ControllerBind(controllerKey = "/sys/rest/piece_group_purchase")
public class PieceGroupPurchaseController extends RestController {

    public void index() {
        Integer masterId = getParaToInt("masterId");
        PieceGroupPurchaseMaster pieceGroupPurchaseMaster = PieceGroupPurchaseMaster.dao.findById(masterId);
        if (pieceGroupPurchaseMaster != null) {
            renderSuccess(pieceGroupPurchaseMaster.getPieceGroupPurchase());
            return;
        }
        renderFailure("piece.group.not.found");
    }
}
