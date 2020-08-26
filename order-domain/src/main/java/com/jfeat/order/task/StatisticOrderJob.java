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
import com.jfeat.order.service.StatisticService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 每天凌晨2点钟进行统计
 * Created by jackyhuang on 16/9/3.
 */
@CronExp(value = "0 0 2 * * ?")
public class StatisticOrderJob implements Job {

    private static Logger logger = LoggerFactory.getLogger(StatisticOrderJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            StatisticService statisticService = new StatisticService();
            statisticService.orderStatistic(DateKit.yesterday("yyyy-MM-dd 00:00:00"), DateKit.yesterday("yyyy-MM-dd 23:59:59"));
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
