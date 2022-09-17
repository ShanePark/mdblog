# [Spring] @PageableDefault 혹은 @SortDefault 여러 컬럼으로 정렬하기

## Intro

컨트롤러를 통해 페이지 정보를 받고, 서비스 레이어 및 Persistence Layer를 통해 여러개의 데이터를 조회 할 때 종종 `@Pageable Default` 어노테이션을 사용합니다.

예를 들어 컨트롤러에서 @PageDefault를 아래와 같이 설정 하면

**Kotlin**

```kotlin
@GetMapping
fun findAll(@PageableDefault(sort = ["id"]) pageable: Pageable): ApiResponse {
  return ApiResponse.ok(quizService.findAll(pageable).map { quiz -> QuizDto.of(quiz) })
}
```

**Java**

```java
@GetMapping
public ApiResponse findAll(@PageableDefault(sort = {"id"}) Pageable pageable) {
  ...
}
```

JPA가 Pageable 객체를 이용 해서, 아래와 같이 Order By가 포함된 쿼리를 만들어줍니다. 

![image-20220917221946201](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/multi-field-default-sorting.assets/image-20220917221946201.png)

> JPA가 만들어준 쿼리를 로그로 확인 하는 방법은 [[Spring Boot JPA] P6Spy 활용해 쿼리 로그 확인하기](https://shanepark.tistory.com/415) 글을 참고 해 주세요.

@SortDefault 어노테이션을 함께 달아도 결과는 같습니다.

```kotlin
@GetMapping
fun findAll(
  @SortDefault(sort = ["id"])
  @PageableDefault pageable: Pageable
): ApiResponse {
  return ApiResponse.ok(quizService.findAll(pageable).map { quiz -> QuizDto.of(quiz) })
}
```

> @SortDefault를 함께 쓴 경우

그렇다면 여러개의 컬럼을 정렬 기준으로 설정 하려면 어떻게 해야 할까요?

## 다중 컬럼으로 정렬

### 배열에 추가

일단 가장 간단하게, sort로 배열을 받기 때문에 여러개의 정렬 기준을 넣어주는 방법을 생각 할 수 있습니다.

```kotlin
@GetMapping
fun findAll(
  @SortDefault(sort = ["id", "createdDate"])
  @PageableDefault pageable: Pageable
): ApiResponse {
  return ApiResponse.ok(quizService.findAll(pageable).map { quiz -> QuizDto.of(quiz) })
}
```

sort 기준에 createdDate를 추가 해서 테스트를 해 보면

![image-20220917223219652](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/multi-field-default-sorting.assets/image-20220917223219652.png)

> order by에 id 와 함께 create_date 가 추가 되었습니다.

direction 설정을 해 주면, 정렬 방향도 설정 할 수 있습니다.

```kotlin
@GetMapping
fun findAll(
  @SortDefault(sort = ["id", "createdDate"], direction = Sort.Direction.DESC)
  @PageableDefault pageable: Pageable
): ApiResponse {
  return ApiResponse.ok(quizService.findAll(pageable).map { quiz -> QuizDto.of(quiz) })
}
```

이렇게 변경 하고 테스트를 실행 하면

![image-20220917223442249](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/multi-field-default-sorting.assets/image-20220917223442249.png)

> 의도한 정렬 기준과 반대라서 테스트는 실패 하지만, order by 에 정렬 기준이 desc로 변경 된 것을 확인 할 수 있습니다.

그렇다면, id는 asc로, createdDate는 desc로 각기 다른 정렬 순서를 설정 하려면 어떻게 해야 할까요?

### SortDefaults

이때는 `@SortDefault`에 내부에 선언된 `@SortDefaults` 어노테이션을 이용해 설정 할 수 있습니다.

![image-20220917223710213](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/multi-field-default-sorting.assets/image-20220917223710213.png)

> 여러 개의 SortDefault 어노테이션을 사용하기 위한 래퍼 어노테이션이라고 안내하고 있습니다.

이를 활용해서 코드를 작성 해 보았는데요.

코틀린에서는 `An annotation can't be used as the annotations argument` 에러가 발생 하며 의도처럼 되지 않았습니다.

**Kotlin**

![image-20220917225712184](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/multi-field-default-sorting.assets/image-20220917225712184.png)

일단 코틀린의 문제가 맞는지 확신을 얻기 위해 자바로 먼저 코드를 작성 해 보았습니다. Kotlin은 자바코드와 상호 호환 됩니다.

**Java**

```java
@GetMapping
public ApiResponse findAll(
  @SortDefault.SortDefaults({
    @SortDefault(sort = {"id"}, direction = Sort.Direction.ASC),
    @SortDefault(sort = {"createdDate"}, direction = Sort.Direction.DESC)})
  @PageableDefault Pageable pageable
) {
  Page<Quiz> result = quizService.findAll(pageable);
  return ApiResponse.Companion.ok(
    new PageImpl<>(result.stream().map(quiz -> QuizDto.Companion.of(quiz)).collect(Collectors.toList()),
                   pageable,
                   result.getTotalElements()));
}
```

이제 테스트를 돌려 보면

![image-20220917225804870](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/multi-field-default-sorting.assets/image-20220917225804870.png)

정확히 의도한 대로 id는 asc로, created_date는 desc로 조회하는 쿼리가 생성 되었습니다.

이제 코틀린에서의 발생했던 `An annotation can't be used as the annotations argument` 문제를 해결 해 보아야 겠네요

### An annotation can't be used as the annotations argument

일단 단순한 자바의 문법적인 차이라는 추측을 해 봅니다. 에러 메시지에서 유추 해 보면, 어노테이션을 어노테이션의 인자로 사용을 할 수 없다는데 저희는 @SortDefault를 인자로 넣긴 해야하거든요. 그래서 이것 저것 찾아보다 Redit에서 힌트를 얻었습니다. Annotation 이지만 `@` 를 지워 보라고 하네요.

![image-20220917230346563](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/multi-field-default-sorting.assets/image-20220917230346563.png)

> https://www.reddit.com/r/Kotlin/comments/78artp/annotation_problems_in_kotlin/

그래서 아래처럼 `@`을 지우고, 중괄호를 지워보니 에러 메시지가 사라졌습니다. 

**Kotlin**

```kotlin
@GetMapping
fun findAll(
  @SortDefault.SortDefaults(
    SortDefault(sort = ["id"], direction = Sort.Direction.ASC),
    SortDefault(sort = ["createdDate"], direction = Sort.Direction.DESC)
  )
  @PageableDefault pageable: Pageable
): ApiResponse {
  return ApiResponse.ok(
    quizService.findAll(pageable).map { quiz -> QuizDto.of(quiz) }
  )
}
```

> 확실히 코틀린쪽의 코드가 훨씬 간결하게 떨어집니다.

![image-20220917230704591](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/multi-field-default-sorting.assets/image-20220917230704591.png)

테스트 결과 역시 정상입니다.

이상입니다. 

**References**

- https://segmentfault.com/a/1190000041059114/en