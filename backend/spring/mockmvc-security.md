# [Spring] mockMvc 스프링 시큐리티와 연동하기. @AuthenticationPrincipal 파라미터 전달 해결

## Intro

mockMvc를 통해 api 테스트를 진행 하는데, 스프링 시큐리티를 연동 하기 전에는 괜찮았는데 연동을 한 후에 테스트가 제법 까다로웠습니다. 일단 단순 로그인 여부만 체크하는 부분은 간단하게 넘어갈 수 있었는데, `@AuthenticationPrincipal ` 어노테이션을 걸고 파라미터로 컨트롤러에서 로그인 정보를 받아오는 부분까지 테스트 하려니 쉽지 않았습니다.

제가 코틀린으로 토이 프로젝트를 하고 있다보니 코드는 코틀린이 대다수지만 사실 자바와 차이가 없기 때문에 자바로 진행하고 계신 분들도 같은 맥락으로 문제 해결을 하실 수 있으며, 마지막에는 자바 코드도 조금 첨부 해 두었습니다.

## MockMvc

### NoSecurity

일단 처음으로, 따로 시큐리티 인증 과정이 없는 api의 mockMvc 테스트 입니다.

```kotlin
package kr.quidev.quiz.controller_api

import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
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
    fun createQuiz() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/quiz/new")
                .with(SecurityMockMvcRequestPostProcessors.user("shane"))
                .param("desc", "desc")
                .param("answer", "answer")
                .param("explanation", "explanation")
                .param("examples", "example1")
                .param("examples", "example2")
                .param("examples", "example3")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("\"description\":\"desc\"")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.answer").value("answer"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.explanation", Matchers.containsString("explanation")))
    }
}

```

이때는, 시큐리티와 따로 연동되는 부분이 없기 때문에 단순하게 mockMvc를 주입 받아서 테스트를 수행 할 수 있습니다.

이제 후에 인증과정이 점점 추가되면서 계속 피곤해집니다.

### Security

이번에는 spring security 에서 인증을 요구하는 페이지를 테스트 해 보도록 하겠습니다.

**SpringSecurity.kt**

```kotlin
@Bean
fun filterChain(http: HttpSecurity): SecurityFilterChain {
  http.authorizeRequests()
  .antMatchers("/adm")
  .access("hasRole('ADMIN')")
  .antMatchers("/", "/join", "/login")
  .permitAll()
  .anyRequest()
  .authenticated()
  .and()
  .cors().and().csrf().disable()
  ...
}
```

위에 보이는 것 처럼, 루트 페이지와 `/join` 및 `/login` 을 제외하면 모두 인증이 필요 합니다. 위에서 post 요청을 보냈던 주소는 `/api/...` 이기 때문에 인증을 요구합니다. 그냥 post 요청을 보내면

![image-20220913214046248](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/mockmvc-security.assets/image-20220913214046248.png)

스프링 시큐리티가 로그인 페이지로 보내버리기 때문에, 기대하고 있는 200 응답 대신 302 응답을 받게 됩니다.

이제 이 문제를 해결 해야 하는데요. MockMvcRequestBuilders의 메서드를 보면 RequestPostProcessor를 파라미터로 받는 with 메서드가 있습니다.

![image-20220913214815713](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/mockmvc-security.assets/image-20220913214815713.png)

> RequestPostProcessor를 받습니다.

SecurityMockMvcRequestPostProcessors 를 보면 user 라는 public static 메서드가 보이는데요. `Authentication.getPrincipal()`을 가능하게 해줄 **UsernamePasswordAuthenticationToken**와 `UsernamePasswordAuthenticationToken.getPrincipal()`을 가능하게 해줄 **User**를 포함하는 SecurityContext를 만들어 준다고 합니다. user 정적 메서드를 호출 하며 원하는 username만 전달 해 주면 해결이 됩니다.

![image-20220913215048773](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/mockmvc-security.assets/image-20220913215048773.png)

반환타입인 UserDetailsRequestPostProcessor의 다이어그램은 아래와 같습니다.

RequestPostProcessor를 구현했기 때문에  `MockMvcRequestBuilders.with()` 의 파라미터로 사용이 가능합니다.

![image-20220913215659722](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/mockmvc-security.assets/image-20220913215659722.png)

이제 테스트 코드에 with 메서드를 활용 해서 로그인 유저도 Mocking을 해 줍니다.

```kotlin
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
    .param("desc", "desc")
    .param("answer", "answer")
    .param("explanation", "explanation")
    .param("examples", "example1")
    .param("examples", "example2")
    .param("examples", "example3")
    .content(jacksonObjectMapper().writeValueAsString(quizCreateDto))
  ).andExpect(MockMvcResultMatchers.status().isOk)
  .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("\"description\":\"desc\"")))
  .andExpect(MockMvcResultMatchers.jsonPath("$.answer").value("answer"))
  .andExpect(MockMvcResultMatchers.jsonPath("$.explanation", Matchers.containsString("explanation")))
}
```

> post 요청을 parameter 전달에서 body에 내용을 채우는 방식으로 변경 했지만 큰 틀은 비슷합니다.

이렇게 하면, 시큐리티가 요구하는 인증부분을 무사히 통과해 API 테스트에 성공 할 수 있습니다. 단순하게 유저명만 지정하면 되기 때문에 정말 간편합니다.

### @AuthenticationPrincipal 

이번에는 컨트롤러에서 로그인 한 사용자의 정보가 필요하기 때문에 `@AuthenticationPrincipal ` 어노테이션을 달았습니다.

그러고 나서 로그인 한 사용자 정보를 좀 더 편하게 사용 하기 위해서 **org.springframework.security.core.userdetails.User** 를 상속한 MemberContext 클래스를 만들었습니다.

**MemberContext.kt**

```kotlin
package kr.quidev.security.domain

import kr.quidev.member.domain.entity.Member
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class MemberContext(
    val member: Member, authorities: MutableCollection<out GrantedAuthority>?) :
    User(member.email, member.password, authorities) {
}

```

그러고 나서는 **UserDetailsService**를 구현한 CustomUserDetailsService를 만들어서, loaduserByUsername을 오버라이드 해, 위에서 만들었던 MemberContext를 반환하도록 했습니다. 

**CustomUserDetailsService.kt**

```kotlin
package kr.quidev.security.service

import kr.quidev.member.repository.MemberRepository
import kr.quidev.security.domain.MemberContext
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service("UserDetailsService")
class CustomUserDetailsService(val memberRepository: MemberRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {

        val member = memberRepository.findMemberByEmail(username).orElse(null)
            ?: throw UsernameNotFoundException("invalid email address")

        val roles = mutableListOf<GrantedAuthority>()
        roles.add(SimpleGrantedAuthority(member.role))

        return MemberContext(member, roles)
    }
}
```

이렇게 하면, 간단하게 memberContext.member로 꺼내서 멤버를 사용 할 수 있습니다.

제가 작성한 APi Controller는 아래와 같습니다.

```kotlin
@PostMapping("new")
fun createQuiz(
  @RequestBody @Valid createDto: QuizCreateDto,
  @AuthenticationPrincipal memberContext: MemberContext,
): ApiResponse {
  val quiz = quizService.createQuiz(memberContext.member, createDto)
  return ApiResponse.ok(mapOf(Pair("id", quiz.id)))
}
```

이렇게 함으로서, createQuiz에 회원 객체를 파라미터로 넘길 수 있게 되었는데요, 문제는 @AuthenticationPrincipal 입니다..

아까 작성한 테스트 코드를 실행 하면 이번에는

![image-20220913221729336](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/mockmvc-security.assets/image-20220913221729336.png)

> Request processing failed; nested exception is java.lang.NullPointerException: Parameter specified as non-null is null: method kr.quidev.quiz.controller_api.QuizApiController.createQuiz, parameter memberContext

분명 컨트롤러가 파라미터로 받기로 한 MemberContext가 넘어오질 못해서 에러가 발생합니다.

`SecurityMockMvcRequestPostProcessors.user(String username)`이 모킹을 열심히 해 주었지만, 사실 실제 회원의 데이터를 이용하는 비즈니스 로직까지 해결을 해 주기에는 무리가 있었습니다.

그래서 이제는, 스프링 시큐리티의 인증도 통과 하면서 실제 비즈니스 로직도 통과 할 수 있는 방법이 필요 합니다.

스프링 시큐리티와 MockMvc를 연동하는 방법에 대해 스프링 공식 문서를 이리 저리 뒤지다 보니 다행히도 비슷한 내용을 찾았습니다.

![image-20220913222147741](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/mockmvc-security.assets/image-20220913222147741.png)

> https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html

그런데 Kotlin에서 같은 내용을 적용 하려 하니 계속 에러가 발생했습니다.

![image-20220913222326636](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/mockmvc-security.assets/image-20220913222326636.png)

> Not enough information to infer type variable T

어쩐지 스프링 문서에서도 Kotlin을 선택하면 아래와 같이 나와있었는데요..

![image-20220913222412531](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/mockmvc-security.assets/image-20220913222412531.png)

> Kotlin은 이슈가 있음.

어쩔 수 없이 테스트 코드를 자바로 작성 해 보았습니다.

QuizApiControllerTestJava.java

```java
package kr.quidev.quiz.controller_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.quidev.member.domain.entity.Member;
import kr.quidev.member.service.MemberService;
import kr.quidev.quiz.domain.entity.QuizCreateDto;
import kr.quidev.quiz.domain.entity.Skill;
import kr.quidev.quiz.service.QuizService;
import kr.quidev.quiz.service.SkillService;
import kr.quidev.security.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.util.Collections;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * Use java test code instead
 * until https://youtrack.jetbrains.com/issue/KT-22208 is fixed
 * <p>
 * ref: https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class QuizApiControllerTestJava implements UserDetailsService {

    @Autowired
    QuizService quizService;

    @Autowired
    SkillService skillService;

    @Autowired
    MemberService memberService;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    WebApplicationContext context;
    @Autowired
    ObjectMapper mapper;

    @Test
    public void createQuiz() throws Exception {
        memberService.createMember(new Member(null, "pw", "name", "email", "role"));
        UserDetails user = customUserDetailsService.loadUserByUsername("email");

        Skill skill = skillService.save(new Skill(null, null, "java"));
        QuizCreateDto quizCreateDto = new QuizCreateDto("desc", "answ", "expl", skill.getId(), new String[]{"ex1", "ex2", "ex3"});

        ResultActions result = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build().perform(
                        MockMvcRequestBuilders.post("/api/quiz/new")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(SecurityMockMvcRequestPostProcessors.user(user))
                                .content(mapper.writeValueAsString(quizCreateDto))
                );

        result.andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new User("username", "pw", Collections.EMPTY_LIST);
    }
}

```

![image-20220913222615073](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/mockmvc-security.assets/image-20220913222615073.png)

그리고 자바로 작성한 테스트 코드가 성공 하였습니다!!

그런데 코틀린 프로젝트에서 자바로 테스트를 작성해놓고 이슈가 해결되길 마냥 기다릴 수는 없어서 코틀린 테스트를 또 기웃 거리다보니

![image-20220913222034218](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/mockmvc-security.assets/image-20220913222034218.png)

방금 자바 코드에서 적용한 것처럼, user 스태틱 메서드의 파라미터로 String 타입의 username 대신 UserDetails 타입의 user를 생성해서 보내면 만들어둔 MemberContext 까지도 충분히 전달이 될 수 있을거란 생각이 들었습니다.

그래서 코틀린에서도 같은 내용으로 코드를 적용 시켜 보았습니다.

```kotlin
package kr.quidev.quiz.controller_api

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kr.quidev.common.ApiResponse
import kr.quidev.member.domain.entity.Member
import kr.quidev.member.service.MemberService
import kr.quidev.quiz.domain.entity.QuizCreateDto
import kr.quidev.quiz.domain.entity.Skill
import kr.quidev.quiz.service.QuizService
import kr.quidev.quiz.service.SkillService
import kr.quidev.security.service.CustomUserDetailsService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.context.WebApplicationContext
import javax.transaction.Transactional


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
internal class QuizApiControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc
    private val log = LoggerFactory.getLogger(javaClass)
    private val mapper = jacksonObjectMapper()

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var quizService: QuizService

    @Autowired
    lateinit var skillService: SkillService

    @Autowired
    lateinit var userDetailService: CustomUserDetailsService

    @Autowired
    lateinit var memberService: MemberService

    val email = "shane@park.dev"

    @BeforeEach
    fun beforeEach() {
        memberService.createMember(Member(name = "name", password = "pass", email = email))
    }

    @Test
    @DisplayName("create quiz test: expected situation")
    fun createQuiz() {
        val user = userDetailService.loadUserByUsername(email)
        val skill = skillService.save(Skill(id = null, parent = null, name = "java"))

        val description = "desc"
        val answer = "answer"
        val explanation = "explanation"
        val quizCreateDto = QuizCreateDto(
            description = description,
            answer = answer,
            explanation = explanation,
            examples = arrayOf("example1", "example2", "example3"),
            skillId = skill.id
        )
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/quiz/new")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.user(user))
                .content(mapper.writeValueAsString(quizCreateDto))
        )
        result.andExpect(MockMvcResultMatchers.status().isOk)

        val content = result.andReturn().response.contentAsString
        log.info("content : {}", content)
        val response: ApiResponse = mapper.readValue(content)
        val body = response.body as Map<*, *>
        val id = body["id"].toString().toLong()

        val findById = quizService.findById(id).orElseThrow()
        assertThat(findById.answer).isEqualTo(answer)
        assertThat(findById.description).isEqualTo(description)
        assertThat(findById.explanation).isEqualTo(explanation)
        assertThat(findById.examples).hasSize(3)
        assertThat(findById.skill).isEqualTo(skill)
        assertThat(findById.skill?.name).isEqualTo("java")
    }

    @Test
    @DisplayName("create quiz test: Description is not provided")
    fun createQuizNoDesc() {
        val user = userDetailService.loadUserByUsername(email)
        for (description in arrayOf("", " ", null)) {
            val quizCreateDto = QuizCreateDto(
                description = description,
                answer = "answer",
                explanation = "explanation",
                examples = arrayOf("example1", "example2", "example3"),
                skillId = null
            )
            val result = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/quiz/new")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(SecurityMockMvcRequestPostProcessors.user(user))
                    .content(mapper.writeValueAsString(quizCreateDto))
            )
            result.andExpect(MockMvcResultMatchers.jsonPath("$.body").isEmpty)
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").isNotEmpty)
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("400"))

            log.info(result.andReturn().response.contentAsString)
        }
    }
}
```

테스트를 실행 하면

![image-20220913223126336](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/spring/mockmvc-security.assets/image-20220913223126336.png)

> 성공

테스트 하기가 까다로울 거라고 생각했는데 `@AuthenticationPrincipal` 로 인증 정보를 전달 하는 것도 성공 했습니다. 

회원 정보는 컨트롤러에서 id만 파라미터로 받고, 그걸로 회원 조회를 해서 비즈니스 로직을 돌려야 하나 고민도 했었는데 그렇게 되면 API 요청한 쪽의 인증 정보와 전달된 회원 정보가 일치하는지 확인하기가 쉽지 않을거라고 생각했고, 다행히 테스트 하는 방법을 찾아 낼 수 있었습니다. 

스프링 시큐리티 인증이 필요한 테스트에서 고생하고 있는 다른 분들에게 조금이나마 도움이 되었으면 합니다.

이상입니다. 감사합니다.

**References**

- https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html

- https://docs.spring.io/spring-security/site/docs/4.0.2.RELEASE/apidocs/org/springframework/security/test/web/servlet/request/SecurityMockMvcRequestPostProcessors.html

- https://stackoverflow.com/questions/38330597/inject-authenticationprincipal-when-unit-testing-a-spring-rest-controller