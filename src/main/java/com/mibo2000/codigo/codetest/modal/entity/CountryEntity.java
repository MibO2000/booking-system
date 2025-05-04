package com.mibo2000.codigo.codetest.modal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "country")
public class CountryEntity {
    @Id
    @Column(name = "country_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID countryId;

    @Column(name = "country_name", length = 100)
    private String countryName;

    @Column(name = "country_code", length = 2)
    private String countryCode;

    @Column(length = 100)
    private String currency;
}
