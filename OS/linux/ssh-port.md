# SSH 기본 포트 변경 방법

## Intro

기본적으로 SSH는 22번 포트를 사용해서 원격 접속을 제공한다. 많은 해커들이 서버의 기본 포트를 대상으로 무차별 대입 공격(Brute Force Attack)을 시도하는 경우가 많다. `/var/log/auth.log` 파일을 보면 외부에서 잘못된 비밀번호로 접속을 시도한 흔적이 남아 있는 경우가 많다. 

22번 포트에서 운영하고 있는 서버의 해당 파일을 보여주도록 하겠다. SSH 포트를 기본 포트로 운영중이라면 해당 파일을 꼭 확인해보길 바란다.

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/ssh-port.assets/1.webp)

> 잠깐 파일을 열어보고 있는 동안에도 끊임없이 침입 시도 로그가 쌓였다.

이는 기본 포트를 그대로 사용하고 있다는 점을 악용한 공격이다. 보안을 강화하려면 SSH 포트를 다른 번호로 바꾸는 게 좋다. 이번 글에서는 SSH 포트를 변경하는 방법과 추가적인 보안 강화 방법을 알아본다.

## 변경 방법

### 1. SSH 설정 파일 수정하기

SSH 설정 파일을 열어본다.

```bash
sudo vi /etc/ssh/sshd_config
```

파일을 열었으면 `#Port 22` 항목을 찾는다.  내 경우에는 15번 라인에 적혀있었다.

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/ssh-port.assets/2.webp)

기본적으로 주석 처리(`#`)가 되어 있을 수 있으니, 주석을 제거하고 포트 번호를 원하는 숫자로 바꿔준다.

```
#Port 22
Port [원하는 포트 번호]
# 예) Port 8022
```

위와 같이 `#`을 제거하고 `Port 8022`처럼 다른 번호로 변경한다.

![3](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/linux/ssh-port.assets/3.webp)

> 참고로 58번 라인에 있는 PasswordAuthentication 도 `no`로 변경해주면 비밀번호를 입력해 로그인 하는 걸 차단할 수 있다. 
>
> 비밀번호 로그인을 차단하기 전에는 반드시 공개키를 먼저 `~/.ssh/authorized_keys` 에 등록 해야한다.

### 2. 변경 사항 저장하고 SSH 서버 재시작하기

설정을 수정했으면 변경 사항을 저장하고 파일을 닫는다. 

서버에 실제 적용하기 전에, 만일 해당 서버가 SSH 접속 외에는 접근방법이 마땅치 않다면 아래의 방화벽 이나 포트포워딩 등의 설정에 대해 먼저 체크하는걸 추천한다. 그렇지 않으면 제법 고생을 할 수 있으니 최소 열려있는 세션은 유지하면서 새 연결을 시도하자.

이제 SSH 서버를 재시작해서 변경 사항을 적용한다.

```bash
sudo systemctl restart sshd
```

> 만약 `systemctl` 명령이 안 된다면, `sudo service ssh restart`를 사용하면 된다.

이제 SSH 서버 설정을 변경하고 나서 이전과 똑같은 방법으로 ssh 연결을 하려고 하면 `port 22: Connection refused` 가 발생할 것이다. 

### 3. 방화벽 설정 업데이트

SSH 포트를 바꿨으면 방화벽 설정도 업데이트해야 한다. `telnet [ip] [Port번호]` 로 테스트해봤을 때 연결이 안된다면 아마 방화벽이 막혀있을 가능성이 크다.

방화벽 설정에 앞서 아래 명령어로 리스닝중인 포트들을 확인해본다. 새로 ssh 로 추가한 포트도 잘 리스닝이 되고 있는지도 보아야 한다.

```bash
netstat -tnlp
```

변경한 포트가 열려있지 않으면 원격으로 접속할 수 없으니 포트를 허용해야 한다. 본인이 주로 사용하는 방화벽에 설정을 추가하면 된다.

**ufw**

```bash
sudo ufw allow [원하는 포트 번호]/tcp
sudo ufw reload
```

**firewalld**

```bash
udo firewall-cmd --permanent --add-port=[원하는 포트 번호]/tcp
sudo firewall-cmd --reload
```

**iptables**

```bash
sudo iptables -A INPUT -p tcp --dport [원하는 포트 번호] -j ACCEPT
```

### 4. SSH 접속 테스트

이제 새로운 포트 번호를 사용해서 SSH로 접속해본다. 로컬에서 접속할 때는 아래 명령어를 사용한다.

```bash
ssh -p [원하는 포트 번호] 사용자명@서버IP주소
```

기본 포트 22번 대신 변경한 포트로 연결되는지 확인한다.

`~/.ssh/config` 파일에 등록해놓고 사용했었다면 아래의 예시처럼 포트에 대한 내용을 추가로 기입 해주면 된다.

```
Host myHost
  Hostname [ip here]
  Port 8022
  User shane
```

### 5. 공유기 사용 시 포트 포워딩 설정

공유기를 사용하고 있다면, 공유기의 포트 포워딩 설정에서 변경한 포트를 서버가 설치된 내부 IP 주소로 전달하도록 설정해야 한다. 이렇게 해야 외부에서 2222번 포트로 접근할 때 공유기가 해당 요청을 서버로 전송하게 된다

## 결론

이번 글에서는 SSH 기본 포트인 22번을 다른 포트번호로 변경하는 방법에 대해 알아보았다.

그 외에도, 일정 횟수 이상 로그인 시도를 실패한 IP를 차단하는 [Fail2Ban](https://github.com/fail2ban/fail2ban)을 설치해서 보안을 강화할 수 있다.

끝