package com.mibo2000.codigo.codetest.modal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "package")
public class PackageEntity extends AbstractEntity{
    @Id
    @Column(name = "package_id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID packageId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id", nullable = false)
    private CountryEntity country;

    @Column(name = "package_name", length = 255)
    private String packageName;

    @Column(name = "credit_amount")
    private int creditAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "expiry_days")
    private int expiryDays;
}
