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

import com.jfeat.core.JFeatConfig;

public class MiscApplicationJFeatConfig extends JFeatConfig {

    public MiscApplicationJFeatConfig() {
        new MiscApplicationModule(this);
    }

}
