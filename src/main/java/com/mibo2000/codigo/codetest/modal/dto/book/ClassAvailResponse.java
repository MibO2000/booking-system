package com.mibo2000.codigo.codetest.modal.dto.book;

import com.mibo2000.codigo.codetest.modal.projection.ClassAvailabilityProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassAvailResponse {
    UUID classId;
    String className;
    String description;
    int requireCredit;
    LocalDate classDate;
    LocalTime startTime;
    LocalTime endTime;
    int capacity;
    Integer availableSlot;
    boolean isBooked;
    boolean isInWaitingList;
    Boolean isTimeSlotFreeToBook;
    Boolean isTimeSlotFreeToWaitList;

    public ClassAvailResponse(ClassAvailabilityProjection p) {
        this.classId = p.getClassId();
        this.className = p.getClassName();
        this.description = p.getDescription();
        this.requireCredit = p.getRequireCredit();
        this.classDate = p.getClassDate();
        this.startTime = p.getStartTime();
        this.endTime = p.getEndTime();
        this.capacity = p.getCapacity();
        this.availableSlot = p.getAvailableSlot();
        this.isBooked = p.isBooked();
        this.isInWaitingList = p.isInWaitingList();
        this.isTimeSlotFreeToBook = null;
        this.isTimeSlotFreeToWaitList = null;
    }
}
