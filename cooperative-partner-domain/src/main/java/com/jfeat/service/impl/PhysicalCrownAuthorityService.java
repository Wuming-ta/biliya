package com.jfeat.service.impl;

import com.jfeat.partner.model.Seller;
import com.jfeat.service.WholesaleAccessAuthorityService;

/**
 * Created by kang on 2017/6/5.
 */
public class PhysicalCrownAuthorityService implements WholesaleAccessAuthorityService {

    public boolean authorized(int userId) {
        Seller seller = Seller.dao.findByUserId(userId);
        return seller != null && seller.isCrownShip() && seller.isPartnerShip();
    }

}
