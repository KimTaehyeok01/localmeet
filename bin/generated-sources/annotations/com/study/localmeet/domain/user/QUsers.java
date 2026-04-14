package com.study.localmeet.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUsers is a Querydsl query type for Users
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUsers extends EntityPathBase<Users> {

    private static final long serialVersionUID = -709590815L;

    public static final QUsers users = new QUsers("users");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath userAddress = createString("userAddress");

    public final StringPath userEmail = createString("userEmail");

    public final NumberPath<Long> userIdx = createNumber("userIdx", Long.class);

    public final NumberPath<Double> userLat = createNumber("userLat", Double.class);

    public final NumberPath<Double> userLng = createNumber("userLng", Double.class);

    public final StringPath userNickname = createString("userNickname");

    public final StringPath userPassword = createString("userPassword");

    public final EnumPath<com.study.localmeet.enumeration.UserRole> userRole = createEnum("userRole", com.study.localmeet.enumeration.UserRole.class);

    public QUsers(String variable) {
        super(Users.class, forVariable(variable));
    }

    public QUsers(Path<? extends Users> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUsers(PathMetadata metadata) {
        super(Users.class, metadata);
    }

}

