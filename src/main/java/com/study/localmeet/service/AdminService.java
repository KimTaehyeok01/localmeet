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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UsersRepository usersRepository;
    private final MeetingRepository meetingRepository;
    private final MeetingMemberRepository meetingMemberRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional(readOnly = true)
    public List<UserResponseDto> findAllUsers() {
        return usersRepository.findAll().stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUser(Long userIdx) {
        Users user = usersRepository.findById(userIdx)
                .orElseThrow(() -> new IllegalArgumentException("없는 회원입니다."));

        List<Long> userMeetingIds = meetingRepository
                .findAllByUsers_UserIdxOrderByCreatedAtDesc(userIdx)
                .stream()
                .map(Meeting::getMeetingIdx)
                .collect(Collectors.toList());

        if (!userMeetingIds.isEmpty()) {
            chatMessageRepository.deleteAllByMeetingIdxIn(userMeetingIds);
            meetingMemberRepository.deleteAllByMeetingIdxIn(userMeetingIds);
            meetingRepository.deleteAllByUsersUserIdx(userIdx);
        }

        chatMessageRepository.deleteAllByUsers_UserIdx(userIdx);
        meetingMemberRepository.deleteAllByUsers_UserIdx(userIdx);
        usersRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public List<MeetingResponseDto> findAllMeetings() {
        List<Meeting> list = meetingRepository.findAllByOrderByCreatedAtDesc();
        Map<Long, Integer> countMap = meetingMemberRepository.countAllGroupByMeeting()
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Long) row[1]).intValue()
                ));
        return list.stream().map(meeting -> {
            MeetingResponseDto dto = new MeetingResponseDto(meeting);
            dto.setCurrentCount(countMap.getOrDefault(meeting.getMeetingIdx(), 0));
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void deleteMeeting(Long meetingIdx) {
        Meeting meeting = meetingRepository.findById(meetingIdx)
                .orElseThrow(() -> new IllegalArgumentException("없는 모임입니다."));
        chatMessageRepository.deleteAllByMeeting_MeetingIdx(meetingIdx);
        meetingMemberRepository.deleteAllByMeeting_MeetingIdx(meetingIdx);
        meetingRepository.delete(meeting);
    }

    @Transactional
    public void updateMeetingStatus(Long meetingIdx, String status) {
        Meeting meeting = meetingRepository.findById(meetingIdx)
                .orElseThrow(() -> new IllegalArgumentException("없는 모임입니다."));
        meeting.updateStatus(MeetingStatus.valueOf(status));
    }
}
