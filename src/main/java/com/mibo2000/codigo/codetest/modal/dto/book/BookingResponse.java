package com.mibo2000.codigo.codetest.modal.dto.book;

import com.mibo2000.codigo.codetest.modal.EnumPool;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BookingResponse {
    private UUID bookingId;
    private ClassResponse classInfo;
    private EnumPool.BookingStatus bookingStatus;
    private LocalDateTime bookedAt;
    private LocalDateTime canceledAt;
}
