# OhgoodpayBE

## 개발 환경 빠른 시작

### 1) Java 버전 확인
```bash
java --version
# JDK 17 필요
```

### 2) FFmpeg 설치
```bash
# Windows
# https://www.ffmpeg.org/download.html 에서 다운로드 후 PATH 등록

# macOS
brew install ffmpeg

# Linux / EC2
sudo yum install -y ffmpeg

# 설치 확인
ffmpeg -version
```

### 3) 프로젝트 빌드
```bash
# Windows
./gradlew.bat clean build

# macOS / Linux
./gradlew clean build
```

### 4) 서버 실행
```bash
# Gradle로 실행
./gradlew bootRun

# 또는 JAR 직접 실행
java -jar build/libs/*.jar
```

**접속 URL**
- Swagger: http://localhost:8080/swagger-ui/index.html
- API Base: http://localhost:8080

### 5) 의존성 관리
의존성 추가/변경 시 build.gradle 수정 후 동기화
```bash
./gradlew clean build    # 의존성 업데이트
./gradlew dependencies   # 의존성 확인
```

---

## 기술 스택
- **JDK**: 17
- **Framework**: Spring Boot 3.5.5
- **Database**: MariaDB
- **Build**: Gradle (Groovy)

## 주요 의존성
- Spring Boot DevTools
- Spring Data JPA
- Spring Security
- MariaDB Driver
- Lombok
- JWT (jsonwebtoken 0.12.6)
- QueryDSL 5.0.0
- Swagger 2.6.0
- FFmpeg 8.0
- ZXing (QR/바코드)
  - com.google.zxing:core:3.5.2
  - com.google.zxing:javase:3.5.2

## 데이터베이스 설정
```properties
# application.properties
spring.datasource.url=jdbc:mariadb://localhost:3306/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
```

## 배포

### Docker 배포
```bash
# 이미지 빌드
docker build -t app-backend .

# 컨테이너 실행
docker run -p 8080:8080 app-backend
```

### Podman 배포
- 애플리케이션 이미지에 FFmpeg 포함 필수
- `/tmp` 폴더 권한 확인 필요
