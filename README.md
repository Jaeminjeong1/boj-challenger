# 1일 1백준 벌칙자 플랫폼

백준 solved.ac 데이터를 기반으로 매일 자정에 풀이 스냅샷을 저장하고, 당일 풀이 현황을 집계하여 그룹별 벌칙자를 선정하는 풀스택 애플리케이션입니다. 백엔드는 Spring Boot 3.x(Java 21), 프런트엔드는 Vue 3 + Vite를 사용합니다.

## 프로젝트 구조

```
bojchallenge/
├─ src/main/java/com/example/bojchallenge/...   # Spring Boot 소스
├─ frontend/                                    # Vue 3 + Vite 프런트
├─ build.gradle                                 # Gradle 설정
└─ README.md
```

## 백엔드 실행

### 요구 사항
- Java 21
- MySQL 8.x (또는 호환 버전)
- Gradle Wrapper 사용 (이미 포함)

### 환경 설정
`src/main/resources/application.yml`에서 다음 항목을 필요에 맞게 설정하세요.

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bojchallenge
    username: root
    password: password
jwt:
  secret: please-change-me
  expiration-ms: 3600000
```

운영 환경은 `prod` 프로필에 맞춰 별도 값으로 주입할 수 있습니다. 테스트 프로필(`application-test.yml`)은 H2(In-Memory) DB를 사용합니다.

### DB 초기화
프로젝트 시작 시 Flyway 대신 JPA `ddl-auto=update` 설정을 사용합니다. 필요하다면 Flyway 또는 `schema.sql`을 추가해도 무방합니다.

주요 테이블 개요:
- `users` : 회원 정보(이메일, BOJ 핸들, 스트릭 등)
- `groups`, `group_members` : 그룹 및 멤버십, PIN 4자리로 참가
- `solve_snapshot` : 매일 00:05 KST 시점 total solved 수 스냅샷
- `daily_solve` : 당일 풀었던 문제 번호 및 개수 저장(JSON 텍스트)
- `punishments` : 그룹별 벌칙자 기록

### 실행
```bash
./gradlew bootRun
```
애플리케이션은 기본적으로 `http://localhost:8080`에서 실행되며, Swagger UI는 `http://localhost:8080/swagger-ui.html`에서 확인할 수 있습니다.

### 스케줄러
- `SolvedService.takeMidnightSnapshotForAllUsers()`가 `@Scheduled(cron = "0 5 0 * * *", zone = "Asia/Seoul")`으로 등록되어 있어 매일 00:05(KST)에 전체 사용자 스냅샷을 저장합니다.
- 그룹 대시보드/강제 새로고침 시 solved.ac API를 조회하여 당일 풀이 현황과 벌칙자를 재계산합니다.

### solved.ac 연동
- `RestClient`를 통해 `user/show`, `user/solved` 엔드포인트를 호출하며, 메모리 캐시와 Graceful Fallback을 제공합니다.
- 호출 실패 시 마지막 캐시 데이터를 사용합니다.

### 보안
- `/api/auth/**` 경로는 인증 없이 접근 가능합니다.
- JWT Access Token만 사용하며 만료 시간은 1시간입니다.
- 엔티티는 순수 JPA 객체를 유지하며, 컨트롤러는 DTO만 반환합니다.

### 테스트
샘플 단위/통합 테스트는 다음 명령으로 실행할 수 있습니다.
```bash
./gradlew test
```
`JwtProviderTest`, `UserServiceTest`, `GroupServiceTest`가 포함되어 있으며, 테스트 프로필은 H2 데이터베이스를 사용합니다.

## 프런트엔드 실행

### 요구 사항
- Node.js 20+
- npm

### 설치 및 실행
```bash
cd frontend
npm install
npm run dev
```

Vite 개발 서버는 기본적으로 `http://localhost:5173`에서 실행되며, `/api` 요청은 프록시를 통해 백엔드 (`http://localhost:8080`) 로 전달됩니다.

### 주요 화면
- **로그인 / 회원가입** : JWT 발급 및 저장(Pinia + LocalStorage)
- **대시보드** : 내 BOJ 핸들, 오늘 푼 문제, 스트릭 카드 표시
- **그룹 목록** : 그룹 생성/참가, 내 그룹 카드 리스트
- **그룹 상세** : 멤버별 오늘 풀이 문제, 벌칙 대상 표시, 새로고침 버튼

## API 요약
- `POST /api/auth/signup` 회원가입
- `POST /api/auth/login` 로그인(JWT 발급)
- `GET /api/me/dashboard` 내 대시보드 정보
- `POST /api/groups` 그룹 생성
- `POST /api/groups/join` 그룹 참가
- `GET /api/groups/my` 내 그룹 카드
- `GET /api/groups/{id}` 그룹 대시보드
- `POST /api/groups/{id}/refresh` 그룹 현황 강제 새로고침
- `GET /api/problems/{number}/url` 백준 문제 URL 반환

## 개발 편의 도구
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI 문서: `http://localhost:8080/v3/api-docs`

## 다음 단계 제안
1. solved.ac API Rate Limit 대비 외부 캐시(Redis 등) 도입
2. 벌칙 내역에 대한 별도 알림(메일/슬랙) 연동
3. 프런트엔드 컴포넌트 테스트(Vitest) 추가 및 e2e 자동화
