package org.fer.syncfiles.domain;

import org.fer.syncfiles.dto.ScheduleCalc;
import org.fer.syncfiles.utils.ScheduleUtils;

import java.util.Date;

/**
 * Created by fensm on 12/01/2017.
 */
public class ScheduleDaily {
    public enum SCHEDULE_DAILY_TYPE {DAY, WEEK_DAY, WEEK_END};

    private SCHEDULE_DAILY_TYPE type;

    private int day;
    private Date time;

    public ScheduleDaily() {
        super();
    }

    public SCHEDULE_DAILY_TYPE getType() {
        return type;
    }

    public void setType(SCHEDULE_DAILY_TYPE type) {
        this.type = type;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ScheduleDaily{" +
                "type=" + type +
                ", day=" + day +
                ", time=" + time +
                '}';
    }

    public ScheduleCalc calcCron(ScheduleCalc cronCalc) {
        String cronExp = null;
        switch (type) {
            case DAY:
                cronExp = " 1/"+day+" * ? *";
                break;
            case WEEK_DAY:
                cronExp = " ? * MON-FRI *";
                break;
            case WEEK_END:
                cronExp = " ? * SAT,SUN *";
                break;
            default:
        }
        cronExp = ScheduleUtils.getCronExpTime(time, cronExp);
        cronCalc.getSchedule().setCronExp(cronExp);
        return cronCalc;
    }

}
