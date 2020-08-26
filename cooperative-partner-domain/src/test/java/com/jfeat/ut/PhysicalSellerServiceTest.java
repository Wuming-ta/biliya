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

package com.jfeat.ut;

import com.google.common.collect.Lists;
import com.jfeat.AbstractTestCase;
import com.jfeat.identity.model.User;
import com.jfeat.kit.DateKit;
import com.jfeat.partner.model.PhysicalPurchaseJournal;
import com.jfeat.partner.model.PhysicalPurchaseSummary;
import com.jfeat.partner.model.PhysicalSeller;
import com.jfeat.partner.model.Seller;
import com.jfeat.partner.service.PhysicalSellerService;
import com.jfinal.aop.Enhancer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackyhuang on 17/1/18.
 */
public class PhysicalSellerServiceTest extends AbstractTestCase {

    private PhysicalSellerService physicalSellerService = Enhancer.enhance(PhysicalSellerService.class);

    List<User> users = new ArrayList<>();
    List<Integer> sellerIds = Lists.newArrayList();

    private User createUser(int i, Integer parentId) {
        User user = new User();
        user.setLoginName("testuser" + i);
        user.setPassword("testuser");
        user.setInvitationCode("testuser");
        user.setInviterId(parentId);
        user.save();
        logger.debug("user: {}", user);
        return user;
    }

    private void dumpData() {
        logger.debug("==========Journal===========");
        for (PhysicalPurchaseJournal journal : PhysicalPurchaseJournal.dao.findAll()) {
            logger.debug(journal.toJson());
        }

        logger.debug("==========Summary===========");
        for (PhysicalPurchaseSummary summary : PhysicalPurchaseSummary.dao.findAll()) {
            logger.debug(summary.toJson());
        }

        logger.debug("==========PhysicalSeller===========");
        for (PhysicalSeller physicalSeller : PhysicalSeller.dao.findAll()) {
            logger.debug(physicalSeller.toJson());
        }

    }

    @Before
    public void setup() throws ParseException {
        for (int i = 0; i < 5; i++) {
            users.add(createUser(i, null));
        }

        for (User user : users) {
            Seller seller = Seller.dao.findByUserId(user.getId());
            seller.setPartnerShip(Seller.PartnerShip.YES.getValue());
            seller.setCrownShip(Seller.CrownShip.YES.getValue());
            seller.update();
            sellerIds.add(seller.getId());
        }

        Integer parentSellerId = null;
        for (int i = 0; i < sellerIds.size(); i++) {
            physicalSellerService.createPhysicalSeller(sellerIds.get(i), parentSellerId, null, null, null);
            parentSellerId = sellerIds.get(0);
        }

        for (PhysicalSeller crownSeller : PhysicalSeller.dao.findCrownSeller()) {
            crownSeller.setTotalAmount(BigDecimal.valueOf(1000));
            crownSeller.setTotalSettledAmount(BigDecimal.valueOf(100));
            crownSeller.update();
        }

    }

    @After
    public void clean() {
        for (User user : users) {
            user.delete();
        }
    }

//    @Test
//    public void testCalcSettlement() throws ParseException {
//        dumpData();
//        String month = DateKit.currentMonth("yyyy-MM-01");
//        for (PhysicalSeller crownSeller : PhysicalSeller.dao.findCrownSeller()) {
//            physicalSellerService.updatePurchase(crownSeller, new BigDecimal(3000.0), month, null);
//        }
//
//
//
//    }
}
