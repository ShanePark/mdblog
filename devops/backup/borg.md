# BorgBackup 사용해서 백업하기

## BorgBackup

BorgBackup (줄여서 Borg) 은 중복을 제거해주는 오픈소스 백업 프로그램 입니다

Borg의 기본 목표는 효율적이고 안전한 데이터 백업을 제공 하는 건데요, 데이터 중복 제거 기술을 활용해 Borg가 오직 변경된 데이터만 저장하도록 하고 있기 때문에 데일리 백업에도 적합합니다. 

### 주요 특징

- 중복제거로 인한 효율적인 저장 공간 사용

  각각의 파일을 다양한 청크로 쪼개고, 이전에 추가된 적이 없는 청크만이 리포지터리에 추가됩니다. 청크는 id hash 값이 같으면 중복으로 간주되는데요, id hash를 에는 `(hmac-)sha256` 와 같은 암호학적으로 강력한 hash 나 MAC 기능이 사용된다고 합니다.

- 빠른 백업속도

- 데이터 암호화

- 데이터 압축 

  모든 데이터는 아래의 다양한 압축 옵션을 활용해 압축 할 수 있습니다.

  - lz4 (매우 빠름, 낮은 압축률) 
  - zstd (넓은 범위의 처리 속도와 압축) 
  - zlib (보통 속도와 보통의 압축률) 
  - lzma (느린 속도, 높은 압축률)

- 원격 백업

  Borg는 어느 원격 호스트에도 SSH 접속을 통해 데이터를 백업 할 수 있습니다. 원격 서버에도 Borg가 설치된 경우 sshfs, nfs 와 같은 기존의 네트워크 파일시스템에 비교해 엄청난 퍼포먼스 증대 효과를 누릴 수 있습니다.

- 백업 데이터들은 파일시스템에 바로 마운트 가능

- 다양한 플랫폼에서 쉽게 설치가능 (Linux, MacOX, FreeBSD, OpenBSD, WSL 등등)

- 무료 오픈 소스 소프트웨어(BSD 라이센스)

- 쉬운 사용방법


## 설치

### 패키지 매니저 이용

패키지 매니저로 설치할 경우 매우 간단하게 설치가 가능합니다.

| Distribution    | Source                                                       | Command                                                      |
| --------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| Debian / Ubuntu | [Debian packages](https://packages.debian.org/search?keywords=borgbackup&searchon=names&exact=1&suite=all&section=all)<br />[Ubuntu packages](https://launchpad.net/ubuntu/+source/borgbackup), [Ubuntu PPA](https://launchpad.net/~costamagnagianfranco/+archive/ubuntu/borgbackup) | `apt install borgbackup`                                     |
| Fedora/RHEL     | [Fedora official repository](https://apps.fedoraproject.org/packages/borgbackup) | `dnf install borgbackup`                                     |
| macOS           | [Homebrew](https://formulae.brew.sh/formula/borgbackup)      | `brew install borgbackup` (official formula, **no** FUSE support) **or** `brew install --cask macfuse` ([private Tap](https://github.com/borgbackup/homebrew-tap), FUSE support) `brew install borgbackup/tap/borgbackup-fuse` |

저는 Ubuntu 를 사용하고 있으니 apt를 이용해 설치 해 보겠습니다.

```bash
sudo apt install borgbackup
```

![image-20221004095556785](/home/shane/Documents/git/shane/mdblog/devops/backup/borg.assets/image-20221004095556785.png)

4MB 정도의 저장공간을 사용한다고 합니다. 엔터키를 입력해서 설치를 시작합니다.

설치가 완료된 후에는 버전을 확인 해 봅니다.

```bash
borg --version
```

![image-20221004095726965](/home/shane/Documents/git/shane/mdblog/devops/backup/borg.assets/image-20221004095726965.png)

> 1.1.15 버전이 설치되었습니다. Ubuntu 버전별로 설치 되는 Borg 버전은 아래와 같습니다.
>
> ![image-20221004095930095](/home/shane/Documents/git/shane/mdblog/devops/backup/borg.assets/image-20221004095930095.png)

### Standalone Binary로 설치

패키지 매니저를 이용 할 수 없는 경우에는 바이너리 파일을 이용해 설치 해 주면 됩니다.

저도 RockyLinux 에 설치하려고 하니 `No match for argument: borgbackup` 라고 뜨며 찾지를 못했습니다. 이때는 직접 설치해야 하는데 각 버전별 릴리즈들은 https://github.com/borgbackup/borg/releases 에서 받을 수 있습니다.

![image-20221004162227667](/home/shane/Documents/git/shane/mdblog/devops/backup/borg.assets/image-20221004162227667.png)

borg-linux64의 파일 경로를 복사한 뒤에 wget으로 설치할 서버에 다운로드 하였습니다.

```bash
wget https://github.com/borgbackup/borg/releases/download/1.1.15/borg-linux64
```

이후 다운로드 받은 파일을 옮겨주고 소유 및 권한을 변경 해 줍니다.

```bash
sudo cp borg-linux64 /usr/local/bin/borg
sudo chown root:root /usr/local/bin/borg
sudo chmod 755 /usr/local/bin/borg
```

이제는 borg 명령어가 작동합니다. 버전을 확인 해 봅니다.

```bash
borg --version
```

그 외 소스코드를 통해 pip 혹은 git 으로 설치 하는 방법에 대해서는 https://borgbackup.readthedocs.io/en/stable/installation.html 에서 확인 하실 수 있습니다.

## 실습

![image-20221004101234835](/home/shane/Documents/git/shane/mdblog/devops/backup/borg.assets/image-20221004101234835.png)

`~/Downloads/` 경로에 `files` 라는 폴더를 만들고 테스트를 위해 더미 파일을 몇가지 넣어 두었습니다.

이 상태로 몇가지 실습을 진행 해 보도록 하겠습니다.

### 초기화 및 백업

1. 백업에 앞서 리포지터리가 초기화 되어야 합니다.

  ```bash
  borg init --encryption=repokey ~/Downloads/repo
  ```

![image-20221004101908267](/home/shane/Documents/git/shane/mdblog/devops/backup/borg.assets/image-20221004101908267.png)

2.  준비해둔 `~/Downloads/files` 폴더를 통째로 Monday 라는 이름의 아카이브로 백업합니다

  ```bash
  borg create ~/Downloads/repo::Monday ~/Downloads/files
  ```

  ![image-20221004102219069](/home/shane/Documents/git/shane/mdblog/devops/backup/borg.assets/image-20221004102219069.png)
  > 총 4기가 정도를 백업하는데 1분 정도 걸렸습니다.

  이제 테스트를 위해 `files` 폴더에 있는 모든 `.jpg` 파일을 삭제 하겠습니다.

```bash
rm  ~/Downloads/files/*.jpg
```

![image-20221004102602194](/home/shane/Documents/git/shane/mdblog/devops/backup/borg.assets/image-20221004102602194.png)

> 모든 이미지 파일이 삭제 되고 1G.file 그리고 3G.file 만이 남아 있습니다.

3. 이번에는 Tuesday 라는 이름으로 이미지가 삭제되어버린 폴더를 백업 합니다. 처음 Monday를 백업 할 때는 꽤 오래 걸렸는데 이번에는 훨씬 빨리 끝납니다.

```bash
borg create ~/Downloads/repo::Tuesday ~/Downloads/files
```

백업시 `--stats` 옵션을 걸어주면 결과를 확인 할 수 있습니다.

```bash
borg create --stats ~/Downloads/repo::Wednesday ~/Downloads/files
```

![image-20221004103037245](/home/shane/Documents/git/shane/mdblog/devops/backup/borg.assets/image-20221004103037245.png)

> 확인 해 보면, 파일을 압축해서 저장 하는데요. 제가 넣어둔 각각 1GB, 3GB 라는 이름의 파일들이 모두 0으로 채워넣은 더미파일이라서 압축된 용량이 굉장히 작습니다.

4. 해당 리포지러티에 있는 모든 아카이브 리스트는 아래의 명령어로 확인 합니다.

```bash
borg list ~/Downloads/repo
```

![image-20221004103332943](/home/shane/Documents/git/shane/mdblog/devops/backup/borg.assets/image-20221004103332943.png)

5. 특정 아카이브 이름으로 내용물들을 확인 해 보는 것도 가능 합니다. Monday 라는 이름을 가진 아카이브의 내용물을 확인 해 보도록 하겠습니다.

```bash
borg list ~/Downloads/repo::Monday
```

![image-20221004103613603](/home/shane/Documents/git/shane/mdblog/devops/backup/borg.assets/image-20221004103613603.png)

> 처음에 있던 총 5개의 파일들이 보입니다.

### 복구

6. Monday 라는 이름의 아카이브를 복구 해 보도록 하겠습니다.

```bash
borg extract ~/Downloads/repo::Monday
```

![image-20221004104154200](/home/shane/Documents/git/shane/mdblog/devops/backup/borg.assets/image-20221004104154200.png)

> 명령어를 실행 한 폴더에 백업 당시 해당 폴더의 루트부터의 경로를 만들어가며 복구가 됩니다.

7. Archive 삭제하기

방금 복구한 Monday 라는 아카이브를 삭제 해 보겠습니다.

```bash
borg delete ~/Downloads/repo::Monday
```

![image-20221004104512760](/home/shane/Documents/git/shane/mdblog/devops/backup/borg.assets/image-20221004104512760.png)

Monday 라는 아카이브가 제거 되었고, Tuesday 와 Wednesday 만이 남아 있습니다.

하지만 Archive를 삭제 한다고 해도 저장 공간이 다시 확보되는 건 아닙니다.



8. 리포지터리의 조각 파일들을 압축 해서 삭제된만큼 저장 공간을 확보하기

위에서 Archive 를 제거 했으니 저장 공간을 조금이라도 더 확보 할 수 있습니다.

```bash
borg compact ~/Downloads/repo
```

![image-20221004104923570](/home/shane/Documents/git/shane/mdblog/devops/backup/borg.assets/image-20221004104923570.png)

> 다만 borg compact 는 1.2.0 버전 부터 지원되기 때문에 1.1.X 버전에서는 compact를 사용할 수 없습니다.

## 원격 서버를 다른 원격 저장소에 백업

이번에는 보다 복잡한 예제를 진행 해 보도록 하겠습니다.

![image-20221004112943806](/home/shane/Documents/git/shane/mdblog/devops/backup/borg.assets/image-20221004112943806.png)

> 각각 클라이언트 서버, 백업할 대상 서버, 백업 내용을 저장할 저장소 서버를 준비했습니다.

1. 저장소 서버에 및 백업할 서버에도 BorgBackup 설치

```bash
sudo apt install borgbackup
```

![image-20221004113113361](/home/shane/Documents/git/shane/mdblog/devops/backup/borg.assets/image-20221004113113361.png)

> 스크린샷에는 나오지 않았지만 가운데 있는 백업 대상 서버에서도 BoardBackup을 설치 해 줍니다.

2. 저장소 서버에 리포지터리 초기화 (클라이언트, 백업할 서버, 저장소 서버 어디에서든 가능)

```bash
borg init -e none 계정명@호스트이름:~/Documents/borgrepo
```

비밀번호를 설정하고 싶지 않으면 `-e none` 옵션만 줘도 됩니다. 저는 `~/.ssh/config` 에 설정이 되어 있기 때문에 간단하게 `asus:` 라는 이름으로 ssh 접속이 가능했습니다. 

SSH로 접속 할 경우 비밀번호를 입력할 필요가 없도록 미리 ssh 키 등록을 해 두셔야 합니다.

> [SSH key 생성하고, 서버에 등록해서 비밀번호 없이 접속하기](https://shanepark.tistory.com/195)

![image-20221004114212505](/home/shane/Documents/git/shane/mdblog/devops/backup/borg.assets/image-20221004114212505.png)

> 보이는 것 처럼, 클라이언트 서버에서 내린 `borg init` 명령으로 저장소 서버의 `~/Documents` 경로 하위에 borgrepo 가 생성 되었습니다.

백업할 데이터 준비

![image-20221004132213426](/home/shane/Documents/git/shane/mdblog/devops/backup/borg.assets/image-20221004132213426.png)

> 백업해야 할 서버에 백업 할 데이터를 간단 하게 만들어 주었습니다. 경로는 `~/mydata` 입니다.

이제 백업서버에 백업 할 내용을 백업 합니다. `{now}`라고 입력 하면 Borg가 자동으로 백업 당시의 날짜 및 시간 데이터를 기록 해 줍니다.

```bash
borg create --stats 'asus:~/Documents/borgrepo::gaia_data-{now}' ~/mydata
```

![image-20221004132355653](/home/shane/Documents/git/shane/mdblog/devops/backup/borg.assets/image-20221004132355653.png)

>  파일이 워낙 없으니 0.12초 만에 백업이 완료 되었습니다.

이제 클라이언트 서버에서 백업서버에 저장된 내용을 확인 해 봅니다.

**목록 조회**

```bash
borg list asus:~/Documents/borgrepo
```

**단건 조회**

```bash
borg list asus:~/Documents/borgrepo::gaia_data-2022-10-04T13:23:07
```

![image-20221004132529825](/home/shane/Documents/git/shane/mdblog/devops/backup/borg.assets/image-20221004132529825.png)

> 클라이언트 서버에서도 백업서버를 통해 해당 내용을 확인 할 수 있습니다.

백업된 데이터를 복원하는 것도 가능합니다. 

```bash
borg extract asus:~/Documents/borgrepo::gaia_data-2022-10-04T13:27:35
```

![image-20221004133254867](/home/shane/Documents/git/shane/mdblog/devops/backup/borg.assets/image-20221004133254867.png)

> 백업 당시의 구조 그대로 파일들이 모두 복원 되었습니다.

## 백업 자동화

### 스크립트 작성

아래와 같은 쉘 스크립트를 생성 해서 Cron에 추가해두면 자동으로 일정 주기로 백업을 하는 것은 물론이고 특정 패턴의 파일들을 제외시킬 수도 있으며 prune 커맨드를 통해 보존할 백업 데이터의 수를 각각 월간, 주간, 일간 설정하여 저장 공간을 확보할 수도 있습니다. 

아래의 쉘 스크립트를 실행하기 전에 해당 리포지터리를 꼭 초기화 해주셔야 합니다.

```sh
#!/bin/sh

# BORG 저장소를 미리 설정해두면 커맨드라인에서 또 작성 할 필요가 없습니다.
export BORG_REPO=asus:~/Documents/borgrepo

# PassPhrase를 설정 한 경우
export BORG_PASSPHRASE=''

# some helpers and error handling:
info() { printf "\n%s %s\n\n" "$( date )" "$*" >&2; }
trap 'echo $( date ) Backup interrupted >&2; exit 2' INT TERM

info "Starting backup"

# Backup the most important directories into an archive named after
# the machine this script is currently running on:

borg create                         \
    --verbose                       \
    --filter AME                    \
    --list                          \
    --stats                         \
    --show-rc                       \
    --compression lz4               \
    --exclude-caches                \
    --exclude '**/data1.txt'        \
                                    \
    ::'{hostname}-{now}'            \
    /home/ubuntu/mydata/            \

backup_exit=$?

info "Pruning repository"

# 일별/ 주간/ 월간 백업을 남겨두고 싶은 갯수를 지정해 그 이전 백업들은 prune >명령어로 제거합니다. prefix 가 같은 백업 단위로 시행되기 때문에, prefix를 올바
르게 지정하는게 매우 중요합니다.

borg prune                          \
    --list                          \
    --prefix '{hostname}-'          \
    --show-rc                       \
    --keep-daily    7               \
    --keep-weekly   4               \
    --keep-monthly  6               \

prune_exit=$?

# use highest exit code as global exit code
global_exit=$(( backup_exit > prune_exit ? backup_exit : prune_exit ))

if [ ${global_exit} -eq 0 ]; then
    info "Backup and Prune finished successfully"
elif [ ${global_exit} -eq 1 ]; then
    info "Backup and/or Prune finished with warnings"
else
    info "Backup and/or Prune finished with errors"
fi

exit ${global_exit}

```

테스트를 위해 위에서 작성한 스크립트를 실행 해 봅니다

```bash
sh ./borg.sh
```

![image-20221004154520027](/home/shane/Documents/git/shane/mdblog/devops/backup/borg.assets/image-20221004154520027.png)

현 상태의 archive 를 생성 하고, prune 명령어를 통해 불필요한 아카이브는 제거가 되는 모습입니다.

### Crontab 등록

이제 스케줄을 등록 해서 매일 작동하게끔 해 둡니다. 

```bash
sudo vi /etc/crontab
```

일단 테스트를 위해 매 분마다 실행이 되게끔 등록을 해 보았습니다.

```bash
*/1 *   * * *   ubuntu  sh ~/borg.sh
```

등록 후 `syslog`를 확인 해서 CRON이 의도대로 작동 하고 있는지를 확인 해 봅니다.

```bash
tail -f /var/log/syslog
```

![image-20221004155420083](/home/shane/Documents/git/shane/mdblog/devops/backup/borg.assets/image-20221004155420083.png)

설정 해 둔 대로 매분 0초 마다 스크립트가 실행 되고 있습니다.

이제 매일 한번만 실행 되도록 설정을 다시 변경 해 줍니다. 매일 새벽 4시 59분마다 동작 하도록 하겠습니다.

```bash
59 4    * * *   ubuntu  sh ~/borg.sh
```

이제 매일 자동으로 백업이 됩니다. 다만 설정이 의도되로 되었는지를 확실히 하기 위해 주기적으로 백업이 잘 되고 있는지 초반에는 주기적으로 몇번 확인을 해 주는 것이 좋을 것 같습니다.

## 주의사항

### 저장공간 확보

새로운 백업을 생성 하기 전에 파일시스템에 충분한 저장 공간이 있는지 미리 확인합니다. 확실히 하기 위해서 아래의 명령어로 Borg에게 추가 공간을 미리할당 해 둘 수도 있습니다.

```
borg config /path/to/repo additional_free_space 2G
```

최소한 오래된 아카이브를 삭제하기 위해서라도 적정량의 저장공간은 제공 되어야 합니다.

### 권한 이슈

권한 관련 이슈를 피하기 위해서는 레포지터리에 항상 같은 사용자 계정으로 접근 해야 합니다.

다른 사용자의 혹은 운영체제의 파일을 백업 하려면, 루트 권한으로 borg를 실행 해야 합니다.

`borg@remote_host` 와 같은 방식으로 원격 서버의 리포지터리에 저장하려면 borg를 실행하고 저장소에 접근하는 원격 사용자는 항상 borg 입니다. 로컬 저장소에 다른 사용자로 접속할 때는 같은 방식의 ssh 접속을 `borg@localhost` 에 할 수 있습니다.

### 동시성 이슈

Borg는 데이터가 백업되는 동안 동시성에 대한 고려를 전혀 하지 않습니다. 그저 파일이 어떤 상태이건 그대로 읽고 백업하기 때문에 두가지 문제가 발생 할 수 있습니다.

- 파일이 백업되는 시점에 백업을 시작했을 때와 다른 상태가 된다.
- 파일을 백업 하는 중에 파일의 상태가 변경되 내부적으로 불일치가 발생 할 수 있음

동시성을 보장해야 하는 파일들을 한번에 백업 할 때는 아래의 테크닉들을 활용 할 수 있습니다.

- 파일을 변경 할 가능성이 있는 프로그램을 실행하지 않는다.
- 파일이나 파일 시스템, 컨테이너 볼륨이나 논리적 볼륨의 스냅샷을 만든다.
- DB를 덤프하거나 중단한다.
- 이미지를 백업하기전에 VM을 멈춘다.
- 컨테이너 볼륨을 백업하기 전에 컨테이너를 멈춘다.




**References**

- https://borgbackup.readthedocs.io/en/stable/index.html#
- https://github.com/borgbackup/borg







