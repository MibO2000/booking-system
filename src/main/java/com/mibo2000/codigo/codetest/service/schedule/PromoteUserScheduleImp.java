package com.mibo2000.codigo.codetest.service.schedule;

import com.mibo2000.codigo.codetest.job.PromoteUserJob;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PromoteUserScheduleImp implements PromoteUserSchedule{
    private final Scheduler scheduler;
    @Override
    public void schedulePromoteUserJob(UUID classId) {
        JobDetail jobDetail = JobBuilder.newJob(PromoteUserJob.class)
                .withIdentity("promoteJob-" + classId, "booking")
                .usingJobData("classId", classId.toString())
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("promoteTrigger-" + classId, "booking")
                .startNow()
                .build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to schedule promote user job", e);
        }
    }
}
