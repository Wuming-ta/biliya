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

import com.jfeat.AbstractTestCase;
import com.jfeat.config.model.Config;
import com.jfeat.core.BaseService;
import com.jfeat.identity.model.User;
import com.jfeat.identity.subject.AttemptingUpdateInviterSubject;
import com.jfeat.partner.model.Seller;
import com.jfeat.partner.service.SellerService;
import com.jfinal.kit.Ret;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by Administrator on 2016/3/11.
 */
public class SellerTest extends AbstractTestCase {

//    List<User> users = new ArrayList<>();
//
//    private User createUser(int i, Integer parentId) {
//        User user = new User();
//        user.setLoginName("testuser" + i);
//        user.setName("testuser" + i);
//        user.setPassword("testuser");
//        user.setInvitationCode("testuser");
//        user.setInviterId(parentId);
//        user.save();
//        logger.debug("user: {}", user);
//        return user;
//    }
//
//    /**
//     * 1
//     * |
//     * +------
//     * |     |
//     * 2     8
//     * |
//     * ------+------
//     * |     |     |
//     * 6     3     9
//     * |     |
//     * +     +
//     * |     |
//     * 7     4
//     * |
//     * +------
//     * |     |
//     * 5     11
//     * |
//     * +
//     * |
//     * 10
//     */
//    @Before
//    public void before() throws InterruptedException {
//        Config config = Config.dao.findByKey("mall.seller_auto_sellership");
//        config.setValue("true");
//        config.update();
//
//        users.add(createUser(1, null));
//        TimeUnit.SECONDS.sleep(1);
//        users.add(createUser(2, users.get(0).getId()));
//        TimeUnit.SECONDS.sleep(1);
//        users.add(createUser(3, users.get(1).getId()));
//        TimeUnit.SECONDS.sleep(1);
//        users.add(createUser(4, users.get(2).getId()));
//        TimeUnit.SECONDS.sleep(1);
//        users.add(createUser(5, users.get(3).getId()));
//        TimeUnit.SECONDS.sleep(1);
//        users.add(createUser(6, users.get(1).getId()));
//        TimeUnit.SECONDS.sleep(1);
//        users.add(createUser(7, users.get(5).getId()));
//        TimeUnit.SECONDS.sleep(1);
//        users.add(createUser(8, users.get(0).getId()));
//        TimeUnit.SECONDS.sleep(1);
//        users.add(createUser(9, users.get(1).getId()));
//        TimeUnit.SECONDS.sleep(1);
//        users.add(createUser(10, users.get(4).getId()));
//        TimeUnit.SECONDS.sleep(1);
//        users.add(createUser(11, users.get(3).getId()));
//        TimeUnit.SECONDS.sleep(1);
//
//        for (Seller seller : Seller.dao.findAll()) {
//            logger.debug("seller: {}", seller);
//        }
//    }
//
//    @After
//    public void after() {
//        for (Seller seller : Seller.dao.findAll()) {
//            logger.debug("#####seller: {}", seller);
//        }
//        for (User user : users) {
//            user.delete();
//        }
//    }
//
//    @Test
//    public void testGetChildren() {
//        Seller seller = Seller.dao.findByUserId(users.get(0).getId());
//        assertNotNull(seller);
//        List<Seller> children = seller.getChildren();
//        assertNotNull(children);
//        assertEquals(2, children.size());
//    }
//
//
//    @Test
//    public void testAssignAndResetPartnerShip() throws InterruptedException {
//        //设 1 为合伙人
//        SellerService service = new SellerService();
//        Seller seller = Seller.dao.findByUserId(users.get(0).getId());
//        service.assignPartnerRight(seller.getId());
//        logger.debug("after assign1: " + Seller.dao.findAll());
//        assertNull(Seller.dao.findByUserId(users.get(0).getId()).getPartnerId());
//        assertEquals(0, Seller.dao.findByPartnerId(seller.getId()).size());
//
//        //在 1 下面添加新的分销商
//        Integer parentId = users.get(0).getId();
//        User newUser = createUser(users.get(users.size() - 1).getId() + 1, parentId);
//        users.add(newUser);
//        TimeUnit.SECONDS.sleep(1);
//        Seller newSeller = Seller.dao.findByUserId(newUser.getId());
//        assertEquals(parentId, newSeller.getPartnerId());
//        assertEquals(1, Seller.dao.findByPartnerId(seller.getId()).size());
//
//        //设 2 为合伙人
//        seller = Seller.dao.findByUserId(users.get(1).getId());
//        service.assignPartnerRight(seller.getId());
//        logger.debug("after assign2: " + Seller.dao.findAll());
//        assertEquals(0, Seller.dao.findByPartnerId(seller.getId()).size());
//
//        //在 2 下面添加新的分销商
//        parentId = users.get(1).getId();
//        newUser = createUser(users.get(users.size() - 1).getId() + 1, parentId);
//        users.add(newUser);
//        TimeUnit.SECONDS.sleep(1);
//        newSeller = Seller.dao.findByUserId(newUser.getId());
//        assertEquals(parentId, newSeller.getPartnerId());
//        assertEquals(1, Seller.dao.findByPartnerId(parentId).size());
//
//    }
//
//    @Test
//    public void testQueryLevelCount() {
//        SellerService service = new SellerService();
//        Seller seller = Seller.dao.findByUserId(users.get(0).getId());
//        Ret ret = service.queryLevelCount(seller.getId());
//        assertEquals(true, ret.get(BaseService.RESULT));
//        assertEquals(3, ret.get("max_level"));
//        List<Integer> levels = ret.get("levels");
//        for (Integer level : levels) {
//            logger.debug("level count = {}", level);
//        }
//    }
//
//    @Test
//    public void testInvitorUpdate() throws InterruptedException {
//        Config config = Config.dao.findByKey("mall.seller_auto_sellership");
//        config.setValue("false");
//        config.update();
//
//        User user1 = createUser(users.get(users.size() - 1).getId() + 1, null);
//        users.add(user1);
//        TimeUnit.SECONDS.sleep(1);
//        User user2 = createUser(users.get(users.size() - 1).getId() + 1, user1.getId());
//        users.add(user2);
//        TimeUnit.SECONDS.sleep(1);
//        User user3 = createUser(users.get(users.size() - 1).getId() + 1, user2.getId());
//        users.add(user3);
//        TimeUnit.SECONDS.sleep(1);
//
//        assertEquals(user2.getId(), user3.getInviterId());
//
//        new AttemptingUpdateInviterSubject(user3.getId(), user1.getId()).notifyObserver();
//
//        TimeUnit.SECONDS.sleep(4);
//        User updatedUser3 = User.dao.findById(user3.getId());
//        assertEquals(user1.getId(), updatedUser3.getInviterId());
//    }
//
//    @Test
//    public void testInvitorUpdateWithSellerShip() throws InterruptedException {
//        Config config = Config.dao.findByKey("mall.seller_auto_sellership");
//        config.setValue("true");
//        config.update();
//
//        //prepare
//        User user1 = createUser(users.get(users.size() - 1).getId() + 1, null);
//        users.add(user1);
//        TimeUnit.SECONDS.sleep(1);
//        Seller seller1 = Seller.dao.findByUserId(user1.getId());
//        seller1.setCrownShip(Seller.CrownShip.YES.getValue());
//        seller1.setPartnerShip(Seller.PartnerShip.YES.getValue());
//        seller1.update();
//
//        User user2 = createUser(users.get(users.size() - 1).getId() + 1, user1.getId());
//        users.add(user2);
//        TimeUnit.SECONDS.sleep(1);
//
//        User user3 = createUser(users.get(users.size() - 1).getId() + 1, user2.getId());
//        users.add(user3);
//        TimeUnit.SECONDS.sleep(1);
//        assertEquals(user2.getId(), user3.getInviterId());
//
//        User user4 = createUser(users.get(users.size() - 1).getId() + 1, null);
//        users.add(user4);
//        TimeUnit.SECONDS.sleep(1);
//        assertNull(user4.getInviterId());
//
//        User user5 = createUser(users.get(users.size() - 1).getId() + 1, user4.getId());
//        users.add(user5);
//        TimeUnit.SECONDS.sleep(1);
//        assertEquals(user4.getId(), user5.getInviterId());
//
//        User user6 = createUser(users.get(users.size() - 1).getId() + 1, null);
//        users.add(user6);
//        TimeUnit.SECONDS.sleep(1);
//        assertNull(user6.getInviterId());
//
//        //process
//        new AttemptingUpdateInviterSubject(user3.getId(), user1.getId()).notifyObserver();
//        new AttemptingUpdateInviterSubject(user4.getId(), user2.getId()).notifyObserver();
//        new AttemptingUpdateInviterSubject(user6.getId(), user2.getId()).notifyObserver();
//
//        //validate
//        TimeUnit.SECONDS.sleep(4);
//        User updatedUser3 = User.dao.findById(user3.getId());
//        assertEquals(user2.getId(), updatedUser3.getInviterId());
//
//        User updatedUser4 = User.dao.findById(user4.getId());
//        assertEquals(user2.getId(), updatedUser4.getInviterId());
//
//        Seller seller2 = Seller.dao.findByUserId(user2.getId());
//        assertEquals(3, seller2.getChildrenCount());
//
//        seller1 = Seller.dao.findByUserId(user1.getId());
//        assertEquals(4, seller1.getTwoLevelsChildrenCount());
//
//        Seller seller6 = Seller.dao.findByUserId(user6.getId());
//        assertEquals(seller2.getId(), seller6.getParentId());
//        assertEquals(seller1.getId(), seller6.getPartnerId());
//        assertEquals(seller1.getId(), seller6.getCrownId());
//
//        //prepare data
//        User user7 = createUser(users.get(users.size() - 1).getId() + 1, null);
//        users.add(user7);
//        TimeUnit.SECONDS.sleep(1);
//
//        User user8 = createUser(users.get(users.size() - 1).getId() + 1, user7.getId());
//        users.add(user8);
//        TimeUnit.SECONDS.sleep(1);
//
//        new AttemptingUpdateInviterSubject(user1.getId(), user7.getId()).notifyObserver();
//        TimeUnit.SECONDS.sleep(4);
//        User updatedUser1 = User.dao.findById(user1.getId());
//        assertEquals(user7.getId(), updatedUser1.getInviterId());
//    }
//
//    @Test
//    public void testInvitorCannotUpdateWithSellerShip() throws InterruptedException {
//        Config config = Config.dao.findByKey("mall.seller_auto_sellership");
//        config.setValue("true");
//        config.update();
//
//        //prepare
//        User user1 = createUser(users.get(users.size() - 1).getId() + 1, null);
//        users.add(user1);
//        TimeUnit.SECONDS.sleep(1);
//        Seller seller1 = Seller.dao.findByUserId(user1.getId());
//        seller1.setCrownShip(Seller.CrownShip.YES.getValue());
//        seller1.setPartnerShip(Seller.PartnerShip.YES.getValue());
//        seller1.update();
//
//        User user2 = createUser(users.get(users.size() - 1).getId() + 1, user1.getId());
//        users.add(user2);
//        TimeUnit.SECONDS.sleep(1);
//
//        User user3 = createUser(users.get(users.size() - 1).getId() + 1, null);
//        users.add(user3);
//        TimeUnit.SECONDS.sleep(1);
//
//        //process
//        new AttemptingUpdateInviterSubject(user2.getId(), user3.getId()).notifyObserver();
//        new AttemptingUpdateInviterSubject(user3.getId(), user3.getId()).notifyObserver();
//
//        //validate
//        TimeUnit.SECONDS.sleep(4);
//        User updatedUser2 = User.dao.findById(user2.getId());
//        assertEquals(user1.getId(), updatedUser2.getInviterId());
//
//        User updatedUser3 = User.dao.findById(user3.getId());
//        assertNull(updatedUser3.getInviterId());
//    }
//
//    @Test
//    public void testCannotSetInvitorToChild() throws InterruptedException {
//            Config config = Config.dao.findByKey("mall.seller_auto_sellership");
//            config.setValue("true");
//            config.update();
//
//            //prepare
//            User user1 = createUser(users.get(users.size() - 1).getId() + 1, null);
//            users.add(user1);
//            TimeUnit.SECONDS.sleep(1);
//
//            User user2 = createUser(users.get(users.size() - 1).getId() + 1, user1.getId());
//            users.add(user2);
//            TimeUnit.SECONDS.sleep(1);
//            assertEquals(user1.getId(), user2.getInviterId());
//
//            User user3 = createUser(users.get(users.size() - 1).getId() + 1, user2.getId());
//            users.add(user3);
//            TimeUnit.SECONDS.sleep(1);
//            assertEquals(user2.getId(), user3.getInviterId());
//
//            User user4 = createUser(users.get(users.size() - 1).getId() + 1, user3.getId());
//            users.add(user4);
//            TimeUnit.SECONDS.sleep(1);
//            assertEquals(user3.getId(), user4.getInviterId());
//
//            User user5 = createUser(users.get(users.size() - 1).getId() + 1, user4.getId());
//            users.add(user5);
//            TimeUnit.SECONDS.sleep(1);
//            assertEquals(user4.getId(), user5.getInviterId());
//
//            User user6 = createUser(users.get(users.size() - 1).getId() + 1, user5.getId());
//            users.add(user6);
//            TimeUnit.SECONDS.sleep(1);
//            assertEquals(user5.getId(), user6.getInviterId());
//
//            //process
//            new AttemptingUpdateInviterSubject(user1.getId(), user6.getId()).notifyObserver();
//
//            //validate
//            TimeUnit.SECONDS.sleep(4);
//            User updatedUser1 = User.dao.findById(user1.getId());
//            assertNull(updatedUser1.getInviterId());
//    }
}
