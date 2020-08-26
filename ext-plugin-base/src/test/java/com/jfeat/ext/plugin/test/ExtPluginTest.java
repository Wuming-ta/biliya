package com.jfeat.ext.plugin.test;

import com.jfeat.ext.plugin.ExtPluginHolder;
import org.junit.Test;

/**
 * @author jackyhuang
 * @date 2018/6/14
 */
public class ExtPluginTest {

    @Test
    public void test() {
        ExtPlugin extPlugin = new ExtPlugin("thekey");
        ExtPluginHolder.me().start(ExtPlugin.class, extPlugin);
        System.out.println(ExtPluginHolder.me().get(ExtPlugin.class).isEnabled());

        ExtPlugin2 extPlugin2 = new ExtPlugin2("thekey2");
        ExtPluginHolder.me().start(ExtPlugin2.class, extPlugin2);
        System.out.println(ExtPluginHolder.me().get(ExtPlugin2.class).isEnabled());


        System.out.println("extPlugin1 " + ExtPluginHolder.me().get(ExtPlugin.class).isEnabled());
        System.out.println("extPlugin2 " + ExtPluginHolder.me().get(ExtPlugin2.class).isEnabled());
    }
}
