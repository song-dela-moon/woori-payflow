```markdown
# 🚀 MSA 완성형 구조 (Eureka + API Gateway)

본 프로젝트는 **Spring Cloud 기반 MSA 구조**를 적용하여  
서비스 디스커버리와 API Gateway를 포함한 실제 운영 환경에 가까운 아키텍처를 구성하였습니다.

---

## 🏗️ 전체 아키텍처

Client 요청은 API Gateway를 통해 진입하며,  
각 서비스는 Eureka에 등록되어 동적으로 라우팅됩니다.

```

Client
↓
API Gateway
↓
├── Orchestrator Service
│      ↓
│   ├── Card Service
│   └── Bank Service
│
└── 기타 서비스 확장 가능
↓
Webhook → Merchant Service

```

---

## 📦 서비스 구성

### 1. API Gateway
- 모든 클라이언트 요청의 단일 진입점 (Single Entry Point)
- 라우팅 및 필터링 담당

**주요 역할**
- 서비스 라우팅 (`/card/**`, `/bank/**`)
- 인증/인가 처리 (확장 가능)
- 로깅 및 공통 필터

---

### 2. Eureka Server (Service Discovery)
- 서비스 등록 및 조회 역할

**특징**
- 각 서비스는 실행 시 Eureka에 자동 등록
- Gateway 및 서비스 간 호출 시 서비스 이름으로 접근 가능

---

### 3. Orchestrator Service
- 전체 결제 흐름 제어
- 서비스 간 호출 조합

---

### 4. Card Service
- 카드 승인 / 취소 처리
- 독립적인 결제 서비스

---

### 5. Bank Service
- 계좌 이체 및 잔액 처리
- 확장 가능한 결제 수단 서비스

---

## 🔄 서비스 등록 흐름

```

각 서비스 실행
↓
Eureka Server 등록
↓
API Gateway가 서비스 목록 조회
↓
요청 시 동적으로 라우팅

```

---

## 🔄 요청 처리 흐름

### 💳 카드 결제
```

Client
↓
API Gateway
↓
Orchestrator Service
↓
Card Service (Eureka 기반 호출)
↓
Orchestrator
↓
Webhook → Merchant

```

---

### 🏦 계좌 결제
```

Client
↓
API Gateway
↓
Orchestrator Service
↓
Bank Service
↓
Orchestrator
↓
Webhook → Merchant

```

---

## ⚙️ 핵심 기술 요소

### ✔️ Service Discovery (Eureka)
- 서비스 위치를 직접 지정하지 않음
- 서비스 이름 기반 호출

**예시**
```

[http://CARD-SERVICE/card/approve](http://CARD-SERVICE/card/approve)
[http://BANK-SERVICE/bank/transfer](http://BANK-SERVICE/bank/transfer)

```

---

### ✔️ API Gateway
- 클라이언트는 내부 서비스 구조를 알 필요 없음
- Gateway가 모든 요청을 라우팅

---

### ✔️ Load Balancing
- 동일 서비스 여러 인스턴스 존재 시
- Gateway 또는 클라이언트에서 로드밸런싱 수행

---

### ✔️ FeignClient (사용 시)
- 선언형 HTTP Client
- 서비스 이름 기반 호출

---

## ⚡ MSA 설계 고도화 포인트

### 1. 서비스 위치 투명성 (Location Transparency)
- IP/Port가 아닌 서비스 이름 기반 통신
- Eureka가 위치 관리

---

### 2. 진입점 단일화 (Single Entry Point)
- 모든 요청은 Gateway를 통해 처리  
👉 보안 및 관리 용이

---

### 3. 서비스 확장성
- Card Service만 여러 인스턴스로 확장 가능
- 트래픽 분산 처리

---

### 4. 장애 대응 구조
- 특정 서비스 장애 시 다른 서비스 영향 최소화  
(확장 시 Circuit Breaker 적용 가능)

---

### 5. 유연한 라우팅
- Gateway 설정만으로 라우팅 변경 가능

---

## 🔐 (확장 가능) 고급 기능
- JWT 인증 필터 (Gateway)
- Rate Limiting
- Circuit Breaker (Resilience4j)
- Config Server (중앙 설정 관리)

---

## 🎯 한 줄 요약

Eureka 기반 서비스 디스커버리와 API Gateway를 적용하여  
서비스 간 결합도를 낮추고 확장성과 유연성을 확보한 MSA 아키텍처
```
