# 스프링 부트 java -jar 혹은 bootrun시 port 변경하기

## Intro

이번글은 매우 간단한 글인데, 자꾸 까먹어서 블로그 글로 정리해두고 필요할 때 찾아보려고 작성 해 두려고 합니다.

스프링 부트 프로젝트를 IDE로 손쉽게 실행 할 수 있지만, Scale out 이나 로드밸런스 등의 테스트를 할 때는 같은 어플리케이션을 여러개 띄워두어야 하는 경우가 생깁니다. 테스트의 경우에는 인텔리제이에서 Run Configuration을 여러개 카피해서 사용하는게 가장 좋기는 하지만 이런 방법도 있다고 알고 있는 편이 좋겠고 실제로 쓸일이 제법 있었습니다.

## 포트 지정해 실행

아래와 같이 포트 설정을 하여 실행 할 수 있습니다.

### java -jar

아마 가장 흔하게 쓰이지 않을까 생각 됩니다.

```bash
java -jar -Dserver.port=18080 ./target/my-app-0.0.1-SNAPSHOT.jar
```

### Gradle

`--server.port` 옵션을 줄 때 앞에 공백이 들어가지 않으면 문제가 생깁니다. 반드시 공백을 입력 해 주어야 합니다.

이 글을 작성하게 된 이유도 gradle로 bootrun 할 경우 포트 지정에 대한 방법을 찾기가 어려웠기 때문에 정리 해 두기 위함 입니다.

```bash
./gradlew bootrun --args ' --server.port=18080'
```

### Maven

```bash
mvn spring-boot:run -Dspring-boot.run.jvmArgument='-Dserver.port=18080'
```

![image-20221114210855440](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/java_jar_port.assets/image-20221114210855440.png)

> Availability Zones가 늘어난 것이 확인 됩니다.

이상입니다. 