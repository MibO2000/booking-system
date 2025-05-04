package com.mibo2000.codigo.codetest.modal.repo;

import com.mibo2000.codigo.codetest.modal.entity.ClassEntity;
import com.mibo2000.codigo.codetest.modal.projection.ClassAvailabilityProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ClassRepo extends JpaRepository<ClassEntity, UUID> {

    @Query(value = """
             select c from ClassEntity c join c.country co
             where
            (:countryCode IS NULL OR :countryCode = '' OR co.countryCode = :countryCode) and
            (:className IS NULL OR :className = '' OR LOWER(c.className) LIKE LOWER(CONCAT('%', :className, '%')))
             """)
    Page<ClassEntity> findClassEntities(@Param("className") String className,
                                        @Param("countryCode") String countryCode,
                                        Pageable pageable);

    @Query(value = """
            SELECT
                c.class_id AS classId,
                c.class_name AS className,
                c.description AS description,
                c.require_credit AS requireCredit,
                c.class_date AS classDate,
                c.start_time AS startTime,
                c.end_time AS endTime,
                c.capacity AS capacity,
                (c.capacity - (
                    SELECT COUNT(*)
                    FROM booking_info b1
                    WHERE b1.class_id = c.class_id
                      AND b1.booking_status <> 'CANCELED'
                )) AS availableSlot,
                EXISTS (
                    SELECT 1 FROM booking_info b
                    WHERE b.class_id = c.class_id
                      AND b.user_id = :userId
                      AND b.booking_status <> 'CANCELED'
                ) AS booked,
                EXISTS (
                    SELECT 1 FROM waiting_list w
                    WHERE w.class_id = c.class_id
                      AND w.user_id = :userId
                ) AS inWaitingList
            FROM class_info c
            JOIN country co ON co.country_id = c.country_id
            WHERE
                (:countryCode IS NULL OR :countryCode = '' OR co.country_code = :countryCode)
                AND (:className IS NULL OR :className = '' OR LOWER(c.class_name) LIKE LOWER(CONCAT('%', :className, '%')))
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM class_info c
                    JOIN country co ON co.country_id = c.country_id
                    WHERE
                        (:countryCode IS NULL OR :countryCode = '' OR co.country_code = :countryCode)
                        AND (:className IS NULL OR :className = '' OR LOWER(c.class_name) LIKE LOWER(CONCAT('%', :className, '%')))
                    """,
            nativeQuery = true)
    Page<ClassAvailabilityProjection> findClassAvailabilityForUser(@Param("userId") UUID userId,
                                                                   @Param("className") String className,
                                                                   @Param("countryCode") String countryCode,
                                                                   Pageable pageable);

    @Query(value = """
            SELECT
                c.class_id AS classId,
                c.class_name AS className,
                c.description AS description,
                c.require_credit AS requireCredit,
                c.class_date AS classDate,
                c.start_time AS startTime,
                c.end_time AS endTime,
                c.capacity AS capacity,
                (c.capacity - (
                    SELECT COUNT(*)
                    FROM booking_info b1
                    WHERE b1.class_id = c.class_id
                      AND b1.booking_status <> 'CANCELED'
                )) AS availableSlot,
                EXISTS (
                    SELECT 1 FROM booking_info b
                    WHERE b.class_id = c.class_id
                      AND b.user_id = :userId
                      AND b.booking_status <> 'CANCELED'
                ) AS booked,
                EXISTS (
                    SELECT 1 FROM waiting_list w
                    WHERE w.class_id = c.class_id
                      AND w.user_id = :userId
                ) AS inWaitingList
            FROM class_info c
            JOIN country co ON co.country_id = c.country_id
            WHERE
                c.class_id = :classId
            """,
            nativeQuery = true)
    ClassAvailabilityProjection findClassAvailabilityForClassId(@Param("classId") UUID classId);
}
