# UptimeRobot 소개 및 모니터링 활용가이드

## Intro

기존에 Granafa 등을 통해 별도의 관제 시스템을 운영하고 있지만, 서비스 어플리케이션과 모니터링 어플리케이션들이 비용상문제로 같은 서버에서 작동하고 있기 때문에 네트워크 장애등의 상황에서 제대로 장애 상황이 전파되지 않을 것이 염려되었다.

이러한 상황을 방지하기 위해 간단히 서버의 응답 여부정도만 확인할 수 있는 보조 모니터링 서비스를 찾아보았고, 괜찮은 무료 서비스인 `UptimeRobot`을 발견하여 공유하려 한다.

## 회원가입

먼저 홈페이지에 접속한다. https://uptimerobot.com/

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/uptime-robot.assets/1.webp)

`Register for FREE` 클릭해 회원가입을 하면 되는데 회원가입은 이메일주소, 비밀번호만 입력하면 간단하게 완료된다. 

이후 이메일 인증만 하면 끝.

## Monitor

### 등록

가입 후에는 모니터링할 서비스들을 등록한다.

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/uptime-robot.assets/2.webp)

>  `Create your first monitor` 클릭

![3](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/uptime-robot.assets/3.webp)

- `URL to monitor`에 모니터하고싶은 주소를 입력한다. 부하가 많이 발생하지 않는 적절한 헬스체크용 API 엔드포인트를 입력하면 되겠다.
- notify 방식은 이메일을 체크한다.
- Monitor interval 에는 최소 1분을 추천한다고 써있지만, 5분보다 아래로 하려면 돈 내야해서 5분으로 그대로 두면 된다.
- 그 외에도 도메인이나 SSL 만료 알림도 있지만 역시 돈내야한다.

다 입력했으면 `Create monitor`를 눌러 등록한다.

![5](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/uptime-robot.assets/5.webp)

> 즉각 모니터링이 시작된다.

`Test Notification`으로 장애상황을 테스트 해볼 수 있다.

![4](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/uptime-robot.assets/4.webp)

`Send test notifications`를 클릭하면

![6](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/uptime-robot.assets/6.webp)

> 이메일로 알림이 온다.

슬랙이나 webhook, 텔레그램 등을 연동하면 좋겠는데 아쉽게도 유료다.

![7](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/uptime-robot.assets/7.webp)

그래도 Discord 나 Google Chat은 무료로 사용할 수 있다.

- Google Chat 의 경우에는 개인 계정은 webhook을 사용할 수 없고 학교나 회사등의 그룹만 가능하다.
- [Uptime Robot 자체 앱](https://apps.apple.com/us/app/uptimerobot-monitor-anything/id1104878581)이 있으니 설치에 거부감이 없다면 그것도 괜찮다.
- 하지만 이번 글에서는 디스코드를 연동해보려 한다

### Discord 연동

디스코드 연동을 해보겠다. 평소 사용해 본 적 없긴 한데 자체 앱 설치보단 디스코드가 낫지 않나 싶어 가입 및 설치를 진행했다.

1. Create Your Server

![8](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/uptime-robot.assets/8.webp)

디스코드 채널을 하나 생성해야 한다. `Create My Own`으로 대충 생성했다.

![9](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/uptime-robot.assets/9.webp)

general 채널 옆에 보면 톱니바퀴 설정 아이콘이 있다. 클릭한다.

![10](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/uptime-robot.assets/10.webp)

Integrations에 들어가 `Create Webhook`을 한다.

![11](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/uptime-robot.assets/11.webp)

Webhook을 생성 했으면 `Copy Webhook URL`을 눌러 URL을 복사한다.

![12](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/uptime-robot.assets/12.webp)

UptimeRobot 의 Integration 메뉴의 Discord에서 `Add`를 선택 한 뒤에  복사한 url을 입력 한다. `Create integration`을 클릭해 저장한다.

이후 원하는 모니터링 서비스로 다시 찾아가 `Edit` 버튼을 누르고, `Integrations & Team` 글자를 입력해 연동 페이지로 들어간다.

![14](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/uptime-robot.assets/14.webp)

방금 추가한 Discord integration에 체크표시를 한 뒤 `Save changes`를 눌러 저장한다.

![13](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/uptime-robot.assets/13.webp)

이후 Test notification에 들어가보면 추가된 Discord가 보인다.

![15](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/uptime-robot.assets/15.webp)

이번에는 테스트를 하면

![16](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/uptime-robot.assets/16.webp)

Discord에 알림 메시지가 전송된다. 모바일 앱이 설치되어 있다면 푸시알람도 같이 오기때문에 알아채기 쉽다.

## 총평

**단점**

- 장애가 일어나고 최대 5분 후에야 알아챌 수 있다
- 국내 서버가 아니기때문에 응답 시간이 형편없게 찍힌다
- 슬랙 연동을 하려면 유료 플랜을 사용해야 한다

[Grafana 모니터링중 이상 발생시 슬랙으로 알림 보내기](https://shanepark.tistory.com/476) 처럼 메인 관제시스템을 따로 두었다면, 서브용으로 사용하기 위한 목적으로는 충분히 만족스러운 서비스다. 특히, 위에 해당되는 단점들도 모두 무료사용을 감안하면 수용 가능한 범위기 때문에 앞으로 계속 사용할 듯 하다.