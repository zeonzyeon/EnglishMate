# EnglishMate

> **개인 영어 학습 자료에서 단어를 추출하고, 나만의 단어장과 맞춤형 학습을 제공하는 영어 학습 웹 서비스**

EnglishMate는 사용자가 직접 영어 학습 자료를 입력하면 텍스트에서 영단어를 추출하고, 개인 단어장을 생성하여 **단어 학습 → 퀴즈 → 복습**까지 이어질 수 있도록 만든 영어 학습 서비스입니다.

단순히 단어를 저장하는 것에 그치지 않고, 사용자가 입력한 학습 자료를 기반으로 단어를 관리하고 학습 결과를 기록하여 반복적으로 복습할 수 있도록 구현했습니다.

---

## 주요 기능

### 1. 회원가입 / 로그인

* 회원가입 및 로그인
* 사용자별 데이터 관리
* 로그인한 사용자에 따른 개인 단어장 및 학습 기록 제공

### 2. 학습 자료 관리

사용자가 직접 영어 학습 자료를 등록하고 관리할 수 있습니다.

* 학습 자료 등록 / 조회 / 수정 / 삭제
* 등록된 학습 자료를 기반으로 영단어 추출

### 3. 영단어 추출 및 정규화

입력한 영어 학습 자료에서 학습에 필요한 영단어를 추출합니다.

* 텍스트에서 영단어 자동 추출
* 대소문자 정규화
* 중복 단어 처리
* 불필요한 문자열 제거
* 추출된 단어를 개인 단어장과 연결

### 4. My Vocabulary

사용자가 학습 자료에서 추출한 단어를 개인 단어장에서 관리할 수 있습니다.

* 개인 단어장 조회
* 단어 및 뜻 확인
* 단어의 출처 확인

> 어떤 학습 자료에서 해당 단어가 추출되었는지 확인할 수 있도록 출처 정보를 제공합니다.

### 5. Quiz

저장된 단어를 활용하여 문제를 풀고 학습 결과를 저장할 수 있습니다.

* 단어 의미 기반 퀴즈
* 정답 확인
* 퀴즈 결과 저장
* 학습 결과 확인

### 6. Review

단어를 반복해서 학습할 수 있도록 플래시카드 형태의 복습 기능을 제공합니다.

* 단어 플래시카드
* 단어와 뜻 확인
* 복습 진행
* 복습 결과 기록

### 7. Review Statistics

최근 학습 데이터를 기반으로 복습 현황을 확인할 수 있습니다.

* 최근 7일 학습 통계
* 퀴즈 / 복습 기록 확인

---

##  기술 스택

### Backend

* Java 21
* Spring Boot
* Spring Data JPA

### Frontend

* Thymeleaf
* HTML5
* CSS3
* JavaScript

### Database

* MySQL

---

## 화면

<img width="2400" alt="Home" src="https://github.com/user-attachments/assets/b72aa506-5349-4467-9b1f-fcad257e928d" />
<img width="2400" alt="Login" src="https://github.com/user-attachments/assets/88f43393-82cf-4bcf-b3a3-1ad280df57cd" />

---

## 서비스 흐름

```text
회원가입 / 로그인
        ↓
학습 자료 등록
        ↓
영어 텍스트 입력
        ↓
영단어 추출 및 정규화
        ↓
My Vocabulary
        ↓
      ┌───────────────┐
      ↓               ↓
    Quiz            Review
      ↓               ↓
  결과 저장       복습 기록 저장
      └───────┬───────┘
              ↓
       최근 7일 통계
```

---

## 실행 방법

### 1. 프로젝트 Clone

```bash
git clone https://github.com/your-username/EnglishMate.git
cd EnglishMate
```

### 2. 데이터베이스 설정

MySQL에서 EnglishMate용 데이터베이스를 생성합니다.

```sql
CREATE DATABASE englishmate;
```

### 3. 데이터베이스 연결 설정

`application.yml` 또는 `application.properties`에 자신의 MySQL 환경에 맞게 설정합니다.

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/englishmate
    username: YOUR_USERNAME
    password: YOUR_PASSWORD
```

### 4. 프로젝트 실행

Gradle을 이용해 Spring Boot 애플리케이션을 실행합니다.

```bash
./gradlew bootRun
```

Windows에서는:

```bash
gradlew.bat bootRun
```

실행 후 브라우저에서 접속합니다.

```text
http://localhost:8080
```

---

## 향후 개선 사항

* 오답·미암기 단어 반복 학습 기능
* 영단어 추출 및 정규화 정확도 개선 (영어 NLP API 활용)
* AI 기반 문맥별 단어 의미 분석
* 단어 난이도 및 품사 자동 분류
* 사용자별 맞춤형 문제 생성 고도화
* 학습 목표 설정 및 연속 학습 기록
* 이메일 인증을 통한 비밀번호 찾기
* 학습 데이터 시각화
* 클라우드 환경 배포
