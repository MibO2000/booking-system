package com.mibo2000.codigo.codetest.service.payment;

import com.mibo2000.codigo.codetest.modal.dto.user.UserPaymentRequest;
import com.mibo2000.codigo.codetest.modal.entity.UserPackageEntity;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImp implements PaymentService {

    @Override
    public boolean isInvalidPaymentRequest(UserPaymentRequest request) {
        return false;
    }

    @Override
    public boolean chargePayment(UserPackageEntity userPackage) {
        return true;
    }

    @Override
    public void reChargePayment(UserPackageEntity userPackage) {
    }
}
