# [JAVA] zxing 활용해 QR코드 생성하기

## Intro

COVID 19 이후로 정말 많은것이 달라졌습니다. 하나하나 나열하기도 힘든 만큼 일상 생활 속에서 달라진 것 들이 많지만 그 중 하나의 기술을 뽑자면 QR 코드가 아닐까 싶습니다.

아주 오래전부터 있었지만 별다른 주목을 받지 못했고 그렇게 잊혀지는가 했는데 코로나로 인한 방문 기록, 전자문진표 등 조금씩 많이 쓰이는가 싶더니 카카오페이를 비롯한 여러가지 간편결제 서비스가 많아지면서 없어서는 안 될 기술이 되었습니다.

어플리케이션을 만들 때에도 곳곳에 QR코드를 활용 할 일이 많아졌는데요, QR코드 생성 한다면 크게 두가지 방법이 있습니다.

1. 구글의 [QR Codes API](https://developers.google.com/chart/infographics/docs/qr_codes) 에 요청
2. QR 코드를 작성하는 OpenSource를 활용해 로컬에서 생성

얼핏 보면 외부 API를 활용 하는게 간단해 보이는데 사실 생성해서 내보내는 것도 생각보다 쉽습니다. 아무리 구글이라고는 하지만 API를 유지 해 줄 의무가 있는건 아니기때문에 QR 코드를 직접 생성해서 내보내는 방식으로 진행 해 보도록 하겠습니다.

## ZXING

> https://github.com/zxing/zxing

Zxing(Zebra Crossing)는 구글에서 제공하는 오픈 소스인데요, 사실상 QR 코드를 스캔 하는 거의 대부분의 어플리케이션이 해당 프로젝트로 부터 파생되었다고 합니다.

![image-20220630175630246](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/zxing.assets/image-20220630175630246.png)

> QR Code를 비롯한 다양한 Format을 지원 합니다.

## 실습

바로 간단한 예제코드를 통해 QR 코드 생성을 해 보도록 하겠습니다. 스프링 부트 프로젝트로 작성 하였습니다.

### 의존성 추가

pom.xml 파일에 zxing core와 javase 를 추가 해 줍니다. 

```xml
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
    <version>3.5.0</version>
</dependency>
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>javase</artifactId>
    <version>3.5.0</version>
</dependency>
```

### 요청 페이지

**index.html**

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Title</title>
</head>
<body>
  QR CODE
  <form action="/qr", method="get">
    <input type="text" name="url"/><button type="submit">create</button>
  </form>
</body>
</html>
```

 QR 코드로 만들 URL 주소를 입력할 페이지를 생성 해 주었습니다. form 태그를 활용했습니다.

![image-20220630180019810](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/zxing.assets/image-20220630180019810.png)

> 위와 같은 간단히 URL을 적을 수 있는 페이지를 만들어 줍니다.

### Controller

이번에는 요청을 처리할 컨트롤러를 만들어 줍니다.

**QrController.java**

```java
@RestController
public class QrController {

    @GetMapping("qr")
    public Object createQr(@RequestParam String url) throws WriterException, IOException {
        int width = 200;
        int height = 200;
        BitMatrix matrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, width, height);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(out.toByteArray());
        }
    }
}
```

url을 특정 높이와 너비의 BitMatrix 로 생성 한 후, MatrixToImageWriter를 활용해서 이미지로 출력 해 주는 코드 입니다.

![image-20220630181230674](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/zxing.assets/image-20220630181230674.png)

> File로 작성 할 수도 있지만, 보통 일회성 이기 때문에 Stream에 작성 한 뒤에 응답 하도록 했습니다.

이제 프로젝트를 실행 해서 확인해 보겠습니다.

### 확인

![image-20220630180725448](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/zxing.assets/image-20220630180725448.png)

> https://shanepark.tistory.com 를 넣고 create 버튼을 클릭 했습니다.

![image-20220630181118949](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/zxing.assets/image-20220630181118949.png)

QR 코드가 생성 된 것이 확인 됩니다.

이제 핸드폰 카메라로 확인 해 보면

![image-20220630181048291](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/zxing.assets/image-20220630181048291.png)

정상적으로 입력한 페이지로 이동 하는 것이 확인 됩니다.

위의 코드와 구조를 조금만 변형 한다면 대부분의 경우에서 필요한 결과를 얻을 수 있을 거라고 생각합니다.

이상입니다.

reference

- https://www.callicoder.com/generate-qr-code-in-java-using-zxing/