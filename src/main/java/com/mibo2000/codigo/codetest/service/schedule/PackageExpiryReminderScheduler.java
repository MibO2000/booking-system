package com.mibo2000.codigo.codetest.service.schedule;

import org.quartz.SchedulerException;

import java.time.LocalDate;
import java.util.UUID;

public interface PackageExpiryReminderScheduler {
    void upsertPackageReminder(UUID userId, UUID packageId, LocalDate expiryDate) throws SchedulerException;
}
