# Linux) Oracle VM Virtual Box 창 이동 안될때

## Intro

Oracle VM Virtual Box는 InnoTek 에서 개발한 가상머신 소프트웨어 입니다. Sun Microsystems가 2008년 초 InnoTek을 인수하고, 2년 후인 2010년 1월 오라클이 썬 마이크로시스템즈를 인수 하며 오라클이 배포하게 되었습니다.

대부분의 OS를 설치 해 사용 할 수 있으며, 또한 대부분의 OS에 설치가 가능하기 때문에 널리 사용  되고 있습니다.

사실, Linux 에서 가상 환경으로 Windows를 구동 하기 위해 여러가지 가상머신 소프트웨어를 사용 해 봤는데 VM Virtual Box가 가장 괜찮아서 IE 호환성 테스트 등이 필요할 때 마다 꾸준히 사용 하고 있습니다.

## 창 크기 조절 및 이동

### 증상

사용하면서 한가지 아쉬운게, 이상하게 창 이동 및 크기 조절이 안된다는 겁니다. 해상도가 문제인가 싶어 해상도를 몇 번 변경 해 보았지만 여전히 이동은 불가능 했습니다.

![peek1](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/virtualbox-window-move.assets/peek1.gif)

위와 같이 가상 윈도우를 띄워 놓고, 모니터의 적당한 위치로 옮기지 못해 곤란 한 상황에 처합니다.

### 원인

화면 내부에 있는 Windows에서 마우스 드래그 등의 입력을 가져가 버리기 때문입니다. VirtualBox 화면을 옮기라고 명령을 하고 있지만, 실제 입력은 윈도우 가상 컴퓨터 내부에서 마우스를 열심히 움직이는 것 뿐이 안되고 있었습니다.

### 해결

두가지 해결 방법이 있습니다.

1. Host Key 입력

기본적으로 `Right Control` 키가 Host Key로 지정되어 있습니다. Host Key를 입력 하면 auto capture mode가 일시적으로 정지되기 때문에, 화면을 이동 할 수 있습니다.

오른쪽 컨트롤 키를 누른 채 화면을 이동 해 봅니다.

![peek2](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/virtualbox-window-move.assets/peek2.gif)

> 잠깐 딜레이가 있다가 창이 이동 됩니다.

2. focus 벗어나기

사실 이 방법이 좀 더 간단하긴 합니다. 그냥 VM window에 있는 focus 에서 벗어나서 우분투 상의 바탕화면 등을 한번 클릭 했다가 다시 창 이동을 시도 하면 자연스럽게 이동 합니다.

![peek3](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/ubuntu/virtualbox-window-move.assets/peek3.gif)

> 뭔가 보여주려고 드래그로 네모를 그려 보았지만, 사실 클릭 한번으로도 충분 합니다.

이상으로 Virtual Box 창 이동이 안될때 해결 방법에 대해 알아 보았습니다.