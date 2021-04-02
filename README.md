#   Document Approval

- 2021-03-31 : init, 최초 작성
  
## 0. Project Requirements
- Apache Maven 3.6.3
- jdk 1.8 +
- Spring Boot 2.4.4
- Lombok
- H2
- Mustache

## 1. 설치 방법 (Linux CentOS 기준)

1. Maven 설치  
  
	  ```shell script  
	  $ wget http://mirror.apache-kr.org/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz  
	  $ wget https://downloads.apache.org/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz  
	  $ tar -xvf apache-maven-3.6.3-bin.tar.gz  
	  $ ln -s apache-maven-3.6.3 maven 
	  ```   
	  
	```shell script  
	 $ vi ~/.bash_profile     
	   (in vi editor)  
		 export MAVEN_HOME=/usr/local/maven 
		 PATH=$PATH:$HOME/bin:$MAVEN_HOME/bin 
		 export PATH    
		 
	 $ source ~/.bash_profile  
	 ```   
5. Git Clone
	```
	$ git clone https://github.com/croquiscom-recruit/backend_TonyJev93.git
	```


## 2. 빌드 및 배포

### 1. Git clone 받은 프로젝트 Path로 이동

```
	$ cd {git-path}/URL-Shortener
```

### 2.  빌드 & 배포 스크립트 실행

```
	$ ./buildAndDeploy.sh
```

#### 2. 1. 스크립트 구성
**1. ./build.sh**
- **역할** : 메이븐 의존성 설치
	
* **빌드 순서**
	```
	1. mvn clean 
	2. mvn package
	```
**2. ./deploy.sh**

* **배포**
	 ```
	# jar file 위치로 이동
	$ cd ./target
	
	# jar 실행 (default port  : 8080)
	$ nohup java -jar -Dserver.port=8080 document-approval-0.0.1-SNAPSHOT.jar &

	# 서버 log 확인
	$ tail -f nohup.out
	```


## 3. 테스트 방법

- curl 명령어를 통해 테스트 시나리오에 대해 설명한다.
- 실제 요청에 대한 정리는 RestDocs API 문서를 통해 참고하는 것이 좋다.
- **API 문서 경로**
    - HTML : localhost:8080 (실제 서버가 올라간 서버IP, 서버 port 입력)
    - PDF : document-approval/src/main/resources/docs/api-guide.pdf
- **Postman** Export
    - 경로 : document-approval/src/main/resources/docs/Z-Z.postman_collection.json
    - 환경변수
        - BASE_URL : {{IP}}:{{PORT}} (ex. localhost:8080)
        - X-AUTH-TOKEN : {{JWT_TOKEN}} (ex. eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MSIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjE2MTcyMTMxODYsImV4cCI6MTYxNzIxNDk4Nn0.8Su_x5S_fyV8BcGxYpZz...
)

    
### 테스트 순서
1. 회원가입
2. 로그인
3. 문서 생성
4. 문서 조회 (단 건)
5. 결재
6. 반려
7. 문서 목록 조회 (OUTBOX / INBOX / ARCHIVE)

모든 상황에 대한 **요청/응답/컬럼설명에 대해  API문서를 통해 확인이 가능**합니다. API 문서를 참고하여 진행하시기 바랍니다.

**예외 처리에 대한 설명**은 API 문서에서 누락되어 아래와 같이 명시하도록 하겠습니다.

|구분 |상황| HTTP Status|
|--|--|--|
| 회원 가입 | 이미 존재하는 사용자 | 409   |
| 로그인 | 비밀번호 불일치 | 400 | 
| 로그인 | 사용자ID 존재하지 않은 경우 | 404 |  
| 문서 조회 | 해당 문서가 존재하지 않는 경우  | 404 |  
| 문서 등록 | 등록한 결재자가 존재하지 않는 사용자인 경우 | 404 |  
| 결재 | 결재 순서가 아닌 경우 | 400 |  
| 결재 | 결재 권한이 없는 경우 | 400 |  
| 결재 | 이미 결재한 문서를 결재할 경우 | 400 |  
| 결재 | 이미 처리된 문서를 결재할 경우 | 400 |  


### 1. 회원가입

- 문서 생성 및 결재/반려를 할 수 있는 회원을 생성한다.

- EX) curl 요청
	```
	curl 'http://localhost:8080/api/v1.0/join' -i -X POST \
        -H 'Content-Type: application/json' \
        -H 'Accept: application/json' \
        -d '{
      "userId" : "test0",
      "password" : "password123",
      "name" : "테스트0",
      "roles" : [ "ROLE_ADMIN" ]
    }'
	- 사용자 ID = test0, 비밀번호 : password123 으로 회원이 등록 된다.

### 2. 로그인

- 회원 가입된 사용자로 로그인을 요청한다.
- EX) curl 요청
	```
	$ curl 'http://localhost:8080/api/v1.0/login' -i -X POST \
          -H 'Content-Type: application/json' \
          -H 'Accept: application/json' \
          -d '{
        "userId" : "test0",
        "password" : "password123"
      }'
	```
- 응답 예시
	```
	{ "jwtToken" : "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MCIsInJvbGVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNjE3MTI4NjAyLCJleHAiOjE2MTcxMzA0MDJ9.UzoFG8nMIHV7TvgV9KEB6tp2gva9CtYFeXcCwEOt3cg" }
	```
	- 실제 토큰 값은 실시간으로 변경되므로 응답 받은 토큰을 잘 복사하여 관리한다.
	- JWT 토큰 안에는 사용자의 ID, 사용자 권한이 포함되어 있으며, 앞으로 /user/** 경로로 시작되는 모든 서비스에서 인증을 위해 사용된다.
	- JWT 토큰은 HTTP 요청시 Header에 X-AUTH-TOKEN에 싣어 보낸다.


### 3. 문서 생성

- 문서를 생성한다.
- EX) curl 요청
	```
	$ curl 'http://localhost:8080/api/v1.0/user/document' -i -X POST \
          -H 'Content-Type: application/json' \
          -H 'Accept: application/json' \
          -H 'X-AUTH-TOKEN: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MSIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjE2MTcxMjg2MDEsImV4cCI6MTYxNzEzMDQwMX0.p6fnoXH-FfcOQIFo-oKnCzE9qYvo8l7S1erisWQWOcs' \
          -d '{
        "title" : "문서의 제목을 입력해 주세요.",
        "classification" : "REPORT",
        "content" : "문서의 내용을 입력해 주세요.",
        "approverList" : [ {
          "seq" : 1,
          "userId" : "test1"
        }, {
          "seq" : 2,
          "userId" : "test2"
        }, {
          "seq" : 3,
          "userId" : "test3"
        } ]
      }'
	```
	- 요청 Header : X-AUTH-TOKEN에 로그인을 통해 얻은 JWT 토큰값을 입력
	- 문서 구분(classification)은 REPORT(보고), VACATION(휴가), NOTIFICATION(공지) 로 임의로 지정하였다.(대문자 입력 필수) -> 그 외 지정 시 Exception 발생
	- 결재자 목록(approverList) 에 포함된 사용자 ID(user id)는 실제 회원 가입된 사용자만을 지정할 수 있다. -> 그 외 지정 시 Exception 발생
	- 문서 생성 시 **문서 ID**는 자동 채번을 통해 증가한다. 문서 ID는 조회/결재/반려 시에 사용 됨.
	- 항목에 대한 자세한 설명은 API문서 참고 바랍니다.

> 주의 : 문서 생성에 실패한 경우(권한 문제 or 미등록 결재자 포함 등)에도 DB의 Auto Increment 특성상 문서 ID(PK) 값이 증가한다. 이 후 진행될 테스트에 대해 생성된 **문서 ID를 잘 확인한 뒤 대입하여 테스트를 진행**하도록 한다.


### 4. 문서 조회 (단 건)

- 특정 문서를 조회한다.
- 문서 생성 시 발생하는 문서 ID를 통해 조회가 가능하다.
- EX) curl 요청
	```
	$ curl 'http://localhost:8080/api/v1.0/user/document/1' -i -X GET \
          -H 'Content-Type: application/json' \
          -H 'Accept: application/json' \
          -H 'X-AUTH-TOKEN: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MSIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjE2MTcxMjg2MDEsImV4cCI6MTYxNzEzMDQwMX0.p6fnoXH-FfcOQIFo-oKnCzE9qYvo8l7S1erisWQWOcs'
	```
	-  문서의 결재 진행상황, 결재자 정보 등을 확인할 수 있다.

### 5. 결재

- 특정 문서를 결재한다.
- 로그인한 사용자의 결재차례가 맞을 경우 결재가 가능하다. -> 결재 순서가 아닌 경우 Exception 발생
- 결재 불가능한 경우
	1. 본인 결재 차례가 아닌 경우
	2. 결재라인에 본인이 포함되어 있지 않은 경우
	3. 이미 결재처리가 완료된 경우(승인 or 반려)
- 모든 결재자가 결재를 완료한 경우, 문서의 결재상태가 APPROVING(결재 중) -> APPROVED(승인) 으로 변경된다.
- 결재 건 별 첨언을 달 수 있다.
-  EX) curl 요청
	```
	$ curl 'http://localhost:8080/api/v1.0/user/document/1/approve' -i -X PATCH \
          -H 'Content-Type: application/json' \
          -H 'Accept: application/json' \
          -H 'X-AUTH-TOKEN: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MSIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjE2MTcxMjg2MDEsImV4cCI6MTYxNzEzMDQwMX0.p6fnoXH-FfcOQIFo-oKnCzE9qYvo8l7S1erisWQWOcs' \
          -d '{
        "comment" : "첨언 입니다."
      }'
	```
	- Request Body에 comment 를 입력하면 결재 건별 첨언을 추가할 수 있다.
	- 결재 완료 후 다음 결재자의 계정으로 로그인 하여 JWT 토큰을 변경하여 테스트를 진행하도록 한다.

### 6. 반려

- 특정 문서를 반려한다.
- 본인 결재 차례에만 반려가 가능하다.
- 반려 시 첨언을 추가 할 수 있다.
- 반려 이후 결재자는 더 이상 결재가 불가능하고, 문서의 결재상태가 APPROVING(결재 중) -> REJECTED(반려) 로 변경된다.
- EX) curl 요청
	```
	$ curl 'http://localhost:8080/api/v1.0/user/document/1/reject' -i -X PATCH \
          -H 'Content-Type: application/json' \
          -H 'Accept: application/json' \
          -H 'X-AUTH-TOKEN: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MSIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjE2MTcxMjg2MDEsImV4cCI6MTYxNzEzMDQwMX0.p6fnoXH-FfcOQIFo-oKnCzE9qYvo8l7S1erisWQWOcs' \
          -d '{
        "comment" : "첨언 입니다."
      }'
	```

### 7. 문서 목록 조회 (OUTBOX / INBOX / ARCHIVE )

- 등록된 문서 목록을 조회한다.
- EX) curl 요청
	```
	$ curl 'http://localhost:8080/api/v1.0/user/document/list/outbox' -i -X GET \
          -H 'Content-Type: application/json' \
          -H 'Accept: application/json' \
          -H 'X-AUTH-TOKEN: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0MSIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjE2MTcxMjg2MDEsImV4cCI6MTYxNzEzMDQwMX0.p6fnoXH-FfcOQIFo-oKnCzE9qYvo8l7S1erisWQWOcs'
	```
	- 요청 URL Path Parameter에  outbox, inbox, archive를 선택하여 조회 타입을 결정할 수 있다. (주의 : 조회 타입 소문자 입력 필수)

> * OUTBOX: 내가 생성한 문서 중 결재 진행 중인 문서  
> * INBOX: 내가 결재를 해야 할 문서  
> * ARCHIVE: 내가 관여한 문서 중 결재가 완료(승인 또는 거절)된 문서


## 4. 핵심문제 해결전략


### 1) 영속성 처리 ( H2, JPA )

H2 DB를 이용하여 별도의 DB 설정없이 내부 메모리를 통해 데이터의 영속성을 구현하였다.

단순한 구조의 데이터 활용과 객체지향적인 프로그래밍을 위해 JPA을 활용하였다.
	- 문서와 결재자의 생명주기가 동일하다는 점을 이용하여, 문서와 결재자 간의 의존 관계를 형성하였다.
	- 문서 : 결재자 = 1 : N

#### DB 설정
- H2 이용 
- H2 Console 경로 : {{서버 IP}}:{{서버 Port}}/h2-console
- ID / PW : sa / '빈 값'
- JPA DDL 설정 : create-drop (서버 실행 시 자동 Drop & Create)
#####  TABLE

> **USER(사용자)**

|컬럼 ID| 컬럼 명| 속성  | 비고 |
|--|--|--|--|
| ID | ID | INT | PK(자동 증가) |
| NAME| 사용자 이름 | VARCHAR |  |
| PASSWORD | 비밀번호 | VARCHAR | 인코딩 되어 저장 |
| USER_ID | 사용자 ID | VARCHAR | 로그인 ID |

----

> **USER_ROLES(사용자 권한)**

|컬럼 ID| 컬럼 명| 속성  | 비고 |
|--|--|--|--|
| USER_ID | 사용자 ID | VARCHAR  |  |
| ROLES| 권한 | VARCHAR | ROLE_ADMIN, ROLE_USER |

----

> **DOCUMENT(문서)**

|컬럼 ID| 컬럼 명| 속성  | 비고 |
|--|--|--|--|
| ID | 문서 ID | INT  | PK(자동 증가)  |
| TITLE | 제목 | VARCHAR | |
| CLASSIFICATION | 분류 | VARCHAR | REPORT(보고), VACATION(휴가), NOTIFICATION(공지)  |
| CONTENT | 내용 | VARCHAR |  |
| APPROVAL_STATUS | 결재 상태 | VARCHAR | APPROVING(결재 중), APPROVED(승인), REJECTED(반려) |

----

> **APPROVER(결재자)**

|컬럼 ID| 컬럼 명| 속성  | 비고 |
|--|--|--|--|
| DOCUMENT_ID | 문서 ID | INT  | PK(자동 증가)  |
| SEQ | 결재 순번 | INT | PK(복합키), 결재 순서 |
| USER_ID | 사용자 ID | VARCHAR |  |
| COMMENT | 첨언 | VARCHAR |  |
| APPROVAL_STATUS | 결재 상태 | VARCHAR | WAITING(결재 대기), APPROVING(결재 중), APPROVED(승인), REJECTED(반려) |
| APPROVAL_YN | 결재 여부 | TINYINT | true(1) / false(0)  |

---- 

### 2) 로그인 구현

로그인 기능을 구현하기 위해 **Spring Security** 를 활용하였다. 또한, **Stateless** 한 네트워크 통신을 위해 로그인 정보를 **Session**이 아닌 **JWT**를 이용하여 주고 받는  방식을 선택하였다.

JWT는 내부적으로 **사용자 ID, 권한이 포함**되어 있으며, **30분(default)의 유효기간**을 가진다.

JWT 발급 후 30분 이후 자동으로 검증에 실패하는 문제로 인해, Refresh 토큰으로 연장하는 기능이 추가적으로 필요하지만 이번 개발 범위에서는 구현하지 못하였다.


### 3) 소스 구조

* **패키지 구성** : 도메인 중심 ( 도메인 별 아래와 같은 Layer 로 구성 ) - User / Document
	- **domain >** 
		- **api** : Controller, Client로부터 오는 요청에 대해 처리(요청 데이터 유효성 검증), 서비스와의 연계 수행.
		- **application** : Service, 특정 기능을 수행하는 서비스로 구성, Domain과 Infra 를 연결지어주며 협력이 가능하도록 함.
		- **domain** : Model, 협력의 주체, 영속성 대상이 됨(Repository), POJO로 구성.
		- **dao** : 외부 요소(DB, API 등), 협력에 필요한 외부 자원을 공급해주는 역할 수행.
	- **global** : 공통 기능(config, audit, exception, util ..)

* **개발 원칙 : OOP**
	* **SRP** : 단일 책임, 한 클래스에서 여러 역할을 수행하지 않도록 분리
	* **OCP** : 개방-폐쇄, 인터페이스 의존을 통한 서비스 간 의존성 제거
	* **LSP**
	* **ISP** : 인터페이스 분리, 서비스 Layer의 세분화를 통한 인터페이스의 응집성을 높힘
	* **DIP** : 의존성 역전, 인터페이스 의존을 통한 서비스 간 의존성 제거

 * **디자인 패턴**
	 * **Template Method Pattern** 
		 * 공통 뼈대를 공유하며, 각각 다른 기능을 구현하고 싶을 때 사용.
		 * 해당 개발 건 중 결재(승인/반려) 기능이 위 내용에 해당하여 적용하였다.
		 * 공통 프로세스
			 * 결재 대상 문서 조회 -> 결재자 목록 조회 -> 결재 가능 여부 체크 -> **결재 진행(승인 or 반려)** -> 문서 상태 저장
			 * **결재 진행**을 처리하는 함수를 abstract로 추상화 한 후 공통 기능을 하는 Template Class를 생성한 후 이를 상속받아 승인 / 결재 서비스 클래스에서 구체화 하여 동일 소스 중복 방지와 역할 분리(SCP)가 가능하였다.

### 4) RestDocs 적용

Rest API에 대한 API문서 관리를 위해 RestDocs를 적용하였다.

RestDocs의 특징은 TestCode를 기반으로 문서를 생성하기 때문에 Test에 통과하지 못할 경우 문서가 만들어 지지 않는다.
즉, 소스코드의 변경으로 인해 Test 의 실패가 발생하면 API 문서가 생성되지 않기 때문에 실제 코드와 Test 코드의 동기화가 의무적으로 이루어져야 한다.
이를 통해 **소스코드 - 테스트 코드 - API** 문서 간의 동기화가 가능하여 소스와 문서간의 이원화를 방지할 수 있어 API문서 작성 시 RestDocs를 선택하여 작성하는 편이다.
