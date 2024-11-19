# 자바스크립트로 이미지에 워터마크 추가하기

## Intro

이미지에 워터마크를 추가하는 것은 저작권 보호나 브랜드 표시를 위해 필요한 작업이다. 

예전에는 포토샵같은 이미지 편집 툴이 필요했지만, 이제는 자바스크립트만으로도 간단하게 워터마크를 추가할 수 있다. 

이번 글에서는 HTML과 자바스크립트를 활용해 이미지를 웹 브라우저에서 직접 처리하고 워터마크를 삽입하는 방법을 알아본다.

## Canvas

이번 워터마크 추가에 사용하는 기술은 HTML5의 `<canvas>` 다. `canvas`는 픽셀 단위로 이미지를 그리고 수정할 수 있는 도구이며, 다음과 같은 과정을 통해 워터마크를 삽입할 예정이다.

1. `<canvas>`를 생성하고, 이미지 파일을 불러와 그대로 그린다.
2. 텍스트나 워터마크 이미지를 원하는 위치에 추가한다.
3. `canvas`의 결과를 다시 이미지로 변환한다.

이를 통해 별도의 서버나 이미지 편집 프로그램 없이 사용자의 브라우저에서 실시간으로 이미지 처리가 가능하다.

## 텍스트 워터마크 추가

아래 예시 코드는 이미지를 불러와 텍스트 워터마크를 추가하는 간단한 방법을 보여준다.

### HTML 파일

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>텍스트 워터마크 추가</title>
</head>
<body>
    <h1>이미지에 텍스트 워터마크 추가하기</h1>
    <img id="target-image" src="달.jpg" alt="Target Image" style="max-width: 100%; height: auto;">

    <script>
        function addWatermark(imgElement) {
            const canvas = document.createElement('canvas');
            const ctx = canvas.getContext('2d');

            // Canvas 크기를 이미지 크기와 동일하게 설정
            canvas.width = imgElement.naturalWidth;
            canvas.height = imgElement.naturalHeight;

            // Canvas에 원본 이미지를 그림
            ctx.drawImage(imgElement, 0, 0, canvas.width, canvas.height);

            // 워터마크 텍스트 추가
            ctx.font = "bold 24px Arial";
            ctx.fillStyle = "rgba(255, 255, 255, 0.5)";
            ctx.textAlign = "center";
            ctx.textBaseline = "middle";

            const text = "나의 워터마크";
            ctx.fillText(text, canvas.width - ctx.measureText(text).width / 2 - 20, canvas.height - 30);

            // Canvas를 이미지로 변환
            imgElement.src = canvas.toDataURL();
        }

        // 이미지 로드 후 워터마크 추가
        const imgElement = document.getElementById('target-image');
        imgElement.onload = () => addWatermark(imgElement);
    </script>
</body>
</html>

```

결과물은 아래와 같다. 이미지 우측 하단에 `나의 워터마크`라는 글씨를 새겼다.

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/frontend/html/watermark.assets/1.webp)

## 이미지 워터마크 추가

이번에는 텍스트 대신 이미지 파일을 워터마크로 추가한다.

참고로 워터마크 이미지는 투명 배경을 가진 PNG 파일이 좋다. 투명 배경이 없는 파일을 사용하면 원본 이미지 위를 덮어버릴 수 있으니 주의하자.

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>이미지 워터마크 추가</title>
</head>
<body>
    <h1>이미지에 워터마크 이미지 추가하기</h1>
    <img id="target-image" src="우주선.jpg" alt="Target Image" style="max-width: 100%; height: auto;">

    <script>
        function addWatermark(imgElement, watermarkSrc) {
            if (imgElement.dataset.processed) return;

            const canvas = document.createElement('canvas');
            const ctx = canvas.getContext('2d');

            // Canvas 크기를 이미지 크기와 동일하게 설정
            canvas.width = imgElement.naturalWidth;
            canvas.height = imgElement.naturalHeight;

            // Canvas에 원본 이미지를 그림
            ctx.drawImage(imgElement, 0, 0, canvas.width, canvas.height);

            // 워터마크 이미지 로드
            const watermark = new Image();
            watermark.src = watermarkSrc;
            watermark.onload = () => {
                const watermarkWidth = 70; // 워터마크의 너비
                const padding = 20; // 여백
                const watermarkHeight = watermark.naturalHeight * (watermarkWidth / watermark.naturalWidth);
                const x = canvas.width - watermarkWidth - padding;
                const y = padding;

                // 워터마크 이미지 그리기
                ctx.drawImage(watermark, x, y, watermarkWidth, watermarkHeight);

                // Canvas를 이미지로 변환
                imgElement.src = canvas.toDataURL();
                imgElement.dataset.processed = "true";
            };
        }

        const imgElement = document.getElementById('target-image');
        imgElement.onload = () => addWatermark(imgElement, 'github-mark-white.png');
    </script>
</body>
</html>
```

이번에는 이미지 우측 상단에 깃헙 로고를 워터마크로 추가해 보았다.

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/frontend/html/watermark.assets/2.webp)

## 결론

HTML5 `canvas`를 사용해 별도의 도구 없이 브라우저에서 실시간으로 이미지에 워터마크를 입혀 보았다.

위 코드를 기반으로 워터마크의 크기, 위치, 투명도 등을 자유롭게 조정할 수 있으니 이를 활용해 워터마크가 필요할때 써먹어보자.

**References**

- https://developer.mozilla.org/en-US/docs/Web/API/Canvas_API
- https://github.com/logos