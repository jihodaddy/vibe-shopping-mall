package com.shop.domain.order.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOrderShipping is a Querydsl query type for OrderShipping
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrderShipping extends EntityPathBase<OrderShipping> {

    private static final long serialVersionUID = 1870401104L;

    public static final QOrderShipping orderShipping = new QOrderShipping("orderShipping");

    public final StringPath courier = createString("courier");

    public final DateTimePath<java.time.LocalDateTime> deliveredAt = createDateTime("deliveredAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> orderId = createNumber("orderId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> shippedAt = createDateTime("shippedAt", java.time.LocalDateTime.class);

    public final StringPath trackingNumber = createString("trackingNumber");

    public QOrderShipping(String variable) {
        super(OrderShipping.class, forVariable(variable));
    }

    public QOrderShipping(Path<? extends OrderShipping> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOrderShipping(PathMetadata metadata) {
        super(OrderShipping.class, metadata);
    }

}

