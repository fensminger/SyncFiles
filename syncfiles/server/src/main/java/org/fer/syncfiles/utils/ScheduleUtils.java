package org.fer.syncfiles.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by fensm on 19/01/2017.
 */
public class ScheduleUtils {
    public static String getCronExpTime(Date date, String cronExp) {
        if (date==null) {
            cronExp = "0 0 0" + cronExp;
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            cronExp = "0 " + calendar.get(Calendar.MINUTE) + " " + calendar.get(Calendar.HOUR_OF_DAY) + cronExp;
        }
        return cronExp;
    }
}
