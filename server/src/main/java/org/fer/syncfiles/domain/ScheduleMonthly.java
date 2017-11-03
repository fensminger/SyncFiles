package org.fer.syncfiles.domain;

import org.fer.syncfiles.dto.ScheduleCalc;
import org.fer.syncfiles.utils.ScheduleUtils;

import java.util.Date;

/**
 * Created by fensm on 12/01/2017.
 */
public class ScheduleMonthly {
    public enum SCHEDULE_MONTHLY_TYPE {NUMBER_DAY, WEEK_DAY};

    private SCHEDULE_MONTHLY_TYPE type;

    private int dayNumber;
    private int month;

    private int numberOfWeek;
    private String dayOfWeek;
    private int month2;

    private Date time;

    public ScheduleMonthly() {
        super();
    }

    public SCHEDULE_MONTHLY_TYPE getType() {
        return type;
    }

    public void setType(SCHEDULE_MONTHLY_TYPE type) {
        this.type = type;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getNumberOfWeek() {
        return numberOfWeek;
    }

    public void setNumberOfWeek(int numberOfWeek) {
        this.numberOfWeek = numberOfWeek;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getMonth2() {
        return month2;
    }

    public void setMonth2(int month2) {
        this.month2 = month2;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ScheduleMonthly{" +
                "type=" + type +
                ", dayNumber=" + dayNumber +
                ", month=" + month +
                ", numberOfWeek=" + numberOfWeek +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", month2=" + month2 +
                ", time=" + time +
                '}';
    }

    public ScheduleCalc calcCron(ScheduleCalc cronCalc) {
        String cronExp = null;

        switch (type) {
            case NUMBER_DAY:
                cronExp = " "+dayNumber+" 1/"+month+" ? *";
                break;
            case WEEK_DAY:
                cronExp = " ? 1/"+month2+" "+dayOfWeek+"#"+numberOfWeek+" *";
                break;
                default:
        }

        cronExp = ScheduleUtils.getCronExpTime(time, cronExp);
        cronCalc.getSchedule().setCronExp(cronExp);
        return cronCalc;
    }

}
