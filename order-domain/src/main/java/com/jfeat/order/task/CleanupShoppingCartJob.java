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

package com.jfeat.order.task;

import com.jfeat.job.CronExp;
import com.jfeat.kit.DateKit;
import com.jfeat.order.service.OrderService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 清理放入购物车超过一个月的数据
 * Created by jackyhuang on 16/12/8.
 */
@CronExp(value = "0 0 1 * * ?")
public class CleanupShoppingCartJob implements Job {

    private static Logger logger = LoggerFactory.getLogger(CleanupShoppingCartJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            OrderService orderService = new OrderService();
            orderService.cleanShoppingCartDaysAgo(30);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            for (StackTraceElement element : e.getStackTrace()) {
                logger.error("    {}:{} - {}:{}",
                        element.getFileName(), element.getLineNumber(), element.getClassName(), element.getMethodName());
            }
        }
    }
}
