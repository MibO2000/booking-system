package com.mibo2000.codigo.codetest.job;

import com.mibo2000.codigo.codetest.modal.repo.BookingInfoRepo;
import com.mibo2000.codigo.codetest.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotifyUsersBeforeClassJob implements Job {
    final MailService mailService;
    final BookingInfoRepo bookingInfoRepo;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        UUID classId = UUID.fromString(jobExecutionContext.getMergedJobDataMap().getString("classId"));
        List<UUID> userIds = bookingInfoRepo.findUserIdsByClassId(classId);
        /*mailService.sendClassReminderEmails(userIds, classId);*/
    }
}
