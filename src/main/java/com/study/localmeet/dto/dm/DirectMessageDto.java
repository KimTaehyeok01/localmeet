package com.study.localmeet.dto.dm;

import com.study.localmeet.domain.dm.DirectMessage;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DirectMessageDto {
    private final Long dmIdx;
    private final Long senderIdx;
    private final String senderNickname;
    private final String dmContent;
    private final LocalDateTime createdAt;

    public DirectMessageDto(DirectMessage dm) {
        this.dmIdx          = dm.getDmIdx();
        this.senderIdx      = dm.getSender().getUserIdx();
        this.senderNickname = dm.getSender().getUserNickname();
        this.dmContent      = dm.getDmContent();
        this.createdAt      = dm.getCreatedAt();
    }
}
