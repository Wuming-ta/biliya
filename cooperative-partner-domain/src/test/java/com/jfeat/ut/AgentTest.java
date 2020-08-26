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
import com.jfeat.identity.model.User;
import com.jfeat.partner.model.Agent;
import com.jfeat.partner.model.AgentPcdQualify;
import com.jfeat.partner.model.PhysicalSeller;
import com.jfeat.partner.model.Seller;
import com.jfeat.partner.service.AgentService;
import com.jfinal.aop.Enhancer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackyhuang on 2018/1/19.
 */
public class AgentTest extends AbstractTestCase {

    private AgentService agentService = Enhancer.enhance(AgentService.class);

    List<User> users = new ArrayList<>();

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

    @Before
    public void setup() throws ParseException {
        users.add(createUser(0, null));
    }

    @After
    public void clean() {
        for (User user : users) {
            user.delete();
        }
    }

    @Test
    public void testUpdateAgentArea() {
        Agent agent = new Agent();
        agent.setUserId(users.get(0).getId());
        agent.save();


        Integer[] pcdQualities = new Integer[] { 1, 2, 3 };
        Integer[] percentages = null;
        agentService.updateAgentArea(agent, pcdQualities, percentages);
        AgentPcdQualify.dao.findByAgentId(agent.getId()).forEach(System.out::println);


        pcdQualities = new Integer[] { 1, 2, 4, 5 };
        agentService.updateAgentArea(agent, pcdQualities, percentages);
        AgentPcdQualify.dao.findByAgentId(agent.getId()).forEach(System.out::println);
    }
}
