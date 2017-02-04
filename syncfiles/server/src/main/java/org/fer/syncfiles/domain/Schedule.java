package org.fer.syncfiles.domain;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import org.fer.syncfiles.dto.ScheduleCalc;
import org.quartz.CronTrigger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.cronutils.model.CronType.QUARTZ;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by fensm on 12/01/2017.
 */
public class Schedule {
    public enum SCHEDULE_TYPE {MINUTES, HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY, CUSTOM_CRON_EXPRESSION};

    private SCHEDULE_TYPE type;

    private ScheduleMinutes minutes;
    private ScheduleHourly hourly;
    private ScheduleDaily daily;
    private ScheduleWeekly weekly;
    private ScheduleMonthly monthly;
    private ScheduleYearly yearly;

    private String cronExp;

    private String paramSyncFilesId;
    private Date lastExecution;

    public Schedule() {
        super();
    }

    public ScheduleCalc calcCron() {
        final ScheduleCalc cronCalc = new ScheduleCalc();
        cronCalc.setSchedule(this);
        try {
            switch (type) {
                case MINUTES:
                    minutes.calcCron(cronCalc);
                    break;
                case HOURLY:
                    hourly.calcCron(cronCalc);
                    break;
                case DAILY:
                    daily.calcCron(cronCalc);
                    break;
                case WEEKLY:
                    weekly.calcCron(cronCalc);
                    break;
                case MONTHLY:
                    monthly.calcCron(cronCalc);
                    break;
                case YEARLY:
                    yearly.calcCron(cronCalc);
                    break;
                case CUSTOM_CRON_EXPRESSION:
                default:
                    break;
            }
        } catch (Exception e) {
            cronCalc.setMsgError("Unable to calc cron expression : " + e.getMessage());
            return cronCalc;
        }

        if (cronCalc.getMsgError()==null) {
            calcNextExecution(cronCalc);
        }
        return cronCalc;
    }

    private void calcNextExecution(ScheduleCalc cronCalc) {
        try {
            final String cronExp = cronCalc.getSchedule().getCronExp();

            CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(QUARTZ);
            CronParser parser = new CronParser(cronDefinition);
            CronDescriptor descriptor = CronDescriptor.instance(Locale.UK);
            String description = descriptor.describe(parser.parse(cronExp));
            cronCalc.setDescription(description);

            List<Date> nextCronList = new ArrayList<>();
            Date currTime = new Date();
            CronTrigger tr = newTrigger()
                    .withIdentity("test", "groupTest")
                    .withSchedule(cronSchedule(cronExp))
                    .forJob("myJobTest", "groupTest")
                    .build();
            for (int i = 0; i < 100; i++) {
                Date nextFireAt = tr.getFireTimeAfter(currTime);
                nextCronList.add(nextFireAt);
                currTime = nextFireAt;
            }

            cronCalc.setNextCron(nextCronList);
        } catch (Exception e) {
            cronCalc.setMsgError("Illegal cron expression : " + cronCalc.getSchedule().getCronExp() + ". " + e.getMessage());
        }
    }

    public ScheduleMinutes getMinutes() {
        return minutes;
    }

    public void setMinutes(ScheduleMinutes minutes) {
        this.minutes = minutes;
    }

    public ScheduleHourly getHourly() {
        return hourly;
    }

    public void setHourly(ScheduleHourly hourly) {
        this.hourly = hourly;
    }

    public ScheduleDaily getDaily() {
        return daily;
    }

    public void setDaily(ScheduleDaily daily) {
        this.daily = daily;
    }

    public ScheduleWeekly getWeekly() {
        return weekly;
    }

    public void setWeekly(ScheduleWeekly weekly) {
        this.weekly = weekly;
    }

    public ScheduleMonthly getMonthly() {
        return monthly;
    }

    public void setMonthly(ScheduleMonthly monthly) {
        this.monthly = monthly;
    }

    public ScheduleYearly getYearly() {
        return yearly;
    }

    public void setYearly(ScheduleYearly yearly) {
        this.yearly = yearly;
    }

    public String getCronExp() {
        return cronExp;
    }

    public void setCronExp(String cronExp) {
        this.cronExp = cronExp;
    }

    public String getParamSyncFilesId() {
        return paramSyncFilesId;
    }

    public void setParamSyncFilesId(String paramSyncFilesId) {
        this.paramSyncFilesId = paramSyncFilesId;
    }

    public Date getLastExecution() {
        return lastExecution;
    }

    public void setLastExecution(Date lastExecution) {
        this.lastExecution = lastExecution;
    }

    public SCHEDULE_TYPE getType() {
        return type;
    }

    public void setType(SCHEDULE_TYPE type) {
        this.type = type;
    }
}
