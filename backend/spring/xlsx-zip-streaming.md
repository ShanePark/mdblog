# 스프링부트 프로젝트에서의 엑셀 및 압축파일에 대한 스트리밍

## Intro

`엑셀파일`이나 `zip 압축파일`을 서버에서 생성해 다운로드를 제공할 때, **최초 응답시간**에 대한 고려가 필요하다. 

물론 미리 준비되어 있는 파일이라면 브라우저에 바로 응답을 보낼 수 있겠지만

- DB에서 데이터를 조회하고, 이를 바탕으로 엑셀 파일을 생성하는 경우 
- 사용자에게 여러 개의 파일을 압축된 형태로 제공하는 경우

이러한 상황에서는 파일을 준비하는 데 시간이 상당히 소요될 수 있다.

데이터의 크기에 따라 응답시간이 크게 달라지기 때문에 예상되는 응답 시간을 구체적으로 제공하기도 어려운 상황에서, 화면에 스피너만 딸랑 띄워놓는 것 만으로는 어지간한 사용자들의 인내심을 달래기 쉽지 않다. 서버에서 아무리 바쁘게 준비하고 있다고 뜨거운 열을 뿜어내며 소리쳐도 사용자는 서버가 멈춘 것으로 오해할 수 있다. 

이 글에서는 이러한 문제를 해결하기 위해 Spring Boot에서 `HttpServletResponse`를 직접 사용하여 파일을 실시간으로 생성 및 전송하는 방법에 대해 다룰 것이다.  일반적으로는 파일을 서버에서 생성하고 난 뒤에 응답을 보낸다면, 사용자는 파일이 완전히 생성될 때까지 대기해야 한다. 그러나 스트리밍 방식을 사용하면, 파일이 조금씩 생성되는 동안 꾸준히 그 부분을 사용자에게 전송할 수 있다. 

이렇게 한다고 실제 다운로드가 완료되는 시간이 개선되는건 아니지만, 즉각 오는 피드백으로 인해 사용자는 다운로드가 진행되고 있음을 알고 안심하게 되고, 더 나은 사용자 경험을 얻을 수 있다. 

## 예제 코드

임시 파일을 만들고, 해당 파일을 응답하는 시나리오에 대해서는 이미 잘 알고 있다고 생각해 개선 이전의 코드를 굳이 작성하지는 않겠다.

### ZIP 파일 스트리밍

ZIP 파일 생성에는 Java의 `ZipOutputStream`을 사용한다. 이 클래스를 이용하면 실시간으로 압축된 데이터를 출력 스트림에 작성할 수 있어, 파일을 일괄 로드하지 않고도 작업을 수행할 수 있다. 

아래의 코드를 살펴보자

```java
@RestController
public class FileController {

    @GetMapping("/zip")
    public void zip(HttpServletResponse response) throws IOException {
        File dir = new File("/여러파일들이/있는/경로/");
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return;
        }

        response.setContentType("application/zip");
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Disposition", "attachment; filename=\"files.zip\"");

        try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zos.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = fis.read(buffer)) != -1) {
                        zos.write(buffer, 0, read);
                    }
                    zos.closeEntry();
                }
            }
        }
    }
}
```

코드를 보면 처음에 response 에 MIME 타입 및 status, 그리고 헤더 설정을 해준다. 

Content-Disposition은 브라우저가 해당 파일을 어떻게 다룰지 결정해주는데, 파일명을 `files.zip`으로 지정했다. 만약 한글로 파일명을 지정하고 싶다면 오래전에 작성한 [일간에러 2021-12-22 파일 다운로드시 한글 파일명 처리](https://shanepark.tistory.com/305) 포스팅을 참고하도록 하자. 아쉽게도 그냥 무작정 한글로 넣는다고 파일명 지정이 잘 되지는 않는다.

추가로, zip 파일을 만들때는 파일명이 중복되지 않도록 특히 유의해야 한다. 테스트로 동일한 파일명의 엔트리를 2개 넣어보니 예상으로는 앞 혹은 뒤에 넣은 것 중 하나는 살아 남을 줄 알았는데 둘 중 하나도 안들어갔다. 중복의 가능성이 있다면 미리 파일명에 대해 처리하는 로직을 추가하도록 하자.

- 파일명 중복 방지를 위한 간단한 예시 코드 

```java
public String getZipEntryFileName(Set<String> fileNames, String filename) {
    if (!fileNames.contains(filename)) {
        fileNames.add(filename);
        return filename;
    }
    for (int i = 1; i < Integer.MAX_VALUE; i++) {
        String newFilename = nextFileName(filename, i);
        if (!fileNames.contains(newFilename)) {
            fileNames.add(newFilename);
            return newFilename;
        }
    }
    throw new IllegalStateException("Cannot find a unique filename for " + filename);
}

private String nextFileName(String filename, int i) {
      int dotIndex = filename.lastIndexOf('.');
      if (dotIndex == -1) {
          return filename + "(" + i + ")";
      }
      return filename.substring(0, dotIndex) + "(" + i + ")" + filename.substring(dotIndex);
  }

```

### 엑셀 파일 스트리밍

엑셀 파일은 Apache POI 라이브러리를 사용하여 `Workbook`과 `Sheet` 객체를 조작한다. 코드를 살펴보자

```java
@RestController
public class FileController {

    @GetMapping("excel")
    public void excel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=sample.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("sheet1");

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            for (int i = 0; i < 100_000; i++) {
                sheet.createRow(i).createCell(0).setCellValue("hello world" + i);
                if (i % 1000 == 0) {
                    workbook.write(outputStream);
                    outputStream.flush();
                }
            }
            workbook.write(outputStream);
            outputStream.flush();
            workbook.close();
        }
    }

}
```

이번에도 마찬가지로 먼저 response 에 MIME 타입과 헤더 설정을 해준다. status 코드는 따로 안넣으면 `200 OK`로 나간다.

outputstream에 workbook을 쓰는 타이밍이 중요한데, workbook을 모두 준비 한 다음에 write를 한다면 결국 그만큼 응답시간이 늦어진다. 그렇다고 매 row 마다 작성하는건 효울적이지 못하다.

**해당 코드의 한계**

하지만 위의 엑셀 작성 예시 코드는 몇가지 한계가 있다.

1. **메모리 문제**: 사용된 `XSSFWorkbook` 클래스는 내부적으로 모든 데이터를 메모리에 유지한다. 따라서 대용량의 엑셀 파일을 생성하려고 하면 OutOfMemoryError가 발생할 위험이 있다. 이를 해결하기 위해서는 `SXSSFWorkbook`와 같은 메모리 효율적인 클래스를 사용해야하는데 내부적으로 디스크에 임시 파일을 생성하므로 지금처럼 바로 응답할 수 없다.
2. **파일 크기 증가**: 예시 코드에서 `workbook.write(outputStream);`를 매 1000행마다 호출하고 있는데, 이렇게 하면 같은 데이터가 여러 번 출력 스트림에 쓰여지므로, 생성되는 엑셀 파일의 크기가 불필요하게 커진다. 일반적으로 `workbook.write()` 메서드는 작업이 완료된 후에 한 번만 호출해야 한다.

이 방법은 엑셀 파일의 총 row가 크지 않아 파일크기가 뻥튀기되는 단점이 수용 가능한 범위 내에서 제한적으로 쓰일 수 있을 것 같으며 몇가지 더 다른 방법을 찾아보고 시도해보았으나 `XML/ZIP` 포맷이 스트리밍 작성을 지원하게 디자인된건 아니라 불가능했다.

## Conclusion

이 방법을 사용하면 총 다운로드 파일 크기를 미리 알 수 없으므로, 정확한 다운로드 용량 및 시간은 제공하지 못한다.

그러나 첫 응답에 오랜 시간을 보내다 사용자의 경험을 해치는 것 보다는 다운로드는 하고 있는데 언제 끝나는지는 정확히 모르는 편이 나을 것이다.

지금까지 알아본 것 처럼 스트리밍을 활용하면 엑셀이나 ZIP 파일도 보다 나은 사용자 경험을 제공하며 생성하고 전송할 수 있다. 

**Referenes**

- https://lists.apache.org/thread/l7gf6dnydc4p9p0xpmvh7021zrk5vhdv