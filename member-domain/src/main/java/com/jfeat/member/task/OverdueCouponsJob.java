package com.jfeat.member.task;

import com.jfeat.job.CronExp;
import com.jfeat.member.service.CouponService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kang on 2016/11/24.
 */
@CronExp(value = "0 0 10,16 * * ?")
public class OverdueCouponsJob implements Job {
    private static Logger logger = LoggerFactory.getLogger(OverdueCouponsJob.class);
    private CouponService couponService = new CouponService();

    //每天早上10点，下午4点执行以下两个任务：
    // 1.找出所有 未激活 和 已激活 的优惠券，如果已经过期，则把状态设为过期
    //2.找出还有N小时就要过期的记录，并发通知给用户，然后把该记录删掉.
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("=========Auto overdue coupons.=========");
        try {
            couponService.overdueCoupons();
            couponService.notifyCouponsWillOverdue();
        } catch (Exception e) {
            logger.error(e.getMessage());
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                logger.error("   {}:{}#{}", stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
            }
        }
    }
}
