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

주요 테이블 개요:
- `users` : 회원 정보(이메일, BOJ 핸들, 스트릭 등)
- `groups`, `group_members` : 그룹 및 멤버십, PIN 4자리로 참가
- `solve_snapshot` : 매일 00:05 KST 시점 total solved 수 스냅샷
- `daily_solve` : 당일 풀었던 문제 번호 및 개수 저장(JSON 텍스트)
- `punishments` : 그룹별 벌칙자 기록

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

