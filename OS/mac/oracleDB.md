# [MacOS] M1 맥북 도커로 ORACLE DB 실행하기

## Intro

Colima를 사용해 M1 맥북에서 오라클 로컬에서 띄우기.

## 설치

### colima 설치

brew 를 활용해서 손쉽게 설치합니다.

```bash
brew install colima
```

![image-20220802074204725](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220802074204725.png)

### docker 설치

```bash
brew install docker
```

docker가 아직 설치되지 않았다면 설치 해 주고, 이미 설치되어있다면 Docker Destop을 종료하거나 삭제 해 주세요.

### colima 실행

```bash
colima start --memory 4 --arch x86_64
```

![image-20220802074246457](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220802074246457.png)

> `docker ps` 명령어가 작동됩니다.



```bash
docker run -e ORACLE_PASSWORD=pass -p 1521:1521 -d gvenzl/oracle-xe
```



![image-20220802074143096](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220802074143096.png)

설치 완료

![image-20220802074435458](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220802074435458.png)

로그를 확인 해 봅니다.

![image-20220802074458233](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220802074458233.png)

```bash
docker logs -f 컨테이너명
```

![image-20220802074511589](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220802074511589.png)

같은 도커 이미지를 Docker destop 에서 돌렸을때에는 실행이 되지 않았는데, Colima로 돌리니 Database mounted가 되었습니다!

조금 더 기다리니..

![image-20220802074556719](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220802074556719.png)

> 마침내 DATABASE IS READY TO USE 가 되었습니다.

## Dbeaver 연결 테스트

이제 DBeaver로 연결 테스트를 해 보겠습니다.

![image-20220802074851769](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/mac/oracleDB.assets/image-20220802074851769.png)

m1 맥북에서 localhost:1521 로 DB 접속에 처음 성공한 감격의 순간 입니다.

이제 마음놓고 M1 맥북에서도 로컬에서 오라클 데이터베이스를 사용하실 수 있게 되었습니다 :) 

ref

- https://github.com/gvenzl/oci-oracle-xe/issues/63
- https://github.com/abiosoft/colima