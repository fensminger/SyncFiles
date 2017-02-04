package org.fer.syncfiles.domain;

import org.fer.syncfiles.dto.ScheduleCalc;

/**
 * Created by fensm on 12/01/2017.
 */
public class ScheduleMinutes {
    private int minute;
    private int start;

    public ScheduleMinutes() {
        super();
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    @Override
    public String toString() {
        return "ScheduleMinutes{" +
                "minute=" + minute +
                ", start=" + start +
                '}';
    }

    public ScheduleCalc calcCron(ScheduleCalc cronCalc) {
        String cronExp = "0 " + start + "/" + minute + " * 1/1 * ? *";
        cronCalc.getSchedule().setCronExp(cronExp);
        return cronCalc;
    }
}
