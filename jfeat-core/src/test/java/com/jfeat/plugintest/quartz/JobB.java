package com.jfeat.plugintest.quartz;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.jfeat.job.CronExp;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@CronExp("*/5 * * * * ?")
public class JobB implements Job {
    static int callTime = 0;
    static int l = 0;
    int [] ii = new int[]{};

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        int i =0,j= 0;
        callTime++;
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " JobB works,callTime is: " + callTime);
        String [] arrs ;
        String arrs2 [] ;
    }

}