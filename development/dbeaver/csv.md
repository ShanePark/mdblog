# DBeaver) CSV 파일을 데이터베이스로 importing 하기

## Intro

엑셀 파일을 데이터베이스로 간편하게 이동하고 싶을 때가 있습니다. 혹은 필요한 데이터들을 csv 파일로 생성 해 두었는데, DB에 한번 에 넣고 싶을 때도, DBeaver를 이용하면 아주 간단하게 데이터를 밀어 넣을 수 있습니다.

### Excel 파일을 csv 파일로

<img src=https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112162516553.webp width=750 height=500 alt=1>

예를 들어 이런 엑셀 파일이 있을 때에, 간단하게 csv 파일을 생성 할 수 있습니다.

제가 사용중인 Libre Office를 예를 들면 `File` > `Save As...` 를 누릅니다. 엑셀에서도 방법은 같습니다.

<img src=https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112162703799.webp width=640 height=530 alt=2>

그럼 어떤 파일로 저장할지 하단에 설정 하는 셀렉트 박스가 있는데요, 거기에서 `.csv`파일을 선택 해 줍니다.

그러면 아래와 같이 names.csv 파일이 생성 됩니다.

![image-20220112162837726](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112162837726.webp)

## CSV 파일 임포팅

### CSV DB 생성

![image-20220112160348057](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112160348057.webp)

> Database 목록에서 Create -> Connection 을 클릭합니다.

![image-20220112160559174](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112160559174.webp)

> Popular DB 들이 나오는데요, 좌측 상단의 All 버튼을 클릭합니다.

![image-20220112160633701](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112160633701.webp)

> 그다음 csv 를 검색 해서 선택 해 줍니다.

![image-20220112160925667](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112160925667.webp)

> Path: 를 설정해주는데요, csv 데이터베이스로 사용할 폴터를 선택해줍니다. 저는 csvDB 라는 폴더를 생성 했습니다.

![image-20220112161021687](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112161021687.webp)

이제 Finish 를 눌러 종료 해 줍니다.

![image-20220112161106046](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112161106046.webp)

>  csvDB가 생성 되었습니다.

![image-20220112161051482](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112161051482.webp)

> csvjdbc 드라이버가 없다면 Download 할 수 있도록 자동으로 팝업이 뜹니다.

### CSV 파일 DB 확인

이제 설정한 csvDB 폴더에 scv 파일을 하나 넣어 보겠습니다.

`username.csv`

```csv
Username,Identifier,First name,Last name
booker12,9012,Rachel,Booker
grey07,2070,Laura,Grey
johnson81,4081,Craig,Johnson
jenkins46,9346,Mary,Jenkins
smith79,5079,Jamie,Smith

```

![image-20220112161430932](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112161430932.webp)

이후 DBeaver에 다시 돌아가 F5로 새로 고침을 해 보면, username. csv 의 파일 이름을 그대로 딴 테이블이 생성되어 있는 것을 확인 할 수 있습니다.

![image-20220112161704663](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112161704663.webp)

> 각각의 Column 들도 확인 됩니다.

```sql
select * from username
```

![image-20220112161751771](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112161751771.webp)

CSV 파일을 데이터 베이스 처럼 접근 가능 하게 되었습니다.

![image-20220112162938569](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112162938569.webp)

> 위에서 만들었던 names.csv 파일도 마찬가지로 접근이 가능합니다.

### Target DB에 접속

내보낼 테이블을 선택 한 뒤에, Export Data를 클릭 합니다.

![image-20220112163443372](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112163443372.webp)

> Export Data

#### Export Targer

![image-20220112163521850](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112163521850.webp)

> Export 할 Target을 선택 합니다. Database를 선택 해 줍니다.

#### Tables mapping

이번에는 Target container를 선택해 줍니다. 

![image-20220112163733375](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112163733375.webp)

> 우측의 Choose...를 눌러서

![image-20220112163645810](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112163645810.webp)

> 데이터를 저장할 테이블을 선택 해 줍니다.

이후에는 next> 를 계속 누르면

![image-20220112163859802](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112163859802.webp)

마지막으로 Confirm 화면이 나옵니다. Proceed를 눌러 진행 해 줍니다.

![image-20220112163937037](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112163937037.webp)

> 제가 지정한 테이블에 csv 파일로부터 읽어온 데이터들이 저장 된 것이 확인 됩니다.

### 더 쉬운 방법: Import Data

사실 더 쉬운 방법이 있긴 한데요.. 원하는 DB에서 우클릭 -> import Data를 선택 후

![image-20220112164104708](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112164104708.webp)

![image-20220112164135420](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112164135420.webp)

> Import source는 csv 파일. next를 누르고

![image-20220112164201026](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112164201026.webp)

> 원하는 파일을 찾아 Open 해줍니다.

![image-20220112164225306](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112164225306.webp)

> 파일을 불러 왔으면 Next 를 누르고

![image-20220112164244272](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112164244272.webp)

> Target container는 알아서 선택이 되었습니다. Target은 테이블 명이 되니 원한다면 더블클릭 해서 수정 해 줍니다.

이제 Next > 를 끝까지 눌러주고..

![image-20220112164309115](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112164309115.webp)

> 마지막으로 Proceed 버튼만 눌러 주면..

아주 간단하게 username_csv 테이블이 생성 됩니다.

![image-20220112164440357](https://raw.githubusercontent.com/Shane-Park/mdblog/main/development/dbeaver/csv.assets/image-20220112164440357.webp)

## 마치며

이상으로 엑셀 파일로 부터 CSV 파일을 만들고, 해당 파일을 데이터 베이스에 그대로 등록 하는방법에 대해 알아보았습니다.

이를 잘 활용하면, 엑셀파일을 DB에 그대로 등록할 수 있을 뿐만 아니라, 데이터베이스를 한번에 업데이트 해야 하고, 거기 필요한 데이터가 파일로 존재 할 때에도 약간의 쿼리만 작성하면 간단하게 해낼 수 있습니다.

CSV 파일을 그대로 DB에 넣을 거라면, `Import Data`로 간단하게 할 수도 있고, 여러개의 파일을 올려 두고 필요한 데이터만 사용 할 거라면, CSV 파일들을 모아놓은 폴더를 데이터베이스로 등록 후 사용하면 되겠습니다.

감사합니다.