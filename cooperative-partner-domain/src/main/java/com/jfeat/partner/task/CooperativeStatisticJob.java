package com.jfeat.partner.task;

import com.jfeat.job.CronExp;
import com.jfeat.kit.DateKit;
import com.jfeat.partner.service.CooperativeStatisticService;
import com.jfinal.aop.Enhancer;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by kang on 2017/4/15.
 */
@CronExp(value = "0 0 0 * * ?")
public class CooperativeStatisticJob implements Job {
    private static Logger logger = LoggerFactory.getLogger(CooperativeStatisticJob.class);
    private CooperativeStatisticService cooperativeStatisticService = Enhancer.enhance(CooperativeStatisticService.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("========= StatisticJob Started =========");
        try {
            cooperativeStatisticService.statistic(new Date(), DateKit.yesterday("yyyy-MM-dd 00:00:00"), DateKit.yesterday("yyyy-MM-dd 23:59:59"));
        } catch (Exception e) {
            logger.error(e.getMessage());
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                logger.error("   {}:{}#{}", stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
            }
        }
    }
}
