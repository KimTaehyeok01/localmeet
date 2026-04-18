package com.study.localmeet.service;

import com.study.localmeet.domain.chat.ChatMessageRepository;
import com.study.localmeet.domain.meeting.Meeting;
import com.study.localmeet.domain.meeting.MeetingRepository;
import com.study.localmeet.domain.meetingmember.MeetingMember;
import com.study.localmeet.domain.meetingmember.MeetingMemberRepository;
import com.study.localmeet.domain.user.Users;
import com.study.localmeet.domain.user.UsersRepository;
import com.study.localmeet.dto.meeting.MeetingResponseDto;
import com.study.localmeet.dto.meeting.MeetingSaveRequestDto;
import com.study.localmeet.enumeration.MeetingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingMemberRepository meetingMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UsersRepository usersRepository;

    @Transactional(readOnly = true)
    public List<MeetingResponseDto> findAll() {
        List<Meeting> list = meetingRepository.findAllByOrderByCreatedAtDesc();
        Map<Long, Integer> countMap = buildCountMap();
        return list.stream().map(meeting -> {
            MeetingResponseDto dto = new MeetingResponseDto(meeting);
            dto.setCurrentCount(countMap.getOrDefault(meeting.getMeetingIdx(), 0));
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MeetingResponseDto findById(Long meetingIdx) {
        Meeting entity = meetingRepository.findById(meetingIdx)
                .orElseThrow(() -> new IllegalArgumentException("없는 모임입니다."));
        MeetingResponseDto dto = new MeetingResponseDto(entity);
        dto.setCurrentCount(meetingMemberRepository.countByMeeting_MeetingIdx(meetingIdx));
        return dto;
    }

    @Transactional
    public Long save(MeetingSaveRequestDto dto, String userEmail) {
        Users users = usersRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        Meeting entity = meetingRepository.save(dto.toEntity(users));
        return entity.getMeetingIdx();
    }

    @Transactional
    public boolean update(Long meetingIdx, MeetingSaveRequestDto dto, String userEmail) {
        Meeting entity = meetingRepository.findById(meetingIdx)
                .orElseThrow(() -> new IllegalArgumentException("없는 모임입니다."));

        if (!entity.getUsers().getUserEmail().equals(userEmail)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        entity.update(dto.getMeetingTitle(), dto.getMeetingContent(), dto.getMeetingAddress(),
                dto.getMeetingLat(), dto.getMeetingLng(), dto.getMeetingMax());
        return true;
    }

    @Transactional
    public void delete(Long meetingIdx, String userEmail) {
        Meeting entity = meetingRepository.findById(meetingIdx)
                .orElseThrow(() -> new IllegalArgumentException("없는 모임입니다."));

        if (!entity.getUsers().getUserEmail().equals(userEmail)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        chatMessageRepository.deleteAllByMeeting_MeetingIdx(meetingIdx);
        meetingMemberRepository.deleteAllByMeeting_MeetingIdx(meetingIdx);
        meetingRepository.delete(entity);
    }

    @Transactional
    public String join(Long meetingIdx, String userEmail) {
        Meeting meeting = meetingRepository.findById(meetingIdx)
                .orElseThrow(() -> new IllegalArgumentException("없는 모임입니다."));
        Users users = usersRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        if (meetingMemberRepository.existsByMeeting_MeetingIdxAndUsers_UserIdx(meetingIdx, users.getUserIdx())) {
            throw new IllegalArgumentException("이미 참가 신청한 모임입니다.");
        }

        if (meeting.getMeetingStatus() != MeetingStatus.OPEN) {
            throw new IllegalArgumentException("현재 참가 신청이 불가능한 모임입니다.");
        }

        meetingMemberRepository.save(MeetingMember.builder()
                .meeting(meeting)
                .users(users)
                .build());

        return "참가 신청이 완료되었습니다.";
    }

    @Transactional
    public String approve(Long mmIdx, String userEmail) {
        MeetingMember meetingMember = meetingMemberRepository.findById(mmIdx)
                .orElseThrow(() -> new IllegalArgumentException("참가 신청 정보가 없습니다."));

        if (!meetingMember.getMeeting().getUsers().getUserEmail().equals(userEmail)) {
            throw new IllegalArgumentException("승인 권한이 없습니다.");
        }

        meetingMember.approve();
        meetingMemberRepository.save(meetingMember);

        Meeting meeting = meetingRepository.findByIdWithLock(meetingMember.getMeeting().getMeetingIdx())
                .orElseThrow(() -> new IllegalArgumentException("모임을 찾을 수 없습니다."));

        int approvedCount = meetingMemberRepository.countByMeeting_MeetingIdxAndIsApproved(
                meeting.getMeetingIdx(), true);
        if (approvedCount >= meeting.getMeetingMax()) {
            meeting.updateStatus(MeetingStatus.FULL);
        }

        return "승인이 완료되었습니다.";
    }

    @Transactional(readOnly = true)
    public List<MeetingResponseDto> searchByAddress(String keyword) {
        List<Meeting> list = meetingRepository.findAllByMeetingAddressContainingOrderByCreatedAtDesc(keyword);
        Map<Long, Integer> countMap = buildCountMap();
        return list.stream().map(meeting -> {
            MeetingResponseDto dto = new MeetingResponseDto(meeting);
            dto.setCurrentCount(countMap.getOrDefault(meeting.getMeetingIdx(), 0));
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isApprovedMember(Long meetingIdx, String userEmail) {
        return meetingMemberRepository.existsByMeeting_MeetingIdxAndUsers_UserEmailAndIsApproved(
                meetingIdx, userEmail, true);
    }

    @Transactional(readOnly = true)
    public boolean canChat(Long meetingIdx, String userEmail) {
        Meeting meeting = meetingRepository.findById(meetingIdx)
                .orElseThrow(() -> new IllegalArgumentException("없는 모임입니다."));

        if (meeting.getUsers().getUserEmail().equals(userEmail)) {
            return true;
        }

        return meetingMemberRepository.existsByMeeting_MeetingIdxAndUsers_UserEmail(meetingIdx, userEmail);
    }

    private Map<Long, Integer> buildCountMap() {
        return meetingMemberRepository.countAllGroupByMeeting()
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Long) row[1]).intValue()
                ));
    }
}
