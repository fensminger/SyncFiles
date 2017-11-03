package org.fer.syncfiles.quartz;

import org.fer.syncfiles.domain.ParamSyncFiles;
import org.fer.syncfiles.repository.ParamSyncFilesRepository;
import org.fer.syncfiles.services.ParamSyncFilesService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Created by david on 2015-01-20.
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SampleJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(QuartzJobListener.class);

    @Autowired
    private ParamSyncFilesService paramSyncFilesService;

    @Autowired
    private ParamSyncFilesRepository paramSyncFilesRepository;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        JobDataMap mapParam = jobExecutionContext.getJobDetail().getJobDataMap();
        String paramSyncFilesId = (String) mapParam.get("paramSyncFilesId");
        log.info("Start Job Executed : " + jobExecutionContext.getFireInstanceId() + " -> key : " + jobExecutionContext.getJobDetail().getKey() + ", paramSyncFilesId : " + paramSyncFilesId);
        ParamSyncFiles paramSyncFiles = paramSyncFilesRepository.findOne(paramSyncFilesId);

        paramSyncFilesService.synchronize(paramSyncFiles);

        paramSyncFiles.getSchedule().setLastExecution(new Date());
        paramSyncFilesRepository.save(paramSyncFiles);
        log.info("Job Executed : " + jobExecutionContext.getFireInstanceId());
    }
}
