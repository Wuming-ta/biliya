package com.jfeat.marketing.piece.service;

import com.jfeat.marketing.common.model.MarketingConfig;

/**
 * Created by kang on 2017/6/5.
 */
public class PieceGroupPurchaseConfigService {

    public static boolean isEnabled() {
        MarketingConfig marketingConfig = MarketingConfig.dao.findFirstByField(MarketingConfig.Fields.TYPE.toString()
                , MarketingConfig.Type.PIECEGROUP.toString());
        return marketingConfig != null && marketingConfig.getEnabled() != null
                && marketingConfig.getEnabled().equals(PieceGroupPurchaseService.ENABLED);
    }
}
