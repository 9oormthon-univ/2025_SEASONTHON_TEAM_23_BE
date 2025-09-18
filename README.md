<h1>🐾 펫어웰: Farewell, my pet</h1>

<blockquote>
  반려동물을 잃은 뒤 보호자가 겪는 상실 극복을 돕는 기록·위로 커뮤니티 서비스
  <strong>펫어웰</strong>의 백엔드 저장소입니다.
</blockquote>

<hr />

<h2>📖 Introduction</h2>

<p>
  펫어웰은 반려동물과의 이별을 겪은 사람들이 <strong>기록하고, 나누고, 회복</strong>할 수 있도록 돕는 서비스를 지향합니다.
</p>

<img width="1106" height="616" alt="image" src="https://github.com/user-attachments/assets/c088bfec-0de0-4f20-ad60-fe6343f226e4" />



<p><strong>서비스 목표</strong></p>
<ol>
  <li><strong>자신의 감정을 솔직하게 되돌아보며</strong> 상실의 감정을 안전하게 표현하고 정리합니다.</li>
  <li><strong>주변(펫어웰 커뮤니티)의 지지를 받고</strong> 서로의 이야기에 공감과 위로를 주고받습니다.</li>
  <li><strong>필요하다면 전문가에게 상담을 받아</strong> 회복을 위한 적절한 도움을 연결합니다.</li>
</ol>

<h2>Infra Structure</h2>
<img width="1435" height="806" alt="image" src="https://github.com/user-attachments/assets/bf9a48a9-3b53-4372-9c47-8acd18e84a2a" />



<h2>Tech Stacks</h2>

|  카테고리  |              스택              |
|:------:|:----------------------------:|
|   언어   |             JAVA             |
| 프레임워크  |          SpringBoot          |
|  ORM   |             JPA              |
|   DB   |            MYSQL             |
| 라이브러리  | Spring Security, Jwt, Lombok |
| Deploy |  Docker, AWS(EC2, RDS, S3)   |
| CI/CD  |        Github Actions        |

<h2>Project Stucture</h2>
<pre> 
gradle
 └── wrapper
      └── gradle-wrapper


src
└── main
├── java
│    └── com.petfarewell
│         ├── auth
│         │    ├── controller
│         │    ├── dto
│         │    ├── entity
│         │    ├── repository
│         │    ├── security
│         │    └── service
│         │
│         ├── config
│         │    ├── AsyncConfig
│         │    ├── S3Config
│         │    ├── SecurityConfig
│         │    └── SwaggerConfig
│         │
│         ├── dailylog
│         │    ├── ai
│         │    ├── controller
│         │    ├── dto
│         │    ├── entity
│         │    ├── repository
│         │    └── service
│         │
│         ├── global
│         │    ├── dto
│         │    └── exception
│         │
│         ├── letter
│         │    ├── controller
│         │    ├── dto
│         │    ├── entity
│         │    ├── repository
│         │    └── service
│         │
│         ├── mypage
│         │    ├── controller
│         │    ├── dto
│         │    └── service
│         │
│         └── PetfarewellApplication
│
└── resources
├── application.yml
└── application-prod.yml
 </pre>



<hr />
