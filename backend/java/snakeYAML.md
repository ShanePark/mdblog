# 자바에서 YAML 파일 작성하기

## YAML

> https://yaml.org

### YAML 이란

YAML은 사람이 읽을 수 있는(human-friendly) 데이터 직렬화 언어 입니다. 모든 프로그래밍 언어 사이에서 호환 됩니다.

서로 다른 시스템 간에 데이터를 주고 받기 위해서는 데이터 직렬화 뿐만 아니라, 데이터 포맷에 대한 약속이 필요한데요, 우리가 흔히 사용하고 있는 XML 이나 Json 등도 그 중 일부 입니다. 

이전까지 xml이 많이 쓰였지만, json이 뜨면서 xml은 대부분 사장되었는데요, 아무래도 xml이 json 형태에 비해 굉장히 거추장 스럽고 한눈에 들어오지도 않았기 때문 이라고 생각합니다. 

XML에서 JSON으로 발전 한 것 처럼, YAML은 json 보다도 더 비교적 직관적이고 읽기 편리합니다. 

### XML, JSON과의 비교

같은 데이터를 각각 XML, JSON, YAML로 표현했을 때는 다음과 같습니다.

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<person>
  <name>John Doe</name>
  <age>42</age>
  <height>180</height>
  <job>officeworker</job>
  <hobby>hiking</hobby>
  <hobby>swimming</hobby>
  <family>
    <father>Richard Doe</father>
    <mother>Jane Doe</mother>
    <wife>Clare Doe</wife>
    <children>Mat Doe</children>
    <children>Linda Doe</children>
  </family>
</person>

```

```json
{
  "name": "John Doe",
  "age": 42,
  "height": 180,
  "job": "officeworker",
  "hobby": [
    "hiking",
    "swimming"
  ],
  "family": {
    "father": "Richard Doe",
    "mother": "Jane Doe",
    "wife": "Clare Doe",
    "children": [
      "Mat Doe",
      "Linda Doe",
    ]
  }
}

```

```yaml
name: John Doe
age: 42
height: 180
job: officeworker
hobby:
- hiking
- swimming
family:
  father: Richard Doe
  mother: Jane Doe
  wife: Clare Doe
  children:
  - Mat Doe
  - Linda Doe
  
```

언뜻 봐도 그 차이가 큽니다.

## JAVA에서 YAML 작성

### SnakeYAML

그럼 YAML 파일을 작성 해 보겠습니다. 최근 진행중인 프로젝트에서 API 응답 데이터들을 수집하는 기능을 작성 하고 있었는데요, 어떤 api에서는  xml이, 어떤 api 에서는 json응답이 오다보니 통일시켜줄 무언가가 필요 했습니다.

그래서 모든 형식과 상위 호환되는 YAM을 사용하면 괜찮지 않을까 생각이 들어 YAML로 테스트를 해 보았습니다.

![image-20211116232053955](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/snakeYAML.assets/image-20211116232053955.png)

> https://yaml.org

YAML 공식 홈페이지에서는 JAVA에서 SnakeYAML 엔진을 사용하길 권장 하고 있습니다. 그럼 해당 라이브러리를 dependency에 추가해야 하지 않을까 하겠지만..

![image-20211116232936985](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/snakeYAML.assets/image-20211116232936985.png)

무려 spring-boot-starter에 기본으로 내장 되어 있습니다!

그럼 바로 코드를 작성 해 보도록 하겠습니다.

### 코드 작성

```java
package com.tistory.shanepark.file.yaml;

import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class createYamlUsingSnakeYaml {
    final static String folder = System.getProperty("user.home") + "/Documents";

    public static void main(String[] args) throws IOException {
        Map<String, Object> data1 = new HashMap<>();
        data1.put("name", "shane");
        data1.put("nationality", "korean");
        data1.put("jobs", new String[]{"programmer", "traveler"});
        data1.put("timestamp", new Date());

        Map<String, Object> data2 = new HashMap<String, Object>();
        data2.put("name", "shane2");
        data2.put("jobs", new String[]{"programmer"});
        data2.put("nationality", "english");

        Map<String, Object> result = new HashMap<>();
        result.put("data1", data1);
        result.put("data2", data2);

        Yaml yaml = new Yaml();
        FileWriter writer = new FileWriter(folder + "/file.yaml");
        yaml.dump(result, writer);
    }
}

```

일단 제가 Mac과 Linux를 번갈아 가며 사용하다보니, 폴더 경로를 하드코딩 하기가 껄끄러워서, `user.home` 프로퍼티를 받아 와 그 안의 Documents 폴더에 작성 하도록 해 두었습니다. Ubuntu에서도, Mac에서도 Documents 폴더가 똑같이 있거든요.

위와 같이 코드를 작성 한 뒤에 실행을 해 보면..

![image-20211116233341487](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/snakeYAML.assets/image-20211116233341487.png)

![image-20211116233401017](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/snakeYAML.assets/image-20211116233401017.png)

짠! 정말 손쉽게 yaml 파일이 생성 됩니다. 간단하죠. data1과 data2의 순서가 바뀐건 Map의 특성 때문인데요, 모두 아시는 것 처럼 Map이나 Set등은 순서가 없습니다. 다만, LinkedHashSet을 사용한다면 Map에도 순서를 부여 할 수 있습니다.

<br><br>

```java
package com.tistory.shanepark.file.yaml;

import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class createYamlUsingSnakeYaml {
    final static String folder = System.getProperty("user.home") + "/Documents";

    public static void main(String[] args) throws IOException {
        Map<String, Object> data1 = new LinkedHashMap<>();
        data1.put("name", "shane");
        data1.put("nationality", "korean");
        data1.put("jobs", new String[]{"programmer", "traveler"});
        data1.put("timestamp", new Date());

        Map<String, Object> data2 = new LinkedHashMap<String, Object>();
        data2.put("name", "shane2");
        data2.put("nationality", "english");
        data2.put("jobs", new String[]{"programmer"});

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("data1", data1);
        result.put("data2", data2);

        Yaml yaml = new Yaml();
        FileWriter writer = new FileWriter(folder + "/file.yaml");
        yaml.dump(result, writer);
    }
}

```

![image-20211116234606286](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/snakeYAML.assets/image-20211116234606286.png)

> 의도한 순서대로 YAML 파일이 작성 되었습니다.

## 마무리

코드가 복잡하지 않으니 쉽게 이해 하고 응용 하실 수 있습니다.

YAML의 장점으로 그 내부에 json을 통째로 포함 시킬 수도 있을 만큼 호환성이 좋을 뿐만 아니라, 보다 가볍고 들여쓰기를 강제하기 때문에 사람이 보기에도 한눈에 잘 들어옵니다. 심지어는 # 기호를 앞에 부여 주석을 작성하는 것도 가능합니다.

아직까지는 기계와의 대화에는 json이 널리 쓰이고 있지만, 사람이 직접 작성(스프링의 application.yaml 등) 할 때에는 yaml의 사용이 늘어나고 있습니다. 마침 스프링 부트에도 기본으로 포함 되어 있으니 많이 쓰면 좋을 것 같습니다.

 