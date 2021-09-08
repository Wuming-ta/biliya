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

package com.jfeat.ext.plugin.flyway;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;

import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.IDataSourceProvider;

public class FlywayPlugin implements IPlugin {
    
    private IDataSourceProvider dataSourceProvider;
    
    public FlywayPlugin(IDataSourceProvider dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    @Override
    public boolean start() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSourceProvider.getDataSource());
        flyway.migrate();
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

}
