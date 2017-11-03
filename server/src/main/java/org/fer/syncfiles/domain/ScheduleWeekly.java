package org.fer.syncfiles.domain;

import org.fer.syncfiles.dto.ScheduleCalc;
import org.fer.syncfiles.utils.ScheduleUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by fensm on 12/01/2017.
 */
public class ScheduleWeekly {
    private List<String> days;
    private Date time;

    public ScheduleWeekly() {
        super();
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ScheduleWeekly{" +
                "days=" + days +
                ", time=" + time +
                '}';
    }

    public ScheduleCalc calcCron(ScheduleCalc cronCalc) {
        String cronExp = null;

        if (days==null || days.size()==0) {
            cronExp = " ? * ? *";
            cronCalc.setMsgError("The scheduling is invalid : no day is specified.");
        } else {
            StringBuilder dayOfWeek = new StringBuilder();
            boolean isFirst = true;
            for(String day : days) {
                if (isFirst) {
                    isFirst=false;
                } else {
                    dayOfWeek.append(",");
                }
                dayOfWeek.append(day);
            }
            cronExp = " ? * " + dayOfWeek.toString() + " *";
        }

        cronExp = ScheduleUtils.getCronExpTime(time, cronExp);
        cronCalc.getSchedule().setCronExp(cronExp);
        return cronCalc;
    }
}
