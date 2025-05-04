package com.mibo2000.codigo.codetest.modal.repo;

import com.mibo2000.codigo.codetest.modal.entity.PackagePurchaseHistoryEntity;
import com.mibo2000.codigo.codetest.modal.entity.UserPackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PackagePurchaseHistoryRepo extends JpaRepository<PackagePurchaseHistoryEntity, UUID> {
    List<PackagePurchaseHistoryEntity> findByUserPackageOrderByPurchaseDateDesc(UserPackageEntity userPackage);
}
