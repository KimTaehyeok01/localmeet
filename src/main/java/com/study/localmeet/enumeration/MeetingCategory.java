package com.study.localmeet.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MeetingCategory {
    SPORTS("🏃 운동"),
    GAME("🎮 게임"),
    STUDY("📚 공부"),
    FOOD("🍽️ 맛집"),
    HOBBY("🎨 취미"),
    TRAVEL("✈️ 여행"),
    PET("🐾 반려동물"),
    IT("💻 IT"),
    ETC("📌 기타");

    private final String value;
}
