# PG Server (Payment Gateway Core Engine)

카드사 직연동 기반의 온라인 결제 게이트웨이(PG) 서버입니다.
가맹점(Merchant)과 카드사 사이에서 결제 요청을 처리하고, 결제 상태 관리 및 웹훅 통보까지 담당합니다.
<img width="2130" height="659" alt="전체 흐름" src="https://github.com/user-attachments/assets/cef2f0ae-0079-47d3-82eb-4ed166b48dbc" />

---

## 🧩 주요 기능

### 1. 결제 생성 (TID 발급)

* 가맹점 인증 후 결제 요청을 등록
* 고유 결제 ID(TID, `paymentUid`) 생성
* 상태: `READY`

### 2. 결제 승인 (카드사 연동)

* 카드 정보 기반으로 카드사 승인 요청
* 승인 성공 시:

  * 결제 상태 `SUCCESS`
  * 승인 코드 저장
* 승인 실패 시:

  * 결제 상태 `FAIL`
  * 실패 코드/메시지 저장

### 3. 결제 취소 (전액 / 부분)

* 카드사 취소 요청 수행
* 전액 취소 → `CANCELED`
* 부분 취소 → `PARTIAL_CANCELED`
* 취소 실패 시 원장 유지 + 이력만 저장

### 4. 트랜잭션 이력 관리

* 승인/취소 요청별 이력 저장 (`PaymentTransaction`)
* SUCCESS / FAIL 결과 기록
* 외부 응답 코드 및 메시지 저장

### 5. 웹훅(Webhook) 비동기 전송

* 결제 결과를 가맹점 서버로 통보

  * 승인: `PAYMENT_APPROVED`
  * 실패: `PAYMENT_FAILED`
  * 취소: `PAYMENT_CANCELLED`
* 비동기 처리 (WebClient + Reactor)
* 전송 결과 DB 저장 (`WebhookHistory`)

### 6. 가맹점 인증

* `merchantUid + apiKey` 기반 검증
* 상태 체크 (`ACTIVE`, `INACTIVE` 등)

---

## 🏗️ 아키텍처

```
Controller
   ↓
Service (비즈니스 로직)
   ↓
Repository (DB 접근)
   ↓
External Client (Card / Webhook)

```
<img width="1920" height="1103" alt="시스템 아키텍처" src="https://github.com/user-attachments/assets/cf68027e-2527-4cf6-9e79-1d160742c857" />

### 구성 모듈

* `payment` : 결제 핵심 로직
* `card` : 카드사 API 연동
* `merchant` : 가맹점 관리 및 인증
* `webhook` : 가맹점 콜백 처리
* `common` : 공통 응답 / 예외 처리

---

## 🔄 핵심 동작 흐름

### 1. 결제 승인 흐름

```
[Client]
   ↓
POST /api/payments/create  → TID 발급
   ↓
POST /api/payments/approve
   ↓
PaymentService
   ↓
CardService → CardCompanyClient (외부 카드사)
   ↓
결과 반환
   ↓
PaymentCommandService → DB 업데이트
   ↓
WebhookService → 가맹점 콜백 (비동기)

```
<img width="3042" height="2142" alt="시퀀스 다이어그램(승인)" src="https://github.com/user-attachments/assets/4c82a5d6-dc49-493f-bbbf-aca34874a1f5" />

---

### 2. 결제 취소 흐름

```
POST /api/payments/cancel
   ↓
가맹점 인증
   ↓
CardService → 카드사 취소 요청
   ↓
성공 → 상태 변경 + 이력 저장
실패 → 이력만 저장
   ↓
Webhook 전송

```
<img width="3000" height="1714" alt="시퀀스 다이어그램(취소)" src="https://github.com/user-attachments/assets/4656df84-0dd6-44ee-8079-172bee68902c" />

---

## 🗂️ 주요 도메인

### Payment (결제 원장)

* 결제 상태 관리
* 승인/취소 금액 관리
* 상태:

  * READY
  * SUCCESS
  * FAIL
  * CANCELED
  * PARTIAL_CANCELED

### PaymentTransaction (이력)

* 승인/취소 요청 단위 기록
* 외부 응답 포함

### Merchant (가맹점)

* API Key 기반 인증
* 콜백 URL 보유

### WebhookHistory

* 웹훅 요청/응답 기록
* 성공 여부 저장

---

## ⚙️ 기술 스택

* **Backend**: Spring Boot
* **DB**: MySQL + JPA (Hibernate)
* **Async/Reactive**: WebClient, Reactor
* **Build Tool**: Gradle

---

## 🔐 공통 처리

### 예외 처리

* `BusinessException` 기반 커스텀 예외
* `GlobalExceptionHandler`로 통합 처리

### 응답 구조

```json
{
  "message": "성공",
  "status": 200,
  "data": {}
}
```

---

## 📌 API 요약

### 결제

| API                        | 설명    |
| -------------------------- | ----- |
| POST /api/payments/create  | 결제 생성 |
| POST /api/payments/approve | 결제 승인 |
| POST /api/payments/cancel  | 결제 취소 |

### 가맹점

| API                          | 설명     |
| ---------------------------- | ------ |
| POST /api/merchants/register | 가맹점 등록 |
| GET /api/merchants           | 가맹점 조회 |

---

## 💡 특징

* 카드사 직접 연동 구조
* 결제 상태와 트랜잭션 이력 분리 설계
* 웹훅 기반 이벤트 통보 시스템
* 부분 취소 지원
* 비동기 처리로 성능 개선

---

## 🚀 실행 방법

```bash
./gradlew build
./gradlew bootRun
```

---

## 📎 기타

* 초기 가맹점 데이터는 `data.sql`로 자동 삽입
* DB 스키마는 `schema.sql` 참고
* 카드사 API는 `application.yaml`에서 설정

---

## 🧠 한 줄 요약

> **결제 생성 → 카드 승인 → 상태 저장 → 웹훅 통보까지 전 과정을 처리하는 PG 코어 서버**
