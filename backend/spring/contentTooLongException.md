# JAVA에서 비동기 HTTP 요청시 멀티파트 파일 전송 (ContentTooLongException: Content length is unknown) 

<br>

## 문제

Multipart 요청을 비동기로 다른 API 서버에 보내려고 하는데 에러가 발생했습니다. 

사용자가 업로드 한 파일에 문제가 있는지 (금지 단어 포함 등) 를 확인 하기 위해 어플리케이션 내부에서 해당 파일을 또 다른 어플리케이션(파일 검사API)으로 보내는 로직을 수행하도록 구현을 하고 있었습니다. 사용자에게서 받은 Multipart file을 그대로 다시 API 서버로 전송을 하는데, Single Thread로 수행 했을 때는 문제가 없었습니다.

하지만 Single Thread로 수행 시, 요청한 사용자 입장에서는 파일 업로드에 걸리는 시간이 `사용자 -> 어플리케이션서버 -> 검증API서버` 이렇게  이렇게 두배로 늘어나게 됩니다.

그래서 어플리케이션 내부에서 검증 서버로의 전송은 비동기 요청으로 처리하기 위해 코드를 수정 하고 있었습니다.

<br><br>

비동기 요청을 위해 ScheduledExecutorService 을 Bean에 쓰레드풀을 지정해 미리 등록 해놓습니다.

```java
@Bean
public ScheduledExecutorService executor() {
    return Executors.newScheduledThreadPool(10);
}
```

<br><br>

Bean으로 등록되었으니 비즈니스 로직에서 사용 할 수 있습니다.	

```java
@Autowired
private ExecutorService executor;
```

<br><br>

URI 객체와 HttpEntity 객체를 생성 해서 post 요청을 보냈습니다.

여기에서 `file`은 Controller를 통해 받은 `MultipartFile` 입니다.

```java
URI uri = UriComponentsBuilder.fromHttpUrl(fileCheckApiServer) 
    .pathSegment("filecheck") 
    .build().toUri();

HttpEntity entity = MultipartEntityBuilder.create() 
    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE) 
    .setContentType(ContentType.MULTIPART_FORM_DATA) 
    .addBinaryBody("file", file.getInputStream(), ContentType.parse(file.getContentType()),
                   file.getOriginalFilename()) 
    .addTextBody("id", id)
    .setCharset(Charset.forName("UTF-8")) 
    .build();
executor.submit(() -> doPost(uri,entity) ); // 비동기 요청
```

> Caused by: org.apache.http.ContentTooLongException: Content length is unknown

잘 될 줄 알았는데 Post 요청을 보내는데 Content lengh is unknown 에러가 발생 했습니다.

<br><br>

## 원인

에러가 발생한 org.apache.http.entity.mime.MultipartFormEntity.class 를 확인 해 보았습니다. 객체의 프로터티 중 contentLength() 가 0 이하라서 문제가 발생 했습니다.

![image-20211103103528908](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/contentTooLongException.assets/image-20211103103528908.png)

그런데 아래줄을 보니 contentLength 가 25*1024 보다 커도 예외를 발생 시킵니다. 동기 요청으로 했을 때는 똑같이 `addBinaryBody` 에 `MultipartFile`의 inputStream을 그대로 담아 보내도 문제가 없었는데, 비동기에서는 동작 원리가 다른 걸로 추정 됩니다.

어쨌든 지금은 contentLength 가 0이하 인데, 비동기로 요청을 보내는 시점에서 스트림을 읽으려고 할 때, 이미 file에서 열었던 inputStream이 자동으로 닫힌 모양입니다.

<br><br>

## 해결

임시 파일을 생성해서 비동기 요청 후에 해당 파일을 삭제하는 방법으로 해결 해 보았습니다.

임시파일을 생성 한다면, 꼭 삭제 처리를 해주는게 좋은데요, 아이에 finally 구문에 넣어서 삭제한다면 예외가 발생해도 삭제처리가 진행 됩니다.

비동기로 실행 하기 위해 메서드를 한단계 더 나누었습니다.

<br><br>

첫번째 메서드에서는 임시 파일을 생성 한 뒤에, 해당 파일을 실제 요청을 진행하는 메서드로 넘겨 줍니다. 임시 파일로 변환된 후에는 기존 파일의 정보가 사라지기 때문에 파일이름, 파일의 컨텐츠 타입 정보를 함께 미리 전달 해 줍니다.

```java
public void checkFile(User user, MultipartFile file) throws IOException {

    File tmp = File.createTempFile("TMP~", "." + FilenameUtils.getExtension(file.getOriginalFilename()));
    try (FileOutputStream output = new FileOutputStream(tmp); InputStream input = file.getInputStream()) {
        IOUtils.copy(input, output);
    }

    executor.submit(() -> {
        checkFile(user, tmp, file.getOriginalFilename(), ContentType.parse(file.getContentType()));
    });

}

```

<br><br>

실제 요청을 진행하는 메서드 입니다. 파일을 전송하는 과정에 시간이 소요되기 때문에 비동기로 요청 합니다.

```java
public void checkFile(User user, File tmp, String originalFileName, ContentType contentType) {
		URI uri = UriComponentsBuilder.fromHttpUrl(fileCheckApiServer) 
            .pathSegment("filecheck") 
            .build().toUri();
		try {
			HttpPost request = new HttpPost(uri);
			HttpEntity entity = MultipartEntityBuilder
                .create()
				.addBinaryBody("file", tmp, contentType, URLEncoder.encode(originalFileName, "UTF-8")) 
				.addTextBody("id", user.getId()) 
				.build(); 
			request.setEntity(entity);
			HttpResponse response = HttpClientBuilder.create().build().execute(request);
			if (response.getStatusLine().getStatusCode() != 200) {
				log.warn("파일 검증 요청 실패 {} {}", request, response.getStatusLine());
			}
		} catch (IOException e) {
			log.warn("{} 파일 체크 에러{}", tmp.getName(), e);
		} finally {
			if (tmp != null && tmp.exists()) {
				if (!tmp.delete()) {
					log.warn("임시 파일 삭제 오류 {}", tmp);
				}
			}
		}
	}

```

<br><br>

참고로 addBinaryBody를 할 때 파일명에 한글을 쓰려면 인코딩 해서 보내고 받을 때에도 디코딩 해서 사용 해야 합니다.

마지막 `addBinaryBody`메서드 에서 Parameter가 파일명이 들어가는 자리 입니다.

나중에 검증하는 api에서는 아래와 같이 파일 명을 받아와 decode 해서 사용 할 수 있습니다.

```java
String fileName = URLDecoder.decode(file.getOriginalFilename(), "UTF-8");
```

<br><br>

임시 파일을 생성 해서 요청을 보낼 때는 요청도 문제없이 전송이 되었으며, 임시 파일도 잘 삭제되는 것을 확인 했습니다.

이상입니다.
