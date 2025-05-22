# STS Project 
클라우드 웹앱 개발자 양성 과정에서 진행된 첫번째 프로젝트입니다.
</br>SpingBoot 와 React 를 활용하여 NFC를 이용한 오더 시스템을 구현하였습니다.
## 👨‍🏫 프로젝트 소개
- 테이블 오더 기능을 개발하여 태블릿의 불필요한 공간차지를 방지할 수 있습니다.
- 자영업자의 인건비 절감과 기기설치 및 유지보수 비용을 줄일 수 있습니다.
- 고객은 복잡함을 줄이고 간단하게 스마트폰으로 주문 및 결제가 가능합니다.
## 🧑‍🤝‍🧑 팀 구성
- Backend 4명, Frontend 3명
- [역할] : Backend
- [구현] : 기본 엔티티 및 리포지토리 생성, 카테고리 CRUD, 메뉴 CRUD, 옵션 CRUD, 옵션아이템 CRUD, 장바구니 CRUD
## ⏲️ 개발 기간
- 2024.09.02(월) ~ 2024.09.30(월)
## ⚙️ 기술 스택 및 개발 환경
- **Language** : <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=OpenJDK&logoColor=white">  <img src="https://img.shields.io/badge/typescript-3178C6?style=for-the-badge&logo=TypeScript&logoColor=black">
- **Framework** :  <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=black"> 
- **Server** : <img src="https://img.shields.io/badge/Amazon%20EC2-FF9900?style=for-the-badge&logo=Amazon%20EC2&logoColor=white">
- **DataBase** : <img src="https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=MariaDB&logoColor=white">  <img src="https://img.shields.io/badge/firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=white"> 
- **ETC** : <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"> <img src="https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=Figma&logoColor=white">  <img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white"> 
## 📝 프로젝트 아키텍쳐
+ ERD 설계
![image](https://github.com/user-attachments/assets/d73aac1c-65dc-4b4c-87f8-9d50c98498cc)

## 📌 주요 기능
1. **사업자**
   + 회원가입 및 로그인
   + 정산 : 일/주/월/ 지정기간 별 합산 금액, 상세내역 클릭시 가장 최신 메뉴리스트부터 역순 배열
   + 수정 : 회원 정보 수정 , 메뉴 CRUD, 메뉴옵션 CRUD
   + 대표메뉴 : 최대 5개 설정
 
2. **사용자**
   + 소셜로그인 및 자동로그인
   + 성인인증 기능
     + 회원이 미성년자이거나 비회원일 경우 주류 선택 불가
   + 가게 및 메뉴 조회
     + 가게 클릭시 해당 가게 대표 메뉴 및 전체 메뉴 조회
   + 직원호출기능
   + 장바구니 기능
     + 메뉴 클릭시 메뉴 옵션 추가
     + 선택 후 장바구니 담기
   + 결제수단, 요청사항, 주문내역, 결제방식 선택후 결제창 진입
   + 회원 정보, 주문내역 불러오기
