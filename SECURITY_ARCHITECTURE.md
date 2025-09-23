# OhGoodPay 보안 아키텍처 문서

## 개요

OhGoodPay 백엔드 애플리케이션의 Spring Security 기반 JWT 인증/인가 시스템 구조를 문서화합니다.

## 1. 보안 구성 요소

### 1.1 핵심 클래스 구조

```
security/
├── config/
│   └── SecurityConfig.java          # 메인 보안 설정
├── filter/
│   ├── ApiLoginFilter.java          # 로그인 인증 필터
│   ├── ApiLoginSuccessHandler.java  # 로그인 성공 핸들러
│   └── TokenCheckFilter.java        # JWT 토큰 검증 필터
├── service/
│   └── ApiCustomerDetailsService.java # 사용자 정보 로드 서비스
├── util/
│   └── JWTUtil.java                 # JWT 토큰 생성/검증 유틸리티
├── dto/
│   └── ApiCustomerDTO.java          # 인증된 사용자 정보 DTO
└── exception/
    └── AccessTokenException.java    # 토큰 관련 예외 처리
```

## 2. 인증/인가 플로우

### 2.1 로그인 인증 플로우

```mermaid
sequenceDiagram
    participant Client
    participant ApiLoginFilter
    participant ApiCustomerDetailsService
    participant ApiLoginSuccessHandler
    participant JWTUtil

    Client->>ApiLoginFilter: POST /auth (emailId, pwd)
    ApiLoginFilter->>ApiCustomerDetailsService: loadUserByUsername(emailId)
    ApiCustomerDetailsService->>ApiLoginFilter: UserDetails 반환
    ApiLoginFilter->>ApiLoginSuccessHandler: 인증 성공
    ApiLoginSuccessHandler->>JWTUtil: generateToken(claims, 1일)
    JWTUtil->>ApiLoginSuccessHandler: accessToken 반환
    ApiLoginSuccessHandler->>Client: {"accessToken": "jwt_token"}
```

### 2.2 API 요청 인가 플로우

```mermaid
sequenceDiagram
    participant Client
    participant TokenCheckFilter
    participant JWTUtil
    participant SecurityContext
    participant Controller

    Client->>TokenCheckFilter: API 요청 (Authorization: Bearer token)
    TokenCheckFilter->>TokenCheckFilter: 경로 검증 (/api/public/** 제외)
    TokenCheckFilter->>JWTUtil: validateToken(token)
    JWTUtil->>TokenCheckFilter: claims 반환
    TokenCheckFilter->>SecurityContext: 인증 정보 설정
    TokenCheckFilter->>Controller: 요청 전달
    Controller->>Client: 응답
```

## 3. 필터 체인 구성

### 3.1 필터 순서

1. **CORS 필터** (CorsConfig)
2. **ApiLoginFilter** - 로그인 인증 처리
3. **TokenCheckFilter** - JWT 토큰 검증
4. **UsernamePasswordAuthenticationFilter** (Spring 기본)

### 3.2 필터별 역할

#### ApiLoginFilter

- **역할**: 로그인 요청 처리 (`/auth` 경로)
- **동작**:
  - JSON 요청에서 `emailId`, `pwd` 추출
  - `UsernamePasswordAuthenticationToken` 생성
  - `AuthenticationManager`를 통한 인증 처리

#### TokenCheckFilter

- **역할**: JWT 토큰 검증 및 인가 처리
- **동작**:
  - `/api` 경로만 처리 (일반 접속 제외)
  - `/api/public/**` 경로는 토큰 검증 제외
  - `Authorization: Bearer {token}` 헤더에서 토큰 추출
  - 토큰 유효성 검증 후 `SecurityContext`에 인증 정보 설정

## 4. JWT 토큰 구조

### 4.1 토큰 생성 (JWTUtil)

```java
// 헤더
{
  "typ": "JWT",
  "alg": "HS256"
}

// 페이로드
{
  "customerId": "사용자ID",
  "exp": "만료시간"
}

// 서명: HS256 알고리즘으로 secretKey 사용
```

### 4.2 토큰 설정

- **알고리즘**: HS256
- **유효기간**: 1일 (24시간)
- **시크릿 키**: `jwt.secret.key` 프로퍼티에서 관리
- **클레임**: `customerId` (사용자 식별자)

## 5. 보안 설정 (SecurityConfig)

### 5.1 접근 권한 설정

```java
// 허용 경로
.requestMatchers("/auth/**", "/api/public/**").permitAll()
.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

// 인증 필요 경로
.anyRequest().authenticated()
```

### 5.2 보안 기능 설정

- **CSRF**: 비활성화 (API 서버)
- **CORS**: 활성화 (CorsConfig 사용)
- **Form Login**: 비활성화 (JWT 기반)
- **Logout**: 비활성화 (JWT 기반)

### 5.3 예외 처리

- **인증 실패**: 401 Unauthorized JSON 응답
- **토큰 오류**: AccessTokenException으로 세분화된 에러 처리

## 6. 사용자 인증 서비스

### 6.1 ApiCustomerDetailsService

- **역할**: 사용자 정보 로드 및 인증
- **동작**:
  - `emailId`로 사용자 조회
  - 차단된 사용자 검증
  - `ApiCustomerDTO` 객체 생성 및 반환

### 6.2 ApiCustomerDTO

- **역할**: Spring Security UserDetails 구현
- **권한**: `ROLE_USER` (모든 인증된 사용자)

## 7. 토큰 예외 처리

### 7.1 AccessTokenException 타입

```java
public enum TOKEN_ERROR {
    UNACCEPT(401, "Token is null or too short"),    // 토큰 없음/짧음
    BADTYPE(401, "Token type Bearer"),              // Bearer 타입 아님
    MALFORM(403, "Malformed Token"),                // 잘못된 형식
    BADSIGN(403, "BadSignatured Token"),            // 잘못된 서명
    EXPIRED(403, "Expired Token")                   // 만료된 토큰
}
```

### 7.2 예외 응답 형식

```json
{
  "msg": "에러 메시지",
  "time": "2024-01-01T00:00:00.000Z"
}
```

## 8. API 엔드포인트 보안

### 8.1 공개 API (인증 불필요)

- `/auth/**` - 로그인 관련
- `/api/public/**` - 공개 결제 API

### 8.2 보호된 API (인증 필요)

- `/api/checkin/**` - 출석체크 관련
- `/api/payment/**` (public 제외) - 결제 관련
- 기타 모든 `/api/**` 경로

## 9. 보안 고려사항

### 9.1 현재 구현의 장점

- JWT 기반 Stateless 인증
- 세분화된 토큰 예외 처리
- CORS 설정으로 프론트엔드 연동 지원
- 사용자 차단 기능

### 9.2 개선 고려사항

- Refresh Token 도입 검토
- 토큰 블랙리스트 관리 (로그아웃 시)
- Rate Limiting 적용
- 토큰 만료 시간 설정 가능화

## 10. 사용 예시

### 10.1 로그인 요청

```bash
POST /auth
Content-Type: application/json

{
  "emailId": "user@example.com",
  "pwd": "password123"
}
```

### 10.2 인증된 API 요청

```bash
GET /api/checkin/today
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 10.3 컨트롤러에서 사용자 ID 추출

```java
@PostMapping("/roulette")
public int rouletteCheckIn(HttpServletRequest request, @RequestParam int point) throws Exception {
    String customerId = jwtUtil.extractCustomerId(request);
    // customerId 사용
}
```

---
