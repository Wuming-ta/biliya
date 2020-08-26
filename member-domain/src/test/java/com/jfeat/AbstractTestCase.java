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
package com.jfeat;


import com.jfeat.common.MemberDomainModule;
import com.jfeat.core.JFeatConfig;
import com.jfeat.core.Module;
import com.jfeat.core.ModuleHolder;
import com.jfinal.config.Constants;
import com.jfinal.config.Plugins;
import com.jfinal.plugin.IPlugin;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AbstractTestCase {
    public static Logger logger = LoggerFactory.getLogger(AbstractTestCase.class);

    private static boolean started = false;
    private static final Constants constants = new Constants();
    private static final Plugins plugins = new Plugins();

    @BeforeClass
    public static void beforeClass() throws Exception {
        if (!started) {
            JFeatConfig config = new JFeatConfig();
            config.configConstant(constants);
            config.configPlugin(plugins);

            new MemberDomainModule(config);

            for (Module module : ModuleHolder.me().getModules()) {
                module.bindTables(null);
            }

            startPlugins();

            started = true;
        }
    }

    @AfterClass
    public static void afterClass() throws Exception {
    }


    private static void startPlugins() {
        List<IPlugin> pluginList = plugins.getPluginList();
        if (pluginList == null)
            return ;

        for (IPlugin plugin : pluginList) {
            try {
                // process ActiveRecordPlugin devMode
                if (plugin instanceof com.jfinal.plugin.activerecord.ActiveRecordPlugin) {
                    com.jfinal.plugin.activerecord.ActiveRecordPlugin arp = (com.jfinal.plugin.activerecord.ActiveRecordPlugin)plugin;
                    if (arp.getDevMode() == null)
                        arp.setDevMode(constants.getDevMode());
                }

                if (plugin.start() == false) {
                    String message = "Plugin start error: " + plugin.getClass().getName();
                    logger.error(message);
                    throw new RuntimeException(message);
                }
            }
            catch (Exception e) {
                String message = "Plugin start error: " + plugin.getClass().getName() + ". \n" + e.getMessage();
                logger.error(message, e);
                throw new RuntimeException(message, e);
            }
        }
    }

}

