package com.mibo2000.codigo.codetest.service.redis;

import java.util.Optional;
import java.util.UUID;

public interface RedisService {
    void saveOtpAndInitRetry(String userId, String otpCode, long ttlSeconds);

    boolean isOtpNotStored(String userId);

    boolean incrementRetryAndCheck(String userId, String providedOtp, int maxRetries);

    boolean isBookingAvailable(UUID classId);

    Long getBookingCount(UUID classId);

    boolean attemptBooking(UUID classId);

    void reAddCountInBooking(UUID classId);

    void addToWaitingList(UUID classId, UUID userId);

    Optional<String> popNextUserFromWaitingList(UUID classId);
}
