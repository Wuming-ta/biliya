
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

package com.jfeat.gui.authentication;

import com.jfeat.gui.GuiTestBase;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Base class for Login if necessary.
 *
 * Created by jacky on 4/25/15.
 */
public class Login extends GuiTestBase {

    @BeforeClass
    public static void loginAsAdminIfNecessary() {
        login("admin", "admin");
    }

    public static void logout() {
        driver.get(baseUrl+"auth/logout");
    }

    public static void login(String user, String password) {
        driver.get(baseUrl);
        if ("Login".equals(driver.getTitle())) {
            WebElement loginNameInput = driver.findElement(By.name("login_name"));
            Assert.assertNotNull(loginNameInput);
            WebElement passwordInput = driver.findElement(By.name("password"));
            Assert.assertNotNull(passwordInput);
            WebElement loginButton = driver.findElement(By.id("loginBtn"));
            Assert.assertNotNull(loginButton);

            loginNameInput.sendKeys(user);
            passwordInput.sendKeys(password);
            loginButton.click();
        }
    }
}
