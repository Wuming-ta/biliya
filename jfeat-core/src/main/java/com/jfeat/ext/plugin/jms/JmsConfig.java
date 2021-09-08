/*
 * Copyright (C) 2014-2015 by ehngjen @ www.jfeat.com
 *
 *  The program may be used and/or copied only with the written permission
 *  from JFeat.com, or in accordance with the terms and
 *  conditions stipulated in the agreement/contract under which the program
 *  has been supplied.
 *
 *  All rights reserved.
 */
package com.jfeat.ext.plugin.jms;

import java.io.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.jfinal.ext.plugin.config.ConfigKit;
import com.jfinal.ext.plugin.config.ConfigPlugin;

public class JmsConfig {
    private static Properties properties;

    public static synchronized void setStr(String key, String value) {
        properties.put(key, value);
    }

    public static synchronized  void init(String resoruceLocation) {
        if (properties != null) {
            return;
        }
        properties = new Properties();
        if (resoruceLocation != null) {
            File file = new File(resoruceLocation);
            InputStream is;
            try {
                if (file.exists() && file.isAbsolute()) {
                    is = new FileInputStream(resoruceLocation);
                }
                else {
                    is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resoruceLocation);
                }
                if (is != null) {
                    properties.load(is);
                }
            } catch (IOException e) {
            throw new RuntimeException("cant load properties in location :" + resoruceLocation, e);
        }
        }
    }
    public static int getInt(String key) {
        return Integer.parseInt(getStr(key));
    }

    public static String getStr(String key) {
        Object objVal = properties.get(key);
        return objVal == null ? "" : objVal + "";
    }

    public static Set<String> keys() {
        Set<String> keySet = new HashSet<String>();
        for (Object objVal : properties.keySet()) {
            keySet.add(objVal + "");
        }
        return keySet;
    }
}
