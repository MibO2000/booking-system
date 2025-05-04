package com.mibo2000.codigo.codetest.modal.dto.book;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class WaitListResponse {
    private UUID waitingListId;
    private ClassResponse classInfo;
    private LocalDateTime joinedAt;
    private boolean refunded;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
