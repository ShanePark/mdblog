# docker로 elastic search 띄우기

## Intro

docker-compose로 ELK 스택을 구축하는 간단한 링크

> https://shanepark.tistory.com/241?category=1203908

하지만 이번 포스팅에서는 지극히 개인적인 [gaia.best](http://gaia.best) 서버를 띄우기 위해 간단하게 오라클과 엘라스틱 서버만 띄우고 로그 스태시로 해당 엘라스틱 서치로 부터 파이프라인을 한차례 받아오는 과정 까지를 기록 해 두려고 합니다.

바로 이전 포스팅 https://shanepark.tistory.com/285 에서 도커 컨테이너를 백업 하려고 하던 찰라에 엘라스틱 서치 컨테이너에 큰 문제가있다는 것을 발견해서, 그냥 엘라스틱 서치 컨테이너를 날려 버리고 데이터를 새로 밀어넣으려고 합니다.

지극히 개인적인 기록을 위한 포스팅이지만 어느 누군가에게는 참고가 되었으면 합니다.

## oracle

자세한 도커로 11g 띄우는 과정은 아래의 링크를 참고 해주세요.

> https://shanepark.tistory.com/240

간단 요약

```bash
docker run --name oracle11g -d -p 1521:1521 jaspeen/oracle-xe-11g
```

```bash
docker exec -it oracle11g bash
```

```bash
password
```

```bash
CREATE USER shane IDENTIFIED BY 1234
```

## Elastic Search

버전은 각자 원하는 버전을 사용하지만 최신버전을 권장합니다. 하지만 LogStach, Kibana와 버전을 꼭 맞춰주세요.

```bash
docker run -d --name elastic -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:7.14.1
```

위에서 -d 옵션은 데몬 으로 실행을 말합니다.

-name 옵션은 컨테이너 이름을 설정하는 것 입니다.

-p 옵션은 포트 설정을 하는 것 입니다.

## LogStash

이제 오라클과 엘라스틱 서치를 띄웠으니 로그스태시로 오라클에 있는 데이터들을 엘라스틱서치에 밀어 넣을 차례 입니다.

일단 오라클 서버에 엘라스틱 서치로 밀어넣을 데이터가 충분히 있다는 전제 하에 진행하겠습니다.

로그스태시는 docker로 띄워서 주기적으로 데이터를 밀어 넣는게 좋겠지만, 일단 일회성으로만 밀어넣을 예정이기 때문에 docker로 띄우지 않고 로컬에서 띄우도록 하겠습니다.

### 준비사항

1. ojdbc가 필요합니다. 오라클 버전에 맞춰 주면 되며 11g 를 사용하기 때문에  ojdbc6.jar 파일을 준비 했습니다.

![image-20211125230241610](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/elastic.assets/image-20211125230241610.png)

2. LogStash를 다운 받아 압축을 풀어 둡니다.

![image-20211125230351076](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/elastic.assets/image-20211125230351076.png)

3. conf 파일을 작성 합니다.

`/config` 폴더에 보통 logstash-sample.conf 파일이 존재하기 때문에 보고 참고 해서 작성 하면 됩니다.

![image-20211125230412982](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/elastic.assets/image-20211125230412982.png)

예제를 위해 제가 작성한 gaia.conf 파일 전문을 올립니다.

```bash
input {
	jdbc {
		jdbc_driver_library => "/home/shane/Documents/ojdbc6.jar"
		jdbc_driver_class => "Java::oracle.jdbc.driver.OracleDriver"
		jdbc_connection_string => "jdbc:oracle:thin:@localhost:1521:xe"
		jdbc_user => "gaia"
		jdbc_password => "password1234"
		statement => "SELECT * FROM MEMBER"
	}
	jdbc {
		jdbc_driver_library => "/home/shane/Documents/ojdbc6.jar"
		jdbc_driver_class => "Java::oracle.jdbc.driver.OracleDriver"
		jdbc_connection_string => "jdbc:oracle:thin:@localhost:1521:xe"
		jdbc_user => "gaia"
		jdbc_password => "password1234"
		statement => "SELECT * FROM issue"
	}
	jdbc {
		jdbc_driver_library => "/home/shane/Documents/ojdbc6.jar"
		jdbc_driver_class => "Java::oracle.jdbc.driver.OracleDriver"
		jdbc_connection_string => "jdbc:oracle:thin:@localhost:1521:xe"
		jdbc_user => "gaia"
		jdbc_password => "password1234"
		statement => "SELECT * FROM milestone"
	}
	jdbc {
		jdbc_driver_library => "/home/shane/Documents/ojdbc6.jar"
		jdbc_driver_class => "Java::oracle.jdbc.driver.OracleDriver"
		jdbc_connection_string => "jdbc:oracle:thin:@localhost:1521:xe"
		jdbc_user => "gaia"
		jdbc_password => "password1234"
		statement => "SELECT * FROM project"
	}
	jdbc {
		jdbc_driver_library => "/home/shane/Documents/ojdbc6.jar"
		jdbc_driver_class => "Java::oracle.jdbc.driver.OracleDriver"
		jdbc_connection_string => "jdbc:oracle:thin:@localhost:1521:xe"
		jdbc_user => "gaia"
		jdbc_password => "password1234"
		statement => "SELECT * FROM issue"
	}
	jdbc {
		jdbc_driver_library => "/home/shane/Documents/ojdbc6.jar"
		jdbc_driver_class => "Java::oracle.jdbc.driver.OracleDriver"
		jdbc_connection_string => "jdbc:oracle:thin:@localhost:1521:xe"
		jdbc_user => "gaia"
		jdbc_password => "password1234"
		statement => "SELECT * FROM kanban"
	}
	jdbc {
		jdbc_driver_library => "/home/shane/Documents/ojdbc6.jar"
		jdbc_driver_class => "Java::oracle.jdbc.driver.OracleDriver"
		jdbc_connection_string => "jdbc:oracle:thin:@localhost:1521:xe"
		jdbc_user => "gaia"
		jdbc_password => "password1234"
		statement => "SELECT * FROM wiki"
	}
	jdbc {
		jdbc_driver_library => "/home/shane/Documents/ojdbc6.jar"
		jdbc_driver_class => "Java::oracle.jdbc.driver.OracleDriver"
		jdbc_connection_string => "jdbc:oracle:thin:@localhost:1521:xe"
		jdbc_user => "gaia"
		jdbc_password => "password1234"
		statement => "SELECT * FROM news"
	}
}

## Add your filters / logstash plugins configuration here

output {
	elasticsearch {
		hosts => "localhost:9200"
		# user => "elastic"
		# password => "changeme"
		# ecs_compatibility => disabled
		index => "gaia"
	}
}
```

요약하자면, 일일히 데이터를 밀어 넣을 테이블들을 `select *` 로 가져옵니다. 그리고 localhost:9200에 있는 엘라스틱에 gaia 라는 인덱스로 모두 밀어 넣습니다.

올바른 방법은 아니라고 생각 하는데, 학원에서 최종 프로젝트를 진행할 때 통합 검색을 최대한 빠른 시간내에 구현해 내려고 어떻게든 쥐어 짜낸 방법이었으며 작동은 합니다. 물론 사용할 용도에 따라 다르게 사용해야 하겠죠.

이제 이렇게 준비가 되었다면 바로 gaia.conf 파일을 이용해 데이터를 밀어 넣습니다.

저는 logstash 폴더 에서 아래의 명령어를 실행 했습니다. 명령어 실행 위치에 따라 경로 설정은 달라 질 수 있습니다.

```bash
./bin/logstash -f "./config/gaia.conf"
```

![image-20211125231055521](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/docker/elastic.assets/image-20211125231055521.png)

이렇게 하면 지정해둔 데이터들을 모두 엘라스틱 서치에 밀어 넣습니다. 

이후 의도했던 통합 검색들이 잘 작동 하는것을 확인 할 수 있습니다.

요 몇일 서버가 자꾸 죽어서 곤란했는데 한동안은 잠잠해질 것으로 예상됩니다. 여유를 번 만큼 빨리 근본적인 문제를 찾아 해결 해야 겠습니다.

