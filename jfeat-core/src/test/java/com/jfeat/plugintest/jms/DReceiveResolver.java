package com.jfeat.plugintest.jms;

import com.jfeat.ext.plugin.jms.ReceiveResolver;

import java.io.Serializable;

/**
 * Created by jacky on 1/6/15.
 */
public class DReceiveResolver implements ReceiveResolver {
    @Override
    public void resolve(Serializable objectMessage) throws Exception {
        System.out.println("DReceiveResolver");
    }
}
