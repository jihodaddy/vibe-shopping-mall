package com.shop.domain.member.entity;

import com.shop.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberGrade grade = MemberGrade.BRONZE;

    @Column(nullable = false)
    private int point = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status = MemberStatus.ACTIVE;

    @Builder
    public Member(String email, String password, String name, String phone) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
    }

    public void addPoint(int amount) {
        this.point += amount;
    }

    public void usePoint(int amount) {
        this.point -= amount;
    }

    public void changeGrade(MemberGrade grade) {
        this.grade = grade;
    }

    public void deactivate() {
        this.status = MemberStatus.INACTIVE;
    }

    public void updateStatus(MemberStatus status) {
        this.status = status;
    }
}
