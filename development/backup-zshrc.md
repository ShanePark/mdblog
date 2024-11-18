# 각 서버의 `.zshrc` 파일을 자동으로 백업하기

## Intro

여러 개의 서버를 관리하다 보면 환경 설정 파일들이 여기저기 흩어져 관리하기 복잡해진다. 특히 `.zshrc` 나 `.bashrc` 같은 셸 설정 파일은 여러가지 도구와 플러그인, alias를 관리하는 파일이라서 해당 파일 내용을 잃어버리거나 실수로 덮어쓴다면 굉장히 번거로워진다.

개인적으로는 이 문제를 해결하기 위해 `.zshrc` 파일을 Git 리포지토리에 자동으로 백업하는 시스템을 만들었다. 

한번 시스템을 구축해두니, 가끔 쉘 설정 파일 편집한 날에는 새벽에 자동으로 백업이 되어 있으니 매우 편해서 해당 내용을 공유해보려한다.

이번 글에서는 cron 스케줄링을 활용해 각 서버에서 `.zshrc` 파일을 매일 자동으로 백업하는 방법을 차근차근 설명해 보겠다.

## Git 리포지토리

### 리포지토리 생성 및 SSH 키 등록
백업 데이터의 버전을 관리할 Git 리포지토리를 먼저 만들어야 한다. GitHub에서 새 리포지토리를 생성한 후, 로컬 서버와 연결할 SSH 키를 설정한다. 서버에 SSH 키가 없으면 아래 명령어로 생성할 수 있다.

```bash
ssh-keygen -t rsa -b 4096 -C "your_email@example.com"
cat ~/.ssh/id_rsa.pub
```

생성된 공개 키를 복사해서 GitHub 리포지토리의 **Settings > Deploy keys** 메뉴에서 추가한다. **Write access**를 체크해 키에 쓰기 권한을 부여하는 걸 잊으면 안된다.

### Git 글로벌 설정 추가
Git을 처음 사용하는 환경이라면 아래 명령어로 글로벌 설정을 추가해 준다.

```bash
git config --global user.name "Your Name"
git config --global user.email "YourEmail@example.com"
```

이제 Git 리포지토리와 SSH 키 설정이 완료되었다.

## 백업 설정

### 백업 스크립트 작성

`.zshrc` 파일을 리포지토리에 백업하기 위한 스크립트를 작성한다. 아래는 예제 스크립트다. `/zshrc-backup` 디렉토리를 기준으로 동작하도록 작성했다.`cron.sh` 파일을 `/zshrc-backup` 디렉토리에 아래와 같이 작성한다.

```bash
#!/bin/bash

# 1. .zshrc 파일 복사
cp ~/.zshrc /zshrc-backup/gcloud

# 2. Git 동기화
cd /zshrc-backup
git pull
git add .
git commit -m "gcloud .zshrc backup"
git push
```

이 스크립트는 `.zshrc` 파일을 백업 디렉토리로 복사한 뒤, Git 명령어로 백업 내용을 저장하고 리포지토리에 푸시한다.

여러개의 서버에서 백업한다면 충돌을 피하기 위해 반드시 서로 다른 폴더에 백업을 하도록 해야 한다.

### 백업 디렉토리 설정 및 초기화

백업 데이터를 저장할 디렉토리를 생성하고, 리포지토리를 클론한다.

```bash
sudo mkdir /zshrc-backup
sudo chown $USER:$USER /zshrc-backup
git clone git@github.com:YourUsername/zshrc-backup.git /zshrc-backup
```

디렉토리 권한을 현재 사용자로 설정하는 것을 잊지 말자. 그렇지 않으면 cron에서 쓰기 권한 오류가 발생할 수 있다.

## Cron 작업 등록

### Cron 작업 스케줄링
pull 과 commit 사이에 동시성 문제가 발생하지 않도록 서버마다 조금 다른 시간에 백업 작업을 실행하도록 `cron`을 설정한다. `cron.sh` 파일에 실행 권한을 부여한 뒤, 시스템의 `crontab` 파일에 작업을 추가한다.

```bash
chmod +x /zshrc-backup/cron.sh
sudo vi /etc/crontab
```

아래와 같은 형식으로 작업을 등록한다.

```
0 1 * * *   shane    /zshrc-backup/cron.sh 2>/zshrc-backup/log.log
```

### Cron 시간표 설정
여러 서버에서 백업 작업이 겹치지 않도록 시간표를 설정한다. 아래의 예시 처럼 Cron 시간이 조금씩 다르면 된다.

| 서버   | 사용자        | Cron 시간 |
| ------ | ------------- | --------- |
| asus   | shane         | 0 1 * * * |
| gcloud | shanepark_dev | 0 2 * * * |

## 결론

위와 같이 설정 한 뒤에, 백업이 잘 이루어 지고 있는지를 확인해보면 된다. Cron job이 생각처럼 잘 작동이 안되었다면 아래의 경로에 있는 로그들을 확인해보면 된다.

- Ubuntu, Debian

```
/var/log/syslog
```

- CentOS, RHEL

```
/var/log/cron
```

이제 쉘 설정 파일에 백업에 대한 걱정은 끝내도록 하자.
