# OhGoodPay 보안 파일별 역할 정리

## 1. 설정 파일 (Config)

### SecurityConfig.java

**경로**: `src/main/java/com/ohgoodteam/ohgoodpay/security/config/SecurityConfig.java`
**역할**: Spring Security의 핵심 설정 클래스
**주요 기능**:

- HTTP 보안 규칙 정의 (인증 필요/불필요 경로)
- 필터 체인 구성 및 순서 설정
- CORS, CSRF 설정
- AuthenticationManager 설정
- 예외 처리 설정 (401 에러 응답)
- PasswordEncoder 빈 등록

## 2. 필터 클래스 (Filter)

### ApiLoginFilter.java

**경로**: `src/main/java/com/ohgoodteam/ohgoodpay/security/filter/ApiLoginFilter.java`
**역할**: 로그인 인증을 처리하는 커스텀 필터
**주요 기능**:

- `/auth` 경로의 POST 요청 처리
- JSON 요청에서 `emailId`, `pwd` 추출
- `UsernamePasswordAuthenticationToken` 생성
- `AuthenticationManager`를 통한 인증 처리
- `AbstractAuthenticationProcessingFilter` 상속

### ApiLoginSuccessHandler.java

**경로**: `src/main/java/com/ohgoodteam/ohgoodpay/security/filter/ApiLoginSuccessHandler.java`
**역할**: 로그인 성공 시 JWT 토큰을 생성하고 응답하는 핸들러
**주요 기능**:

- 인증 성공 시 JWT 토큰 생성 (1일 유효기간)
- `customerId`를 클레임으로 포함
- JSON 형태로 `accessToken` 응답
- `AuthenticationSuccessHandler` 인터페이스 구현

### TokenCheckFilter.java

**경로**: `src/main/java/com/ohgoodteam/ohgoodpay/security/filter/TokenCheckFilter.java`
**역할**: JWT 토큰 검증 및 인가 처리를 담당하는 필터
**주요 기능**:

- `/api` 경로만 처리 (일반 접속 제외)
- `/api/public/**` 경로는 토큰 검증 제외
- `Authorization: Bearer {token}` 헤더에서 토큰 추출
- JWT 토큰 유효성 검증
- SecurityContext에 인증 정보 설정
- `OncePerRequestFilter` 상속

## 3. 서비스 클래스 (Service)

### ApiCustomerDetailsService.java

**경로**: `src/main/java/com/ohgoodteam/ohgoodpay/security/service/ApiCustomerDetailsService.java`
**역할**: 사용자 정보를 로드하고 인증하는 서비스
**주요 기능**:

- `emailId`로 사용자 조회
- 차단된 사용자 검증 (`isBlocked()` 체크)
- `ApiCustomerDTO` 객체 생성 및 반환
- `UserDetailsService` 인터페이스 구현
- `CustomerRepository`와 `CustomerService` 의존성 사용

## 4. 유틸리티 클래스 (Util)

### JWTUtil.java

**경로**: `src/main/java/com/ohgoodteam/ohgoodpay/security/util/JWTUtil.java`
**역할**: JWT 토큰 생성, 검증, 추출을 담당하는 유틸리티
**주요 기능**:

- JWT 토큰 생성 (`generateToken()`)
- JWT 토큰 검증 (`validateToken()`)
- HTTP 요청에서 토큰 추출 (`extractTokenFromRequest()`)
- 요청에서 고객 ID 추출 (`extractCustomerId()`)
- HS256 알고리즘 사용
- `jwt.secret.key` 프로퍼티에서 시크릿 키 관리

## 5. DTO 클래스 (Data Transfer Object)

### ApiCustomerDTO.java

**경로**: `src/main/java/com/ohgoodteam/ohgoodpay/security/dto/ApiCustomerDTO.java`
**역할**: 인증된 사용자 정보를 담는 DTO
**주요 기능**:

- Spring Security의 `User` 클래스 상속
- `customerId`, `pwd` 필드 추가
- Spring Security 인증 시스템과 연동
- `GrantedAuthority` 컬렉션으로 권한 관리

## 6. 예외 처리 클래스 (Exception)

### AccessTokenException.java

**경로**: `src/main/java/com/ohgoodteam/ohgoodpay/security/exception/AccessTokenException.java`
**역할**: JWT 토큰 관련 예외를 처리하는 커스텀 예외 클래스
**주요 기능**:

- 토큰 오류 타입별 상수 정의 (`TOKEN_ERROR` enum)
- HTTP 상태 코드와 에러 메시지 매핑
- JSON 형태의 에러 응답 생성 (`sendResponseError()`)
- 5가지 토큰 오류 타입 지원:
  - `UNACCEPT`: 토큰 없음/짧음 (401)
  - `BADTYPE`: Bearer 타입 아님 (401)
  - `MALFORM`: 잘못된 형식 (403)
  - `BADSIGN`: 잘못된 서명 (403)
  - `EXPIRED`: 만료된 토큰 (403)

## 7. 파일별 의존성 관계

```
SecurityConfig
├── JWTUtil (토큰 유틸리티)
├── CorsConfig (CORS 설정)
├── ApiCustomerDetailsService (사용자 서비스)
├── ApiLoginFilter (로그인 필터)
│   └── ApiLoginSuccessHandler (성공 핸들러)
│       └── JWTUtil
└── TokenCheckFilter (토큰 검증 필터)
    ├── JWTUtil
    └── AccessTokenException

ApiCustomerDetailsService
├── CustomerRepository (사용자 저장소)
├── CustomerService (사용자 서비스)
└── ApiCustomerDTO (사용자 DTO)
```

## 8. 파일별 처리 흐름

### 로그인 흐름

1. **ApiLoginFilter** → 요청 받기
2. **ApiCustomerDetailsService** → 사용자 정보 로드
3. **ApiLoginSuccessHandler** → 성공 시 JWT 생성
4. **JWTUtil** → 토큰 생성

### API 요청 흐름

1. **TokenCheckFilter** → 토큰 검증
2. **JWTUtil** → 토큰 유효성 검증
3. **AccessTokenException** → 오류 시 예외 처리
4. **SecurityContext** → 인증 정보 설정

## 9. 설정 파일

### application.properties

**경로**: `src/main/resources/application.properties`
**역할**: JWT 시크릿 키 등 보안 관련 설정
**주요 설정**:

- `jwt.secret.key`: JWT 서명용 시크릿 키

## 10. 각 파일의 핵심 메서드

### SecurityConfig

- `securityFilterChain()`: 보안 필터 체인 구성
- `passwordEncoder()`: BCrypt 패스워드 인코더 빈 등록

### ApiLoginFilter

- `attemptAuthentication()`: 로그인 인증 시도

### ApiLoginSuccessHandler

- `onAuthenticationSuccess()`: 인증 성공 시 JWT 토큰 생성

### TokenCheckFilter

- `doFilterInternal()`: 토큰 검증 및 인가 처리
- `validateAccessToken()`: 토큰 유효성 검증

### ApiCustomerDetailsService

- `loadUserByUsername()`: 사용자 정보 로드

### JWTUtil

- `generateToken()`: JWT 토큰 생성
- `validateToken()`: JWT 토큰 검증
- `extractCustomerId()`: 요청에서 고객 ID 추출

### AccessTokenException

- `sendResponseError()`: JSON 형태의 에러 응답 생성

---
