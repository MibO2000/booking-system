package com.mibo2000.codigo.codetest.service.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
@Slf4j
public class RedisServiceImp implements RedisService{
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void saveOtpAndInitRetry(String userId, String otpCode, long ttlSeconds) {
        String key = "otp:" + userId;

        Map<String, String> data = new HashMap<>();
        data.put("otp", otpCode);
        data.put("retry", "0");

        redisTemplate.opsForHash().putAll(key, data);
        redisTemplate.expire(key, Duration.ofSeconds(ttlSeconds));
    }

    @Override
    public boolean isOtpNotStored(String userId) {
        String key = "otp:" + userId;
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            log.error("OTP does not exist or expired for user: " + userId);
            return true;
        }
        return false;
    }

    @Override
    public boolean incrementRetryAndCheck(String userId, String providedOtp, int maxRetries) {
        String key = "otp:" + userId;

        String storedOtp = (String) redisTemplate.opsForHash().get(key, "otp");
        String retryStr = (String) redisTemplate.opsForHash().get(key, "retry");

        int retry = Integer.parseInt(retryStr);

        if (retry >= maxRetries) {
            return false;
        }

        if (storedOtp.equals(providedOtp)) {
            redisTemplate.delete(key);
            return true;
        } else {
            redisTemplate.opsForHash().increment(key, "retry", 1);
            return false;
        }
    }

    @Override
    public boolean isBookingAvailable(UUID classId) {
        Long remaining = redisTemplate.opsForValue().decrement("class_capacity:" + classId);
        if (remaining >= 0) {
            return true;
        } else {
            redisTemplate.opsForValue().set("class_capacity:" + classId, "0");
            return false;
        }
    }

    @Override
    public Long getBookingCount(UUID classId) {
        return Long.valueOf((String) redisTemplate.opsForValue().get("class_capacity:" + classId));
    }

    @Override
    public boolean attemptBooking(UUID classId) {
        Long remaining = redisTemplate.opsForValue().decrement("class_capacity:" + classId);
        if (remaining >= 0) {
            return true;
        } else {
            redisTemplate.opsForValue().set("class_capacity:" + classId, "0");
            return false;
        }
    }

    @Override
    public void reAddCountInBooking(UUID classId) {
        Long remaining = redisTemplate.opsForValue().increment("class_capacity:" + classId);
        redisTemplate.opsForValue().set("class_capacity:" + classId, String.valueOf(remaining));
    }

    @Override
    public void addToWaitingList(UUID classId, UUID userId) {
        String streamKey = "waiting_list:class:" + classId;
        Map<String, String> data = new HashMap<>();
        data.put("userId", userId.toString());
        redisTemplate.opsForStream().add(streamKey, data);
    }

    @Override
    public Optional<String> popNextUserFromWaitingList(UUID classId) {
        String streamKey = "waiting_list:class:" + classId;
        List<MapRecord<String, Object, Object>> messages = redisTemplate.opsForStream().read(StreamReadOptions.empty().count(1), StreamOffset.fromStart(streamKey));

        if (!messages.isEmpty()) {
            MapRecord<String, Object, Object> record = messages.getFirst();
            String userId = (String) record.getValue().get("userId");
            redisTemplate.opsForStream().delete(streamKey, record.getId());
            return Optional.of(userId);
        }
        return Optional.empty();
    }
}
