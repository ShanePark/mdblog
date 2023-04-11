# Linux) watch 명령을 이용해 ls 명령어 실시간 호출하기

## Intro

서버에 파일 업로드할 때 임시 파일이 생성되고 소멸되는 시점과 소요 시간 등을 확인 하기 위해 `ls -al` 명령어를 계속 치고 있었습니다. 

<img src=https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/watch-ls.assets/image-20220617103225058.webp width=670 height=500 alt=1>

임시 파일을 생성 하거나 삭제 할 때에 로그를 남기는 방법이 있지만, 스프링이 MultipartFile 을 받아 임시 파일을 생성하는 시점을 확인 하려니 쉽지 않았습니다.

```yaml
spring:
  servlet:
    multipart:
      location: /home/shane/Downloads
```

> SpringBoot 에서는 aplication.yml에 위의 설정을 통해 임시 파일이 생성되는 경로를 변경 할 수 있습니다.

탐색기를 띄워 두어 눈으로 확인 할 수도 있지만, SSH 로 서버에 접속해서 상태를 확인 할 때는 얄짤없이 `ls -al`을 계속 타이밍 해야 했는데요

## watch

이때 watch 명령어를 활용 하면 손쉽게 모니터링 가능합니다.

기본 예제는 아래와 같습니다.

```bash
watch -n 1 "ls -altr"
```

- -n은 ---interval 의 약자 입니다. 단위는 `초` 이며, 소수점 단위로도 설정 할 수 있습니다.
- ls 뒤에 붙은 t 옵션은 시간순으로 정렬을 의미합니다.
- ls 뒤에 붙은 r 옵션은 역순으로 정렬하는 것을 의미합니다. `-tr` 을 하면 최근에 추가된 파일이 가장 아래에 표기됩니다.

이제 명령어를 직접 입력 해 확인 해보면

![peek](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/watch-ls.assets/peek.webp)

> 파일이 생성 되거나 삭제 될 때 실시간으로 확인 할 수 있습니다.

이상입니다.

ref: https://stackoverflow.com/questions/18645759/tail-like-continuous-ls-file-list