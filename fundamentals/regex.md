# regex

용량이 큰 파일을 찾기 위해 검색에 사용했던 정규식

xml 파일 예시

```xml
<file name="radar.tar.gz" format="gz" size="1613936130">/radar.tar.gz</file>
```

사용한 정규식

```
(?=size="[0-9]{10})
```

> size="로 시작해 10개의 숫자로 이어지는 패턴