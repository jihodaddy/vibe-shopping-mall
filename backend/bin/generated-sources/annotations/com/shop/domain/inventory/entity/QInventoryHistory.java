package com.shop.domain.inventory.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QInventoryHistory is a Querydsl query type for InventoryHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInventoryHistory extends EntityPathBase<InventoryHistory> {

    private static final long serialVersionUID = 16810866L;

    public static final QInventoryHistory inventoryHistory = new QInventoryHistory("inventoryHistory");

    public final NumberPath<Integer> balanceAfter = createNumber("balanceAfter", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Integer> delta = createNumber("delta", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> optionId = createNumber("optionId", Long.class);

    public final NumberPath<Long> orderId = createNumber("orderId", Long.class);

    public final NumberPath<Long> productId = createNumber("productId", Long.class);

    public final EnumPath<InventoryType> type = createEnum("type", InventoryType.class);

    public QInventoryHistory(String variable) {
        super(InventoryHistory.class, forVariable(variable));
    }

    public QInventoryHistory(Path<? extends InventoryHistory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInventoryHistory(PathMetadata metadata) {
        super(InventoryHistory.class, metadata);
    }

}

