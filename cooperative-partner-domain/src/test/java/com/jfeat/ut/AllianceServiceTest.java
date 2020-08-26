package com.jfeat.ut;

import com.jfeat.AbstractTestCase;
import com.jfeat.core.BaseService;
import com.jfeat.identity.model.User;
import com.jfeat.kit.DateKit;
import com.jfeat.partner.model.Alliance;
import com.jfeat.partner.service.AllianceService;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.Ret;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author jackyhuang
 * @date 2019/10/10
 */
public class AllianceServiceTest extends AbstractTestCase {

    private AllianceService allianceService = Enhancer.enhance(AllianceService.class);

    private User user;
    private User invitor;

    private User createUser(int i) {
        User user = new User();
        user.setLoginName("testuser" + i);
        user.setPassword("testuser");
        user.save();
        logger.debug("user: {}", user);
        return user;
    }

    @Before
    public void before() {
        user = createUser(1);
        invitor = createUser(2);
    }

    @After
    public void after() {
        user.delete();
        invitor.delete();
    }

    @Test
    public void testAssignTempAllianceShipFailedWithInvitorIsNull() {
        Ret ret = allianceService.assignTempAllianceShip(user.getId(), invitor.getId());
        assertFalse(BaseService.isSucceed(ret));
        assertEquals("invitor.is.null", BaseService.getMessage(ret));
    }

    @Test
    public void testAssignTempAllianceShipFailedWithInvitorIsNotAlliance() {
        Alliance alliance = new Alliance();
        alliance.setUserId(invitor.getId());
        alliance.save();
        Ret ret = allianceService.assignTempAllianceShip(user.getId(), invitor.getId());
        assertFalse(BaseService.isSucceed(ret));
        assertEquals("invitor.is.not.alliance", BaseService.getMessage(ret));
    }

    @Test
    public void testAssignTempAllianceShipFailedWithInvitorIsTempAlliance() {
        Alliance alliance = new Alliance();
        alliance.setUserId(invitor.getId());
        alliance.setAllianceShip(Alliance.AllianceShip.TEMP.getValue());
        alliance.setTempAllianceExpiryTime(DateKit.daysLater(1));
        alliance.save();
        Ret ret = allianceService.assignTempAllianceShip(user.getId(), invitor.getId());
        assertFalse(BaseService.isSucceed(ret));
        assertEquals("invitor.is.temp.alliance", BaseService.getMessage(ret));
    }

    @Ignore
    @Test
    public void testAssignTempAllianceShipSuccess() {
        Alliance invitorAlliance = new Alliance();
        invitorAlliance.setUserId(invitor.getId());
        invitorAlliance.setAllianceShip(Alliance.AllianceShip.REGULAR.getValue());
        invitorAlliance.save();
        Ret ret = allianceService.assignTempAllianceShip(user.getId(), invitor.getId());
        assertTrue(BaseService.isSucceed(ret));

        Alliance alliance = Alliance.dao.findByUserId(user.getId());
        assertNotNull(alliance);
        assertSame(Alliance.AllianceShip.TEMP.getValue(), alliance.getAllianceShip());
        assertEquals(invitorAlliance.getId(), alliance.getInvitorAllianceId());
    }

    @Test
    public void testHandleAllianceShipExpired() {
        Alliance alliance = new Alliance();
        alliance.setUserId(user.getId());
        alliance.setAllianceShip(Alliance.AllianceShip.TEMP.getValue());
        alliance.setTempAllianceExpiryTime(DateKit.daysLater(1));
        alliance.save();
        Ret ret = allianceService.handleAllianceShipExpired(alliance.getId());
        assertTrue(BaseService.isSucceed(ret));

        alliance = Alliance.dao.findByUserId(user.getId());
        assertNotNull(alliance);
        assertSame(Alliance.AllianceShip.NO.getValue(), alliance.getAllianceShip());
    }
}
