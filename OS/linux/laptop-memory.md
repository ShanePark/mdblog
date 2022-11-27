# (Linux 서버로 사용중인) 노트북 메모리 추가

## Intro

집에서 2년째 가벼운 서버 용도로 사용하고 있는 노트북이 있습니다.

클라우드를 저렴하게 사용할 수 있다면 베스트겠지만, 여러 가지 무료 클라우드를 비교 해 보았을때, 오라클에서 제공하는게 가장 좋음에도 불구하고 최대 2개의 인스턴스 및 각각 1GB의 메모리를 사용 할 수 있다보니 도커 기반의 무엇인가를 올리기에는 엄두가 나지 않습니다. 그러다보니 오라클 클라우드는 개인 ip 주소를 드러내지 않기 위한 간단한 프론트 서버 용도 정도로만 사용 하고 있습니데.

제가 필요한 컨테이너 중에는 메모리를 크게 차지하는게 몇몇 있는데, 그 중에서도 엘라스틱 서치는 평소에도 제 PC 기준 5GB 정도의 많은 메모리를 잡아먹습니다. 램 용량과 클라우드 비용은 정비례를 할 만큼 램의 크기는 비용에 큰 영향을 주다보니 그나마 대안으로 집에 사용하지 않는 노트북을 서버처럼 사용 하고 있습니다. 데스크탑PC 보다 전력 소비가 훨씬 적기 때문에 전기요금이 별로 들지 않습니다.

8GB 메모리로 잘 사용 해 왔지만, 더 많은 학습 용도의 사용을 위해 이번에 램을 증설 하기로 했습니다.

## 메모리 확인

일단 메모리 구매에 앞서 램 슬롯과 최대 램 용량등을 체크 해 봅니다. 굳이 열어보지 않아도 간단한 명령어로 램 슬롯 정보를 확인 할 수 있습니다.

```bash
sudo dmidecode -t memory
```

![image-20221126134415612](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-memory.assets/image-20221126134415612.png)

> 명령어 실행 결과

가장 먼저 Physical Memory Array를 확인 해 보면

![image-20221126134431387](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-memory.assets/image-20221126134431387.png)

>  4개의 슬롯이 있고 최대 64GB 까지 증설이 가능 합니다. 슬롯당 16GB 까지 인식이 되겠네요.

![image-20221126135347402](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-memory.assets/image-20221126135347402.png)

> 슬롯에 램은 한개가 꼽혀 있고 DDR4 타입 8GB 짜리 한개입니다. 2400 MT/s 의 속도 램이고 Micron 제품이며 2133 MT/s 로 설정이 되어 있습니다.

## 메모리 구입

이제 구매를 위해 다나와에서 검색을 해 봅니다.

![image-20221126135710547](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-memory.assets/image-20221126135710547.png)

기존에 사용중인 메모리 클럭이 2400 MT/s 고, 설정된 값이 2133 MT/s 기 때문에 그 근방의 클럭이면 충분 하지만 다나와에서 구입 했을 때 오히려 3200MHz 제품보다 2666 제품이 더 비쌌습니다. 아무래도 더이상 생산이 잘 되지 않고 있는 모양 입니다.

DDR4-3200 제품으로 구입을 하였습니다.

## 장착

### 종료

일단 컴퓨터를 종료 해 줍니다.

```bash
sudo shutdown now
```

![image-20221126162031557](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-memory.assets/image-20221126162031557.png)

> now 옵션을 주지 않으면 1분 후에 종료가 됩니다.

### 노트북 분해

![image-20221126164218613](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-memory.assets/image-20221126164218613.jpg)

일단 노트북을 뒤집어서 나사를 확인 해봅니다. 아래와 같은 모양입니다.

![image-20221126164142290](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-memory.assets/image-20221126164142290.jpg)

> 별 모양의 나사

집에 저런 모양의 나사가 없어서 바로 자전거를 타고 근처 다이소에 찾으러 가 보았는데요.

다이소에 있던 모든 제품을 확인 해 본 결과, 맞는 나사가 없습니다.

![image-20221126165217705](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-memory.assets/image-20221126165217705.jpg)

그나마 다이소에는 별 모양 나사가 딱 하나 있었는데요

![IMG_0007](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-memory.assets/IMG_0007.jpeg)

별모양이 있길래 구입하고 보니 T6, T7, T8 뿐 이었습니다. 노트북의 나사는 보통 T5 입니다.

다이소에서는 요즘 나오는 노트북 분해 나사를 구할 수 없었습니다.

![cou](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-memory.assets/cou.jpg)

> 그래서 결국 쿠팡에서 3천원자리 드라이버 세트를 주문 하고 하루 기다렸습니다.

이제 모든 나사를 풀어주면 되는데, 힘을 주면 나사가 갈릴 수가 있으니 조심조심 해서 하나씩 다 풀어 줍니다.

모든 나사를 푼 이후에는 모서리를 들어 올려야 하는데요, 이게 만만치가 않습니다. 저는 명함 2개를 돌려가며 넣어서 열었습니다.

얇은 카드나 플라스틱 조각 칼 등등을 사용 할 수 있습니다. 막 쓰는 컴퓨터라면 얇은 일자 드라이버를 넣어 들어 올리는 방법도 있습니다.

![lap](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-memory.assets/lap.png)

> 찌그러진 명함

마침내 완전히 열렸습니다.

![IMG_0024 Large](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-memory.assets/IMG_0024%20Large.jpeg)

생각했던 것 보다는 상태가 좋아서 놀랐는데요, 그래도 쿨러에 끈적이는 먼지가 많아 청소를 한번 해 주었습니다.

![clean](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-memory.assets/clean.png)

> 청소 전 후

램 슬롯은 먼저 확인했을 때는 4개가 있고 1개가 사용중이라고 했었는데, 하나밖에 보이지 않았습니다.

아마도 하나는 온보드 메모리로 보이고, 실제로는 슬롯이2개 였거나 혹은 나머지 2개는 키보드를 뜯어 내야 있는 모양입니다. 

![before](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-memory.assets/before.png)

이 비어있는 램 슬롯에 새로 구입한 램을 꼽아 줍니다.

하단의 구분 공간이 중간보다 살짝 한 쪽에 치우쳐있기 때문에, 위 아래 방향은 어렵지 않게 구분 해 낼 수 있습니다. 살짝 기울인 상태로 꽂아 넣고 아래로 눌러 주면 고정이 됩니다.

![after](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-memory.assets/after.png)

> 램이 장착 된 상태

이후 조립은 분해의 역순으로 진행하면 되며, 조립 하기 전에 전원을 넣어 이상이 없는지를 먼저 확인 해 주는게 좋습니다.

조립을 다 해놓았는데 문제가 발견되면 참 곤란합니다.

## 확인

이제 다시 컴퓨터를 켜고 free 명령어로 램 공간을 확인 해 보았습니다. 16B는 14.9 GiB 이기 때문에 15Gi 라고 나오는 것이 맞습니다.

![image-20221127102612379](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-memory.assets/image-20221127102612379.png)

> 정상적으로 확장한 램이 인식 됩니다.
>
> 엘라스틱 서치는 램이 늘어나자마자 귀신같이 램 사용량을 두배로 늘렸네요. 

### 도커 컨테이너 메모리 제한

엘라스틱 서치 컨테이너의 메모리를 제한해보겠습니다. 기본적으로 전체 메모리의 절반을 항상 차지하도록 셋팅이 되어 있는것으로 보입니다.

```bash
docker update --memory="1g" elastic
```

![image-20221127145328553](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-memory.assets/image-20221127145328553.png)

> 실행중이지 않은 컨테이너에 명령을 주면 명령이 먹히지 않고, 실행중인 컨테이너에 명령을 주면 당장 메모리 반납을 다 하지 못해서 설정은 하지만 오류가 발생합니다. 설정 이후 컨테이너를 새로 시작 해 줍니다.

![image-20221127145432960](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/laptop-memory.assets/image-20221127145432960.png)

> elastic 컨테이너가 1GiB 이하의 메모리를 양심적으로 사용하고 있습니다.

지금까지 노트북 메모리 변경에 대해 알아보았습니다.

이상입니다. 