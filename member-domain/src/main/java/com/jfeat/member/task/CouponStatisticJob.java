package com.jfeat.member.task;

import com.jfeat.job.CronExp;
import com.jfeat.kit.DateKit;
import com.jfeat.member.service.CouponStatisticService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kang on 2017/3/13.
 */
@CronExp("0 0 2 * * ?")
public class CouponStatisticJob implements Job {
    private CouponStatisticService couponStatisticService = new CouponStatisticService();
    private static Logger logger = LoggerFactory.getLogger(CouponStatisticJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            String yesterdayStart = DateKit.yesterday("yyyy-MM-dd 00:00:00");
            String yesterdayEnd = DateKit.yesterday("yyyy-MM-dd 23:59:59");
            couponStatisticService.statistic(yesterdayStart, yesterdayEnd);
        } catch (Exception e) {
            logger.error(e.getMessage());
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                logger.error("   {}:{}#{}", stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
            }
        }
    }
}
