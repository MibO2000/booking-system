package com.mibo2000.codigo.codetest.service.mail;

import com.mibo2000.codigo.codetest.modal.entity.BookingInfoEntity;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImp implements MailService{

    @Override
    public boolean sendVerificationCode(String email, String url) {
        return true;
    }

    @Override
    public boolean sendOTPResetPasswordMail(String otp, String email, String resetPassword) {
        return true;
    }

    @Override
    public void sendBookingInfoMailAfterWait(BookingInfoEntity bookingInfo) {

    }
}
