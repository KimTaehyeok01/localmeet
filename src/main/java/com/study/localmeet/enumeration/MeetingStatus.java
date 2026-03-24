package com.study.localmeet.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MeetingStatus {
    OPEN("모집중"),
    FULL("모집완료"),
    CLOSED("종료");

    private final String value;
}
