# Linux) 대용량의 더미 파일 생성하기

## Intro

10GB가 넘는 대용량의 파일 업로드 처리에서 문제가 있었습니다. 해당 파일을 제공받아 테스트를 진행 해 보았는데 이후에 다양한 파일 용량별로 처리 여부와 핸들링에 걸리는 시간을 확인해보려고 하는데, 적당한 파일을 구할 방법이 생각나지 않았습니다.

![image-20220525172611947](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/dummy.assets/image-20220525172611947.png)

> https://testfiledownload.com/

인터넷에 이런식으로 더미 파일을 제공하는 사이트가 있기는 하지만, 초당 300kb/s 정도의 처참한 속도가 나오기 때문에 테스트를 위한 파일을 다운로드로 해결 할 수는 없었습니다.

**다행히도 이런 경우에는 간단한 명령어로 더미 파일을 생성 할 수 있습니다.** 

여러가지 명령어를 모두 정리 해 두었으니 각자 편하신 명령어를 사용 해서 더미 파일을 생성하시면 됩니다.

## Linux

### dd

첫번째로 가장 흔하게 사용되는 dd 입니다. unix 명령어기 때문에 MacOS에서도 사용 할 수 있습니다.

아래는 현재 경로에 1GB 짜리 파일을 생성하는 명령어 입니다.

```bash
dd if=/dev/zero of=1g_file bs=1 count=0 seek=1G
```

![image-20220525174215929](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/dummy.assets/image-20220525174215929.png)

**dd** 명령어를 사용 할 때, `if=/dev/urandom` 옵션을 준다면 파일을 모두 0이 아닌 랜덤 값으로 채울 수 있다는 장점이 있습니다. 파일을 압축하는등의 가공이 필요한 경우에는 해당 옵션이 필요할 수 있습니다.

또한, count와 블럭 사이즈를 설정 해서 원하는 크기의 파일을 생성 할 수도 있습니다.

**1GB 크기의 더미 파일을 만드는 또 다른 예**

```bash
dd if=/dev/zero of=1g_file bs=100M count=10
```

![image-20220525174424733](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/dummy.assets/image-20220525174424733.png)

### fallocate

그 다음으로는 비교적 사용법이 간단한 fallocate 명령어 입니다. 사용 방법은 `fallocate -l 크기 파일명` 순으로 입력 하면 됩니다.

아래의 예시는 10GB 파일을 만드는 예시 입니다.

```bash
fallocate -l 10GB 10G.file
```

![image-20220525173657767](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/dummy.assets/image-20220525173657767.png)

> GB와 GiB 모두 입력 해 보았는데 각각 구분 해서 파일의 용량이 생성 되었습니다.
>
> 그냥 `fallocate -l 10g 10g.file` 을 입력 했을때는 GiB로 생성 되었습니다.

### truncate

그 외에 truncate 명령어도 사용 할 수 있습니다. 사용법은 거의 동일합니다.

```bash
truncate -s 500MB half-giga.file
```

![image-20220525174624001](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/dummy.assets/image-20220525174624001.png)

세가지 명령어 모두 입력 하자 마자 순식간에 파일을 생성 해 주기 때문에 평소에는 사용법이 간단한 fallocate나 truncate를 이용하고, 특별한 옵션이 필요 할 때는 dd를 사용 하면 되겠습니다.

## 번외: Windows / MacOS

### Windows

윈도우 사용자라면 PowerShell을 켜고 fsutil 명령어를 이용해 생성 할 수 있습니다.

아래의 예제는 1GB 짜리 더미 파일을 1GB.file 이라는 이름으로 생성합니다.

```bash
fsutil file createnew 1GB.file 1048576000
```

### Mac

맥에서는 Linux의 `dd`를 사용해도 되지만 mac 전용인 mkfile 명령어도 제공 하고 있습니다. 

**10GB 파일 생성 예시**

```bash
mkfile -n 10g 10GB_file
```

<br><br>

이상입니다.

ref: https://zetawiki.com/wiki/%EB%A6%AC%EB%88%85%EC%8A%A4_%EB%8C%80%EC%9A%A9%EB%9F%89_%ED%8C%8C%EC%9D%BC_%EC%83%9D%EC%84%B1