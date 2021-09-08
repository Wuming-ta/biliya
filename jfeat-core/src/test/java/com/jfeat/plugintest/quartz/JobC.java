package com.jfeat.plugintest.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jacky on 3/12/16.
 */
public class JobC implements Job {
    static int callTime = 0;
    static int l = 0;
    int [] ii = new int[]{};

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        int i =0,j= 0;
        callTime++;
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " JobC works,callTime is: " + callTime);
        String [] arrs ;
        String arrs2 [] ;
    }
}
