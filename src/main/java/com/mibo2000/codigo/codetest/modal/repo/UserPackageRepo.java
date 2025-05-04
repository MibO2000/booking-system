package com.mibo2000.codigo.codetest.modal.repo;

import com.mibo2000.codigo.codetest.modal.entity.CountryEntity;
import com.mibo2000.codigo.codetest.modal.entity.PackageEntity;
import com.mibo2000.codigo.codetest.modal.entity.UserEntity;
import com.mibo2000.codigo.codetest.modal.entity.UserPackageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserPackageRepo extends JpaRepository<UserPackageEntity, UUID> {
    Optional<UserPackageEntity> findByUserPackageIdAndUser(UUID id, UserEntity user);

    Page<UserPackageEntity> findByUser(UserEntity user, Pageable pageable);

    Optional<UserPackageEntity> findByUserAndPackageEntity(UserEntity user, PackageEntity packageEntity);

    @Query(value = """
            select u from UserPackageEntity u
            join u.packageEntity p
            where u.user = :user and p.country = :country
            """)
    Optional<UserPackageEntity> findByUserAndCountry(@Param("user") UserEntity user,
                                                     @Param("country") CountryEntity country);
}
