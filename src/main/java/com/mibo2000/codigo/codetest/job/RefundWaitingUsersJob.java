package com.mibo2000.codigo.codetest.job;

import com.mibo2000.codigo.codetest.modal.entity.UserPackageEntity;
import com.mibo2000.codigo.codetest.modal.repo.WaitingListRepo;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefundWaitingUsersJob implements Job {
    final WaitingListRepo waitingListRepo;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        UUID classId = UUID.fromString(jobExecutionContext.getMergedJobDataMap().getString("classId"));
        List<UserPackageEntity> userPackages = waitingListRepo.findUserPackagesByClassIdAndRefund(classId, false);
        /*refundService.processRefunds(users);*/
    }
}
