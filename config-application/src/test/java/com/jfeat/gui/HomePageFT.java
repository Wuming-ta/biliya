
/*
 *   Copyright (C) 2014-2016 GIMC
 *
 *    The program may be used and/or copied only with the written permission
 *    from GIMC or in accordance with the terms and
 *    conditions stipulated in the agreement/contract under which the program
 *    has been supplied.
 *
 *    All rights reserved.
 *
 */

package com.jfeat.gui;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Created by ehngjen on 4/24/2015.
 */
public class HomePageFT extends GuiTestBase {

    WebElement element;

    @Test
    public void homePage() {
        driver.get(baseUrl);
    }
}
