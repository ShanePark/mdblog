# [MYSQL Docker] 데이터베이스 매일 자동 백업하기

## Intro

집에서 개인적으로 운영하고 있는 MYSQL 데이터베이스가 있습니다. 옛날에는 이용자가 없어 개발용으로만 사용하고 있었는데, 요즘에는 어느정도 유의미한 데이터가 쌓이고 있다 보니 괜히 실수로 데이터베이스에 흠집이 갈까 걱정이 되어 이제 슬슬 주기적인 백업을 하려고 합니다.

DB에 문제가 생겼을 때, 한시간 이내에 그대로 복구를 해 낼지 아니면 영영 모든 데이터를 잃게 될지 그 결과에 엄청 큰 차이가 있습니다.

규모가 정말 큰 서비스에서는 좀 더 효과적이고 빠른 백업 방법을 찾아야겠지만 손쉽고 간단하게 mysqldump를 이용해서 백업을 하려고 합니다.

거기에 더해 도커 컨테이너에 띄워져 있는 컨테이너를 자동으로 백업 할 수 있도록 스케줄러를 돌리도록 설정을 해서 크게 신경쓰지 않고 DB 데이터 손실에 대한 걱정도 덜어보려고 합니다.

## mysqldump

### 사용법

mysqldump의 사용법은 아래와 같습니다.

```bash
mysqldump -u {사용자 계정} -p {원본 데이터베이스명} > {생성할 백업 데이터베이스명}.sql
```

저같은 경우에는 mysql을 로컬에 직접 설치한게 아니고 Docker container로 띄워져 있기 때문에, 도커명령어를 통해 실행 할 수 있습니다.

현재 컨테이너 명은 dutypark-db 이며, 데이터베이스 이름은 dutypark 입니다.  모든 데이터베이스를 모두 백업 하려면 `--all-databases` 옵션을 줄 수 있는데, 그랬을 경우 불필요한게 많이 추가되어서 저는 DB를 타겟팅 해서 백업 해 보았습니다.

```bash
docker exec dutypark-db mysqldump -u"root" -p"비밀번호입력" dutypark > ~/Downloads/dutypark.sql
```

![image-20230107160214401](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/mysql/backup-schedule.assets/image-20230107160214401.png)

> 보이는 것 처럼 dutypark.sql 파일이 생성 되었습니다.

```bash
view dutypark.sql
```

파일을 확인 해 보면 백업이 정상적으로 잘 되어있습니다.

![image-20230107160324322](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/mysql/backup-schedule.assets/image-20230107160324322.png)

### 파일명에 현재 날짜 입력

그런데, 항상 파일명이 같으면 이전 백업을 덮어 쓰게 됩니다. 백업 할 때 마다 현재 시간을 이용해 파일명을 동적으로 구성하도록 합니다.

```bash
docker exec dutypark-db mysqldump -u"root" -p"비밀번호입력" dutypark > ~/Downloads/dutypark-$(date +%Y-%m-%d-%H.%M.%S).sql
```

![image-20230107160517730](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/mysql/backup-schedule.assets/image-20230107160517730.png)

> 백업 당시의 시간이 포함된 파일명으로 백업 파일이 생성됩니다.

이제 백업 할 준비가 되었으니 스케줄만 돌리면 되겠습니다.

## Cron

Cron은 미리 설정 해 둔 시간에 특정 작업들을 실행하도록 할당 해주는 스케줄러 도구 인데요, 시스템에서 수행해야 하는 일들을 특정 간격으로 자동화 하는데 손쉽게 사용 할 수 있습니다.

스케쥴 등록에 앞서 Shell script를 작성 해 줍니다. cron에 바로 등록하는 것 보다 재활용 하기도 좋고 나중에 떼내기도 편할거라 생각했습니다.

아래와 같은 내용의 backup.sh 파일을 생성 했습니다.

**backup.sh**

```sh
echo "Dutypark Backup start"
docker exec dutypark-db mysqldump -u"root" -p"암호입력" dutypark > /home/shane/dutypark/backup/dutypark-$(date +%Y-%m-%d-%H.%M.%S).sql
```

실행 가능하도록 권한을 부여합니다.

```bash
chmod a+x ./backup.sh
```

이제 Cron 설정을 켭니다

```bash
sudo vi /etc/crontab
```

테스트를 위해 1분마다 백업하도록 스케쥴을 등록 해 보겠습니다.

저는 DB가 아직 작아서 금방되는데, 이미 DB가 크다면 1분 보다는 시간을 넉넉하게 잡아주세요.

```
*/1 *   * * *   root /home/shane/dutypark/backup.sh 2>/home/shane/dutypark/backup.log
```

![image-20230107172941529](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/mysql/backup-schedule.assets/image-20230107172941529.png)

> 마지막 줄에 등록 하면 됩니다.

위와 같이 한줄을 추가 해 줍니다. `2>경로` 를 입력 해서, 혹시 에러가 생겼을 때 확인 할 수 있도록 단일 로그 기록 하도록 합니다. 이게 없으면 문제가 있을 때 찾아내기가 어려웠습니다. 단적인 예로 저의경우 `chmod a+x ./backup.sh` 를 안했다가 스케쥴은 도는데 왜 실행이 안되나 갑갑했는데 알고 보니 권한 문제였습니다.

이제 저장을 하고, DB가 자동으로 백업 되는지 잠시 시켜 봅니다. 스케쥴 도는게 궁금하면 아래 명령어로 확인 해 볼 수 있습니다.

```bash
tail -f /var/log/syslog
```

![image-20230107173253396](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/mysql/backup-schedule.assets/image-20230107173253396.png)

> 1분마다 스케쥴이 정상적으로 도는중

이제 어느정도 시간이 지난 후에 백업이 잘 진행 되었는지 확인 해 봅니다.

![image-20230107173340991](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/mysql/backup-schedule.assets/image-20230107173340991.png)

> 매 1분마다 DB가 정상적으로 백업 되었습니다.

이제 1분은 너무 짧으니 매일 새벽 4시마다 백업을 하도록 변경 해 두었습니다.

```bash
sudo vi /etc/crontab
```

![image-20230107173557288](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/db/mysql/backup-schedule.assets/image-20230107173557288.png)

이상으로 자동 백업 설정이 완료되었습니다. 이제 데이터베이스 유실에 대한 걱정은 좀 덜 수 있습니다.

실제 운영 할 때는 crontab에 바로 스케쥴을 등록 하는 것 보다는 백업을 담당하는 컨테이너를 따로 하나 띄우는게 더 좋을 것 같습니다.

이상입니다.  

## References

- https://stackoverflow.com/questions/4536376/generate-backup-file-using-date-and-time-as-filename
- https://stackoverflow.com/questions/55432520/how-to-create-a-docker-container-which-every-night-make-a-backup-of-mysql-databa
- https://stackoverflow.com/questions/65382892/docker-exec-and-mysqldump-in-cronjob-problem/66152329#66152329
- https://dev.mysql.com/doc/refman/8.0/en/mysqldump.html