package com.mibo2000.codigo.codetest.service.schedule;

import com.mibo2000.codigo.codetest.job.PackageExpiryReminderJob;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PackageExpiryReminderSchedulerImp implements PackageExpiryReminderScheduler{
    private final Scheduler scheduler;

    @Override
    public void upsertPackageReminder(UUID userId, UUID packageId, LocalDate expiryDate) throws SchedulerException {
        String jobKeyStr = "package-expiry-" + userId + "-" + packageId;
        String triggerKeyStr = "trigger-package-expiry-" + userId + "-" + packageId;

        JobKey jobKey = JobKey.jobKey(jobKeyStr);
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerKeyStr);

        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }

        JobDetail job = JobBuilder.newJob(PackageExpiryReminderJob.class)
                .withIdentity(jobKey)
                .usingJobData("userId", userId.toString())
                .usingJobData("packageId", packageId.toString())
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .startAt(java.sql.Date.valueOf(expiryDate.minusDays(1)))
                .build();

        scheduler.scheduleJob(job, trigger);
    }
}
