package com.shop.domain.admin.entity;

import com.shop.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admin_users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminUser extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdminRole role = AdminRole.ADMIN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdminStatus status = AdminStatus.ACTIVE;

    @Builder
    public AdminUser(String email, String password, String name, AdminRole role, AdminStatus status) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role != null ? role : AdminRole.ADMIN;
        this.status = status != null ? status : AdminStatus.ACTIVE;
    }

    public boolean isActive() {
        return this.status == AdminStatus.ACTIVE;
    }
}
