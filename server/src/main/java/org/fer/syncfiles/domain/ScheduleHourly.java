package org.fer.syncfiles.domain;

import org.fer.syncfiles.dto.ScheduleCalc;

/**
 * Created by fensm on 12/01/2017.
 */
public class ScheduleHourly {
    private int hour;
    private int minute;

    public ScheduleHourly() {
        super();
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    @Override
    public String toString() {
        return "ScheduleHourly{" +
                "hour=" + hour +
                ", minute=" + minute +
                '}';
    }

    public ScheduleCalc calcCron(ScheduleCalc cronCalc) {
        String cronExp = "0 "+minute+" 0/"+hour+" 1/1 * ? *";
        cronCalc.getSchedule().setCronExp(cronExp);
        return cronCalc;
    }
}
