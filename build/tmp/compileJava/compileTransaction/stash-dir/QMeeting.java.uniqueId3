package com.study.localmeet.domain.meeting;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMeeting is a Querydsl query type for Meeting
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMeeting extends EntityPathBase<Meeting> {

    private static final long serialVersionUID = -1654952948L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMeeting meeting = new QMeeting("meeting");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath meetingAddress = createString("meetingAddress");

    public final StringPath meetingContent = createString("meetingContent");

    public final NumberPath<Long> meetingIdx = createNumber("meetingIdx", Long.class);

    public final NumberPath<Double> meetingLat = createNumber("meetingLat", Double.class);

    public final NumberPath<Double> meetingLng = createNumber("meetingLng", Double.class);

    public final NumberPath<Integer> meetingMax = createNumber("meetingMax", Integer.class);

    public final EnumPath<com.study.localmeet.enumeration.MeetingStatus> meetingStatus = createEnum("meetingStatus", com.study.localmeet.enumeration.MeetingStatus.class);

    public final StringPath meetingTitle = createString("meetingTitle");

    public final com.study.localmeet.domain.user.QUsers users;

    public QMeeting(String variable) {
        this(Meeting.class, forVariable(variable), INITS);
    }

    public QMeeting(Path<? extends Meeting> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMeeting(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMeeting(PathMetadata metadata, PathInits inits) {
        this(Meeting.class, metadata, inits);
    }

    public QMeeting(Class<? extends Meeting> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.users = inits.isInitialized("users") ? new com.study.localmeet.domain.user.QUsers(forProperty("users")) : null;
    }

}

