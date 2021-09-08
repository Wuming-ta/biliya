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
package com.jfeat.ext.plugin.quartz;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfinal.ext.kit.Reflect;
import com.jfinal.ext.kit.ResourceKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.IPlugin;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QuartzPlugin implements IPlugin {
    private static final String JOB = "job";
    private static Logger logger = LoggerFactory.getLogger(QuartzPlugin.class);

    // Just for pre-load config. runtime is not used this map.
    // <JobClass, List<CronExp>>
    private Map<Class<? extends Job>, List<String>> jobClasses = Maps.newLinkedHashMap();

    private SchedulerFactory sf;
    private Scheduler scheduler;
    private String jobConfig;
    private String confConfig;
    private Map<String, String> jobProp;

    public QuartzPlugin(String jobConfig, String confConfig) {
        this.jobConfig = jobConfig;
        this.confConfig = confConfig;
    }

    public QuartzPlugin(String jobConfig) {
        this.jobConfig = jobConfig;
    }

    public QuartzPlugin() {
    }

    public QuartzPlugin add(String jobCronExp, Class<? extends Job> jobClass) {
        List<String> cronList = jobClasses.get(jobClass);
        if (cronList == null) {
            cronList = Lists.newArrayList();
        }
        if (!cronList.contains(jobCronExp)) {
            cronList.add(jobCronExp);
        }
        jobClasses.put(jobClass, cronList);
        return this;
    }

    /**
     * Add job after scheduler is started.
     */
    public void addJobRuntime(Class<? extends Job> jobClass, String cronExp) {
        List<String> cronList = jobClasses.get(jobClass);
        if (cronList != null && cronList.contains(cronExp)) {
            logger.warn("job {} with expr {} already exists.", jobClass.getName(), cronExp);
            return;
        }

        try {
            JobKey jobKey = new JobKey(jobClass.getName(), jobClass.getName());
            scheduler.pauseJob(jobKey);
            scheduler.deleteJob(jobKey);

            add(cronExp, jobClass);
            logger.info("added job: " + jobClass.getName());
            createJob(jobClass, jobClasses.get(jobClass));
            if (scheduler.isShutdown()) {
                scheduler.start();
            }
        } catch(Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public void removeJobRuntime(Class<? extends Job> jobClass, String cronExp) {
        List<String> cronList = jobClasses.get(jobClass);
        if (cronList == null || !cronList.contains(cronExp)) {
            logger.warn("job {} {} not found.", jobClass.getName(), cronExp);
            return;
        }
            try {
                JobKey jobKey = new JobKey(jobClass.getName(), jobClass.getName());
                TriggerKey triggerKey = new TriggerKey(jobClass.getName()+cronExp);
                scheduler.pauseJob(jobKey);
                scheduler.deleteJob(jobKey);
                scheduler.pauseTrigger(triggerKey);
                cronList.remove(cronExp);
                logger.info("removed job : {}, cron: {}", jobKey, cronExp);
                createJob(jobClass, jobClasses.get(jobClass));
            } catch (SchedulerException e) {
                e.printStackTrace();
            }

    }

    
    public Scheduler getScheduler() {
        return scheduler;
    }

    @Override
    public boolean start() {
        loadJobsFromProperties();
        startJobs();
        return true;
    }

    private void createJob(Class<? extends Job> jobClass, List<String> jobCronExpList) throws SchedulerException {
        if (jobCronExpList.size() == 0) {
            return;
        }

        String jobClassName = jobClass.getName();
        String cronExp = jobCronExpList.get(0);
        JobKey jobKey = new JobKey(jobClassName, jobClassName);
        TriggerKey triggerKey = new TriggerKey(jobClassName + cronExp);

        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobKey).build();
        }

        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        if (trigger == null) {
            trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExp)).build();
        }
        if (!scheduler.checkExists(jobKey) && !scheduler.checkExists(triggerKey)) {
            Date date = scheduler.scheduleJob(jobDetail, trigger);
            logger.info("1. Schedule job: {}, cron: {}, {}", jobDetail.getKey(), cronExp, date);
        }

        for (int i = 1; i < jobCronExpList.size(); i++) {
            cronExp = jobCronExpList.get(i);
            triggerKey = new TriggerKey(jobClassName + cronExp);
            if (!scheduler.checkExists(triggerKey)) {
                trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
                        .withSchedule(CronScheduleBuilder.cronSchedule(cronExp)).forJob(jobDetail).build();
                Date date = scheduler.scheduleJob(trigger);
                logger.info("2. Schedule job: {}, cron: {}, {}", jobDetail.getKey(), cronExp, date);
            }
        }

//        //JobDetail and CornTrigger are classes in 1.x version,but are interfaces in 2.X version.
//        if (VERSION_1.equals(version)) {
//            jobDetail = Reflect.on("org.quartz.JobDetail").create(jobClassName, jobClassName, job.getClass()).get();
//            trigger = Reflect.on("org.quartz.CronTrigger").create(jobClassName, jobClassName, jobCronExp).get();
//        } else {
//            jobDetail = Reflect.on("org.quartz.JobBuilder").call("newJob", job.getClass()).call("withIdentity", jobClassName, jobClassName)
//                    .call("build").get();
//            Object temp = Reflect.on("org.quartz.TriggerBuilder").call("newTrigger").get();
//            temp = Reflect.on(temp).call("withIdentity", jobClassName, jobClassName).get();
//            temp = Reflect.on(temp).call("withSchedule",
//                    Reflect.on("org.quartz.CronScheduleBuilder").call("cronSchedule", jobCronExp).get())
//                    .get();
//            trigger = Reflect.on(temp).call("build").get();
//        }








//        Date ft = Reflect.on(scheduler).call("scheduleJob", trigger).get();
//        JobKey jobKey = Reflect.on(jobDetail).call("getKey").get();
//        jobKeys.put(key, jobKey);
//        logger.debug("JOBKEY=" + jobKey);
//        logger.debug(Reflect.on(jobDetail).call("getKey") + " has been scheduled to run at: " + ft + " " +
//                "and repeat based on expression: " + Reflect.on(trigger).call("getCronExpression"));

    }

    private void startJobs() {
        try {
            if (StrKit.notBlank(confConfig)) {
                sf = new StdSchedulerFactory(confConfig);
            } else {
                sf = new StdSchedulerFactory();
            }
            scheduler = sf.getScheduler();

            Set<Map.Entry<Class<? extends Job>, List<String>>> set = jobClasses.entrySet();
            for (Map.Entry<Class<? extends Job>, List<String>> entry : set) {
                Class<? extends Job> jobClass = entry.getKey();
                List<String> jobCronExpList = entry.getValue();
                createJob(jobClass, jobCronExpList);
            }

            scheduler.start();
        } catch (SchedulerException e) {
            Throwables.propagate(e);
        }
    }

    private void loadJobsFromProperties() {
        if (StrKit.isBlank(jobConfig)) {
            return;
        }
        jobProp = ResourceKit.readProperties(jobConfig);
        Set<Map.Entry<String, String>> entries = jobProp.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            if (!key.endsWith(JOB) || !isEnableJob(enable(key))) {
                continue;
            }
            String jobClassName = jobProp.get(key) + "";
            String jobCronExp = jobProp.get(cronKey(key)) + "";
            Class<Job> job = Reflect.on(jobClassName).get();
            add(jobCronExp, job);
        }
    }

    private String enable(String key) {
        return key.substring(0, key.lastIndexOf(JOB)) + "enable";
    }

    private String cronKey(String key) {
        return key.substring(0, key.lastIndexOf(JOB)) + "cron";
    }

    private boolean isEnableJob(String enableKey) {
        Object enable = jobProp.get(enableKey);
        if (enable != null && "false".equalsIgnoreCase((enable + "").trim())) {
            return false;
        }
        return true;
    }

    @Override
    public boolean stop() {
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            Throwables.propagate(e);
        }
        return true;
    }

    public QuartzPlugin confConfig(String confConfig) {
        this.confConfig = confConfig;
        return this;
    }

    public QuartzPlugin jobConfig(String jobConfig) {
        this.jobConfig = jobConfig;
        return this;
    }
}
