package com.mibo2000.codigo.codetest.modal.repo;

import com.mibo2000.codigo.codetest.modal.entity.PackageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PackageRepo extends JpaRepository<PackageEntity, UUID> {
    @Query(value = """
            select p from PackageEntity p join p.country c
            where (:packageName IS NULL OR :packageName = '' OR LOWER(p.packageName) LIKE LOWER(CONCAT('%', :packageName, '%')))
            and (:countryCode IS NULL OR :countryCode = '' OR c.countryCode = :countryCode)
            """)
    Page<PackageEntity> getPackagePage(@Param("packageName") String packageName,
                                       @Param("countryCode") String countryCode,
                                       Pageable pageable);
}
