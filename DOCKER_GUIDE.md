# Docker Compose with .env.docker to Spring Setup Guide

이 가이드는 로컬 환경에 있는 Spring 빌드파일인 app.jar 파일을 Docker 컨테이너에서 실행하고, 외부 환경 변수 파일(.env.docker)을 사용하여 데이터베이스 및 Redis 설정을 관리하는 방법을 설명합니다.

---

## 1. 파일 구조 준비
프로젝트 디렉토리에 다음 3개의 파일이 위치해야 합니다.

``` Python
/your-project-dir
├── app.jar
├── docker-compose.yml
└── .env.docker
```

---

## 2. Docker Compose 파일 설정 (docker-compose.yml)
이미지는 템플릿 이미지를 사용하고, 로컬의 JAR 파일을 컨테이너 내부에 마운트합니다.
환경 변수는 .env.docker에서 가져온 값을 매핑합니다.

```dockerfile
services:
backend:
image: eclipse-temurin:21-jdk-alpine
container_name: spring-dev
ports:
- "8080:8080"
volumes:
- ./app.jar:/app.jar
environment:
- DB_HOST=${DB_HOST}
- DB_PORT=${DB_PORT}
- DB_NAME=${DB_NAME}
- DB_USERNAME=${DB_USERNAME}
- DB_PASSWORD=${DB_PASSWORD}
- REDIS_HOST=${REDIS_HOST}
- REDIS_PORT=${REDIS_PORT}
- REDIS_PASSWORD=${REDIS_PASSWORD}
- JWT_SECRET=${JWT_SECRET}
command: java -jar /app.jar
extra_hosts:
- "host.docker.internal:host-gateway"
```
---

## 3. 환경 변수 파일 설정 (.env.docker)
프로젝트 루트 디렉토리에 .env.docker 파일을 생성합니다.
* 중요: 로컬 DB 접속 시 DB_HOST는 host.docker.internal이어야 합니다.

```text
# DB 설정
DB_HOST=host.docker.internal
DB_PORT="your-db-port"
DB_NAME="your-db-name"
DB_USERNAME="your-db-username"
DB_PASSWORD="your-db-password"

# Redis 설정 (SaaS)
REDIS_HOST="your-redis-host"
REDIS_PORT="your-redis-port"
REDIS_PASSWORD="your-redis-password"

# JWT 설정
JWT_SECRET="your-jwt-secret"
```


---

## 4. 컨테이너 실행 명령어
```text
--env-file 옵션을 사용하여 .env.docker 파일을 명시적으로 지정하여 실행합니다.
```

# 컨테이너 실행
```shell
$ docker-compose --env-file .env.docker up -d
```

# 실시간 로그 확인
```shell
$ docker logs -f spring-dev
```

# 컨테이너 중지 및 제거
```shell
$ docker-compose --env-file .env.docker down
```
