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

package com.jfeat.partner.observer;

import com.jfeat.core.BaseService;
import com.jfeat.identity.model.User;
import com.jfeat.observer.Observer;
import com.jfeat.observer.Subject;
import com.jfeat.partner.model.MerchantOptions;
import com.jfeat.partner.model.Seller;
import com.jfeat.partner.service.SellerService;
import com.jfinal.kit.Ret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 当新建User时，创建一个Seller， 用以维护层级关系。
 * Created by jacky on 3/22/16.
 */
public class SellerObserver implements Observer {

    private static Logger logger = LoggerFactory.getLogger(SellerObserver.class);

    @Override
    public void invoke(Subject subject, int event, Object param) {
        if (subject instanceof User && event == User.EVENT_SAVE) {
            try {
                logger.debug("user created. going to create seller.");
                SellerService service = new SellerService();
                User user = (User) subject;
                Seller parentSeller = Seller.dao.findByUserId(user.getInviterId());
                Seller seller = new Seller();
                seller.setUserId(user.getId());
                Ret ret = service.createSeller(seller, parentSeller);

                logger.debug("seller create result: {}", ret.getData());
                logger.debug("seller is : {}", seller);

                if (BaseService.isSucceed(ret)) {
                    ret = service.promotePartnerLevel(seller.getPartner());
                    logger.debug("partner promote result: {}", ret.getData());
                    service.promoteParentSellerToPartner(seller);
                }
            }
            catch(Exception ex) {
                ex.printStackTrace();
                logger.error(ex.getMessage());
                for (StackTraceElement element : ex.getStackTrace()) {
                    logger.error("    {}:{} - {}:{}",
                            element.getFileName(), element.getLineNumber(), element.getClassName(), element.getMethodName());
                }
            }
        }
    }
}
