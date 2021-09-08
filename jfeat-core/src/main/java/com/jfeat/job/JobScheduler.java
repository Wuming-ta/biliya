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

package com.jfeat.job;

import com.google.common.collect.Lists;
import com.jfeat.ext.plugin.quartz.QuartzPlugin;
import com.jfinal.kit.StrKit;
import org.apache.commons.lang3.tuple.Pair;
import org.quartz.Job;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * call registerPlugin after QuartzPlugin started.
 * then other methods are available.
 */
public class JobScheduler {
    private static Logger logger = LoggerFactory.getLogger(JobScheduler.class);
    private QuartzPlugin quartzPlugin;
    
    private static JobScheduler me = new JobScheduler();

    //Pair<CronExp, JobClass>
    private List<Pair<String, Class<? extends  Job>>> jobList = Lists.newLinkedList();
    
    public static JobScheduler me() {
        return me;
    }

    //Module.addJob should call this method to register job into system.
    public void addJob(Class<? extends Job> jobClass) {
        CronExp cronExp = (CronExp) jobClass.getAnnotation(CronExp.class);
        addJob(cronExp.value(), jobClass);
    }

    public void addJob(String cronExp, Class<? extends Job> jobClass) {
        if (StrKit.isBlank(cronExp)) {
            throw new IllegalArgumentException("cronExp can not be empty.");
        }
        boolean found = false;
        for (Pair<String, Class<? extends Job>> pair : jobList) {
            if (cronExp.equals(pair.getKey()) && jobClass.equals(pair.getValue())) {
                found = true;
                break;
            }
        }
        if (!found) {
            Pair pair = Pair.of(cronExp, jobClass);
            jobList.add(pair);
        }
        else {
            logger.error("job {} with cronExp {} already registered.", jobClass.getName(), cronExp);
        }
    }

    // JFeatConfig.configPlugin will call this method.
    public void registerPlugin(QuartzPlugin quartzPlugin) {
        this.quartzPlugin = quartzPlugin;
        for (Pair<String, Class<? extends Job>> pair : jobList) {
            String cronExp = pair.getKey();
            Class jobClass = pair.getValue();
            try {
                this.quartzPlugin.add(cronExp, jobClass);
                logger.info("registered job: " + jobClass.getName());
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }

        }
    }

    ///////////////////////////////////////////////////////////////
    // below methods only available after registerPlugin called. //
    ///////////////////////////////////////////////////////////////
    
    public void standby() throws SchedulerException {
        this.quartzPlugin.getScheduler().standby();
    }
    
    public void start() throws SchedulerException {
        this.quartzPlugin.getScheduler().start();
    }

    public void shutdown() throws SchedulerException {
        if (!this.quartzPlugin.getScheduler().isShutdown()) {
            this.quartzPlugin.getScheduler().shutdown();
        }
    }

    /**
     * 运行时添加新JOB
     * @param cronExp
     * @param jobClass
     */
    public void addJobRuntime(String cronExp, Class<? extends Job> jobClass) {
        this.quartzPlugin.addJobRuntime(jobClass, cronExp);
    }


    /**
     * 运行时删除JOB
     * @param jobClass
     */
    public void removeJobRuntime(String cronExp, Class<? extends Job> jobClass) {
        this.quartzPlugin.removeJobRuntime(jobClass, cronExp);
    }
    
    public void printJobs() {

    }
}
