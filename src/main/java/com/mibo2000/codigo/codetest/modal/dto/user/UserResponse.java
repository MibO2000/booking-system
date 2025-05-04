package com.mibo2000.codigo.codetest.modal.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String username;
    private String fullName;
    private String email;
    private String paymentMethod;
}
