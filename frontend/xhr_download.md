# jQuery AJAX대신 XHR로 파일 다운로드하기

## Intro

a 링크를 통한 파일 다운로드를 제공 하고 있었는데, 개선이 필요했습니다.

전체 데이터를 poi를 활용해 엑셀 파일을 생성 한 뒤에 반출을 해 주는 과정인데, 데이터가 클 수록 파일을 작성하는데 워낙 시간이 오래 걸리다보니 기다리는동안 사용자 경험이 너무 좋지 않았습니다.

그래서 파일 다운로드 과정을 AJAX를 이용해 비동기로 요청하고, 요청을 기다리는 동안 waitMe 를 이용해 로딩바를 보여주는 식으로 개선을 하기로 했습니다. 

<img src=https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/xhr_download.assets/gif.webp width=411 height=457 alt=1>

> https://github.com/vadimsva/waitMe

## AJAX

이를 위해 jQuery AJAX를 활용해 코드를 구현 해 보았습니다.

```javascript
$('#export').on('click', function () {
    var wait = $(this);
    wait.waitMe();
    $.ajax({
        url: '/file/download/excel',
    }).done(function (data) {
        var blob = new Blob([data], { type: "application/octetstream" });
        var link=document.createElement('a');
        link.href=window.URL.createObjectURL(blob);
        link.download="projets.xlsx";
        link.click();
    }).always(function(){
        wait.waitMe('hide');
    });
})
```

이렇게 해서 테스트 했더니 blob 데이터로 a 링크를 만들어서 파일 다운로드도 까지 문제 없이 성공했습니다.

그런데.. 데이터가 다 손상이 되어버리는 문제가 발생했습니다.

AJAX 응답을 통해 받은 받은 응답을 확인해보니 `typeOf(data)`가 string으로 오고 있었는데, jQuery AJAX가 데이터를 먼저 처리 하면서 binary 데이터가 인코딩/디코딩 되는 과정에서 손상 된 것으로 보입니다.

## XHR

다행히도 이때는, XMLHttpRequest를 바로 사용해서 해결이 가능했습니다. 

아래의 링크에 따르면

>  https://developer.mozilla.org/en-US/docs/Web/API/XMLHttpRequest/Sending_and_Receiving_Binary_Data

XMLHttpRequest 를 통해 Binary 데이터를 보내거나 받을 수 있다고 안내되어 있는데요.

이미지 파일을 다운 받는 예제 코드를 아래와 같이 작성 할 수 있습니다.

```javascript
const req = new XMLHttpRequest();
req.open("GET", "/이미지파일주소.webp", true);
req.responseType = "blob";

req.onload = (event) => {
  const blob = req.response;
  // blob 활용
};

oReq.send();
```

이를 토대로 아까의 코드를 XHR 을 사용하는 코드로 다시 작성 해 보았습니다.

```javascript
$('#export_project').on('click', function () {
    var wait = $(this);
    wait.waitMe();
    const req = new XMLHttpRequest();
    req.open("GET", '/file/download/excel', true);
    req.responseType = "arraybuffer";
    req.onload = function() {
        const arrayBuffer = req.response;
        if (arrayBuffer) {
            var blob = new Blob([arrayBuffer], { type: "application/octetstream" });
            var link=document.createElement('a');
            link.href=window.URL.createObjectURL(blob);
            link.download="projets.xlsx";
            link.click();
            wait.waitMe('hide');
        }
    };
    req.send();
})
```

이렇게 코드를 작성하니 정상적으로 아래의 순서대로 진행이 되었고

- 파일 다운로드 요청
- 로딩 시작
- 다운로드 완료 
- 로딩 종료

파일도 전혀 깨지지 않고 온전하게 다운로드 할 수 있었습니다.

XMLHttpRequest 호환성은 아래와 같습니다. 드디어 IE가 호환성 목록에서도 사라졌네요

![image-20220823174132612](https://raw.githubusercontent.com/Shane-Park/mdblog/main/frontend/xhr_download.assets/image-20220823174132612.webp)

> https://developer.mozilla.org/en-US/docs/Web/API/XMLHttpRequest

AJAX로 binary data를 다루는 방법에 대해 정말 많이 찾아보았는데도 애초에 그렇게 활용하는 사람들이 없는지 정말 많은 어려움을 겪었습니다.

혹시 꼭 AJAX를 써야겠다면 아래의 방법으로 할 수 있기는 한 듯 한데.. 확인해 보니 너무 복잡해서 XHR을 사용하는 편이 좋아 보입니다. 

> http://www.henryalgus.com/reading-binary-files-using-jquery-ajax/

이상입니다.

**References**

- https://developer.mozilla.org/en-US/docs/Web/API/XMLHttpRequest/Sending_and_Receiving_Binary_Data
- https://stackoverflow.com/questions/33902299/using-jquery-ajax-to-download-a-binary-file