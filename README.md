# EnglishMate

> **개인 영어 학습 자료에서 단어를 추출하고, 나만의 단어장과 맞춤형 학습을 제공하는 영어 학습 웹 서비스**

EnglishMate는 사용자가 직접 영어 학습 자료를 입력하면 텍스트에서 영단어를 추출하고, 개인 단어장을 생성하여 **단어 학습 → 퀴즈 → 복습**까지 이어질 수 있도록 만든 영어 학습 서비스입니다.

단순히 단어를 저장하는 것에 그치지 않고, 사용자가 입력한 학습 자료를 기반으로 단어를 관리하고 학습 결과를 기록하여 반복적으로 복습할 수 있도록 구현했습니다.

<br>

## 주요 기능

### 1. 회원가입 / 로그인

* 회원가입 및 로그인
* 사용자별 데이터 관리
* 로그인한 사용자에 따른 개인 단어장 및 학습 기록 제공

### 2. 학습 자료 관리

* 학습 자료 등록 / 조회 / 수정 / 삭제
* 등록된 학습 자료를 기반으로 영단어 추출

### 3. 영단어 추출 및 정규화

* 텍스트에서 영단어 자동 추출
* 대소문자 정규화
* 중복 단어 처리
* 불필요한 문자열 제거
* 추출된 단어를 개인 단어장과 연결

### 4. My Vocabulary

* 개인 단어장 조회
* 단어 및 뜻 확인
* 단어의 출처 확인

> 어떤 학습 자료에서 해당 단어가 추출되었는지 확인할 수 있도록 출처 정보를 제공합니다.

### 5. Quiz

* 단어 의미 기반 퀴즈
* 정답 확인
* 퀴즈 결과 저장
* 학습 결과 확인

### 6. Review

* 단어 플래시카드
* 단어와 뜻 확인
* 외웠어요 / 다시 볼래요 기록
* 복습 결과 기록

### 7. Review Statistics

* 최근 7일 학습 통계
* 퀴즈 / 복습 기록 확인

<br>

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

<br>

## 화면

### 1. Home (비회원)
<img width="2400" alt="Home" src="https://github.com/user-attachments/assets/b72aa506-5349-4467-9b1f-fcad257e928d" />

### 2. Login
<img width="2400" alt="Login" src="https://github.com/user-attachments/assets/88f43393-82cf-4bcf-b3a3-1ad280df57cd" />

### 3. Home (회원)
<img width="2400" alt="Home" src="https://github.com/user-attachments/assets/681734f0-ece3-428c-8ef5-22d4d565f5e4" />

### 4. Study Materials
#### 4-1. 학습 지문 목록
<img width="2400" alt="Study Materials" src="https://github.com/user-attachments/assets/93dc8d27-d8a0-4771-9ec4-c945b2c6ac13" />

#### 4-2. 단어 추출
<img width="2400" alt="Study Materials" src="https://github.com/user-attachments/assets/c8cb28ae-7962-477f-b639-eca952e4bcad" />

### 5. My Vocabulary
#### 5-1. 단어장 목록
<img width="2400" alt="My Vocabulary" src="https://github.com/user-attachments/assets/5d4fa7da-1646-44f3-9814-e4108703178c" />

#### 5-2. 단어 정보 수정
<img width="2400" alt="My Vocabulary" src="https://github.com/user-attachments/assets/44b1a57e-cb69-45bd-b68b-e54de637d14a" />

### 6. Quiz
#### 6-1. 퀴즈 유형 및 학습자료 선택
<img width="2400" alt="Quiz" src="https://github.com/user-attachments/assets/4b57b336-feff-4752-9f48-72f35ef6ba70" />

#### 6-2. 퀴즈 풀기
<img width="2400" alt="Quiz" src="https://github.com/user-attachments/assets/e3e42e8b-434e-430c-a893-aa04dccd2fc4" />

#### 6-3. 퀴즈 결과
<img width="2400" alt="Quiz" src="https://github.com/user-attachments/assets/adff0357-8211-481f-996d-5dddedf6241c" />

### 7. Review
#### 7-1. 학습자료 선택
<img width="2400" alt="Review" src="https://github.com/user-attachments/assets/71ad2ce8-9317-44b3-94b9-cd13ab41bf11" />

#### 7-2. 플래시카드
<img width="2400" alt="Review" src="https://github.com/user-attachments/assets/d3deb697-e5ca-4db4-a455-5e52abb3002e" />

#### 7-3. 복습 결과
<img width="2400" alt="Review" src="https://github.com/user-attachments/assets/bc4639e4-0fa9-459f-b00f-58ce40afbdbc" />

### 8. MyPage
<img width="2400" alt="MyPage" src="https://github.com/user-attachments/assets/083be7b3-9294-40ea-832e-f147292a38ca" />

<br>
<br>

## 서비스 흐름

```text
    회원가입 / 로그인
            ↓
    Study Materials: : 학습 자료 등록 (영어 지문 입력)
            ↓
    영단어 추출 및 정규화
            ↓
    My Vocabulary : 단어 의미 및 품사 등록
            ↓
    ┌───────────────┐
    ↓               ↓
  Quiz            Review
    ↓               ↓
 결과 저장       복습 기록 저장
    └───────┬───────┘
            ↓
    MyPage :  최근 7일 학습 통계
```

<br>

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

<br>

## 한계점

### 영단어 정규화의 정확도 한계

현재 영어 학습 자료에서 추출한 단어의 기본형을 직접 구현한 정규화 로직을 통해 처리하고 있습니다.

단순한 규칙 기반으로 단어의 형태를 변환하기 때문에 다음과 같은 한계가 있습니다.

- 불규칙 동사의 기본형 변환 오류
- 동일한 형태의 단어가 문맥에 따라 다른 품사나 의미를 가지는 경우 처리 한계
- 단어의 활용형을 기본형으로 변환하는 과정에서 잘못된 단어로 정규화되는 경우
- 복합어, 파생어 및 예외적인 단어 형태에 대한 처리 한계

따라서 현재 정규화 과정에서 일부 단어가 잘못된 기본형으로 변환될 수 있으며, 이는 이후 단어장 및 Quiz 데이터의 정확도에도 영향을 줄 수 있습니다.

### 개선 방향

향후 영어 형태소 분석 또는 NLP API를 활용하여 문맥과 품사 정보를 함께 분석하고, 단어의 원형을 보다 정확하게 추출하는 방식으로 개선할 예정입니다.

<br>

## 향후 개선 사항

* 오답·미암기 단어 반복 학습 기능
* 영단어 추출 및 정규화 정확도 개선
* AI 기반 문맥별 단어 의미 분석
* 단어 난이도 및 품사 자동 분류
* 사용자별 맞춤형 문제 생성 고도화
* 학습 목표 설정 및 연속 학습 기록
* 이메일 인증을 통한 비밀번호 찾기
* 정렬 및 페이징 기능 추가
* 학습 데이터 시각화
* 클라우드 환경 배포
