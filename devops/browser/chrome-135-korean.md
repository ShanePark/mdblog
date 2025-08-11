# Ubuntu 22.04 Chrome 135 한글 입력 문제

> UPDATE: 2025년 8월 5일 릴리즈 된 Chrome 139 버전으로 업데이트 후 문제 해결됨.
>
> https://developer.chrome.com/release-notes/139
>
> ```
> # APT 저장소 최신 Chrome 버전 및 Candidate 확인. 139버전이 릴리즈 되어있어야함.
> apt policy google-chrome-stable
> 
> # 기존의 134버전에 홀드 mark 했던 것 풀기
> sudo apt-mark unhold google-chrome-stable
> 
> sudo apt update
> sudo apt install google-chrome-stable
> 
> # 새로 설치된 버전 확인 후 Chrome 브라우저 종료 후 재시작
> google-chrome --version
> ```
>
> 

## Intro

우분투에서 크롬을 135 버전으로 올린 이후 일주일째 한글 입력할 때 마다 스트레스를 받고있다.

135버전 업데이트 직후부터 발생했기때문에 이유는 확실한데, 아무리 찾아봐도 Chrome 브라우저 다운그레이를 하는 방법이 공식적으로 제공되는게 없어서 그냥 이슈 리포트 후 파이어폭스를 사용하며 새로운 업데이트를 기다리기로 했다.

새로운 업데이트를 두 번 해서 `Google Chrome 135.0.7049.95 ` 까지 왔는데도 여전히 해결이 되지 않길래 이제 좀 더 적극적으로 해결책을 찾아보기로 했다.

다행히도 잘 찾아보니 같은 문제가 있다고 한 사람이 30건을 넘긴 이슈였다. 같은 문제를 겪는 사람이 많을수록 문제가 해결될 확률은 높아진다.

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/browser/chrome-135-korean.assets/2.webp)

> https://support.google.com/chrome/thread/337302644/%EC%9A%B0%EB%B6%84%ED%88%AC-22-04-%ED%81%AC%EB%A1%AC%EC%97%90%EC%84%9C%EB%A7%8C-%ED%95%9C%EA%B8%80-%EB%AA%A8%EB%93%9C%EC%97%90%EC%84%9C-%EC%8A%A4%ED%8E%98%EC%9D%B4%EC%8A%A4-%EA%B0%99%EC%9D%80-%ED%82%A4-%EC%9E%85%EB%A0%A5%EC%8B%9C-%EB%AC%B8%EC%A0%9C-%EB%B0%9C%EC%83%9D?hl=ko

## 원인

![3](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/browser/chrome-135-korean.assets/3.webp)

> https://issues.chromium.org/issues/407930251

Chrome 135 버전부터 GTK4가 default로 사용되며 GTK IME 가 활성화되었는데, CharacterComposer 기능이 빠져있다보니 문제가 발생했다고 한다. 137 버전에서야 수정된 패치가 적용되었다고 하는데 배포를 기다리기엔 너무 멀다.

## 해결

`chrome://flags` 에 들어가서 `Wayland text-input-v3`을 활성화하는 방법이 있다고하는데 나는 wayland가 아닌 x11 을 사용중이라그런지 효과가 없었다.

이제 선택을 해야 하는데, 해결되었다고 하는 137버전의 canary 혹은 unstable 버전을 쓸 것인가, 아니면 134버전으로 다운그레이드 할 것인가.

업무용 pc다 보니 안전한 선택으로 134버전을 택했다. 과거 버전은 아래의 링크에서 다운받을 수 있다.

> https://mirror.cs.uchicago.edu/google-chrome/pool/main/g/

```bash
sudo apt remove google-chrome-stable
sudo dpkg --install google-chrome-stable_134.0.6998.88-1_amd64.deb

chrome --version            
# Google Chrome 134.0.6998.88 
```

여기서 한가지 더 해야할게 있는데, 크롬의 자동 업데이트를 차단하는 것이다.

```bash
sudo apt-mark hold google-chrome-stable

apt-mark showhold
# google-chrome-stable
```

이렇게 해놓고 몇달정도 잊고 살았다가 나중에 해결되고 나서 업데이트를 하면 되겠다.

나중에 hold를 풀고 싶다면 아래의 명령어를 입력하면 된다.

```bash
sudo apt-mark unhold google-chrome-stable
```

당연하게도 134버전의 크롬에서는 한글 입력에 아무런 문제가 없다.

끝



**References**

- https://support.google.com/chrome/thread/337302644/%EC%9A%B0%EB%B6%84%ED%88%AC-22-04-%ED%81%AC%EB%A1%AC%EC%97%90%EC%84%9C%EB%A7%8C-%ED%95%9C%EA%B8%80-%EB%AA%A8%EB%93%9C%EC%97%90%EC%84%9C-%EC%8A%A4%ED%8E%98%EC%9D%B4%EC%8A%A4-%EA%B0%99%EC%9D%80-%ED%82%A4-%EC%9E%85%EB%A0%A5%EC%8B%9C-%EB%AC%B8%EC%A0%9C-%EB%B0%9C%EC%83%9D?hl=ko
- https://issues.chromium.org/issues/407930251