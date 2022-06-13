# Linux 에서 열려있는 파일을 삭제할 때 일어나는 일

## Intro

### 진행상황

회사에서 진행중인 프로젝트에서 기능 추가를 위해 임시 파일을 다루던 중 예기치 못한 동작을 확인 했습니다.

지금의 구조를 간략하게 보면

![image-20220613211303109](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/delete-open-file.assets/image-20220613211303109.png)

> 대략적으로 데이터 파일 저장에 관련된 부분만 보았을 때 이런 식으로 이루어 져 있습니다.

사용자가 파일을 업로드 할 경우에, 사용자를 응대해 주는 서버가 저장담당 서버와 통신을 하고, 그 파일을 전달 받은 후에는 DB에 관련된 메타데이터 정보를 저장 하고 실제 파일은 또 다른 파일 저장에 관련된 부분만을 관리 하는 또 다른 파일관리 어플리케이션에 전달을 하게 됩니다.

1번 서버에서 2번 서버로 전달되는 과정에서의 비용도 제법 아까운데, 시간을 측정 했을 때에 파일 저장소에 저장해내는 4번의 과정에서 대부분의 병목이 일어나는 것이 확인 되었습니다.

심지어 사용자는 서버1에만 파일을 전달하면 업로드에서 할일은 끝났는데, 2번, 3번, 4번 과정을 다 기다리고 나서야 응답을 받으니 사용자 경험이 상당히 떨어질 수 밖에 없었습니다. 

특히 파일용량이 5GB 정도를 넘어 갈 경우에는, 사실 1번 과정은 금방 끝나는데 (100Mbps 속도로도 10분 이내) 이후의 과정에서 응답에 너무 지연되다 보니 타임아웃도 빈번하게 일어나고 있었습니다.

![image-20220613212130417](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/delete-open-file.assets/image-20220613212130417.png)

이 문제 해결을 위해, 서버1과 서버2 양쪽에서 접근할 수 있는 폴더에 임시 파일을 떨어뜨려 놓은 후에 꼭 필요한 과정까지만 시행해 응답을 해 내고, 시간이 상당히 소모되는 4번 과정의 경우에는 별도의 쓰레드에서 작업을 하도록 코드를 변경 해 보았습니다.

> 서버1과 서버2가 물리적으로 하나의 서버에 각자의 도커 컨테이너에서 실행되고 있기 때문에 docker-compose 에서 volume으로 하나의 폴더를 함께 볼 수 있도록 설정을 해 주면 서버1과 서버2 간에는 실제로 파일을 이동시키지 않고 파일의 경로만을 전달 해 전송 과정을 절약 할 수 있습니다.

이 상황에서 시간이 상당 소비되는 4번 과정이 진행 되는 동안 업로드 한 파일의 요청이 올 경우에는, 원래대로라면 File Storage 에서 시작해 서버2 , 서버1을 거쳐 사용자에게 전달이 되어야 하지만 아직 준비가 된 상태가 아니기 때문에 마운트 된 저장소에 놓여진 임시 파일을 이용해 응답하도록 코드를 구성 했습니다.

그러다가, `4번 과정`이 모두 완료가 되면 데이터베이스상에 해당 파일이 서빙 될 준비가 되었다는 표시를 하며 동시에 mount 된 임시 파일을 삭제 함으로서 임시로 응답을 담당하던 부분의 역할이 끝나게 됩니다.

### 그런데

여기에서 고민이 생겼습니다. 4번 과정이 모두 완료 되어서 임시 파일을 삭제 할 경우에 만약 해당 파일이 누군가가 다운로드를 받고 있어 사용중이라면 어떤 문제가 생기는지, 또 해당 이슈를 어떻게 처리 할 지 생각이 필요했습니다.

이 경우 파일 삭제가 되지 않거나, 임시 파일을 다운로드 받고 있는 쪽에서 에러가 발생 하는 둘 중 하나의 경우에 놓일 거라고 예상했고, 그 결과에 따라 해결 방법을 찾아보려고 했습니다.

하지만 테스트 결과는 놀랍게도 임시 파일의 삭제는 문제 없이 이루어 졌으며, 심지어 삭제시점에 다운을 받고 있던 유저도 끝까지 아무런 에러 없이 온전한 파일을 다운 로드 받을 수 있었습니다. 이런 믿기 힘든 일이 어떻게 일어난 것 일까요?

## 사용중인 파일 삭제

우연에 기대어 코딩을 했다가는 후에 더 큰 비용을 지불하게 될 것이 뻔하기 때문에 확실히 하기 위해 다른 테스트 코드를 작성 해 보았습니다.

**FileInputStreamTest.java**

```java

/**
 * ## FileInputStream Test
 * once open a inputStream, it doesn't matter even if the original file is removed
 * <p>
 * 1. open a file stream
 * 2. delete the original file
 * 3. copy the file from input stream
 * 4. rename it to original one
 */
@Slf4j
public class FileInputStreamTest {

    public static void main(String[] args) throws IOException {

        final String HOME = System.getProperty("user.home");
        final String ORIGINAL = HOME + "/Downloads/test.jpeg";
        final String TARGET = HOME + "/Downloads/targetFile.jpeg";

        File file = new File(ORIGINAL);
        try (InputStream inputStream = Files.newInputStream(file.toPath())
        ) {
            if (file.delete()) {
                log.info("Original file deleted");
            } else {
                log.info("Failed deleting the original file");
            }

            for (int i = 0; i < 10; i++) {
                log.info("waits {}/10s", i + 1);
                Thread.sleep(1000);
            }

            File targetFile = new File(TARGET);
            Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            if (targetFile.isFile()) {
                log.info("target file created");
                if (targetFile.renameTo(file)) {
                    log.info("target file renamed");
                } else {
                    log.info("target file rename failed");
                }
            } else {
                log.info("{} is not a file", targetFile);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
```

> 코드 전문

코드는 간단합니다. 진행 과정을 차근 차근 살펴보면

![image-20220613214434276](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/delete-open-file.assets/image-20220613214434276.png)

일단 실행 전에 `~/downloads` 경로에 test.jpeg 파일을 하나 준비 해 두어야 합니다.

코드를 실행 하면, 일단 ORIGINAL 경로에 있는 `~/Downloads/test.jpeg` 파일로 inputStream을 하나 오픈 합니다.

```java
File file = new File(ORIGINAL);
  try (InputStream inputStream = Files.newInputStream(file.toPath())
      ) {
    if (file.delete()) {
      log.info("Original file deleted");
    } else {
      log.info("Failed deleting the original file");
    }
...}
```

그러고는 inputStream을 열자 마자 바로 해당 파일을 삭제 해 버립니다.

```java
for (int i = 0; i < 10; i++) {
  log.info("waits {}/10s", i + 1);
  Thread.sleep(1000);
}
```

파일 삭제 후에는 10초동안 카운팅을 하며 기다리고는

```java
File targetFile = new File(TARGET);
Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
```

파일이 삭제 되기 전에 오픈해둔 inputStream으로 targetFile 경로에 파일을 작성 합니다.

그럼 코드 실행 결과를 살펴보면..

![giphy](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/delete-open-file.assets/giphy.gif)

> 코드 실행과 함께 원본 파일이 삭제 되고, 10초의 카운팅 후에는 targetFile 이라는 이름으로 같은 내용의 파일을 작성 하고, 마지막에는 rename으로 감쪽같이 원상 복귀 해 냅니다.

상식적으로 쉽사리 납득하기 어려운데요.. inputStream을 여는 순간 파일이 통째로 메모리에라도 올라오는 걸까요? 아니면 파일이 삭제되지 않고 귀신이 남아 있는 걸까요..?

혹시나 싶어 리눅스에서도 맥북에서도 둘 다 같은 코드를 테스트 해 보았으나 여전히 정상적으로 작동 하였습니다.

## UNIX 시스템의 파일 관리

이 현상에 대해 InputStream 의 특이 동작인가 싶어 stackoverflow.com에 해당 코드와 함께 질문을 올렸고, 고맙게도 금방 답변을 받을 수 있었습니다.

![image-20220613215704118](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/delete-open-file.assets/image-20220613215704118.png)

Linux나 MacOS와 같은 Unix 시스템에서는 사용중인 파일을 삭제하거나 이동 할 수 없다고 하는데요. 윈도우에서는 이렇게 동작 하지 않는다고 합니다.

분명 파일이 삭제가 되었고, 폴더에서도 사라졌는데 삭제가 안된다니!

![image-20220613215917158](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/delete-open-file.assets/image-20220613215917158.png)

> https://stackoverflow.com/questions/2028874/what-happens-to-an-open-file-handle-on-linux-if-the-pointed-file-gets-moved-or-d

좀 더 자세히 작성된 설명을 읽어 보면..

사용중인 파일이 삭제 된다고 해도, 여전히 해당 파일은 열려 있고 **정상적으로 사용 가능한 상태** 라고 합니다. 일반적으로 우리가 예상하는 것과는 반대죠. 파일이 삭제 되기 이전에 해당 파일에 접근한 `사용`이 전부 닫히기 전까지는 해당 파일은 사실 진짜로 **삭제**된 상태는 아닙니다.

Unix 시스템에서는 사실 삭제라는 개념은 없고 `unlink` 만이 있는데, 파일을 삭제한 행위는 해당 폴더로부터의 링크를 삭제 한 것 이기 때문에 위의 코드에서는 삭제 이전에 InputStream을 열어 두었고 그렇기에 파일이 삭제되었어도 여전히 원본 파일에 접근이 가능 했던 것 입니다.

마찬가지로, 리눅스상에서 특정 프로그램이 실행 상태에서 열심히 로그를 찍고 있던 와중에 사용자가 해당 `.log` 파일을 삭제 해 버린다면, 실제로 로그는 사용자가 접근 할 수 없는 어딘가에 계속해서 작성이 되고 있으며 해당 디스크 공간도 확보가 되지 않습니다.

> 이 경우에는 해당 프로그램을 종료 해야만 해당 디스크 공간이 확보 되며, 로그도 접근가능한 영역에 작성이 됩니다.

맨 처음으로 돌아가

![image-20220613212130417](https://raw.githubusercontent.com/Shane-Park/mdblog/main/OS/linux/delete-open-file.assets/image-20220613212130417.png)

이 상황에서 mounted 폴더의 임시 파일을 서버2가 삭제 시켜 버렸다고 해도, 유저가 해당 임시 파일을 다운받는 중 이었다면 여전히 문제 없이 요청에 대한 응답을 해낼 수 있을 거라고 기대할 수 있습니다.

> 다만, 임시파일이 의도치 않게 overwrite 되지 않도록, 경로나 파일명 선정에 있어 무결성이 요구됩니다. 

## 마치며

지금까지 유닉스 기반의 OS 에서 파일 삭제가 일어나는 과정에 대해 알아 보았습니다.

직관적으로 충분히 예측 가능한 동작의 범위를 벗어 나기 때문에 당황스러웠지만 덕분에 새로운 사실을 알 수 있었고 종종 큰 용량의 파일을 삭제 해도 바로 여유공간으로 반환이 되지 않던 문제들에 대해서도 그 이유를 이해 할 수 있었습니다.

감사합니다. 