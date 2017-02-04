package org.fer.syncfiles.dto;

import org.fer.syncfiles.domain.Schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by fensm on 16/01/2017.
 */
public class ScheduleCalc {
    private String msgError;
    private String description;
    private List<Date> nextCron;
    private Schedule schedule;

    public ScheduleCalc() {
        super();
    }

    public String getMsgError() {
        return msgError;
    }

    public void setMsgError(String msgError) {
        this.msgError = msgError;
    }

    public List<Date> getNextCron() {
        return nextCron;
    }

    public void setNextCron(List<Date> nextCron) {
        this.nextCron = nextCron;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
