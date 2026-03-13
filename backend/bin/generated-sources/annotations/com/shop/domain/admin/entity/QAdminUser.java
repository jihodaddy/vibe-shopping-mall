package com.shop.domain.admin.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAdminUser is a Querydsl query type for AdminUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAdminUser extends EntityPathBase<AdminUser> {

    private static final long serialVersionUID = 1567045389L;

    public static final QAdminUser adminUser = new QAdminUser("adminUser");

    public final com.shop.global.entity.QBaseTimeEntity _super = new com.shop.global.entity.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath password = createString("password");

    public final EnumPath<AdminRole> role = createEnum("role", AdminRole.class);

    public final EnumPath<AdminStatus> status = createEnum("status", AdminStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QAdminUser(String variable) {
        super(AdminUser.class, forVariable(variable));
    }

    public QAdminUser(Path<? extends AdminUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAdminUser(PathMetadata metadata) {
        super(AdminUser.class, metadata);
    }

}

