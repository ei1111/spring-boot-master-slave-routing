## 🚀 기술 스택 (Tech Stack)

- **Java 21**
- **Spring Boot 4.0.1**
- **Spring Data JPA (Hibernate)**
- **MySQL 8.x**
- **HikariCP**
- **Docker / Docker Compose**
- **Master–Slave Replication**
- **AbstractRoutingDataSource**
- **Transaction AOP**
- **ThreadLocal Context**
---
## 📌 Architecture Overview

Write 트래픽과 Read 트래픽을 분리하여
DB 부하를 효율적으로 분산시키는 구조를 목표로 합니다.

- **Write 트랜잭션 → Master DB**

- **Read 트랜잭션 → Slave DB**

- **Spring의 @Transactional(readOnly = true) 속성을 기준으로 런타임에 DataSource를 동적으로 라우팅**

---

## 📌 로직
```text
[ Service 메서드 호출 ]
        │
        ▼
[ @Transactional 진입 (AOP) ]
        │
        ▼
[ readOnly 여부 판단 ]
        │
        ▼
[ ThreadLocal 에 DB 타입 저장 ]
[   MASTER / SLAVE          ]
        │
        ▼
[ RoutingDataSource ]
[ determineCurrentLookupKey() ]
        │
        ▼
[ DataSource 선택 ]
[  - Master DB    ]
[  - Slave DB     ]
        │
        ▼
[ Connection 획득 ]
        │
        ▼
[ SQL 실행 ]
```