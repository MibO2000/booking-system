package com.mibo2000.codigo.codetest.modal.repo;

import com.mibo2000.codigo.codetest.modal.entity.BookingInfoEntity;
import com.mibo2000.codigo.codetest.modal.entity.ClassEntity;
import com.mibo2000.codigo.codetest.modal.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingInfoRepo extends JpaRepository<BookingInfoEntity, UUID> {

    @Query(value = """
            select b.user.id from BookingInfoEntity b join b.classInfo c  where c.classId = :classId
            """)
    List<UUID> findUserIdsByClassId(@Param("classId") UUID classId);

    @Query(value = """
            SELECT COUNT(*) > 0
            FROM booking_info b
            JOIN class_info c ON b.class_id = c.class_id
            WHERE b.user_id = :userId
              AND b.booking_status <> 'CANCELED'
              AND c.class_date = :classDate
              AND c.start_time < :newEndTime
              AND :newStartTime < c.end_time
            """, nativeQuery = true)
    boolean existsBookingConflict(@Param("userId") UUID userId,
                                  @Param("classDate") LocalDate classDate,
                                  @Param("newStartTime") LocalTime newStartTime,
                                  @Param("newEndTime") LocalTime newEndTime);

    boolean existsByUserAndClassInfo(UserEntity user, ClassEntity classEntity);

    @Query("SELECT COUNT(b) FROM BookingInfoEntity b WHERE b.classInfo = :class AND b.bookingStatus <> 'CANCELED'")
    long countByClassInfo(@Param("class") ClassEntity classEntity);

    Optional<BookingInfoEntity> findByBookingIdAndUser(UUID id, UserEntity user);

    Page<BookingInfoEntity> findByUser(UserEntity user, Pageable pageable);
}
