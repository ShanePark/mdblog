# JAVA) Date 를 LocalDateTime 으로, 혹은 그 반대로 변환하기

## Intro

제법 오래된 프로젝트를 유지보수 하고 개선하다보니, Date 타입을 다루어야 할 일이 제법 많이 있습니다.

**Date** API는 JDK 1.0 부터 제공된 유구한 역사를 가진 클래스인데요, 워낙 문제가 많다보니 JDK 1.1에서 바로 Calendar 클래스가 추가 되며 Date의 많은 메서드들을 `@Deprecated` 시켜버렸지만 여전히 오랜기간 꾸준히 애용되어 왔습니다.

그러다 마침내 JDK 1.8 버전에서 JodaTime 의 날짜와 시간 API가 추가되면서 LocalDateTime 이나 LocalDate를 사용 할 수 있게 되었습니다.

기존에 있는 Date를 걷어낼 수 있으면 참 좋겠지만, 그럴 여유가 없을 경우에는 일단 새로 작성하는 코드들에서 최대한 Date 사용을 피하지만, 어쩔 수 없이 부딪치는 경우에는 서로간의 변환이 필요합니다.

지금까지 서로간 변환하는 일은 종종 있었는데, 스스로 레퍼런스를 작성 해 두어서 필요할 때 좀 더 쉽게 찾아 쓰려고 합니다. 프로젝트에 있는 유틸 클래스에 해당 기능을 저장 해 둔다면 좀 더 편하게 사용 할 수 있습니다.

## Date -> LocalDateTime

첫번째로, Date 객체를 LocalDateTime으로 변환하는 것을 알아보겠습니다.

LocalDateTime이나 LocalDate 둘 다 변환하는 과정은 비슷 하니, 가장 많이 쓰이는 LocalDateTime으로 해보겠습니다.

```java
Date now = new Date();
LocalDateTime localDateTime = now.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
```

Date -> Instant -> ZonedDateTime -> LocalDateTime 순서로 변환되며 최종적으로 LocalDateTime 객체를 얻습니다.

특정 ZoneId를 사용해야 한다면, `ZoneId.systemDefault()` 대신 작성 하면 됩니다.

아래는 제대로 변환이 되는지 확인을 위해 작성 해둔 테스트 코드 입니다.

```java
@Test
@DisplayName("Date -> LocalDateTime")
public void DateToLocalDateTime() {
    Date now = new Date();
    LocalDateTime localDateTime = now.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

    Calendar cal = Calendar.getInstance();
    cal.setTime(now);

    // year
    assertThat(cal.get(Calendar.YEAR)).isEqualTo(localDateTime.getYear());
    // month
    assertThat(cal.get(Calendar.MONTH) + 1).isEqualTo(localDateTime.getMonth().getValue());
    // date
    assertThat(cal.get(Calendar.DATE)).isEqualTo(localDateTime.getDayOfMonth());
    // ms
    assertThat(now.getTime()).isEqualTo(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
}
```

## LocalDateTime -> Date

이번에는 반대의 경우입니다.

```java
LocalDateTime now = LocalDateTime.now();
Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
```

LocalDateTime -> ZonedDateTime -> Instant -> Date로, 위에서와 정확히 반대의 과정을 거쳐 변환됩니다.

테스트 코드는 아래와 같이 작성 해 보았습니다.

```java
@Test
@DisplayName("LocalDateTime -> Date")
public void LocalDateTimeToDate() {
    LocalDateTime now = LocalDateTime.now();

    Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);

    // year
    assertThat(cal.get(Calendar.YEAR)).isEqualTo(now.getYear());
    // month
    assertThat(cal.get(Calendar.MONTH) + 1).isEqualTo(now.getMonth().getValue());
    // date
    assertThat(cal.get(Calendar.DATE)).isEqualTo(now.getDayOfMonth());
    // ms
    assertThat(date.getTime()).isEqualTo(now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
}
```

![image-20220523144727992](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/java/Date-LocalDateTime.assets/image-20220523144727992.png)

모든 테스트 케이스를 정상적으로 통과하여, 동작에 문제 없음을 확인 하였습니다.

이상입니다.

ref: https://www.baeldung.com/java-date-to-localdate-and-localdatetime