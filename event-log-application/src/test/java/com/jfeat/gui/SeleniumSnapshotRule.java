
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

import org.apache.commons.io.FileUtils;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;

/**
 * Created by jacky on 4/26/15.
 */
public class SeleniumSnapshotRule extends TestWatcher {
    private RemoteWebDriver driver;
    public SeleniumSnapshotRule(RemoteWebDriver driver) {
        this.driver = driver;
    }

    @Override
    protected void failed(Throwable e, Description description) {
        String basePath = "target/screenshot/";
        File targetDir = new File(basePath);
        String outputFileName = description.getClassName() + "_" + description.getMethodName() + ".png";
        File screenshot = driver.getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFileToDirectory(screenshot, targetDir);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
