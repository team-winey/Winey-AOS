# 👑 Winey - AOS 💸

[GO SOPT](https://www.sopt.org/) 32기 앱잼 Winey Android 팀 레포입니다.

<img src="https://github.com/team-winey/Winey-AOS/assets/68090939/a44c2b4e-68e2-40c1-b687-535f323cead8"/> 

## 💁 Introduction

위니(₩iney)는 무겁게만 느껴졌던 절약을 몰입도 높은 세계관과 쉽고 재미있는 기능을 통해 절약을 라이프 스타일로 변화시켜주는 서비스입니다.

위니(₩iney)는 ₩in+Money의 합성어입니다. '나와의 절약 싸움에서 이겨 위니 왕국으로 돌아가자'라는 세계관과 흥미로운 스토리텔링이 담긴 게이미피케이션 요소를 통해 쉽고
재미있게 절약을 표현했습니다.

절약 방법이 어려운 고객을 위해 일상에서도 언제든 가능한 절약 방법을 추천하고, 사진을 통한 절약 인증과 절약 목표 달성률에 따라 캐릭터를 성장시키는 기능이 담겨있습니다.

한마디로 위니는 '좌충우돌 나만의 절약 방법을 공유하는 게임형 절약 SNS' 서비스입니다.

## 🎉 Download

<a href="https://play.google.com/store/apps/details?id=org.go.sopt.winey"><img width="40%" src="https://play.google.com/intl/ko/badges/static/images/badges/ko_badge_web_generic.png"/></a>

## 🌱 Contributors

|                                          [@leeeha](https://github.com/leeeha)                                          |                                          [@sxunea](https://github.com/sxunea)                                          |                                     [@Sangwook123](https://github.com/Sangwook123)                                     |
|:----------------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------------------:|
| <img width="200" src="https://github.com/team-winey/Winey-AOS/assets/68090939/c393f52d-2e3f-42c8-9f38-a935184831f1" /> | <img width="200" src="https://github.com/team-winey/Winey-AOS/assets/68090939/7eb22b00-ef67-4ad0-9ae9-1bc5e579524b" /> | <img width="200" src="https://github.com/team-winey/Winey-AOS/assets/68090939/352352e9-9a4c-4de8-8fdb-dc73c26a271e" /> |
|                                         스플래시, 온보딩, 피드 업로드 <br> 댓글 생성/삭제, 회원 탈퇴                                         |                                             위니 피드, 마이 피드 <br> 상세 피드, 댓글 조회                                             |                                            카카오 로그인, 마이페이지 <br> 추천 피드, 알림 목록                                            |

## 🛠 Tech Stack

| 구분                      | 기술 스택                                                                     |
|-------------------------|---------------------------------------------------------------------------|
| Architecture            | MVVM                                                                      |
| Design Pattern          | Observer Pattern, Repository Pattern, State Pattern                       |
| JetPack Components      | LifeCycle, ViewModel, LiveData, StateFlow, DataBinding, DataStore, Paging |
| Dependency Injection    | Hilt                                                                      |
| Network                 | Retrofit, OkHttp, kotlinx.serialization                                     |
| Asynchronous Processing | Coroutine, Flow                                                           |
| Third Party Library     | Kakao Login, Coil, Lottie, Timber, CircleImageView                        |
| Branch Strategy         | Git Flow                                                                  |
| CI/CD                   | GitHub Actions (KtLint, Compile Check)                                    |
| Data Analytics          | Amplitude                                                                 |
| Communication Tool      | Notion, Slack, Figma, Postman, Swagger                                    |

## ✨ Convention

- [Git Convention](https://www.notion.so/Git-Convention-8b890a83aed94c9fbf727b4088bc2670?pvs=4)
- [Code Convention](https://www.notion.so/Code-Convention-d39ec34c2d1240f297f6027b8f9839c3?pvs=4)
- [Package Convention](https://www.notion.so/Package-Convention-b5a7ccc1f2b64f5d86ea9fc9179b7516?pvs=4)

## 📋 Kanban Board

- [Github Projects](https://github.com/orgs/team-winey/projects/2)

## 📂 Foldering

```
📂 org.go.sopt.winey
┣ 📂 data
┃ ┣ 📂 interceptor
┃ ┣ 📂 model
┃ ┣ 📂 repository
┃ ┣ 📂 service
┃ ┣ 📂 source
┣ 📂 di
┃ ┣ 📂 qualifier
┣ 📂 domain
┃ ┣ 📂 entity
┃ ┣ 📂 repository
┣ 📂 presentation
┃ ┣ 📂 main
┃ ┃ ┣ 📂 feed
┃ ┃ ┃ ┣ 📂 detail
┃ ┃ ┃ ┣ 📂 upload
┃ ┃ ┣ 📂 mypage
┃ ┃ ┃ ┣ 📂 myfeed
┃ ┃ ┣ 📂 notification
┃ ┃ ┣ 📂 recommend
┃ ┣ 📂 nickname
┃ ┣ 📂 onboarding
┃ ┃ ┣ 📂 guide
┃ ┃ ┣ 📂 login
┃ ┃ ┣ 📂 story
┃ ┣ 📂 splash
┣ 📂 util
```
