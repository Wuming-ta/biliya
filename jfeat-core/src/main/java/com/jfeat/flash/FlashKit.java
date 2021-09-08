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

package com.jfeat.flash;

/**
 * Created by ehngjen on 5/4/2015.
 */
public class FlashKit {
    /**
     * 默认采用session来实现。
     */
    private static IFlashManager flashManager = new SessionFlashManager();

    public static IFlashManager getFlashManager() {
        return flashManager;
    }

    public void setFlashManager(IFlashManager flashManager) {
        this.flashManager = flashManager;
    }

    public static void init() {

    }

    public static void init(IFlashManager flashManager) {
        FlashKit.flashManager = flashManager;
    }

}
