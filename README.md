# 🍃 Kanban Backend API

> **Project Status**: 🔄 Connected with Frontend Repo

### 🔗 Repository Links
- **Backend (Current)**: [https://github.com/Yithian01/kanban-backend.git](https://github.com/Yithian01/kanban-backend.git)
- **Frontend (Partner)**: [https://github.com/Yithian01/kanban-frontend.git](https://github.com/Yithian01/kanban-frontend.git)

---

## 🛠 Project Overview
본 프로젝트는 **Spring Boot 3** 기반의 칸반 보드 API 서버입니다.
JWT 인증 및 Redis를 통한 토큰 관리 시스템이 구축되어 있으며, 위 프론트엔드 레포지토리와 연동되어 동작합니다.

---
<br>

# 📋 Git Commit Convention (Backend)

Java/Spring 기반의 백엔드 레포지토리 컨벤션입니다.

### 1. 커밋 메시지 구조
```text
<type>: <subject>

<body>
```

### 2. 커밋 타입 (Type)
| 타입 | 설명 |
| :--- | :--- |
| **feat** | 새로운 API 엔드포인트 추가, 서비스 로직 구현 |
| **fix** | 버그 수정 (예: NullPointerException 해결) |
| **refactor** | 기능 변경 없는 코드 개선 (코드 구조 변경, 가독성 향상) |
| **chore** | 빌드 설정(Gradle), 의존성 추가, 주석 수정 등 코드 외적 변경 |
| **docs** | README, Javadoc 주석 수정 |

### 3. 규칙 및 제약 사항
1.  **제목과 본문 사이**: 반드시 **1줄의 공백**을 둡니다.
2.  **함수 주석 필수**: 모든 `Method` 위에는 필수적으로 `/** ... */` 형태의 JavaDoc 주석을 작성하여 API 기능 및 파라미터를 설명합니다.

### 4. 커밋 예시
```text
feat: 작업(Task) 생성 API 구현

- TaskController 생성
- TaskService 내 비즈니스 로직 구현
- DTO 유효성 검증 로직 추가
```