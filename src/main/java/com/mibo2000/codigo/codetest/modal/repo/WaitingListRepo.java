package com.mibo2000.codigo.codetest.modal.repo;

import com.mibo2000.codigo.codetest.modal.entity.ClassEntity;
import com.mibo2000.codigo.codetest.modal.entity.UserEntity;
import com.mibo2000.codigo.codetest.modal.entity.UserPackageEntity;
import com.mibo2000.codigo.codetest.modal.entity.WaitingListEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WaitingListRepo extends JpaRepository<WaitingListEntity, UUID> {

    @Query(value = """
            select w.userPackage from WaitingListEntity w join w.classInfo c
            where c.classId = :classId and w.refunded = :refunded
            """)
    List<UserPackageEntity> findUserPackagesByClassIdAndRefund(@Param("classId") UUID classId,
                                                               @Param("refunded") boolean refunded);


    @Query(value = """
            select w from WaitingListEntity w join w.classInfo c join w.user u
            where u.id = :userId and c.classId = :classId and w.refunded = :refunded
            """)
    Optional<WaitingListEntity> findByUserIdAndClassId(@Param("userId") UUID userId,
                                                       @Param("classId") UUID classId,
                                                       @Param("refunded") boolean refunded);

    Optional<WaitingListEntity> findByWaitingListIdAndUser(UUID waitId, UserEntity user);

    Page<WaitingListEntity> findByUser(UserEntity user, Pageable pageable);

    boolean existsByClassInfoAndRefunded(ClassEntity classEntity, boolean refunded);
}
