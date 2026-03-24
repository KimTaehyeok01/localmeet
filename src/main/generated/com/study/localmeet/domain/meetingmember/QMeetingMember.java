package com.study.localmeet.domain.meetingmember;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMeetingMember is a Querydsl query type for MeetingMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMeetingMember extends EntityPathBase<MeetingMember> {

    private static final long serialVersionUID = 1522133920L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMeetingMember meetingMember = new QMeetingMember("meetingMember");

    public final BooleanPath isApproved = createBoolean("isApproved");

    public final DateTimePath<java.time.LocalDateTime> joinedAt = createDateTime("joinedAt", java.time.LocalDateTime.class);

    public final com.study.localmeet.domain.meeting.QMeeting meeting;

    public final NumberPath<Long> mmIdx = createNumber("mmIdx", Long.class);

    public final com.study.localmeet.domain.user.QUsers users;

    public QMeetingMember(String variable) {
        this(MeetingMember.class, forVariable(variable), INITS);
    }

    public QMeetingMember(Path<? extends MeetingMember> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMeetingMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMeetingMember(PathMetadata metadata, PathInits inits) {
        this(MeetingMember.class, metadata, inits);
    }

    public QMeetingMember(Class<? extends MeetingMember> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.meeting = inits.isInitialized("meeting") ? new com.study.localmeet.domain.meeting.QMeeting(forProperty("meeting"), inits.get("meeting")) : null;
        this.users = inits.isInitialized("users") ? new com.study.localmeet.domain.user.QUsers(forProperty("users")) : null;
    }

}

