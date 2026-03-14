package com.shop.domain.member.repository;

import com.shop.domain.member.entity.Member;
import com.shop.domain.member.entity.MemberGrade;
import com.shop.domain.member.entity.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByStatus(MemberStatus status);

    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT m FROM Member m WHERE " +
           "(:keyword IS NULL OR m.email LIKE %:keyword% OR m.name LIKE %:keyword%) AND " +
           "(:grade IS NULL OR m.grade = :grade) AND " +
           "(:status IS NULL OR m.status = :status)")
    Page<Member> findByCondition(
        @Param("keyword") String keyword,
        @Param("grade") MemberGrade grade,
        @Param("status") MemberStatus status,
        Pageable pageable
    );
}
