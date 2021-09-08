package com.jfeat.service;

import com.jfeat.core.Service;
import com.jfeat.core.ServiceContext;
import com.jfeat.service.impl.AServiceImpl;
import com.jfeat.service.impl.BServiceImpl;
import com.jfeat.service.impl.ServiceImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

/**
 * Created by jackyhuang on 16/10/13.
 */
public class ServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testValidRegister() {
        ServiceContext.me().register(new AServiceImpl());
        assertEquals(true, ServiceContext.me().getService("com.jfeat.service.MyService") instanceof AServiceImpl);
    }

    @Test
    public void testDuplicationRegister() {
        ServiceContext.me().register(new AServiceImpl());
        ServiceContext.me().register(new BServiceImpl());
        Service service = ServiceContext.me().getService("com.jfeat.service.MyService");
        assertEquals(true, service instanceof BServiceImpl);

    }

    @Test
    public void testErrorRegister() {
        thrown.expect(RuntimeException.class);
        ServiceContext.me().register(new ServiceImpl());

    }
}
