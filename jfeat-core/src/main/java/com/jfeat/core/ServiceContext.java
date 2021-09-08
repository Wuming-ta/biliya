package com.jfeat.core;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by jackyhuang on 16/10/13.
 */
public class ServiceContext {
    private static Logger logger = LoggerFactory.getLogger(ServiceContext.class);
    private static ServiceContext me = new ServiceContext();

    private Map<String, Service> map = Maps.newHashMap();

    private ServiceContext() {

    }

    public static ServiceContext me() {
        return me;
    }

    public void register(String name, Service service) {
        register(service);
        if (map.get(name) != null) {
            logger.warn("service {} already exists, replace it.", name);
        }
        map.put(name, service);
    }

    public void register(Service service) {
        Class[] ifs = service.getClass().getInterfaces();
        if (ifs.length > 1) {
            throw new RuntimeException("Service can not implement more than one interface.");
        }
        if (ifs[0].getName().equals(Service.class.getName())) {
            throw new RuntimeException("Can not implement Servie directly. Please make an interface extends it, and your class implement the interface.");
        }
        String serviceName = ifs[0].getName();
        logger.info("registering service {}", serviceName);
        if (map.get(serviceName) != null) {
            logger.warn("service {} already exists, replace it.", serviceName);
        }
        map.put(serviceName, service);
    }

    public Service getService(String serviceName) {
        return map.get(serviceName);
    }
}
