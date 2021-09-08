package com.jfeat.plugintest.quartz;

import com.jfeat.job.CronExp;
import org.junit.BeforeClass;
import org.junit.Test;
import com.jfeat.job.JobScheduler;
import com.jfeat.ext.plugin.quartz.QuartzPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestQuartz {

    //@Test
    public void test() throws InterruptedException {
        QuartzPlugin quartzPlugin = new QuartzPlugin();
        JobScheduler.me().addJob(JobB.class);
        String cronExp = "*/4 * * * * ?";
        JobScheduler.me().addJob(cronExp, JobC.class);
        JobScheduler.me().registerPlugin(quartzPlugin);
        quartzPlugin.start();
//        TimeUnit.SECONDS.sleep(12);
//        JobScheduler.me().printJobs();
//        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//
//        JobScheduler.me().removeJobRuntime(JobB.class.getAnnotation(CronExp.class).value(), JobB.class);
//        JobScheduler.me().addJobRuntime("*/2 * * * * ?", JobC.class);
//        TimeUnit.SECONDS.sleep(12);
//        JobScheduler.me().printJobs();
//        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//
//
//        JobScheduler.me().addJobRuntime("*/5 * * * * ?", JobC.class);
//        TimeUnit.SECONDS.sleep(12);
//        JobScheduler.me().printJobs();
//        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//
//
//        System.out.println("=========remove jobs==========");
//        JobScheduler.me().removeJobRuntime("*/2 * * * * ?", JobC.class);
//        JobScheduler.me().removeJobRuntime("*/5 * * * * ?", JobC.class);
//        JobScheduler.me().printJobs();
//        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

        System.out.println("=========same jobClass diff cronExp==========");
        JobScheduler.me().addJobRuntime("*/10 * * * * ?", JobC.class);
        TimeUnit.SECONDS.sleep(30);
        JobScheduler.me().removeJobRuntime(cronExp, JobC.class);
        JobScheduler.me().addJobRuntime("*/33 * * * * ?", JobC.class);
        TimeUnit.SECONDS.sleep(120);
        JobScheduler.me().printJobs();
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

    }


}