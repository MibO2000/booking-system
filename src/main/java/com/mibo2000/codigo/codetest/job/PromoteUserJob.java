package com.mibo2000.codigo.codetest.job;

import com.mibo2000.codigo.codetest.modal.EnumPool;
import com.mibo2000.codigo.codetest.modal.entity.BookingInfoEntity;
import com.mibo2000.codigo.codetest.modal.entity.WaitingListEntity;
import com.mibo2000.codigo.codetest.modal.repo.BookingInfoRepo;
import com.mibo2000.codigo.codetest.modal.repo.WaitingListRepo;
import com.mibo2000.codigo.codetest.service.mail.MailService;
import com.mibo2000.codigo.codetest.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PromoteUserJob implements Job {
    private final WaitingListRepo waitingListRepo;
    private final RedisService redisService;
    private final BookingInfoRepo bookingInfoRepo;
    private final MailService mailService;

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getMergedJobDataMap();
        UUID classId = UUID.fromString(dataMap.getString("classId"));

        Optional<String> nextValidUser = Optional.empty();

        while ((nextValidUser = this.redisService.popNextUserFromWaitingList(classId)).isPresent()) {
            UUID userId = UUID.fromString(nextValidUser.get());

            Optional<WaitingListEntity> waitingListOpt = this.waitingListRepo.findByUserIdAndClassId(userId, classId, false);
            if (waitingListOpt.isPresent()) {
                WaitingListEntity waitingList = waitingListOpt.get();
                BookingInfoEntity bookingInfo = BookingInfoEntity
                        .builder()
                        .classInfo(waitingList.getClassInfo())
                        .bookedAt(LocalDateTime.now())
                        .userPackage(waitingList.getUserPackage())
                        .user(waitingList.getUser())
                        .bookingStatus(EnumPool.BookingStatus.BOOKED)
                        .build();
                this.bookingInfoRepo.save(bookingInfo);
                this.mailService.sendBookingInfoMailAfterWait(bookingInfo);
                break;
            }
        }
    }
}
