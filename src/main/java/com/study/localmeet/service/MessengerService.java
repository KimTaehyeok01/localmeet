package com.study.localmeet.service;

import com.study.localmeet.domain.dm.*;
import com.study.localmeet.domain.user.Users;
import com.study.localmeet.domain.user.UsersRepository;
import com.study.localmeet.dto.dm.ConversationDto;
import com.study.localmeet.dto.dm.DirectMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessengerService {

    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository conversationMemberRepository;
    private final DirectMessageRepository directMessageRepository;
    private final UsersRepository usersRepository;

    // 내 대화 목록 조회
    @Transactional(readOnly = true)
    public List<ConversationDto> getMyConversations(String userEmail) {
        Users me = findUser(userEmail);
        List<Conversation> conversations = conversationRepository.findAllByUserIdx(me.getUserIdx());

        return conversations.stream().map(conv -> {
            // 상대방 조회
            List<ConversationMember> members = conversationMemberRepository
                    .findAllByConversation_ConvIdx(conv.getConvIdx());
            ConversationMember partnerMember = members.stream()
                    .filter(cm -> !cm.getUser().getUserIdx().equals(me.getUserIdx()))
                    .findFirst().orElse(null);

            String partnerNickname = partnerMember != null
                    ? partnerMember.getUser().getUserNickname() : "(알 수 없음)";
            Long partnerUserIdx = partnerMember != null
                    ? partnerMember.getUser().getUserIdx() : null;

            // 마지막 메시지
            DirectMessage lastDm = directMessageRepository
                    .findFirstByConversation_ConvIdxOrderByCreatedAtDesc(conv.getConvIdx())
                    .orElse(null);

            long unread = directMessageRepository.countUnread(conv.getConvIdx(), me.getUserIdx());

            return ConversationDto.builder()
                    .convIdx(conv.getConvIdx())
                    .partnerUserIdx(partnerUserIdx)
                    .partnerNickname(partnerNickname)
                    .lastMessage(lastDm != null ? lastDm.getDmContent() : "")
                    .lastMessageTime(lastDm != null ? lastDm.getCreatedAt() : conv.getCreatedAt())
                    .unreadCount(unread)
                    .build();
        })
        .sorted((a, b) -> b.getLastMessageTime().compareTo(a.getLastMessageTime()))
        .collect(Collectors.toList());
    }

    // DM 대화방 가져오기 (없으면 생성)
    @Transactional
    public Long getOrCreateDM(String myEmail, Long targetUserIdx) {
        Users me = findUser(myEmail);
        Users target = usersRepository.findById(targetUserIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        if (me.getUserIdx().equals(targetUserIdx)) {
            throw new IllegalArgumentException("자기 자신과는 대화할 수 없습니다.");
        }

        // 이미 대화방이 있으면 반환
        return conversationRepository
                .findDMBetween(me.getUserIdx(), target.getUserIdx())
                .map(Conversation::getConvIdx)
                .orElseGet(() -> {
                    Conversation conv = conversationRepository.save(new Conversation(null));
                    conversationMemberRepository.save(ConversationMember.builder()
                            .conversation(conv).user(me).build());
                    conversationMemberRepository.save(ConversationMember.builder()
                            .conversation(conv).user(target).build());
                    return conv.getConvIdx();
                });
    }

    // 메시지 목록 조회 + 읽음 처리
    @Transactional
    public List<DirectMessageDto> getMessages(Long convIdx, String userEmail) {
        Users me = findUser(userEmail);
        checkMember(convIdx, me.getUserIdx());

        // 읽음 처리
        conversationMemberRepository
                .findByConversation_ConvIdxAndUser_UserIdx(convIdx, me.getUserIdx())
                .ifPresent(cm -> {
                    cm.updateLastRead();
                    conversationMemberRepository.save(cm);
                });

        return directMessageRepository
                .findAllByConversation_ConvIdxOrderByCreatedAtAsc(convIdx)
                .stream()
                .map(DirectMessageDto::new)
                .collect(Collectors.toList());
    }

    // 메시지 전송
    @Transactional
    public DirectMessageDto sendMessage(Long convIdx, String senderEmail, String content) {
        Users sender = findUser(senderEmail);
        checkMember(convIdx, sender.getUserIdx());

        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("메시지를 입력해주세요.");
        }

        Conversation conv = conversationRepository.findById(convIdx)
                .orElseThrow(() -> new IllegalArgumentException("대화방을 찾을 수 없습니다."));

        DirectMessage dm = directMessageRepository.save(DirectMessage.builder()
                .conversation(conv)
                .sender(sender)
                .dmContent(content.trim())
                .build());

        // 내 읽음 처리
        conversationMemberRepository
                .findByConversation_ConvIdxAndUser_UserIdx(convIdx, sender.getUserIdx())
                .ifPresent(cm -> {
                    cm.updateLastRead();
                    conversationMemberRepository.save(cm);
                });

        return new DirectMessageDto(dm);
    }

    // 전체 미읽은 메시지 수
    @Transactional(readOnly = true)
    public long getTotalUnread(String userEmail) {
        Users me = findUser(userEmail);
        return conversationMemberRepository.countTotalUnread(me.getUserIdx());
    }

    // 유저 검색 (닉네임 포함)
    @Transactional(readOnly = true)
    public List<java.util.Map<String, Object>> searchUsers(String nickname, String myEmail) {
        Users me = findUser(myEmail);
        return usersRepository.findByUserNicknameContaining(nickname).stream()
                .filter(u -> !u.getUserIdx().equals(me.getUserIdx()))
                .map(u -> java.util.Map.of(
                        "userIdx", (Object) u.getUserIdx(),
                        "userNickname", u.getUserNickname()
                ))
                .collect(Collectors.toList());
    }

    // ─── private ─────────────────────────────────────────────────────────────

    private Users findUser(String email) {
        return usersRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
    }

    private void checkMember(Long convIdx, Long userIdx) {
        if (!conversationMemberRepository.existsByConversation_ConvIdxAndUser_UserIdx(convIdx, userIdx)) {
            throw new IllegalArgumentException("대화방 접근 권한이 없습니다.");
        }
    }
}
