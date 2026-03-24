package com.study.localmeet.domain.chat;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChatMessage is a Querydsl query type for ChatMessage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatMessage extends EntityPathBase<ChatMessage> {

    private static final long serialVersionUID = 1471773109L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChatMessage chatMessage = new QChatMessage("chatMessage");

    public final StringPath chatContent = createString("chatContent");

    public final NumberPath<Long> chatIdx = createNumber("chatIdx", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final com.study.localmeet.domain.meeting.QMeeting meeting;

    public final com.study.localmeet.domain.user.QUsers users;

    public QChatMessage(String variable) {
        this(ChatMessage.class, forVariable(variable), INITS);
    }

    public QChatMessage(Path<? extends ChatMessage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChatMessage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChatMessage(PathMetadata metadata, PathInits inits) {
        this(ChatMessage.class, metadata, inits);
    }

    public QChatMessage(Class<? extends ChatMessage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.meeting = inits.isInitialized("meeting") ? new com.study.localmeet.domain.meeting.QMeeting(forProperty("meeting"), inits.get("meeting")) : null;
        this.users = inits.isInitialized("users") ? new com.study.localmeet.domain.user.QUsers(forProperty("users")) : null;
    }

}

