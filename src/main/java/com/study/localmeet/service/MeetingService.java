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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingMemberRepository meetingMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UsersRepository usersRepository;

    // 전체 모임 목록 조회
    @Transactional(readOnly = true)
    public List<MeetingResponseDto> findAll() {
        List<Meeting> list = meetingRepository.findAllByOrderByCreatedAtDesc();
        return list.stream().map(meeting -> {
            MeetingResponseDto dto = new MeetingResponseDto(meeting);
            int count = meetingMemberRepository.countByMeeting_MeetingIdx(meeting.getMeetingIdx());
            dto.setCurrentCount(count);
            return dto;
        }).collect(Collectors.toList());
    }

    // 모임 단건 조회
    @Transactional(readOnly = true)
    public MeetingResponseDto findById(Long meetingIdx) {
        Meeting entity = meetingRepository.findById(meetingIdx)
                .orElseThrow(() -> new IllegalArgumentException("없는 모임입니다."));
        MeetingResponseDto dto = new MeetingResponseDto(entity);
        int count = meetingMemberRepository.countByMeeting_MeetingIdx(meetingIdx);
        dto.setCurrentCount(count);
        return dto;
    }

    // 모임 저장
    @Transactional
    public Long save(MeetingSaveRequestDto dto, String userEmail) {
        Users users = usersRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        Meeting entity = meetingRepository.save(dto.toEntity(users));
        return entity.getMeetingIdx();
    }

    // 모임 수정
    @Transactional
    public boolean update(Long meetingIdx, MeetingSaveRequestDto dto, String userEmail) {
        Meeting entity = meetingRepository.findById(meetingIdx)
                .orElseThrow(() -> new IllegalArgumentException("없는 모임입니다."));

        // 작성자 본인만 수정 가능
        if (!entity.getUsers().getUserEmail().equals(userEmail)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        entity.update(dto.getMeetingTitle(), dto.getMeetingContent(), dto.getMeetingAddress(),
                dto.getMeetingLat(), dto.getMeetingLng(), dto.getMeetingMax());
        return true;
    }

    // 모임 삭제
    @Transactional
    public void delete(Long meetingIdx, String userEmail) {
        Meeting entity = meetingRepository.findById(meetingIdx)
                .orElseThrow(() -> new IllegalArgumentException("없는 모임입니다."));

        // 작성자 본인만 삭제 가능
        if (!entity.getUsers().getUserEmail().equals(userEmail)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        // 자식 데이터 먼저 삭제 (FK 제약조건)
        chatMessageRepository.deleteAll(
                chatMessageRepository.findAllByMeeting_MeetingIdxOrderByCreatedAtAsc(meetingIdx));
        meetingMemberRepository.deleteAll(
                meetingMemberRepository.findAllByMeeting_MeetingIdx(meetingIdx));

        meetingRepository.delete(entity);
    }

    // 모임 참가 신청
    @Transactional
    public String join(Long meetingIdx, String userEmail) {
        Meeting meeting = meetingRepository.findById(meetingIdx)
                .orElseThrow(() -> new IllegalArgumentException("없는 모임입니다."));
        Users users = usersRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        // 이미 신청한 경우 체크
        if (meetingMemberRepository.existsByMeeting_MeetingIdxAndUsers_UserIdx(meetingIdx, users.getUserIdx())) {
            throw new IllegalArgumentException("이미 참가 신청한 모임입니다.");
        }

        // 모집 중인 경우에만 신청 가능
        if (meeting.getMeetingStatus() != MeetingStatus.OPEN) {
            throw new IllegalArgumentException("현재 참가 신청이 불가능한 모임입니다.");
        }

        meetingMemberRepository.save(MeetingMember.builder()
                .meeting(meeting)
                .users(users)
                .build());

        return "참가 신청이 완료되었습니다.";
    }

    // 참가 승인 (모임 작성자만 가능)
    @Transactional
    public String approve(Long mmIdx, String userEmail) {
        MeetingMember meetingMember = meetingMemberRepository.findById(mmIdx)
                .orElseThrow(() -> new IllegalArgumentException("참가 신청 정보가 없습니다."));

        // 모임 작성자인지 확인
        if (!meetingMember.getMeeting().getUsers().getUserEmail().equals(userEmail)) {
            throw new IllegalArgumentException("승인 권한이 없습니다.");
        }

        meetingMember.approve();

        // 승인 인원이 최대 인원에 도달하면 모집완료로 변경
        int approvedCount = meetingMemberRepository.countByMeeting_MeetingIdxAndIsApproved(
                meetingMember.getMeeting().getMeetingIdx(), true);
        if (approvedCount >= meetingMember.getMeeting().getMeetingMax()) {
            meetingMember.getMeeting().updateStatus(MeetingStatus.FULL);
        }

        return "승인이 완료되었습니다.";
    }

    // 주소 키워드로 모임 검색
    @Transactional(readOnly = true)
    public List<MeetingResponseDto> searchByAddress(String keyword) {
        List<Meeting> list = meetingRepository.findAllByMeetingAddressContainingOrderByCreatedAtDesc(keyword);
        return list.stream().map(meeting -> {
            MeetingResponseDto dto = new MeetingResponseDto(meeting);
            int count = meetingMemberRepository.countByMeeting_MeetingIdx(meeting.getMeetingIdx());
            dto.setCurrentCount(count);
            return dto;
        }).collect(Collectors.toList());
    }
}
