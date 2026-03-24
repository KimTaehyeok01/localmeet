package com.study.localmeet.service;

import com.study.localmeet.domain.chat.ChatMessage;
import com.study.localmeet.domain.chat.ChatMessageRepository;
import com.study.localmeet.domain.meeting.Meeting;
import com.study.localmeet.domain.meeting.MeetingRepository;
import com.study.localmeet.domain.user.Users;
import com.study.localmeet.domain.user.UsersRepository;
import com.study.localmeet.dto.chat.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final MeetingRepository meetingRepository;
    private final UsersRepository usersRepository;

    // 채팅 메시지 저장
    @Transactional
    public ChatMessageDto save(Long meetingIdx, String userEmail, String chatContent) {
        Meeting meeting = meetingRepository.findById(meetingIdx)
                .orElseThrow(() -> new IllegalArgumentException("없는 모임입니다."));
        Users users = usersRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        ChatMessage entity = chatMessageRepository.save(
                ChatMessage.builder()
                        .meeting(meeting)
                        .users(users)
                        .chatContent(chatContent)
                        .build()
        );
        return new ChatMessageDto(entity);
    }

    // 모임별 채팅 내역 조회
    @Transactional(readOnly = true)
    public List<ChatMessageDto> findAllByMeetingIdx(Long meetingIdx) {
        List<ChatMessage> list = chatMessageRepository.findAllByMeeting_MeetingIdxOrderByCreatedAtAsc(meetingIdx);
        return list.stream().map(ChatMessageDto::new).collect(Collectors.toList());
    }
}
