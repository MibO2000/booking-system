package com.mibo2000.codigo.codetest.service.mail;

import com.mibo2000.codigo.codetest.modal.entity.BookingInfoEntity;

public interface MailService {
    boolean sendVerificationCode(String email, String url);

    boolean sendOTPResetPasswordMail(String otp, String email, String resetPassword);

    void sendBookingInfoMailAfterWait(BookingInfoEntity bookingInfo);
}
