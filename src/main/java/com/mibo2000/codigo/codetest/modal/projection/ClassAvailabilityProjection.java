package com.mibo2000.codigo.codetest.modal.projection;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public interface ClassAvailabilityProjection {
    UUID getClassId();
    String getClassName();
    String getDescription();
    int getRequireCredit();
    LocalDate getClassDate();
    LocalTime getStartTime();
    LocalTime getEndTime();
    int getCapacity();
    Integer getAvailableSlot();
    boolean isBooked();
    boolean isInWaitingList();
}
