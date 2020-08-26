
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

package com.jfeat.gui;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.concurrent.TimeUnit;

/**
 * Created by ehngjen on 4/24/2015.
 */
public class GuiTestBase {
    protected static RemoteWebDriver driver;
    private static Server server;
    private static int port = 9091;
    private static String webDir = "src/main/webapp";
    private static String contextPath = "/";
    protected static String baseUrl = "localhost:" + port + contextPath;

    static void startJetty() throws Exception {
        if (server == null) {
            server = new Server();
            SelectChannelConnector connector = new SelectChannelConnector();
            connector.setPort(port);
            server.addConnector(connector);
            WebAppContext webApp = new WebAppContext();
            webApp.setContextPath(contextPath);
            webApp.setResourceBase(webDir);
            webApp.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
            webApp.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");

            server.setHandler(webApp);
            server.start();
        }
    }

    static void startSelenium() {
        if (driver == null) {
            driver = new FirefoxDriver();
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            Runtime.getRuntime().addShutdownHook(new Thread("Selenium Quit Hook") {
                @Override
                public void run() {
                    driver.quit();
                }
            });
        }
    }

    @Rule
    public TestRule snapshotRule = new SeleniumSnapshotRule(driver);


    @BeforeClass
    public static void openBrowser() throws Exception {
        startJetty();
        startSelenium();

    }
}
