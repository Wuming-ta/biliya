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
package com.jfeat.common;

import com.jfeat.core.JFeatConfig;

public class ConfigApplicationJFeatConfig extends JFeatConfig {

    public ConfigApplicationJFeatConfig() {
        new ConfigApplicationModule(this);
    }

}
