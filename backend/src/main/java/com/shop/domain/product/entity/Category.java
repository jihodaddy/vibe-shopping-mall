package com.shop.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> children = new ArrayList<>();

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private byte depth = 1;

    @Column(nullable = false)
    private int sortOrder = 0;

    @Column(nullable = false)
    private boolean isActive = true;

    @Builder
    public Category(Category parent, String name, byte depth) {
        this.parent = parent;
        this.name = name;
        this.depth = depth;
    }

    public void update(String name, int sortOrder, boolean isActive) {
        this.name = name;
        this.sortOrder = sortOrder;
        this.isActive = isActive;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
