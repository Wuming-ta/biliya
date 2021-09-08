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

import java.util.concurrent.ExecutorService;

/**
 * Created by ehngjen on 4/17/2015.
 */
public class AsyncTaskKit {
    private static ExecutorService executor;

    public static void init(ExecutorService executor) {
        AsyncTaskKit.executor = executor;
    }

    public static void submit(Runnable task) {
        executor.submit(task);
    }

}
