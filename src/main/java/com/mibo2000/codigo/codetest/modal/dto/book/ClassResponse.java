package com.mibo2000.codigo.codetest.modal.dto.book;

import com.mibo2000.codigo.codetest.modal.dto.pub.CountryDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
public class ClassResponse {
    private UUID classId;
    private CountryDto country;
    private String className;
    private String description;
    private int requireCredit;
    private LocalDate classDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int capacity;
    private LocalDateTime createdAt;
}
