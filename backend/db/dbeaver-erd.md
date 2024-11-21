# DBeaver 사용해 ERD 추출

## Intro

DBeaver는 데이터베이스 관리할 때 정말 유용한 도구다. 

특히, ERD(Entity-Relationship Diagram) 추출 기능은 데이터베이스 구조를 시각적으로 확인하거나 팀원들과 공유할 때 아주 유용하다. 평소 개발하고 DB 접속할때는 인텔리제이를 사용하더라도 ERD 추출할때만큼은 DBeaver를 사용하고 있는 이유다.

이 글에서는 DBeaver를 사용해 ERD 이미지를 추출하는 방법을 단계별로 알아본다.

## ERD 추출

### Step 1: ER Diagram 생성

먼저 DBeaver에서 데이터베이스에 접속한다. 그리고 하단의 Project 윈도우에 있는 `ER Diagrams` 를 우클릭 하고 `Create New ER Diagram`을 클릭한다.

![ER Diagram 우클릭](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/db/dbeaver-erd.assets/1.webp)

### Step 2: 데이터베이스 범위 선택

이제 ERD 창이 열렸으면 전체 데이터베이스를 대상으로 할지, 아니면 특정 테이블만 포함할지를 선택한다.

public Schema를 클릭해서 생성하면 전체 데이터베이스가 들어가는데, 데이터 베이스의 버전관리 (liquibase, flyway 등) 에 사용되는 테이블은 ERD에 들어갈 필요가 없으니 보통 제외해서 생성했다.

![데이터 베이스 선택](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/db/dbeaver-erd.assets/2.webp)

다이어그램의 이름과 원하는 범위를 지정한 다음 `Finish`를 클릭한다.

그러면 아래와 같이 ERD가 생성된다.

![완성된 ERD](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/db/dbeaver-erd.assets/3.webp)

> 처음엔 보기 안좋으니, 마우스로 이리 저리 테이블 위치를 옮겨 보기 좋게 만드는 작업이 필요하다.

### Step 3: ERD 저장 옵션

ERD를 만들었으면 이제 이걸 저장하거나 출력해보자. 화면 우측 하단 메뉴에서 관련 옵션을 확인할 수 있다.

- **Print Diagram**: 우측 세번째 프린터모양 아이콘이다. ERD를 프린터로 출력.
- **Save diagram in external format**: 우측 두번째 사진 아이콘. ERD를 PNG로 저장한다.

![우측 하단 메뉴](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/db/dbeaver-erd.assets/4.webp)

### Step 4: 이미지 파일로 저장

파일로 저장하려면 `Save diagram in external format` 옵션을 선택한 뒤 원하는 파일 형식을 정한다. 여기서는 PNG 파일로 저장해보았다.

PNG 외에도 `gif` `bmp` `.erd` `.graphml` 등의 파일 형식을 지원한다.

![5](https://raw.githubusercontent.com/ShanePark/mdblog/main/backend/db/dbeaver-erd.assets/5.webp)

## 결론

이번 글에서는 DBeaver로 ERD를 생성하고 이미지로 저장하는 방법을 알아보았다. 

직접 해보면 정말 간단하니, 데이터베이스 구조를 시각적으로 정리할 때 적극 활용해보길 바란다.