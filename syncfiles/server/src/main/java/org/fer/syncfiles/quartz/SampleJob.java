package org.fer.syncfiles.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by david on 2015-01-20.
 */
public class SampleJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(QuartzJobListener.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        log.info("Job Executed : " + jobExecutionContext.getFireInstanceId());
    }
}
