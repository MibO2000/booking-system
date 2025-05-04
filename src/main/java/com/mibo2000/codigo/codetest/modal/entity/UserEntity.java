package com.mibo2000.codigo.codetest.modal.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "class_user")
public class UserEntity extends AbstractEntity implements UserDetails {
    @Id
    @Column(name = "user_id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    @Column(name = "full_name", nullable = false, unique = true, length = 50)
    private String fullName;
    @Column(name = "email", nullable = false, unique = true, length = 254)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "verified")
    private Boolean verified;
    @Column(name = "verification_code")
    private String verificationCode;
    @Column(name = "payment_method")
    private String paymentMethod;
    @Column(name = "payment_info")
    private String paymentInfo;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}
