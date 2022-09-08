# [Kotlin] 코틀린에서 Validation 이 안될때 해결방법

## 문제

create를 위한 DTO를 생성 해서 자바에서 사용했던 것 처럼 validation을 해 보려 했는데 밸리데이션이 전혀 먹히지가 않았습니다.

**QuizCreateDto.kt**

```kotlin
data class QuizCreateDto(

    @NotBlank
    val description: String,
    @NotBlank
    val answer: String,
    @NotBlank
    val explanation: String,
    val examples: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QuizCreateDto) return false

        if (description != other.description) return false
        if (answer != other.answer) return false
        if (explanation != other.explanation) return false
        if (!examples.contentEquals(other.examples)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = description.hashCode()
        result = 31 * result + answer.hashCode()
        result = 31 * result + explanation.hashCode()
        result = 31 * result + examples.contentHashCode()
        return result
    }
}
```

**Controller**

```kotlin
@PostMapping("new")
fun createQuiz(
  @RequestBody @Valid createDto: QuizCreateDto,
  bindingResult: BindingResult
): Quiz {
  return quizService.createQuiz(createDto)
}
```

아주 단순한 코드인데, `@NotBlank` 로 설정 되어 있는 description에 빈 문자열을 보냈는데도, 밸리데이션이 전혀 이루어지지 않아 bindingResult에 아무런 에러가 담기지 않는 문제가 발생 했습니다.

영 이상해 테스트 코드도 작성 해서 확인 해 보았습니다만, 밸리데이션이 전혀 동작하지 않고 있었습니다.

```kotlin
package kr.quidev.quiz.domain.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import javax.validation.Validation
import javax.validation.Validator

internal class QuizCreateDtoTest {

    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun descriptionBlank() {
        val quizCreateDto = QuizCreateDto(
            description = "",
            answer = "answer",
            explanation = "explanation",
            examples = arrayOf("e1", "e2", "e3")
        )
        val validate = validator.validate(quizCreateDto)
        assertThat(validate).hasSize(1)
    }

}
```

![image-20220907224546684](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/validation.assets/image-20220907224546684.png)

> 실패

## 원인 및 해결

프로퍼티나 주 생성자에 어노테이션을 달았을 때, 해당 코틀린 엘리먼트로 부터 생성되는 자바 엘리먼트들이 다양하기 때문에 정확히 어느 요소에 어노테이션이 달릴지 알 수 없습니다. 

![image-20220907230145276](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/validation.assets/image-20220907230145276.png)

> https://kotlinlang.org/docs/annotations.html#annotation-use-site-targets

이 때 Use-site Targets 를 이용하면 자바 코드로 변환시 원하는 대상에 대한 어노테이션 지정할 수 있습니다. 필드에 붙어야 하는 상황 이기 때문에, @field 어노테이션을 이용 하면 지금의 상황을 해결 할 수 있습니다.

`@NotBlank` 를 아래와 같이 `@field:NotBlank`로 변경 했습니다.

```kotlin
package kr.quidev.quiz.domain.entity

import javax.validation.constraints.NotBlank

data class QuizCreateDto(

    @field:NotBlank
    val description: String,
    @field:NotBlank
    val answer: String,
    @field:NotBlank
    val explanation: String,
    val examples: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QuizCreateDto) return false

        if (description != other.description) return false
        if (answer != other.answer) return false
        if (explanation != other.explanation) return false
        if (!examples.contentEquals(other.examples)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = description.hashCode()
        result = 31 * result + answer.hashCode()
        result = 31 * result + explanation.hashCode()
        result = 31 * result + examples.contentHashCode()
        return result
    }
}
```

이제 테스트 코드를 다시 실행 해 보면

![image-20220907230414818](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/validation.assets/image-20220907230414818.png)

이제는 아까 실패했던 테스트가 정상적으로 수행되는 것을 확인 할 수 있습니다.

API 요청시에도 검증이 되는지를 확인 해 봅니다. 이번에는 bindlingResult를 따로 받지 않고 400 에러가 발생하는지 확인을 해 보도록 하겠습니다.

```kotlin
@PostMapping("new")
fun createQuiz(
  @RequestBody @Valid createDto: QuizCreateDto,
): Quiz {
  return quizService.createQuiz(createDto)
}
```

api 요청시 의도대로라면 4xx 에러가 발생 해야 합니다. `.andExpect(MockMvcResultMatchers.status().is4xxClientError)` 로 검증 해 보도록 하겠습니다.

**테스트 코드**

```kotlin
package kr.quidev.quiz.controller_api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kr.quidev.quiz.domain.entity.QuizCreateDto
import org.hamcrest.Matchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@AutoConfigureMockMvc
@SpringBootTest
internal class QuizApiControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    @DisplayName("create quiz test: expected situation")
    fun createQuiz() {
        val quizCreateDto = QuizCreateDto(
            description = "desc",
            answer = "answer",
            explanation = "explanation",
            examples = arrayOf("example1", "example2", "example3")
        )
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/quiz/new")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.user("shane"))
                .content(jacksonObjectMapper().writeValueAsString(quizCreateDto))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("\"description\":\"desc\"")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.answer").value("answer"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.explanation", Matchers.containsString("explanation")))
    }

    @Test
    @DisplayName("create quiz test: Description is not provided")
    fun createQuizNoDesc() {
        val quizCreateDto = QuizCreateDto(
            description = "",
            answer = "answer",
            explanation = "explanation",
            examples = arrayOf("example1", "example2", "example3")
        )
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/quiz/new")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.user("shane"))
                .content(jacksonObjectMapper().writeValueAsString(quizCreateDto))
        )
            .andExpect(MockMvcResultMatchers.status().is4xxClientError)
    }

}

```

![image-20220907230737296](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/kotlin/validation.assets/image-20220907230737296.png)

이제는 원하는 대로 밸리데이션이 이루어 지고 있습니다.

Validation 뿐만 아니라, 자바 기반의 어노테이션 라이브러리를 사용 한다면 어디에 붙어야 하는지 정확히 명시해 줄 필요가 있다고 합니다.

이상입니다. 

**References**

- https://stackoverflow.com/questions/70215736/kotlin-spring-boot-bean-validation-not-working
- https://unluckyjung.github.io/kotlin/spring/2022/06/06/kotlin-validation-annotation/
- https://kotlinlang.org/docs/annotations.html#annotation-use-site-targets 