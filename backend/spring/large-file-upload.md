# 스프링 부트 프로젝트에서의 안정적인 대용량 파일 업로드

## Intro

프로젝트에서 파일 업로드 부분에 대한 개선을 진행 했습니다.

기존의 파일 업로드를 처리하는 부분에 몇가지 문제가 있었는데, 특히 대용량 파일을 안정적으로 업로드 하기 위해서 해결해야 하는 포인트들이 여러가지가 있었고, 꽤나 난해했습니다.

1. 사용자가 파일 업로드를 마쳤지만, 서버에서 일어나는 다양한 작업을 마칠때까지 오랜 시간동안 응답을 받지 못함
2. 사용자 입장에서 업로드 100% 에서 오랜시간 화면이 멈추어 있어 UX가 좋지 않음
3. 대용량의 파일을 업로드할 때 응답에 걸리는 시간이 너무 오래걸려 timeout 되는 문제
4. 사용자는 파일을 성공적으로 업로드 했지만, 서버에서 해당 파일을 처리중 문제가 생겼을 때의 방안
5. 대용량의 파일 업로드시 대량의 IO 작업으로 시스템 자원이 쏠리는 현상

간단히 추려보면 위에 쓴 내용들 정도가 핵심적으로 고려해야 할 상황들이었는데요. 여러가지 고민도 해 보았고 정보도 여기저기 찾아 보았는데 대용량 파일의 업로드를 지원하는 방법에 대한 정보가 생각보다 많지 않아 정말 애를 많이 먹었습니다. 

500MB 의 파일도 업로드 하기 힘들었던 시스템을 수십, 수백 GB 의 파일도 안정적으로 업로드 할 수 있도록 개편한 이야기를 나누어보려고 합니다.

지금은 많은 고민 끝에 관련 정보가 많지 않았던 이유에 대해서도 어느정도 납득을 할 수 있게 되었지만 저와 같은 고민을 하고 있을 분들에게 조금이나마 도움이 되었으면 합니다.

## 기존 업로드 과정

먼저 기존의 파일 업로드 과정을 먼저 살펴보겠습니다. 

검색엔진, 세션 저장소, 인증 모듈, 썸네일 생성모듈, 모니터링 시스템 등 복잡한 구조는 최대한 생략 하고 파일 업로드에 직접적인 관련이 있는 부분만 간추려 보았습니다. 각자 도커 컨테이너에서 동작합니다.

- NginX : 웹 서버
- App1: 사용자의 요청을 처리하는 어플리케이션 서버 (SpringBoot)
- App2: DB에 및 파일 관리시스템과 직접 통신을 담당하는 어플리케이션 서버 (SpringBoot)
- MainDB: 업로드된 파일의 Entity 및 다양한 다른 Entity 와의 정보를 저장 (Postgres)
- Data Management Software: 파일 데이터 및 메타데이터 관리 (iRods)

![image-20221211084609285](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/large-file-upload.assets/image-20221211084609285.png)

> 편의상 v1.0 으로 칭하겠습니다.

1. 최초 사용자가 파일에 대한 업로드 요청을 하면 브라우저에서는 Ajax fileUpload가 FormData 를 이용해 업로드를 요청 합니다.
2. 요청을 웹서버인 Nginx가 받아 App1 에게 전달합니다. 이 때, NginX 설정에서 `proxy_request_buffering off;` 해줘야 하는데, 그렇지 않으면 프록시 서버에 요청을 보내기 전에 request body 전체를 한번 읽습니다. [nginx docs 참고](http://nginx.org/en/docs/http/ngx_http_proxy_module.html#proxy_request_buffering)
3. App1이 MultipartFile로 파일을 읽고 App2에 요청을 보내며 다시 한번 파일을 전달 합니다.
4. App2는 서비스 로직을 처리 하며 DB에 관련 정보를 저장 합니다.
5. 정상적으로 DB에 저장되면 생성된 ID 값을 돌려줍니다.
6. 파일 저장 시스템에 다시 한번 전체 파일을 전달 합니다. 파일 저장시스템에서는 파일에 대한 모든 메타데이터를 자체 DB에 저장 하고 스스로의 방식대로 파일들을 처리합니다. 필요시 분산 저장 및 암호화 등의 작업도 이루어 집니다.
7. 파일 저장 시스템에서의 처리결과를 App2에 전달 합니다.
8. App2에서는 모든 로직의 처리를 완료했으니, 파일업로드에 관한 응답정보를 만들어 App1에 전달 합니다.
9. App1은 App2에서 전달 받은 응답 정보를 그대로 반환 합니다.
10. 최종적으로 브라우저에서는 파일업로드에 대한 응답정보를 토대로 브라우저에 업로드된 파일 정보를 그려줍니다.

이 과정에서 사실 사용자는 2번 과정만 완료 되면 그 이후는 관심사가 아니기때문에 바로 응답이 이루어져야 하는 상황인데, 추가적으로 파일이 몇번의 전달과정을 거치고 서버에서의 다양한 처리과정이 이루어지는 동안 멀뚱멀뚱 영문도 모른채 기다리는 상황 이었습니다. 그 중에서도 특히 6번과 7번 작업의 사이에서 이루어지는 과정의 병목이 가장 심했는데, 오죽하면 저는 위의 그림에서 저장소를 자기테이프로 표현해 두었습니다.

- 위의 상황에서는 1GB는 커녕 500MB 정도의 파일을 업로드 할 때에도 타임아웃이 발생하곤 했습니다. 100% 까지 업로드는 금방 이루어지는데 그 상황에서 멍하니 멈춰있다가 타임아웃이 발생했습니다.

- 그정도의 크기가 아니라도 파일 크기가 100MB 정도만 되도 업로드가 정상적으로 이루어지긴 하지만 100% 상태에서 100% 까지 차는데 걸렸던 시간보다 훨씬 더 오래 기다려야 하는 상황이었습니다.

이후 한단계씩 거친 개선 과정을 살펴 보겠습니다. 

참고로 업로드 테스트를 진행 할 때 더미 파일을 용량별로 생성하는게 필요 한데 아래의 명령어로 더미파일들을 용량별로 생성 했습니다.

```bash
# fallocate : Linux 시스템에서 좀 더 나은 선택
fallocate -l 1G 1G.file

# truncate : macOS 에서도 사용 가능
truncate -s 500MB 0.5G.file
```

## 개선 과정

### 1. Progress를 좀 더 정확하게

일단 무엇보다도 UX 측면에서 100% 까지의 Progress가 정확히 나오는게 가장 급선무였을겁니다. 이 과정은 제가 프로젝트를 맡기 전에 이미 개선이 이루어졌는데요 Progress 가 100% 까지 가는 과정의 정확도를 높이는 작업이었습니다.

![image-20221211101455923](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/large-file-upload.assets/image-20221211101455923.png)

> v1.1

`proxy_request_buffering off;` 설정으로 NginX가 요청을 버퍼링하지 않기 때문에 1번, 2번 작업은 하나로 보았을 때, 파일이 통째로 전달되는 과정은 아래와 같이 총 3번이 있는데요. 파일 전달이 업로드에서 대부분의 시간을 차지 합니다.

- 1번 및 2번 과정
- 3번 과정
- 6번 과정

이 중 첫번째 과정은 사용자가 실시간으로 Progress를 확인 할 수 있지만, 3번 및 6번에서의 전송은 알 수가 없었습니다.

이 때, 3번 및 6번 과정에서 파일을 전달하기 위해 InputStream을 읽는 과정에 FilterInputStream 을 상속해 특별한 기능을 넣은 InputStream으로 대체하도록 개선 되었습니다. 주기적으로 어느 크기까지 파일을 읽었는지 정보를 꾸준히 제공 하고, 그 모든 과정을 Progress에 포함시킴으로서 사용자는 이제 업로드 과정에서 가장 시간이 오래 걸리던 세가지 부분을 포함한 Progress를 받아보게 되었습니다. 

물론 100%가 찬 뒤에 기다려야 하는 부분이 전혀 없는건 아니지만, 이전에 비하면 훨씬 나아졌기 떄문에 100~200MB 정도는 불편함 없이 업로드 할 수 있게 되었습니다. 하지만 여전히 업로드에 걸리는 시간은 이전과 같습니다.

### 2. 파일 전달을 줄여보자

여기서부터 제가 파일 업로드 개선 작업을 맡아 진행 하기 시작했습니다.

Progress가 좀 더 정확해지기는 했지만 여전히 어플리케이션간의 불필요한 파일 전송은 부담입니다. 이번에는 3번 과정에서의 중복된 MultipartFile 전송을 제거 하도록 요청을 받았고 여러 가지 방안들을 검토 해 보았습니다. 기본적으로 App1과 App2는 도커 컨테이너위에 떠 있는데 각각 같은 경로의 폴터를 마운트 해 두고 (Docker volume) App1은 그 폴더에 파일을 저장하는 것 까지만 하고 App2에는 Multipart File 대신 그 path만을 전달하도록 변경 하기로 했습니다.

![image-20221211105259104](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/large-file-upload.assets/image-20221211105259104.png)

> v1.2

App1 에서는 3번 작업을 하기 전에 MultipartFile로 전달 받은 파일을 `SHARED_PATH` 라고 칭하고 있는 App2와 동시에 마운트 된 경로에 파일을 저장 합니다. 이후 App2에 요청을 할 때 실제 FormData 를 이용해 Multipart File을 전달하던 것 대신 파일이 저장된 경로만을 전달 하게 변경함으로서 업로드에 걸리는 시간을 조금 단축 할 수 있게 되었습니다.

스프링부트 어플리케이션은 멀티파트 파일을 저장 할 때 `spring.servlet.multipart.file-size-threshold` 로 설정 설정한 threshold를 초과하는 파일들은 location으로 지정해 둔 경로에 임시로 저장합니다. threshold 의 기본 값은 0 입니다.

location을 따로 설정 하지 않았을 때에 확인 해 보니 보통 `/tmp` 경로에 저장이 되었습니다.

![image-20221211110011803](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/large-file-upload.assets/image-20221211110011803.png)

> https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/autoconfigure/web/servlet/MultipartProperties.html

 이 과정에 `spring.servlet.multipart.location`에서 `SHARED_PATH`로 파일을 쓰는 과정에서 시간이 오래 걸리는 것을 방지하기 위해 임시 저장되는 경로도 같은 경로로 맞추어 주었습니다. App2에서는 이제 6번 과정을 진행 할 때 SHARED_PATH에 있는 파일을 원래부터 자기가 가지고 있던 것 처럼 사용 합니다.

이로서 전체적인 업로드에 걸리는 시간이 다소 줄어들었고, 그 덕택에 업로드 할 수 있는 용량이 조금이나마 증가 했습니다. 다만 1GB 파일을 업로드 할 수 없는건 마찬가지 입니다.

### 3. 사용자에게 응답을 최대한 빨리 보내자

지금까지의 전체적인 업로드 과정에서 가장 큰 문제는 사용자가 불필요하게 서버에서 이루어지는 일련의 작업들이 완료될 때 까지 기다린다는 점 입니다.

사실 사용자는 서버에서 어떤 일이 일어나는지는 관심사 밖이고, 본인이 올릴 파일만 다 업로드를 마쳤다면 (전체 프로세스 중 1,2번 과정) 그다음부터는 다른 할 일을 할 수 있어야 합니다.

그래서 서버에서 파일을 처리하는 과정을 별도의 쓰레드에서 작업 하도록 개선을 해 보았습니다.

![image-20221211111441242](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/large-file-upload.assets/image-20221211111441242.png)

> v2.0

여전히 DB에 저장하는 4번과 5번 작업을 거치기는 하지만 이전과는 조금 다릅니다. 

전에는 데이터 관리 시스템에 파일 저장을 마치고 나서 받는 응답을 DB에 저장했다면, 지금은 일단 데이터베이스에는 SHARED_PATH 에 저장된 임시 파일 경로만을 저장 해 둡니다. 그렇게 DB에 저장한 엔티티 정보를 토대로 응답을 만들어 App1을 통해 사용자에게 반환 하고, 이후로는 App2의 별도 쓰레드에서 SHARED_PATH 에 있는 파일을 데이터 관리 시스템에 저장하는 작업을 진행합니다.

`java.util.concurrent.Executors` 에 static 메서드로 등록 되어 있 여러가지 ExecutorService 중 원하는걸 Bean으로 등록 해 두고 주입받아 사용 하면 되는데, 적절한 쓰레드풀을 설정해 주면 됩니다.

![image-20221211112221805](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/large-file-upload.assets/image-20221211112221805.png)

이렇게 개선 한 후 확인을 해 보니, 사용자는 업로드 100%가 진행 된 후에 App1이 `SHARED_PATH`에 큰 덩치의 파일을 작성하는데 필요한 약간의 시간을 제외하고는 거의 즉시 응답을 받을 수 있었습니다. (파일이 커지면 이 시간도 무시할 수 없습니다)

이전까지 1GB도 처리하지 못하던 시스템은, 이제 3~5GB는 별 문제 없이 처리해내기 시작했습니다. 네트워크 상황에 따라 다르긴 하지만 종종 5GB 이상의 업로드에도 성공 했습니다.

### 4. 문제생기면 복구가 가능하도록

이때부터 몇가지 다른 고민이 시작되었습니다. 

- 업로드는 완료되었는데 파일이 준비 되어 있지 않음

사용자가 원래는 업로드 후에 모든 파일에 대한 처리가 끝나고 나서야 응답을 받았었는데, 이젠 서버에서는 파일을 처리 하고 있는데 사용자는 이미 업로드를 마쳤고 해당 파일이 즉시 서비스 준비가 된 것 까지 기대하고 있습니다. 파일 다운로드 과정까지 이 글에서 자세히 다루지는 않을거지만 어쨌든 다운로드 과정에서는 최종적으로 Data Management System에서 파일을 받아와야 하는데 준비가 되지 않았으니 곤란한 상황입니다.  

- 예외 발생시 복구가 되지 않음

또한, 별도 쓰레드에서 업로드된 파일에 대한 후처리를 하다가 어떠한 종류의 예외라도 발생하였다면 사용자는 정상적으로 파일 업로드를 완료 했음에도 불구하고 아무리 지나도 해당 파일은 영영 서비스 되지 않습니다. 거기에 더해 업로드가 제대로 되었다고 해도 지금 상황에서는 준비가 된 파일인지 아닌지도 알 방도가 없는 상태죠.

이를 해결하기 위해서 파일 업로드 작업 하나하나를 단지 별도의 쓰레드에서 처리하는 것 뿐만 아니라 관리하고 추적 할 수 있는 기능이 필요했습니다. 또한 한순간 많은 업로드 요청이 몰리더라도 순차적으로 처리 할 수 있어야 합니다.

이를 위해 파일 업로드 상황에 대한 정보를 기록할 필요가 생겼는데, 업로드 상태에 대한 별도의 테이블을 생성 하는 방법도 있을 수 있을테고, 기존의 파일정보를 저장하던 Entity에 업로드 진행에 대한 정보도 기록하는 방법이 있을 수 있겠습니다.

> 지금 와서 생각을 해 보면, 별도의 테이블을 작성하고 필요시 조인을 하는게 좀 더 낫지 않았을 까 싶기는 한데 당시에는 기존의 파일 Entity에 업로드 진행 상태 (업로드 필요, 업로드 중, 처리 완료, 오류.. 등) 를 기록하도록 컬럼을 추가 했습니다.
>
> 임시 파일이 저장된 경로는 위의 **3. 파일 전달을 줄여보자** 항목에서 이미 필요해서 추가 된 상태 입니다.

이제 파일 업로드를 작업을 처리하는 쓰레드는 평소에는 할당 될 필요가 없고, 업로드 요청이 존재 할 때만 감지를 해서 DB에서 처리할 정보를 확인 하고, 모든 작업이 처리 된 후에는 DB에 상태를 업데이트 하고 문제가 생겼을때는 오류 횟수를 카운트해서 재시도 횟수만큼 복구를 시도하도록 하였습니다. 그렇게 해도 오류가 해결되지 않으면 관리자에게 오류 상황을 알립니다. 

파일이 준비되지 않았던 상황에 대해서도 나름의 해결책을 제시 했습니다. 파일이 서버에서 처리되는 동안은 App1 에서 App2를 거치고 데이터 관리 시스템까지 거칠 필요 없이 스스로가 파일에 직접 접근 할 수 있기 때문에 그 파일을 즉시 서비스 하면 됩니다. 서버에서의 파일 업로드 처리가 완료되면 App2가 해당 파일을 제거 해 버리기 때문에 서비스중 파일이 제거되는 문제가 있을 수 있기 때문에 lock을 거는 등의 조치가 필요할 거라고 생각을 했는데 테스트를 진행 해 보니 특별히 문제되지도 않았습니다. 직관적인 예상에서 벗어나기 때문에 의아할 수 있는데 사실 Unix 시스템에서 파일 삭제시 실제 삭제가 아닌  unlink를 하기 때문에 원본파일을 다른곳에서 읽고 있다면 그 작업이 모두 종료되고 나서야 실제 제거 및 저장공간 재 할당이 이루어 집니다.

> 파일 삭제에 대한 더 자세한 내용은 https://shanepark.tistory.com/381 를 참고 해 주세요.

업로드를 여러 개의 쓰레드로 진행 할 필요가 있다면 동기화에 대해서도 신경을 써야 하고 너무 많은 자원을 쓰지 않도록 제한해야 합니다.

이 쯔음에 서버에서의 파일 처리를 할 때 최대한 커넥션을 짧게 물고 있기 위해서 트랜잭션을 수동으로 관리하도록 코딩을 했었는데 트랜잭션 반납이 정상적으로 이루어지지 않아 결국 장애가 일어나는 아찔한 상황도 있었습니다.

이번 과정으로 서버에서의 처리는 조금 더 안정성을 갖췄지만 사용자가 업로드하는 과정에 대해서는 아무런 개선이 없었습니다.

### 5. 더 큰 용량을 업로드 하도록

이때쯤 되어 개선 요구사항이 점점 더 늘어나기 시작했습니다. 특히 서비스중인 한 기관에서는 오래 걸리는건 상관 없으니 100GB가 넘는 파일에 대한 업로드도 이루어질 수 있도록 개선해 달라고 했습니다. 이쯤에서부터 슬슬 WAS 에서 이정도 사이즈의 업로드를, 그것도 HTTP 프토토콜로 처리하는게 맞는지에 대한 의문이 들기 시작했습니다. 사실 파일 업로드만 따로 처리하는 모듈을 분리하는쪽이 맞겠다고 생각했고 실제로 대용량 파일 업로드를 스프링부트에서 처리하는 방법에 대한 정보가 많지 않은 이유도 애초에 그정도 요구가 있을 때에는 다른 방법을 찾기 때문 이었습니다.

특히 AWS S3(Simple Storage Service)를 사용하면 가격도 저렴하고 용량 걱정도 없기 때문에 많이들 그 방법을 활용하는 것 같습니다. 

> AWS lambda + S3 로 파일 업로드 하는 방법은 아래의 링크를 참고해주세요
>
> https://aws.amazon.com/ko/blogs/korea/uploading-to-amazon-s3-directly-from-a-web-or-mobile-application/

하지만 지금의 시스템을 유지하며 파일 업로드 가능 최대 용량만 높여야 하는 상황이기 때문에 다양한 방법을 찾아보았습니다.

처음에는 단순히 nginx에서 타임아웃이 뜨기 때문에 타임아웃 시간을 대폭 늘리고, 리퀘스트 버퍼링이나 바디사이즈 제한등을 푸는 등의 방법을 시도 해 보았는데 물론 이렇게 했을 때 어느정도 제한 용량이 늘어나는 효과는 있었지만 그렇게 해서 해결할 수 있는건 기껏해야 10GB 안팎이었습니다. 

가끔 그 이상이 업로드 되기도 하긴 했지만 안정적이지 못했고 무엇보다 100% 달성 이후 파일을 쓰는 시간이 점점 더 늘어나다 보니 큰 용량의 파일에서는 UX가 다시 나빠지기 시작했습니다. 업로드 요청을 chunk 단위로 나누어 응답까지 걸리는 시간을 줄일 필요가 있습니다.

시중에는 이미 파일 업로드 솔루션들이 몇가지 나와 있습니다. 업로드 컴포넌트라고도 불리는 것 같은데 가격이 비쌉니다. 

실제 결재해서 사용중인 기관에 작업을 해줄 일이 있어서 모듈연동을 해 보았는데 샘플코드도 적절하지 않고 버그도 있어서 엄청 투덜대며 작업을 했지만 막상 전부 연동시켜 놓고 보니 그쪽에서는 100GB 파일 업로드도 문제 없이 처리 해 냈습니다. 제가 진행중인 프로젝트에서도 이런게 필요했습니다.

사수님이 TUS 라는 오픈소스 파일 업로드 프로토콜을 소개 해 주셔서 이걸 기반으로 개발 해 보기로 정했고, 오픈소스로 구현된 프로젝트들을 먼저 적용해 테스트 해 보고 여의치 않으면 직접 구현 해 보기로 하고 다시 작업을 시작 했습니다.

![image-20221211131638014](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/large-file-upload.assets/image-20221211131638014.png)

> https://tus.io/

## TUS

### 소개

지금까지 제가 위에 적었던 여러가지 고민들을 해결 하기 위해 TUS가 세상에 나왔습니다.

TUS는 재개 가능한 업로드를 지원하는 새로운 오픈소스 프로토콜로서 간단하고 쉽게 모든 언어 및 플랫폼과 네트워크 기반에서 작동하는것을 목표로 하고 있습니다. 

![image-20221211132433519](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/large-file-upload.assets/image-20221211132433519.png)

지금까지 `0.1`, `0.2` 버전 그리고 2016년에 `1.0.0` 버전이 공개가 되었고 최근에는 [tus-v2](https://github.com/tus/tus-v2)도 준비가 되어 2022년 11월 9일에 저장소가 아카이브 되기도 했지만 아직은 1.0 버전이 주력인 것으로 보입니다. MIT 라이센스로 공개되었기 때문에 자유롭게 사용 할 수 있습니다.

간단하게 프로토콜에 대해 요약해보면

일단 기본적으로 요청 헤더에는 Tus-Version 에 지원하는 버전 정보들을 `,`로 구분해 기록 해 줍니다. Tus-Resumable 헤더도 모든 OPTIONS 리퀘스트를 제외한 모든 request및 response에 반드시 포함되어야 합니다.

- 최초에는 **HEAD** 요청으로 offset을 확인 합니다.
- 후에 업로드를 이어 하기 위해 **PATCH** 메소드를 보내는데요, 헤더에 Upload-offset 정보와 Content-Length를 함께 보내며 Conten-Type은 `application/offset+octet-stream` 로 보냅니다. 
- 그 외 **OPTIONS** 메서드로 서버의 설정 정보를 받아 오고, **DELETE** 메서드로 특정 업로드를 제거 합니다.

프로토콜에 대한 더 자세한 정보는 아래의 링크에서 확인 할 수 있습니다.

![image-20221211132740630](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/large-file-upload.assets/image-20221211132740630.png)

> https://tus.io/protocols/resumable-upload.html#license

TUS 구현체들에 대한 정보는 https://tus.io/implementations.html 에서 확인 할 수 있는데요. 이 중 클라이언트는 Uppy를, 서버는 Official 구현이 없어 https://github.com/tomdesair/tus-java-server 를 테스트 해 보았습니다. 둘다 MIT 라이센스 입니다.

### 테스트

도입에 앞서 간단한 테스트 프로젝트를 만들어 TUS 프로토콜 및 각각의 구현체들을 테스트 해 보았습니다.

샘플 코드는 Kotlin 으로 작성 하였으며 https://github.com/Shane-Park/tus-upload-sample 에서 전체 코드를 확인 해볼 수 있습니다.

#### 프로젝트 생성

스프링부트 프로젝트를 생성 하고, tus-java-server 의존성을 추가 해 줍니다.

**Maven**

```xml
<dependency>
  <groupId>me.desair.tus</groupId>
  <artifactId>tus-java-server</artifactId>
  <version>1.0.0-2.0</version>
</dependency>
```

**gradle**

```kotlin
implementation("me.desair.tus:tus-java-server:1.0.0-2.0")
```

#### 설정파일 생성

**TusConfiguration.kt**

```kotlin
package com.example.tusuploadsample.config

import me.desair.tus.server.TusFileUploadService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File
import javax.annotation.PostConstruct
import javax.servlet.ServletContext

@Configuration
class TusConfiguration(
    @Value("\${tus.upload.dir}")
    val tusStoragePath: String,

    @Value("\${tus.upload.expiration}")
    val tusUploadExpirationPeriod: Long,

    private val servletContext: ServletContext
) {

    private val log = org.slf4j.LoggerFactory.getLogger(TusConfiguration::class.java)

    @PostConstruct
    fun init() {
        if (File("$tusStoragePath/uploads").mkdirs()) {
            log.info("Created tus upload directory")
        }
        if (File("$tusStoragePath/locks").mkdirs()) {
            log.info("Created tus lock directory")
        }
    }

    @Bean
    fun tusService(): TusFileUploadService {
        return TusFileUploadService()
            .withUploadURI(servletContext.contextPath + "/upload")
            .withStoragePath(tusStoragePath)
            .withDownloadFeature()
            .withUploadExpirationPeriod(tusUploadExpirationPeriod)
    }
}
```

제일 먼저 tus 설정파일을 생성 해 줍니다. 저장 경로 및 만료 시간은 설정값으로 빼 두었습니다. 주의해야 할 건 uploadURI가 contextPath를 포함해서 등록 되어야 하기 떄문에 ServletContext를 주입 받은 후에 아래와 같이 입력 해 주는 편이 좋습니다.

```kotlin
.withUploadURI(servletContext.contextPath + "/upload")
```

**application.yml**

```yaml
tus.upload:
  dir: /home/shane/Downloads
  expiration: 86400000
```

설정값으로 뺀 값들도 입력 해 줍니다. ${환경변수} 형식으로 추가 하거나 프로필별로 다르게 설정 해 줍니다. 24시간으로 만료시간을 정했습니다.

**FileCleanupScheduler.kt**

```kotlin
package com.example.tusuploadsample.config

import me.desair.tus.server.TusFileUploadService
import org.slf4j.Logger
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@EnableScheduling
class FileCleanupScheduler(
    private val fileUploadService: TusFileUploadService,
) {
    val log: Logger = org.slf4j.LoggerFactory.getLogger(FileCleanupScheduler::class.java)

    @Scheduled(fixedDelayString = "PT12H")
    fun cleanup() {
        log.info("clean up")
        fileUploadService.cleanup()
    }
}

```

중단된 파일 업로드의 정리하는 스케쥴러를 등록 합니다.  `.withUploadExpirationPeriod()`에서 전달한 시간이 지나면 해당 파일은 정리 대상이 됩니다. 

![image-20221211143236866](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/large-file-upload.assets/image-20221211143236866.png)

파일이 업로드 될 때 uploads/{UUID} 하위에 파일 데이터는  data 로 메타정보는 info 로 저장이 되는데 info 에 지정이 됩니다. expirationPeriod를 후에 변경 하여도 info 에 등록된 만료 시기를 따르게 됩니다. fixedDelayString 으로 지정한 주기로 청소가 진행 되는데 위와 같이 입력 할 경우 12시간 마다 한번 스케쥴러가 동작 합니다.

**UploadController.kt**

```kotlin
package com.example.tusuploadsample.controller

import me.desair.tus.server.TusFileUploadService
import org.apache.commons.io.FileUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.*
import org.springframework.web.bind.annotation.RestController
import java.io.File
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@CrossOrigin(exposedHeaders = ["Upload-Offset", "Location"])
class UploadController(
    private val fileUploadService: TusFileUploadService,
    @Value("\${tus.upload.dir}")
    private val tusStoragePath: String,
) {

    @RequestMapping(
        value = ["/upload", "/upload/**"],
        method = [GET, POST, HEAD, PATCH, DELETE]
    )
    fun tusUpload(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Any> {
        fileUploadService.process(request, response)

        val requestURI = request.requestURI
        val uploadInfo = fileUploadService.getUploadInfo(requestURI)

        uploadInfo?.let {
            if (!uploadInfo.isUploadInProgress) {
                val file = File(tusStoragePath, uploadInfo.fileName)
                fileUploadService.getUploadedBytes(requestURI).use {
                    FileUtils.copyInputStreamToFile(it, file)
                }
                fileUploadService.deleteUpload(requestURI)
            }
        }

        return ResponseEntity.ok().build()
    }

}
```

이제 위에서 설정한 uploadURL에 처리를 맡을 컨트롤러를 추가 해 주면 되는데요, `TusConfiguration.kt` 에서 Bean으로 등록 한 TusFileUploadService를 주입 받아 단순하게 `fileUploadService.process(request, response)` 만 해 주면 됩니다. 

그 후에 추가된 코드들은 `uploadInfo` 를 체크 해 보고 이미 완료 된 `UploadInfo` 라면 파일을 원하는 경로에 저장 한 후 업로드 정보를 제거합니다. 아래의 과정은 굳이 같은 컨트롤러에 있을 필요는 없고 나중에 업로드가 완료 되었을 때 완료된 requestUrI를 따로 처리하도록 코드를 구성하는 편이 좀 더 좋지만 샘플 코드를 간단하게 구성 하기 위해 하나의 라우터에 담아 보았습니다.

여기까지만 하면 서버는 준비가 끝 입니다.

**index.html**

```html
<html>
<head>
  <meta charset="utf-8">
  <title>Uppy</title>
  <link href="https://releases.transloadit.com/uppy/v3.3.1/uppy.min.css" rel="stylesheet">
</head>
<body>
<div id="drag-drop-area"></div>

<script type="module">
    import {Uppy, Dashboard, Tus} from "https://releases.transloadit.com/uppy/v3.3.1/uppy.min.mjs"
    var uppy = new Uppy()
        .use(Dashboard, {
            inline: true,
            target: '#drag-drop-area',
            showProgressDetails: true,
        })
        .use(Tus, {
            endpoint: 'http://localhost:8080/upload',
            chunkSize: 5000000, // 5MB
        })

    uppy.on('complete', (result) => {
        console.log('Upload complete! We’ve uploaded these files:', result.successful)
    })
</script>
</body>
</html>

```

클라이언트는 Uppy를 이용해 간단하게 구성 해 보았습니다. 

중요한 설정은 chunkSize가 있는데 이 값에 따라 요청이 너무 많아지거나 메모리 사용량이 많아지는 등의 문제가 있을 수 있기 때문에 성능 테스트를 통해 적절한 값을 찾아야 합니다.

- new Uppy() 할 때 `locale: Uppy.locales.ko_KR` 설정 해 주면 한국어로 표시할 수 있습니다. Ko_KR.min.js 파일을 추가 해 주어야 합니다.
- Tus 옵션에 endpoint를 정확히 입력 해 주어야 합니다.
- Tus 옵션에 `overridePatchMethod: true`로 설정 해 주면 PATCH 메서드가 막힌 서버에서도 POST 요청으로 대신 보낼 수 있습니다.

테스트를 해 보면

![image-20221211143749570](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/large-file-upload.assets/image-20221211143749570.png)

> 위와 같은 파일 업로드 폼이 있고

![image-20221211144022669](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/large-file-upload.assets/image-20221211144022669.png)

> 업로드 할 파일들을 선택 후 Upload 버튼을 누르면

![image-20221211144216423](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/large-file-upload.assets/image-20221211144216423.png)

> 설정 한 Chunk Size 단위로 파일 업로드가 진행됩니다.

![image-20221211144334252](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/large-file-upload.assets/image-20221211144334252.png)

> 업로드 진행 중에는 진행률, 남은시간을 확인 할 수 있고 일시 정지 및 취소도 가능합니다.

업로드 완료 후에는 설정해둔 대로 파일이 이동 저장되며 `uploads/` 에 있던 부분도 알아서 정리됩니다.

## 결과

테스트 결과가 만족스러워서 Uppy 및 `tus-java-server`를 활용해 파일 업로드 부분을 개선 해 보았고 그 결과 수십기가의 파일 업로드도 아무런 문제 없이 업로드 되기 시작했습니다. 100% 후에 불필요하게 멈춰 있던 부분도 완전히 개선되었고 Uppy 에서 파일 업로드  UI 부분도 다 해결해준 덕에 짧은 시간 내에 파일 업로드를 구현 할 수 있었습니다.

파일 업로드 workflow에 여전히 개선이 필요한 부분이 존재하기는 하지만 가장 고민이었던 대용량 파일 업로드를 안정적으로 지원할 수 있게된 점은 만족스럽습니다. 서버 및 클라이언트 구현 모두 이미 훌륭한 구현이 존재하는 덕에 시간을 많이 아끼긴 했지만 직접 고생하며 구현 할 기회를 빼앗긴 것 같아 아쉬운 마음도 많습니다. 이렇게 아낀 시간은 더욱 유용한 곳에 쏟아야 겠습니다, 더불어 저도 다른 개발자들의 시간을 아껴줄 수 있는 오픈소스에 기여를 해야겠다는 다짐이 섭니다.

이 글이 저와 같은 고민을 하고 있을 다른 분들에게 도움이 되었으면 합니다.

**References**

- https://github.com/tomdesair/tus-java-server
- https://tus.io
- https://aws.amazon.com/ko/blogs/korea/uploading-to-amazon-s3-directly-from-a-web-or-mobile-application/
- https://github.com/ddoleye 