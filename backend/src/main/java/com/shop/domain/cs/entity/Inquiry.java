package com.shop.domain.cs.entity;

import com.shop.domain.member.entity.Member;
import com.shop.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inquiries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "order_id")
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InquiryType type;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InquiryStatus status = InquiryStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String answer;

    private java.time.LocalDateTime answeredAt;

    @Column(nullable = false)
    private boolean isSecret = false;

    @OneToMany(mappedBy = "inquiry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InquiryAnswer> answers = new ArrayList<>();

    @Builder
    public Inquiry(Member member, Long productId, Long orderId, InquiryType type,
                   String title, String content, boolean isSecret) {
        this.member = member;
        this.productId = productId;
        this.orderId = orderId;
        this.type = type;
        this.title = title;
        this.content = content;
        this.isSecret = isSecret;
    }

    public void markAnswered() {
        this.status = InquiryStatus.ANSWERED;
    }

    public void close() {
        this.status = InquiryStatus.CLOSED;
    }
}
