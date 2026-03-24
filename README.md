# 🗺️ LocalMeet - 동네 기반 모임 커뮤니티 플랫폼

> 같은 동네 사람들끼리 관심사 기반 모임을 만들고, 실시간으로 소통하는 웹 서비스

<br>

## 📌 프로젝트 소개

LocalMeet은 위치 기반으로 동네 모임을 탐색하고 참가 신청할 수 있는 커뮤니티 플랫폼입니다.  
Spring Boot와 Thymeleaf를 기반으로 REST API 방식으로 구현하였으며,  
JWT 인증, WebSocket 실시간 채팅, SSE 알림, 카카오맵 API, 소셜 로그인 등 실무에서 자주 사용하는 기술들을 적용했습니다.

<br>

## 🛠 기술 스택

### Backend

|기술               |버전    |
|-----------------|------|
|Java             |17    |
|Spring Boot      |3.4.3 |
|Spring Security  |6.x   |
|Spring Data JPA  |-     |
|QueryDSL         |5.1.0 |
|WebSocket (STOMP)|-     |
|JWT (jjwt)       |0.12.6|
|OAuth2 (카카오, 구글) |-     |

### Frontend

|기술                     |설명             |
|-----------------------|---------------|
|Thymeleaf              |서버 사이드 템플릿 엔진  |
|HTML / CSS / JavaScript|바닐라 JS         |
|SockJS + STOMP.js      |WebSocket 클라이언트|
|카카오맵 API               |위치 기반 지도 표시    |

### Database & Infra

|기술       |설명       |
|---------|---------|
|MySQL 8.x|운영 데이터베이스|
|Gradle   |빌드 도구    |

<br>

## ✨ 주요 기능

### 👤 회원

- 이메일 / 비밀번호 회원가입 및 로그인
- 카카오 / 구글 소셜 로그인 (OAuth2)
- JWT 기반 인증 / 인가 (`ROLE_USER`, `ROLE_ADMIN`)
- 마이페이지 (내 정보 조회)

### 🏘️ 모임

- 모임 생성 / 수정 / 삭제
- 카카오 주소 검색 + 카카오맵 위치 표시
- 동네 키워드 검색
- 참가 신청 / 승인 (모임장만 승인 가능)
- 모임 상태 관리 (모집중 / 모집완료 / 종료)

### 💬 실시간 채팅

- WebSocket + STOMP 기반 모임별 실시간 채팅
- 채팅방 입장 시 이전 메시지 내역 자동 로드

### 🔔 실시간 알림

- SSE(Server-Sent Events) 기반 알림
- 참가 신청 시 모임장에게 실시간 알림 전송

<br>

## 📐 아키텍처

```
[Thymeleaf + JS] ──REST API──▶ [Spring Boot]
                                      │
              ┌───────────────────────┼────────────────────┐
       [Spring Security]        [WebSocket]            [외부 API]
       JWT / OAuth2             STOMP Broker        카카오맵, 카카오·구글 OAuth2
                                      │
                               [MySQL / JPA]
                               [QueryDSL]
```

<br>

## 📦 패키지 구조

```
src/main/java/com/study/localmeet/
├── config/         # Security, JWT, WebSocket, QueryDSL 설정
├── controller/     # REST API + View 컨트롤러
├── domain/         # Entity + Repository
│   ├── user/
│   ├── meeting/
│   ├── meetingmember/
│   └── chat/
├── dto/            # Request / Response DTO
├── enumeration/    # UserRole, MeetingStatus
└── service/        # 비즈니스 로직
```

<br>

## 🗄️ ERD

```
Users ──────────────── Meeting
  │                      │
  │               MeetingMember (중간 테이블)
  │                      │
  └────────────── ChatMessage
```

|테이블           |설명                          |
|--------------|----------------------------|
|users         |회원 정보 (이메일, 닉네임, 동네, 위도/경도) |
|meeting       |모임 정보 (제목, 내용, 위치, 최대인원, 상태)|
|meeting_member|모임 참가 신청 / 승인 관리            |
|chat_message  |모임별 채팅 메시지                  |

<br>

## ⚙️ 실행 방법

### 1. MySQL 데이터베이스 생성

```sql
CREATE DATABASE localmeet CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. application.properties 설정

```properties
spring.datasource.username=root
spring.datasource.password=본인_비밀번호

kakao.map.api-key=카카오_JavaScript_키

spring.security.oauth2.client.registration.kakao.client-id=카카오_REST_API_키
spring.security.oauth2.client.registration.kakao.client-secret=카카오_Client_Secret

spring.security.oauth2.client.registration.google.client-id=구글_클라이언트_ID
spring.security.oauth2.client.registration.google.client-secret=구글_클라이언트_Secret
```

### 3. 실행

```bash
./gradlew bootRun
```

### 4. 접속

```
http://localhost:8080
```

<br>

## 🔑 테스트 계정

|이메일           |비밀번호|권한        |
|--------------|----|----------|
|admin@test.com|1234|ROLE_ADMIN|
|user@test.com |1234|ROLE_USER |

<br>

## 📡 주요 API 명세

|Method|URL                          |설명             |인증|
|------|-----------------------------|---------------|--|
|POST  |/api/auth/signup             |회원가입           |❌ |
|POST  |/api/auth/login              |로그인 (JWT 발급)   |❌ |
|GET   |/api/auth/mypage             |내 정보 조회        |✅ |
|GET   |/api/meetings                |모임 전체 목록       |❌ |
|POST  |/api/meetings                |모임 등록          |✅ |
|GET   |/api/meetings/{id}           |모임 상세 조회       |❌ |
|POST  |/api/meetings/{id}/update    |모임 수정          |✅ |
|GET   |/api/meetings/{id}/delete    |모임 삭제          |✅ |
|POST  |/api/meetings/{id}/join      |참가 신청          |✅ |
|POST  |/api/meetings/approve/{mmIdx}|참가 승인          |✅ |
|GET   |/api/meetings/search?keyword=|동네 검색          |❌ |
|GET   |/api/chat/{meetingIdx}       |채팅 내역 조회       |❌ |
|GET   |/api/notifications/subscribe |SSE 알림 구독      |✅ |
|WS    |/ws/chat                     |WebSocket 채팅 연결|- |

<br>

## 💡 구현 포인트

**JWT Stateless 인증**  
세션을 사용하지 않고 매 요청마다 헤더의 JWT 토큰을 검증하는 Stateless 방식으로 구현했습니다. `JwtAuthenticationFilter`에서 토큰 유효성 검사 후 `SecurityContext`에 인증 정보를 저장합니다.

**WebSocket 채팅 보안**  
일반 HTTP 요청과 달리 WebSocket은 Spring Security 필터를 거치지 않기 때문에, STOMP 헤더에 JWT 토큰을 직접 담아 서버에서 검증하는 방식으로 채팅방 인증을 처리했습니다.

**OAuth2 소셜 로그인 + JWT 연동**  
소셜 로그인 성공 후 `OAuth2SuccessHandler`에서 JWT 토큰을 발급하고, 쿼리 파라미터로 프론트에 전달하여 `localStorage`에 저장하는 방식으로 JWT 기반 인증과 연동했습니다.

**SSE 실시간 알림**  
서버에서 클라이언트로 단방향 데이터를 전송하는 SSE를 활용해, 참가 신청 이벤트 발생 시 모임장에게 실시간 알림을 전송합니다.