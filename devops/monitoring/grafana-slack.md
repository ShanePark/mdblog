# Grafana 모니터링중 이상 발생시 슬랙으로 알림 보내기

## Intro

Oracle Cloud는 1기가 메모리 인스턴스 무료로 제공한다. 그것도 무려 2개나.

몇년간 여러가지 클라우드 옵션들을 찾아봤지만 이정도로 파격적인 조건은 전혀 찾을 수 없었다. 

그래서 그 두개의 인스턴스를 정말 요긴하게 잘 사용하고 있다. 고정 아이피도 제공해주기 때문에 토이프로젝트 정도는 물론이요, 트래픽이 많지 않다면 작은 서비스도 운영할 수 있을 것이다. 이정도 스펙이면 네이버 클라우드 기준으로도 월 3만원 이상 과금을 해야 하는데 공짜로 쓸 수 있으니 정말 좋다.

하지만 망각하고 있었던게 있으니, 그들의 이름은 악명높은 `오라클` 이였다. 난 이제 2년정도 사용했는데 여태 잠잠하더니 슬슬 점유율을 끌어 높이기 위해 썼던 그들 스스로의 묘책을 거둘 셈으로 보인다.

요즘따라 툭하면 이메일을 보냈다. 

> 당신이 사용하고 있는 인스턴스의 CPU 사용량이 적습니다. 사용 잘 안하는거 같으니 조만간 종료해버리겠습니다. 그래도 너무 걱정하지마시오 당신의 인스턴스가 그 지역에서 사용가능하다면 재시작 할 수 있으니. 다만 자동으로 종료되는걸 원하지 않는 다면 Always Free 요금제가 아닌 Pay As You GO(PAYG) 플랜으로 변경하시오. 무료 범위를 넘어가지 않으면 청구는 하지 않을테니 !

처음 이 이메일을 받았을때는 너무 황당했다. CPU 사용량이 꾸준히 높다면 그거야말로 문제 있는 상황 아닌가. 램은 항상 점유율 80~90% 을 유지 하지만, CPU는 제법 평온한 상태로 유지되는 편인데 오라클에서는 그걸 사용하지 않는 인스턴스 취급하며 강제로 종료해버린다는 것이다.

나는 꽤나 초창기에 가입을 했다보니, 오라클에 신용카드도 등록하지 않은 진정한 `Always Free` 요금제를 사용하고 있다. 내가 가입해본건 아니라 확실하지는 않지만, 내가 예전에 작성한 [오라클 클라우드 사용법 6부작](https://shanepark.tistory.com/170) 에 달린 댓글들을 보면 요즘에는 크레딧카드 등록을 요구하는 듯 하다. 

처음엔 저렇게 경고만 하고 말거라고 생각했는데 실제로 어느날 갑자기 인스턴스를 새벽에 강제 종료시켜버렸다. 그러고는 바로 종료를 했다는 이메일을 보내주었는데 아침에 보고 와~진짜끄네 하면서 새로 구동을 했던 기억이 있다. 재시작시 이것저것 다시 구동하는것도 이참에 다 자동으로 뜨게 해두고, 방화벽 설정도 재부팅 후에도 남아있도록 저장을 해 두었다.

문제는 오늘 발생했으니, 오라클에서는 또 꺼버리고 껐다는 이메일이나 메시지도 따로 주지를 않았다. 와이프 쓰라고 만들어준 서비스가 하나 있는데 그게 접속이 안된다고 아침에 아내가 말을 해줘서야 알았다. 오라클에서는 강제종료 후 5시간이 지나서야 종료시켰다며 이메일을 보내주었다.

기존에 이미 외부에 `Prometheus`, `Grafana`로 모니터링을 구축해두었는데, 이참에 문제 발생시 슬랙으로 즉시 알림을 보내도록 설정해야겠다.

## Grafana

서버가 죽었을 때 어떻게 확인해서 알림을 보내도록 할지 고민을 해보았다. 처음에는 Actuator 헬스포인트를 주기적으로 체크해서 문제발생시 알림을 보내는게 제일 간단할거라고 생각했는데 그걸 위해서 따로 서비스를 또 띄우기엔 번거롭다. 

이미 있는것들을 활용해서 가장 간단하게 할 수 있는 방법이 뭘까 생각해보다가 그라파나의 메뉴들을 보니 Alerting이 눈에 띈다.

![image-20230607213332980](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/grafana-slack.assets/1.webp)

> Alerting

이걸 보니 Grafana에서 어느정도 알림에 대해 빌트인이 되어있겠구나 싶었고 번거롭게 따로 구축을 하지 않아도 되겠다는 안도가 들었다.

한번 차근차근 메뉴들을 살펴 보며 적용을 시켜보자

### Contact points

![image-20230607213609896](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/grafana-slack.assets/2.webp)

기본적으로 이메일이 등록되어 있는데, 문제가 발생하면 알림을 보낼 포인트들을 추가하는 걸로 보인다.

`+ Add contact point`를 클릭해서 어떤게 있는지 체크해보았다. 정말 많다.

![image-20230607213815743](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/grafana-slack.assets/3.webp)

위에 보이는 모든걸 사용할 수 있다. Line이 눈에 띈다. 

개인적인 소망으로는 대한민국의 Kakao Talk도 WebHook과 같은 돈 안되는 일에도 관심을 가져주면 좋겠다. 2년 넘게 기다린 끝에 애플 실리콘 네이티브 카카오톡도 드디어 지원하기 시작하는데 웹훅도 기다리다 보면 혹시 지원해줄지도 모르겠다.

어쨌든 나는 이미 여러가지 프로젝트 관련 알림을 받고 있는 Slack을 사용하도록 하겠다.

![image-20230607214300300](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/grafana-slack.assets/4.webp)

>  채널명, Token, 웹훅 URL 중 필요한 정보들을 입력해준다. 
>
> 혹시 아직 슬랙 채널 및 웹훅 생성을 해두지 않았다면 [[SpringBoot] 에러 발생시 Slack으로 알림 보내기](https://shanepark.tistory.com/430) 글을 참고하도록 한다.

입력을 완료 하고는 우측의 `Test` 버튼을 눌러 알림이 잘 전송되는지 확인해 본다.

![image-20230607214809127](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/grafana-slack.assets/5.webp)

여기에서 `Send test notification`을 클릭 하고

설정이 제대로 되어 있다면 바로 슬랙 알람이 전송되어 확인이 가능하다.

![image-20230607214838574](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/grafana-slack.assets/6.webp)

> 알림이 온 모습

알림까지 확인 되었다면, 저장을 해 준다.

![image-20230607214918746](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/grafana-slack.assets/7.webp)

> Contact points에 추가된 Slack Alarm

이제 알림을 전송할 준비는 끝났다.

### Alert Rules

알림 전송을 준비 했으니, 이제 어떨때 알림을 보낼지만 설정해주면 되겠다.

알림을 보낼때 가장 중요한건 양치기 소년이 되지 않는 것이다. 시도때도 없이 알림이 울린다면 정작 필요할때 울린 알림에 적절히 대응하지 못하는 경우가 생길 수 있다. 만약 필요없는 알림이 울렸다면 확인하고 말 것이 아니고, 다음번에는 비슷한 종류의 무의미한 알람이 다시 오지 않도록 확실하게 대응해주는 것이 필요하다.

Alerting 에서 `Alert rules`를 클릭한 후, `Create alert rule`을 선택한다.

![image-20230607215313448](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/grafana-slack.assets/8.webp)

> Create alert rule

`spring-actuator`의 `up`여부를 통해 어플리케이션의 상태를 체크하고, up 이 아닐 경우에 즉각 알림을 전송하게 해보려 한다.

![image-20230607215740290](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/grafana-slack.assets/9.webp)

위에 보이는 것 처럼, Metric을 `up`으로 잡고, 라벨 필터에 `job=spring-actuator` 로 설정하니 서버가 강제로 종료되었던 02시 30분 가량부터 아침 8시까지의 문제 상황이 보인다.

컨디션은 아래와 같이, 마지막에 확인한 값이 1보다 낮을 경우 문제 생황으로 체크하도록 했다.

![image-20230607220428285](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/grafana-slack.assets/10.webp)

다음으로는 Alert evaluation behavior 설정을 해준다.

![image-20230607221336839](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/grafana-slack.assets/11.webp)

5분 마다 체크를 하지만, 문제가 인지되었을때 바로 알림을 전송하지는 않고  일단 10분동안 문제가 해결되었는지 지켜 본 뒤에 알림을 보낸다. 그래서 알림을 보낼 때 문제가 해결되었는지 여전히 남아있는지에 대한 정보까지 함께 넘길 수 있다.

개인적으로 5분/10분 으로 설정했을때는 좀 늦은 것 같아서 나중에는 1분마다 체크, 5분 후 알림으로 설정을 변경했다.

설정을 저장한 뒤에는 Default contact point를 위에서 생성한 슬랙 알람으로 변경해준다.

![image-20230607221733383](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/grafana-slack.assets/12.webp)

> Repeat interval이 적절히 설정되어있지 않다면 알람 지옥을 맞닥들일 수 있다.

## 확인

자 이제 알람 설정이 모두 준비되었다면 과감하게 앱에 장애를 일으켜보자. 잘 돌고 있는 프로젝트를 종료 해 보았다.

![image-20230607222028964](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/grafana-slack.assets/13.webp)

잠시후 Alert 상태가 Pending이 되었다. 5분 후에 다음 evaluation을 할 것이고, 10분동안 상태를 지켜볼것이다.

![image-20230607223102621](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/grafana-slack.assets/14.webp)

10분이 지나자 드디어 알림이 발송되었다. 

그리고 어플리케이션을 다시 구동하고 나면

![image-20230607223743588](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/monitoring/grafana-slack.assets/15.webp)

Resolve가 되었을때에도 알림이 발송된다. 

이제 오라클이 내 인스턴스를 강제로 종료시킨다고 해도 금방 확인 하여 대응 할 수 있다. 

끝.