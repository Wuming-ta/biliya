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

package com.jfeat.ext.plugin.async;

import com.jfinal.plugin.IPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by ehngjen on 4/17/2015.
 */
public class AsyncTaskPlugin implements IPlugin {

    // thread pool number
    private int threadNumber = 5;
    private ExecutorService executor;

    public AsyncTaskPlugin() {

    }

    public AsyncTaskPlugin(int threadNumber) {
        this.threadNumber = threadNumber;
    }

    @Override
    public boolean start() {
        executor = Executors.newFixedThreadPool(threadNumber);
        AsyncTaskKit.init(executor);
        return true;
    }

    @Override
    public boolean stop() {
        if (executor != null) {
            executor.shutdownNow();
            try {
                executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
