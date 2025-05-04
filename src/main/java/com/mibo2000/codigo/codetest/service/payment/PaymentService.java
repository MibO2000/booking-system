package com.mibo2000.codigo.codetest.service.payment;

import com.mibo2000.codigo.codetest.modal.dto.user.UserPaymentRequest;
import com.mibo2000.codigo.codetest.modal.entity.UserPackageEntity;

public interface PaymentService {
    boolean isInvalidPaymentRequest(UserPaymentRequest request);

    boolean chargePayment(UserPackageEntity userPackage);

    void reChargePayment(UserPackageEntity userPackage);
}
