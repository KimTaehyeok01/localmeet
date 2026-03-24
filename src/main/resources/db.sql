-- ================================
-- LocalMeet DB 초기화 SQL
-- ================================

-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS localmeet
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE localmeet;

-- ================================
-- 1. 회원 테이블
-- ================================
CREATE TABLE IF NOT EXISTS users (
    user_idx      BIGINT          NOT NULL AUTO_INCREMENT,
    user_email    VARCHAR(100)    NOT NULL UNIQUE,
    user_password VARCHAR(255)    NOT NULL,
    user_nickname VARCHAR(50)     NOT NULL UNIQUE,
    user_role     VARCHAR(20)     NOT NULL DEFAULT 'ROLE_USER',
    user_address  VARCHAR(255),
    user_lat      DOUBLE,
    user_lng      DOUBLE,
    created_at    DATETIME        NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_idx)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ================================
-- 2. 모임 테이블
-- ================================
CREATE TABLE IF NOT EXISTS meeting (
    meeting_idx     BIGINT          NOT NULL AUTO_INCREMENT,
    meeting_title   VARCHAR(100)    NOT NULL,
    meeting_content TEXT            NOT NULL,
    meeting_address VARCHAR(255),
    meeting_lat     DOUBLE,
    meeting_lng     DOUBLE,
    meeting_max     INT             NOT NULL,
    meeting_status  VARCHAR(20)     NOT NULL DEFAULT 'OPEN',
    created_at      DATETIME        NOT NULL DEFAULT NOW(),
    user_idx        BIGINT          NOT NULL,
    PRIMARY KEY (meeting_idx),
    FOREIGN KEY (user_idx) REFERENCES users(user_idx)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ================================
-- 3. 모임 참가자 테이블
-- ================================
CREATE TABLE IF NOT EXISTS meeting_member (
    mm_idx      BIGINT      NOT NULL AUTO_INCREMENT,
    meeting_idx BIGINT      NOT NULL,
    user_idx    BIGINT      NOT NULL,
    is_approved TINYINT(1)  NOT NULL DEFAULT 0,
    joined_at   DATETIME    NOT NULL DEFAULT NOW(),
    PRIMARY KEY (mm_idx),
    FOREIGN KEY (meeting_idx) REFERENCES meeting(meeting_idx),
    FOREIGN KEY (user_idx)    REFERENCES users(user_idx)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ================================
-- 4. 채팅 메시지 테이블
-- ================================
CREATE TABLE IF NOT EXISTS chat_message (
    chat_idx     BIGINT   NOT NULL AUTO_INCREMENT,
    meeting_idx  BIGINT   NOT NULL,
    user_idx     BIGINT   NOT NULL,
    chat_content TEXT     NOT NULL,
    created_at   DATETIME NOT NULL DEFAULT NOW(),
    PRIMARY KEY (chat_idx),
    FOREIGN KEY (meeting_idx) REFERENCES meeting(meeting_idx),
    FOREIGN KEY (user_idx)    REFERENCES users(user_idx)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ================================
-- 테스트 데이터
-- ================================

-- 테스트 유저 2명 (비밀번호 모두 '1234' -> BCrypt 인코딩)
INSERT INTO users (user_email, user_password, user_nickname, user_role, user_address, user_lat, user_lng, created_at)
VALUES
    ('admin@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '관리자', 'ROLE_ADMIN', '서울 마포구 합정동', 37.5498, 126.9137, NOW()),
    ('user@test.com',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '테스터',  'ROLE_USER',  '서울 마포구 망원동', 37.5558, 126.9037, NOW());

-- 테스트 모임 3개
INSERT INTO meeting (meeting_title, meeting_content, meeting_address, meeting_lat, meeting_lng, meeting_max, meeting_status, created_at, user_idx)
VALUES
    ('마포구 러닝 모임',     '매주 토요일 아침 한강에서 함께 달려요!',       '서울 마포구 망원한강공원', 37.5551, 126.8976, 10, 'OPEN', NOW(), 1),
    ('합정 독서 클럽',       '한 달에 한 권, 함께 읽고 이야기 나눠요.',      '서울 마포구 합정동',       37.5498, 126.9137,  8, 'OPEN', NOW(), 1),
    ('망원동 보드게임 모임', '보드게임 좋아하시는 분들 모여요! 초보 환영.',  '서울 마포구 망원동',       37.5558, 126.9037,  6, 'FULL', NOW(), 2);

-- 테스트 참가 신청
INSERT INTO meeting_member (meeting_idx, user_idx, is_approved, joined_at)
VALUES
    (1, 2, 1, NOW()),
    (2, 2, 0, NOW());
