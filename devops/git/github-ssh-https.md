# Github 22 번 포트 막혔을 때 git ssh fetch/pull 하는 방법

## Intro

[Github / Gitlab SSH 공개 키 등록하기](https://shanepark.tistory.com/247) 를 통해 ssh 키를 등록해두었다면, 암호입력이나 토큰발급등의 번거로운 작업 없이 간단하게 git remote 저장소로부터 clone을 비롯한 fetch, pull, push 등의 작업을 간편하게 할 수 있다.

그런데 카페를 간다거나 다른사람의 집에 방문하는 등 평소와 다른 환경에서 와이파이에 접속 했는데, Github 의 SSH 통신이 안먹히는 경우가 있다. 

그런경우 아래와 같은 에러가 발생한다.

```
ssh: connect to host github.com port 22: Operation timed out
```

이걸 겪은건 설날에 가족집에 방문해서 커밋을 하는 과정이었는데 하필 인터넷이 B사 제품이었고, B사에서는 22번 포트를 막아두었기 때문에 이런 일이 발생했다. 

다행인건 언젠가 이 문제를 겪은 사람이 관련된 이야기를 들은 적이 있었고, <u>오 이건 생각도 못했는데 그럴수 있겠다</u>.. 라고 생각만 하고 넘어갔었는데 마침 같은 상황이 처했기 때문에 어렵지 않게 원인을 파악할 수 있었다는 것이다. 

### 방화벽이 막는경우 테스트

git에 ssh 키를 등록한 경우 아래의 명령어로 github에 본인의 ssh 상태를 확인할 수 있다. 정상적인 경우 `Hi {username}!`으로 응답이 온다.

```bash
ssh git@github.com
```

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/git/github-ssh-https.assets/1.webp)

> 응답이 안옴

## 해결

이럴 경우는 ssh 포트를 우회해야 한다. Github에서는 이같은 경우를 대비해 HTTPS 포트인 443도 같은 용도로 열어두었다.

이번에는 포트를 지정해서 요청해보겠다. 참고로 `The authenticity of host '[ssh.github.com]:443`어쩌고 하며 경고 메시지가 나오는데 `Y`로 대답해야 한다.

```bash
ssh -T -p 443 git@ssh.github.com
```

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/git/github-ssh-https.assets/2.webp)

> 문제 없이 ssh 통신이 가능하다.

그럼 이제 매번 번거롭게 포트지정을 하지 않기 위해 설정을 추가해서 간단히 해보자.

```bash
vi ~/.ssh/config
```

ssh 설정파일에 아래의 내용을 추가한다.

```
Host github.com
    Hostname ssh.github.com
    Port 443
    User git
```

이제 다시 시도해본다.

```bash
ssh git@github.com
```

![3](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/git/github-ssh-https.assets/3.webp)

간단히 우회해내며 문제 없이 github에 ssh 통신이 된다.

이제 ssh로 추가한 github 저장소들의 fetch, pull 등도 문제없이 진행됨을 확인할 수 있다. 한번 설정해두면 다시는 신경쓰지 않아도 된다.

**References**

- https://docs.github.com/en/authentication/troubleshooting-ssh/using-ssh-over-the-https-port