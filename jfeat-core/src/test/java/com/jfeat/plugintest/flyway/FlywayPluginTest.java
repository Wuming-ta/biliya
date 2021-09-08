package com.jfeat.plugintest.flyway;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import com.jfeat.ext.plugin.flyway.FlywayPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;

public class FlywayPluginTest {
    
    private C3p0Plugin dbPlugin;
    private FlywayPlugin flywayPlugin;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        // 配置C3p0数据库连接池插件
        dbPlugin = new C3p0Plugin("jdbc:mysql://127.0.0.1/jfeat_test", "root", "");
        dbPlugin.start();
        
        flywayPlugin = new FlywayPlugin(dbPlugin);
        flywayPlugin.start();
    }
    
    @After
    public void tearDown() throws Exception {
        flywayPlugin.stop();
        //dbPlugin.stop();
        
    }

    //@Test
    public void test() {

    }

}
