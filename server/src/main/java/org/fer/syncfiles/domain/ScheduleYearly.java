package org.fer.syncfiles.domain;

import org.fer.syncfiles.dto.ScheduleCalc;
import org.fer.syncfiles.utils.ScheduleUtils;

import java.util.Date;

/**
 * Created by fensm on 12/01/2017.
 */
public class ScheduleYearly {
    public enum SCHEDULE_YEARLY_TYPE {EVERY, NUMBER};

    private SCHEDULE_YEARLY_TYPE type;

    private String month;
    private int dayNumber;

    private int numberInMonth;
    private String dayOfWeek;
    private String numberOfMonth;

    private Date time;

    public ScheduleYearly() {
        super();
    }

    public ScheduleCalc calcCron(ScheduleCalc cronCalc) {
        String cronExp = null;

        switch (type) {
            case EVERY:
                cronExp = " "+dayNumber+" "+month+" ? *";
                break;
            case NUMBER:
                cronExp = " ? "+numberOfMonth+" "+dayOfWeek+"#"+numberInMonth+" *";
                break;
            default:
        }

        cronExp = ScheduleUtils.getCronExpTime(time, cronExp);
        cronCalc.getSchedule().setCronExp(cronExp);
        return cronCalc;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public SCHEDULE_YEARLY_TYPE getType() {
        return type;
    }

    public void setType(SCHEDULE_YEARLY_TYPE type) {
        this.type = type;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public int getNumberInMonth() {
        return numberInMonth;
    }

    public void setNumberInMonth(int numberInMonth) {
        this.numberInMonth = numberInMonth;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getNumberOfMonth() {
        return numberOfMonth;
    }

    public void setNumberOfMonth(String numberOfMonth) {
        this.numberOfMonth = numberOfMonth;
    }
}
