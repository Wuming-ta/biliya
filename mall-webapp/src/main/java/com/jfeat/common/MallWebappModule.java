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
package com.jfeat.common;

import com.jfeat.config.model.Config;
import com.jfeat.core.JFeatConfig;
import com.jfeat.core.Module;
import com.jfeat.core.ServiceContext;
import com.jfeat.identity.service.CustomerRoleProvider;
import com.jfeat.identity.service.DefaultRole;
import com.jfeat.kit.DateKit;
import com.jfeat.service.impl.SysAuthorizationProviderDbImpl;
import com.jfinal.kit.StrKit;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.Cipher;
import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.Key;
import java.util.*;

public class MallWebappModule extends Module {

    public MallWebappModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        MallWebappModelMapping.mapping(this);

        // 1. register your controllers
        // addController(YourDefinedController.class);

        // 3. config the module you dependencied.
        // new YouDependenciedModule(jfeatConfig);

        new ConfigDomainModule(jfeatConfig);
        new IdentityApplicationModule(jfeatConfig);
        new OrderApplicationModule(jfeatConfig);
        new ProductApplicationModule(jfeatConfig);
        new CooperativePartnerApplicationModule(jfeatConfig);
        new SettlementApplicationModule(jfeatConfig);
        new PcdApplicationModule(jfeatConfig);
        new MemberApplicationModule(jfeatConfig);
        new ConfigApplicationModule(jfeatConfig);
        new EventLogApplicationModule(jfeatConfig);
        new MiscApplicationModule(jfeatConfig);
        new WechatApplicationModule(jfeatConfig);
        new MarketingApplicationModule(jfeatConfig);
        new MerchantApplicationModule(jfeatConfig);
        new CaptchaServiceModule(jfeatConfig);
//        new ExtPluginHtmlModule(jfeatConfig);
        new AlipayApplicationModule(jfeatConfig);
        new MallCronModule(jfeatConfig);
    }

    @Override
    public void afterJFinalStart() {
        super.afterJFinalStart();
        //inject default role provider. so that the register api can use it.
        Integer roleId = null;
        Config config = Config.dao.findByKey("misc.customer_role_id");
        if (config != null) {
            roleId = config.getValueToInt();
        }
        DefaultRole.me().setRoleProvider(new CustomerRoleProvider(roleId));

        ServiceContext.me().register(new SysAuthorizationProviderDbImpl());

//
//        String license = getJFeatConfig().getProperty("license");
//        if (StrKit.isBlank(license)) {
//            throw new RuntimeException("Invalid License");
//        }
//        try {
//            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("public_key");
//            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
//            Key secureKey = (Key) objectInputStream.readObject();
//            objectInputStream.close();
//
//            Cipher cipher = Cipher.getInstance("RSA");
//            cipher.init(Cipher.DECRYPT_MODE, secureKey);
//            byte[] data = Base64.decodeBase64(license.getBytes());
//            byte[] decodedByteArray = new byte[]{};
//            for (int i = 0; i < data.length; i += 128) {
//                byte[] result = cipher.doFinal(ArrayUtils.subarray(data, i, i + 128));
//                decodedByteArray = ArrayUtils.addAll(decodedByteArray, result);
//            }
//            Properties props = new Properties();
//            props.load(new ByteArrayInputStream(decodedByteArray));
//            if (Calendar.getInstance().getTime().compareTo(DateKit.toDate(props.getProperty("expiredDate"))) > 0) {
//                throw new RuntimeException("License Expired.");
//            }
//
//            Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
//            List<String> ipList = new ArrayList<>();
//            while (allNetInterfaces.hasMoreElements()) {
//                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
//                Enumeration addresses = netInterface.getInetAddresses();
//                while (addresses.hasMoreElements()) {
//                    InetAddress ip = (InetAddress) addresses.nextElement();
//                    if (ip != null && ip instanceof Inet4Address) {
//                        ipList.add(ip.getHostAddress());
//                    }
//                }
//            }
//            if (!ipList.contains(props.get("ip"))) {
//                throw new RuntimeException("Invalid ip");
//            }
//        }
//        catch (Exception ex) {
//            throw new RuntimeException("Invalid License " + ex.getMessage());
//        }
    }
}
