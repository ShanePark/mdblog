# 네이버 Clova를 이용한 OCR

## Intro

네이버 Clova OCR은 이미지를 분석해 텍스트를 추출하는 도구다. 영수증, 명함, 문서 스캔 등 다양한 케이스에 활용할 수 있다. 특히 네이버가 제공하는 OCR API는 한국어 문서 처리에 최적화되어 있어, 다른 OCR 서비스보다 높은 정확도를 제공한다. Tesseract 를 활용한 인식에서 정확도가 떨어지는 문제가 있어서 비교를 위해 시도해보았다.

이번 포스팅에서는 네이버 Clova OCR을 설정하고 사용하는 방법을 단계별로 정리해 보겠다.

------

## Clova OCR 시작하기

### 1. 네이버 Clova OCR API 신청

먼저, 네이버 Clova OCR API를 사용하려면 네이버 클라우드 플랫폼(NCP) 계정이 필요하다.

1. **NCP 회원가입 및 로그인** 네이버 클라우드 플랫폼([https://www.ncloud.com](https://www.ncloud.com/))에 가입한다.
2. OCR 서비스 신청
   - https://www.ncloud.com/product/aiService/ocr
   - 이용신청 (결제수단을 등록해야 함)
   - 프로젝트를 생성하고 OCR API 사용 신청.

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/clova-ocr.assets/1.webp)

> 서비스가 상당히 많다

- 도메인을 생성할 때는 General로 생성하자. Template 서비스를 생성하면 Free 서비스 플랜 외에는 API 를 호출하지 않아도 과금이 된다.

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/clova-ocr.assets/2.webp)

> 도메인 생성 후에는 Demo를 통해 OCR을 테스트 해볼 수 있다. 100회까지 무료이며 데모에서도 1회 사용횟수가 차감된다. 100회 초과시에는 호출당 3원의 요금이 청구된다.

![3](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/clova-ocr.assets/3.webp)

> 테스트를 해보니 확실히 한글 인식율이 좋다

![4](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/clova-ocr.assets/4.webp)

이제 도메인 목록에서 우측 `옵션` 메뉴에 에 있는 `API Gateway 연동` 버튼을 클릭 한다.

![5](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/clova-ocr.assets/5.webp)

이제 `생성` 버튼을 클릭해 Secret Key를 생성한다.

아래의 API Gateway 수동 연동을 클릭하면 CLOVA OCR Invoke URL 를 확인할 수 있는데, 이번 글에서는 API Gateway 자동 연동을 이용해보겠다. `자동연동` 버튼을 클릭하기 앞서서 서비스에서 API Gateway 도 사용신청을 해야 한다.

![6](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/clova-ocr.assets/6.webp)

### 2. API 요청

자동연동을 누르면 APIGW Invoke URL이 생성된다.

API Gateway 서비스의 My Products 에 가서 OCR_CUSTOM_API_KR이 생성된 것을 확인한다.

![7](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/clova-ocr.assets/7.webp)

Postman을 활용해서 API를 요청해보자. Post 요청을 보내는데

헤더에는 아래 내용들을 입력한다.

```
Content-Type : application/json
X-OCR-SECRET : {X-OCR-SECRET}
```

![8](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/clova-ocr.assets/8.webp)

바디에는 이미지 인식 요청 BODY 형식으로 작성한다. raw 타입으로 넣어주면 된다.

```
{
    "images": [
      {
        "format": "png",
        "name": "menu",
        "data": null,
        "url": "https://modo-phinf.pstatic.net/20241122_49/1732242234132kUiCN_PNG/mosa6x3VP8.png"
      }
    ],
    "lang": "ko",
    "requestId": "string",
    "resultType": "string",
    "timestamp": {{$timestamp}},
    "version": "V1"
}
```

![9](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/clova-ocr.assets/9.webp)

요청을 보내 응답을 확인한다.

![10](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/clova-ocr.assets/10.webp)

## 결론

네이버 Clova OCR은 높은 정확도와 한국어에 최적화된 기능으로 실제 비즈니스에서도 활용 가능하다. 특히 한글 인식율이 굉장히 높아서 인상적이었다.

이번 글에서 제공된 예제를 참고해 Clova OCR API를 쉽게 적용해보자.

**References**

- https://guide.ncloud-docs.com/docs/clovaocr-example01
- https://api.ncloud-docs.com/docs/ai-application-service-ocr