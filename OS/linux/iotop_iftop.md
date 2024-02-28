# [Linux] iotop / iftop 디스크 I/O 와 네트워크 부하 측정

## Intro

리눅스 서버를 운영하다 보면, 종종 시스템의 성능 저하를 경험할 수 있다. 

이러한 문제의 원인을 진단하기 위해서는 시스템의 다양한 자원 사용 상황을 모니터링할 필요가 있다. 특히, 디스크 I/O와 네트워크 트래픽은 시스템 성능에 큰 영향을 미칠 수 있는 중요한 요소다.

이 글을 통해  `iotop`과 `iftop`으로 시스템에서 발생하는 디스크 I/O 작업을 실시간으로 관찰할 수 있는 방법과 네트워크의 사용량을 실시간으로 파악하는 방법을 확인할 것이다. 

## iotop

`iotop`은 리눅스에서 I/O 사용량을 모니터하는 도구다.

시스템에서 발생하는 디스크 I/O 작업을 실시간으로 관찰하고, 어떤 프로세스가 디스크 I/O 부하를 많이 차지하는지, 어떤 종류의 I/O 작업이 주를 이루는지 확인할 수 있다. 파일 입출력 부하가 이어지는 상황에서 병목 지점을 찾아 해결하기 위해 사용해보았는데 아주 유용했다.

### 설치

우분투를 기준으로 작성하였지만, 마찬가지로 다른 배포판에서는 `yum` 등을 사용하면 된다.

```bash
sudo apt install iotop
```

설치시 재시작해야할 서비스가 있다면 재시작한다. 선택된 걸 확인하고 `Ok`를 선택하면 된다.

![3](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/iotop_iftop.assets/3.webp)

### 사용

이제 iotop으로 디스크 I/O를 체크해본다. 여러가지 옵션을 사용할 수 있는데 나는 보통 아래의 옵션을 사용한다.

```bash
 sudo iotop -aoP
```

각 옵션의 의미는 다음과 같다

- `-a`: 이 옵션은 누적 모드를 활성화하여 `iotop`이 실행된 이후부터 현재까지의 누적 I/O를 보여준다. 기본적으로 `iotop`은 순간적인 I/O 사용량을 표시하는데, 누적 모드에서는 프로세스가 시작된 이후의 전체 I/O 사용량을 볼 수 있다.
- `-o`: 이 옵션은 I/O를 수행 중인 프로세스 또는 스레드만 보여주는 필터링 기능을 제공한다. 즉, 실제로 디스크 I/O가 없는 프로세스는 목록에서 제외된다. 이를 통해 현재 시스템의 I/O 병목을 일으키는 주요 프로세스에 집중할 수 있다.
- `-P`: 이 옵션은 프로세스별로 I/O 사용량을 보여준다. 기본적으로 `iotop`은 모든 스레드의 I/O 사용량을 개별적으로 보여주는데, `-P` 옵션을 사용하면, 이러한 스레드를 그룹화하여 각 프로세스의 총 I/O 사용량을 표시한다.

실행 결과는 다음과 같다.

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/iotop_iftop.assets/1.webp)

> 특별한 I/O 부하가 없는 상황이지만, 대용량 파일 입출력등의 상황에서는 최소 수백MB/s 가 찍힌다.

## iftop

`iftop`은 리눅스에서 네트워크 인터페이스의 실시간 트래픽을 모니터링하는 도구다. 

네트워크의 사용량을 실시간으로 파악하고, 어떤 호스트가 네트워크 트래픽을 많이 사용하는지, 어떤 종류의 트래픽이 주를 이루는지 확인할 수 있다. 네트워크 성능 문제를 진단하거나, 네트워크 사용 패턴을 분석하는 데 유용하게 사용할 수 있다. 

### 설치

```bash
sudo apt install iftop
```

### 사용

사용방법은 간단하다.

```bash
sudo iftop
```

그런데 네트워크 장비가 한개가 아닌 경우 (내부망, 외부망, 무선 네트워크 등)에는 모니터링하고자 하는 특정 네트워크 인터페이스를 지정해야 할 수 있다. 네트워크 목록은 `ifconfig`로 확인 가능하다.

```bash
sudo iftop -i enp3s0
```

실행 결과는 아래와 같다.

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/iotop_iftop.assets/2.webp)

이를 통해 전체적인 네트워크 트래픽을 관찰할 수 있다.

### nethogs

만약 프로세스별 네트워크 사용량을 보고 싶다면 `nethogs`를 사용할 수 있다.

```bash
sudo apt install nethogs
sudo nethogs
```

![4](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/iotop_iftop.assets/4.webp)

끝.