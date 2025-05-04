package com.mibo2000.codigo.codetest.job;

import com.mibo2000.codigo.codetest.service.mail.MailService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PackageExpiryReminderJob implements Job {
    @Autowired
    private MailService emailService;

    @Override
    public void execute(JobExecutionContext context) {
        UUID userId = UUID.fromString(context.getMergedJobDataMap().getString("userId"));
        UUID packageId = UUID.fromString(context.getMergedJobDataMap().getString("packageId"));
        /*emailService.sendPackageExpiryReminder(userId, packageId);*/
    }
}
