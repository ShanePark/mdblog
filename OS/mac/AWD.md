# 맥북 와이파이 핑 튀는 문제 해결 [Sonoma 14.1.2]

## Intro

맥북을 사용하면서 언제부턴가 와이파이로 연결된 상태에서는 한번씩 핑이 심하게 튀는 현상이 나타났다. 

- 처음에는 `Private relay`를 의심했는데 비활성화 해도 고쳐지지 않았다.

- 그 다음으로는 공유기를 의심했는데, 어딘가에서 토렌트처럼 많은 커넥션을 물고 있을까 의심되어 공유기 내 모든 커넥션을 확인해봤지만 특이사항은 없었고, 공유기를 껐다 다시 켜도 여전히 같은 현상이 발생했다. 

무엇보다 같은 와이파이내에서도 핸드폰으로 핑 테스트를 할때는 핑이 안정적으로 일정하게 잘 나왔다.

## 원인

찾아보니 AWDL 문제가 오래전부터 잘 알려진 이슈였다.

AWDL은 Apple Wireless Direct Link의 약자로 애플 기기간의 통신에 사용된다. 즉, 주변에 아이폰이나 아이패드등의 기기가 있을 때 awdl 통신이 이루어지며 사용중인 와이파이에 문제가 일어난것으로 보인다.

한동안 괜찮았는데 요즘 이러는걸 보면 OS 업데이트가 되면서 문제가 롤백된게 아닌가 싶다. 문제가 된 OS 버전은 `Sonoma 14.1.2` 이다.

## 해결

해결방법은 두가지가 있다. 

### 1. AWDL 비활성화

당연히 원인이 되는 awdl을 비활성화 하면 와이파이 문제가 해결된다. 서비스명은 `awdl0`다. 

```bash
sudo ifconfig awdl0 down
```

물론 awdl 서비스를 종료시키면 에어드랍과 같은 서비스는 사용할 수 없다.

다시 켜려면 up 명령어를 입력하면 된다.

```bash
sudo ifconfig awdl0 up
```

ifconfig를 사용하기 싫다면 블루투스와 에어드랍을 비활성화하는 방법이 있겠다.

Github에 awdl 을 내리고 올리는 스크립트가 작성된게 있는데 이걸 사용해도 된다.

> https://github.com/meterup/awdl_wifi_scripts

### 2. 공유기 Wifi 채널 변경

개인적으로는 이 방법으로 해결했다.

awdl이 선호하는 채널이 있다고 하는데 2.4Ghz 에서는 Channel 6이고 5Ghz에서는 149 라고한다. awdl이 사용하는 밴드에 와이파이 채널을 똑같이 맞춰두면 awdl과 와이파이가 채널을 변환하며 생기는 딜레이가 없어지는 원리다.

iptime 공유기에서는 아래와 같은 화면에서 채널을 변경할 수 있다. 

![image-20231231112401578 pm](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/AWD.assets/1.webp)

> 공유기 설정에서 Advanced Setup > Wireless > Wireless Setup 순서로 들어가면 된다. 채널을 149로 맞췄다.

설정을 완료한 후에는 핑 테스트를 통해 문제가 해결되었는지를 확인한다.

핑 테스트는 아래의 사이트중 하나를 이용하면 되는데 meter.net 을 사용한다면 테스트 서버를 꼭 한국으로 맞춰야한다.

- https://www.meter.net/ping-test/
- https://kr.piliapp.com/speed-test/ping/

채널을 변경하기 전에는 툭하면 핑이 100 이상으로 튀었지만 변경 이후에는 10정도로 일정한 핑을 안정적으로 뽑아주었다.

개인적으로는 다음 macOS 업데이트에 해결되었으면 좋겠고, 그렇게 될거라고 생각한다.

**References**

- https://stackoverflow.com/questions/19587701/what-is-awdl-apple-wireless-direct-link-and-how-does-it-work
- https://wlanprofessionals.com/an-overview-of-apple-wireless-direct/
- https://apple.stackexchange.com/questions/451646/force-disabling-awdl-on-ventura-or-above