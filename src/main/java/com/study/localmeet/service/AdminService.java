package com.study.localmeet.service;

import com.study.localmeet.domain.chat.ChatMessageRepository;
import com.study.localmeet.domain.meeting.Meeting;
import com.study.localmeet.domain.meeting.MeetingRepository;
import com.study.localmeet.domain.meetingmember.MeetingMemberRepository;
import com.study.localmeet.domain.user.Users;
import com.study.localmeet.domain.user.UsersRepository;
import com.study.localmeet.dto.auth.UserResponseDto;
import com.study.localmeet.dto.meeting.MeetingResponseDto;
import com.study.localmeet.enumeration.MeetingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UsersRepository usersRepository;
    private final MeetingRepository meetingRepository;
    private final MeetingMemberRepository meetingMemberRepository;
    private final ChatMessageRepository chatMessageRepository;

    // 전체 회원 목록
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAllUsers() {
        return usersRepository.findAll().stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());
    }

    // 회원 강제 탈퇴 (관련 데이터 cascade 삭제)
    @Transactional
    public void deleteUser(Long userIdx) {
        Users user = usersRepository.findById(userIdx)
                .orElseThrow(() -> new IllegalArgumentException("없는 회원입니다."));

        // 이 회원이 만든 모임과 관련 데이터 삭제
        List<Meeting> userMeetings = meetingRepository.findAllByUsers_UserIdxOrderByCreatedAtDesc(userIdx);
        for (Meeting meeting : userMeetings) {
            Long meetingIdx = meeting.getMeetingIdx();
            chatMessageRepository.deleteAll(
                    chatMessageRepository.findAllByMeeting_MeetingIdxOrderByCreatedAtAsc(meetingIdx));
            meetingMemberRepository.deleteAll(
                    meetingMemberRepository.findAllByMeeting_MeetingIdx(meetingIdx));
        }
        meetingRepository.deleteAll(userMeetings);

        // 다른 모임에서의 채팅, 참가 신청 삭제
        chatMessageRepository.deleteAllByUsers_UserIdx(userIdx);
        meetingMemberRepository.deleteAllByUsers_UserIdx(userIdx);

        usersRepository.delete(user);
    }

    // 전체 모임 목록 (관리자용)
    @Transactional(readOnly = true)
    public List<MeetingResponseDto> findAllMeetings() {
        return meetingRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(meeting -> {
                    MeetingResponseDto dto = new MeetingResponseDto(meeting);
                    int count = meetingMemberRepository.countByMeeting_MeetingIdx(meeting.getMeetingIdx());
                    dto.setCurrentCount(count);
                    return dto;
                }).collect(Collectors.toList());
    }

    // 모임 강제 삭제
    @Transactional
    public void deleteMeeting(Long meetingIdx) {
        Meeting meeting = meetingRepository.findById(meetingIdx)
                .orElseThrow(() -> new IllegalArgumentException("없는 모임입니다."));
        chatMessageRepository.deleteAll(
                chatMessageRepository.findAllByMeeting_MeetingIdxOrderByCreatedAtAsc(meetingIdx));
        meetingMemberRepository.deleteAll(
                meetingMemberRepository.findAllByMeeting_MeetingIdx(meetingIdx));
        meetingRepository.delete(meeting);
    }

    // 모임 상태 변경
    @Transactional
    public void updateMeetingStatus(Long meetingIdx, String status) {
        Meeting meeting = meetingRepository.findById(meetingIdx)
                .orElseThrow(() -> new IllegalArgumentException("없는 모임입니다."));
        meeting.updateStatus(MeetingStatus.valueOf(status));
    }
}
