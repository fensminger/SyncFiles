package org.fer.syncfiles.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fensm on 05/01/2017.
 */
public class QuartzJobListener implements JobListener {
    private static final Logger log = LoggerFactory.getLogger(QuartzJobListener.class);

    @Override
    public String getName() {
        return "QuartzJobListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {
        System.out.println("Job to be executed: " + jobExecutionContext.getFireInstanceId() + ", job listener: " + getName());
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {

    }

    @Override
    public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException e) {
        System.out.println("Job was executed: " + jobExecutionContext.getFireInstanceId() + ", job listener: " + getName());
    }
}
