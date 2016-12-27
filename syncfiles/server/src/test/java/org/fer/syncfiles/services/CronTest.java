package org.fer.syncfiles.services;

import static com.cronutils.model.CronType.QUARTZ;
import static com.cronutils.model.field.expression.FieldExpressionFactory.*;

import com.cronutils.builder.CronBuilder;
import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.field.value.SpecialChar;
import com.cronutils.parser.CronParser;
import org.junit.Test;
import org.quartz.CronTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.CronScheduleBuilder.*;
import static org.quartz.DateBuilder.*;

import java.util.Date;
import java.util.Locale;

/**
 * Created by fensm on 24/12/2016.
 */
public class CronTest {
    private final Logger log = LoggerFactory.getLogger(CronTest.class);


    @Test
    public void cronTest() {
        CronDefinition cronDefinition =
                CronDefinitionBuilder.defineCron()
                        .withSeconds().and()
                        .withMinutes().and()
                        .withHours().and()
                        .withDayOfMonth()
                        .supportsHash().supportsL().supportsW().and()
                        .withMonth().and()
                        .withDayOfWeek()
                        .withIntMapping(7, 0) //we support non-standard non-zero-based numbers!
                        .supportsHash().supportsL().supportsW().and()
                        .withYear().and()
                        .lastFieldOptional()
                        .instance();

// or get a predefined instance
        cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(QUARTZ);

        Cron cron = CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(QUARTZ))
                .withYear(always())
                .withDoM(between(SpecialChar.L, 3))
                .withMonth(always())
                .withDoW(questionMark())
                .withHour(always())
                .withMinute(always())
                .withSecond(on(0))
                .instance();
        // Obtain the string expression
        String cronAsString = cron.asString(); // 0 * * L-3 * ? *
        log.info(cronAsString);

        CronParser parser = new CronParser(cronDefinition);
        Cron quartzCron = parser.parse("0 23 * ? * 1-5 *");
        log.info(quartzCron.asString());


// Create a descriptor for a specific Locale
        CronDescriptor descriptor = CronDescriptor.instance(Locale.UK);

// Parse some expression and ask descriptor for description
        String description = descriptor.describe(parser.parse("*/45 * * * * ?"));
// Description will be: "every 45 seconds"

        description = descriptor.describe(quartzCron);
// Description will be: "every hour at minute 23 every day between Monday and Friday"
// which is the same description we get for the cron below:
        String res = descriptor.describe(parser.parse("0 23 * ? * MON-FRI *"));
        log.info(res);
        res = descriptor.describe(parser.parse("0 * * L-3 * ? *"));
        log.info(res);

        Date currTime = new Date();
        CronTrigger tr = newTrigger()
                .withIdentity("trigger3", "group1")
                .withSchedule(cronSchedule("0 0 23 3,18 * ? *"))
                .forJob("myJob", "group1")
                .build();
        Date nextFireAt = tr.getFireTimeAfter(currTime);
        System.out.println("Reference time: " + currTime);
        System.out.println("Next fire after reference time: " + nextFireAt);
    }



}
